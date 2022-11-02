--hw5-triggers.sql

--Q5 planeUpgrade Trigger
--Trigger Function for upgrading Plane
CREATE OR REPLACE PROCEDURE upgradePlaneHelper(flight_num integer, flight_time timestamp) AS
$$
DECLARE
    numberOfSeats    integer;
    upgradeFound     boolean := FALSE;
    currentPlaneType varchar(4);
    airplane_row     RECORD;
    airlinePlanes CURSOR FOR
        SELECT p.plane_type, p.plane_capacity
        FROM flight f
                 JOIN plane p ON f.airline_id = p.owner_id
        WHERE f.flight_number = flight_num
        ORDER BY plane_capacity;
BEGIN
    -- get number of seats for the flight
    numberOfSeats = getNumberOfSeats(flight_num, flight_time);
    raise notice '% number of seats for %', numberOfSeats, flight_num;

    -- get plane type
    SELECT plane_type
    INTO currentPlaneType
    FROM flight
    WHERE flight_number = flight_num;

    -- open cursor
    OPEN airlinePlanes;

    -- check if another plane owned by the airlines can fit current seats
    LOOP
        -- get next plane
        FETCH airlinePlanes INTO airplane_row;
        --exit when done
        EXIT WHEN NOT FOUND;

        -- found a plane can fit (we are starting from the smallest)
        IF numberOfSeats IS NULL OR numberOfSeats + 1 <= airplane_row.plane_capacity THEN
            upgradeFound := TRUE;
            raise notice '% should be upgraded', flight_num;
            -- if the next smallest plane can fit is not the one already scheduled for the flight, then change it
            IF airplane_row.plane_type <> currentPlaneType THEN
                raise notice '% is being upgraded to %', flight_num, airplane_row.plane_type;
                UPDATE flight SET plane_type = airplane_row.plane_type WHERE flight_number = flight_num;
            END IF;
            -- mission accomplished (either we changed the plane OR it is already the next smallest we can fit)
            EXIT;
        END IF;

    END LOOP;

    -- close cursor
    CLOSE airlinePlanes;
    IF NOT upgradeFound THEN
        RAISE EXCEPTION 'There is not any upgrade for the flight % on %',flight_num,flight_time;
    END IF;
END;
$$ language plpgsql;


CREATE OR REPLACE FUNCTION upgradePlane()
    RETURNS TRIGGER AS
$$
BEGIN
    raise notice '% is attempting upgrading', new.flight_number;
    -- downgrade plane in case it is upgradable
    CALL upgradePlaneHelper(new.flight_number, new.flight_date);
    RETURN NEW;
END;
$$ language plpgsql;

DROP TRIGGER IF EXISTS upgradePlane ON RESERVATION_DETAIL;
CREATE TRIGGER upgradePlane
    BEFORE INSERT
    ON RESERVATION_DETAIL
    FOR EACH ROW
EXECUTE PROCEDURE upgradePlane();

--TEST: Check the trigger upgradePlane

INSERT INTO plane (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
VALUES ('t001', 'Plane 01', 1, '2020-12-12', 2020, 3);
INSERT INTO plane (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
VALUES ('t002', 'Plane 02', 2, '2020-12-12', 2020, 3);
INSERT INTO plane (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
VALUES ('t003', 'Plane 03', 3, '2020-12-12', 2020, 3);
UPDATE flight
SET plane_type = 't001'
WHERE flight_number = 3;

INSERT INTO RESERVATION_DETAIL (reservation_number, flight_number, flight_date, leg)
VALUES (2, 3, TO_TIMESTAMP('11-05-2020 14:15', 'MM-DD-YYYY HH24:MI'), 3);

SELECT getNumberOfSeats(3, TO_TIMESTAMP('11-05-2020 14:15', 'MM-DD-YYYY HH24:MI')::timestamp without time zone);
-- should return 3

--Q6 cancelReservation Trigger
CREATE OR REPLACE PROCEDURE downgradePlaneHelper(flight_num integer, flight_time timestamp)
AS
$$
DECLARE
    numberOfSeats    integer;
    currentPlaneType varchar(4);
    airplane_row     RECORD;
    airlinePlanes CURSOR FOR
        SELECT p.plane_type, p.plane_capacity
        FROM flight f
                 JOIN plane p ON f.airline_id = p.owner_id
        WHERE f.flight_number = flight_num
        ORDER BY plane_capacity;
BEGIN
    -- get number of seats for the flight
    numberOfSeats = getNumberOfSeats(flight_num, flight_time);
    raise notice '% number of seats for %', numberOfSeats, flight_num;

    -- get plane type
    SELECT plane_type
    INTO currentPlaneType
    FROM flight
    WHERE flight_number = flight_num;

    -- open cursor
    OPEN airlinePlanes;

    -- check if another plane owned by the airlines can fit current seats
    LOOP
        -- get next plane
        FETCH airlinePlanes INTO airplane_row;
        --exit when done
        EXIT WHEN NOT FOUND;

        -- found a plane can fit (we are starting from the smallest)
        IF numberOfSeats - 1 <= airplane_row.plane_capacity THEN
            raise notice '% should be downgraded', flight_num;
            -- if the smallest plane can fit is not the one already scheduled for the flight, then change it
            IF airplane_row.plane_type <> currentPlaneType THEN
                raise notice '% is beign downgraded to %', flight_num, airplane_row.plane_type;
                UPDATE flight SET plane_type = airplane_row.plane_type WHERE flight_number = flight_num;
            END IF;
            -- mission accomplished (either we changed the plane OR it is already the smallest we can fit)
            EXIT;
        END IF;

    END LOOP;

    -- close cursor
    CLOSE airlinePlanes;

END;
$$ language plpgsql;


CREATE OR REPLACE FUNCTION reservationCancellation()
    RETURNS TRIGGER AS
$$
DECLARE
    currentTime      timestamp;
    cancellationTime timestamp;
    reservation_row  RECORD;
    reservations CURSOR FOR
        SELECT *
        FROM (SELECT DISTINCT reservation_number
              FROM RESERVATION AS R
              WHERE R.ticketed = FALSE) AS NONTICKETED
                 NATURAL JOIN (SELECT DISTINCT reservation_number, flight_date, flight_number
                               FROM RESERVATION_DETAIL AS RD
                               WHERE (RD.flight_date >= currentTime)) AS CANCELLABLEFLIGHT ;
BEGIN
    -- capture our simulated current time
    currentTime := new.c_timestamp;

    -- open cursor
    OPEN reservations;

    LOOP
        -- get the next reservation number that is not ticketed
        FETCH reservations INTO reservation_row;

        -- exit loop when all records are processed
        EXIT WHEN NOT FOUND;

        -- get the cancellation time for the fetched reservation
        cancellationTime = getcancellationtime(reservation_row.reservation_number);
        raise notice 'cancellationTime = % and currentTime = %', cancellationTime,currentTime;
        -- delete customer reservation if departures is less than or equal 12 hrs
        IF (cancellationTime <= currentTime) THEN
            raise notice '% is being cancelled', reservation_row.reservation_number;
            -- delete the reservation
            DELETE FROM RESERVATION WHERE reservation_number = reservation_row.reservation_number;
            raise notice '% is attempting downgrading', reservation_row.flight_number;
            CALL downgradePlaneHelper(reservation_row.flight_number, reservation_row.flight_date);
        END IF;

    END LOOP;

    -- close cursor
    CLOSE reservations;

    RETURN new;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS cancelReservation ON ourtimestamp;
CREATE TRIGGER cancelReservation
    AFTER UPDATE
    ON OURTIMESTAMP
    FOR EACH ROW
EXECUTE PROCEDURE reservationCancellation();

--TEST: Check the trigger cancelReservation
-- Insert the following tuples if you haven't already done it for Q5
INSERT INTO plane (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
VALUES ('t001', 'Plane 01', 1, '2020-12-12', 2020, 3);
INSERT INTO plane (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
VALUES ('t002', 'Plane 02', 2, '2020-12-12', 2020, 3);
INSERT INTO plane (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
VALUES ('t003', 'Plane 03', 3, '2020-12-12', 2020, 3);
UPDATE flight
SET plane_type = 't001'
WHERE flight_number = 3;

--INSERT values of RESERVATION_DETAIL Table
BEGIN;
INSERT INTO OURTIMESTAMP (c_timestamp)
VALUES (TO_TIMESTAMP('11-05-2020 02:15', 'MM-DD-YYYY HH24:MI'));
COMMIT;
SELECT getNumberOfSeats(3, TO_TIMESTAMP('11-05-2020 14:15', 'MM-DD-YYYY HH24:MI')::timestamp without time zone);
-- should return 3

BEGIN;
UPDATE OURTIMESTAMP
SET c_timestamp = TO_TIMESTAMP('11-03-2020 20:25', 'MM-DD-YYYY HH24:MI')
WHERE TRUE;
COMMIT;

/*
Create a trigger, called planeDowngrade, that changes the plane (type) of a flight
to an immediately smaller capacity plane (type) owned by the airline, if it exists,
when a reservation is deleted on that flight, making the larger capacity plane
unnecessary.
*/
CREATE OR REPLACE FUNCTION downgradePlane()
    RETURNS TRIGGER AS
$$
BEGIN
    RAISE NOTICE '% is attempting downgrading', old.flight_number;
    CALL downgradePlaneHelper(old.flight_number, old.flight_date);
    RETURN OLD;
END;
$$ language plpgsql;

DROP TRIGGER IF EXISTS planeDowngrade ON reservation_detail;
CREATE TRIGGER planeDowngrade
    BEFORE DELETE
    ON reservation_detail
    FOR EACH ROW
EXECUTE PROCEDURE downgradePlane();

/*
Create a trigger, called adjustTicket, that adjusts the cost of a reservation
when the price of one of its legs changes before the ticket is issued.
*/
CREATE OR REPLACE PROCEDURE updateReservationCostHelper(res_number integer, res_cost integer)
AS
$$
BEGIN
    UPDATE reservation SET cost = res_cost WHERE reservation_number = res_number;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION calculateReservationCost(res_number integer, res_date date)
    RETURNS INTEGER AS
$$
DECLARE
    res_dt_cursor CURSOR FOR SELECT reservation_number, DATE(flight_date) AS flight_dt, leg, airline_id, low_price, high_price
                  FROM reservation NATURAL JOIN reservation_detail NATURAL JOIN flight NATURAL JOIN price
                  WHERE reservation_number = res_number;
    res_dt_rec RECORD;
    total_res_cost integer = 0;
    leg_cost integer = 0;
BEGIN
    OPEN res_dt_cursor;
    LOOP
        FETCH res_dt_cursor INTO res_dt_rec;
        IF NOT FOUND THEN
            EXIT;
        END IF;
        IF (res_dt_rec.flight_dt = res_date) THEN
            leg_cost := res_dt_rec.high_price;
        END IF;
        IF (res_dt_rec.flight_dt != res_date) THEN
            leg_cost = res_dt_rec.low_price;
        END IF;
        total_res_cost = total_res_cost + leg_cost;
    END LOOP;
    CLOSE res_dt_cursor;
    RETURN total_res_cost;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION updateReservationCost()
    RETURNS TRIGGER AS
$$
DECLARE
    res_cursor CURSOR FOR SELECT * FROM reservation WHERE ticketed = 'false';
    reservation_rec RECORD;
    res_number integer;
    res_date date;
    res_cost integer;
BEGIN
    OPEN res_cursor;
    LOOP
        FETCH res_cursor INTO reservation_rec;
        IF NOT FOUND THEN
            EXIT;
        end if;
        SELECT reservation_rec.reservation_number INTO res_number;
        SELECT DATE(reservation_rec.reservation_date) INTO res_date;

        res_cost = calculateReservationCost(res_number, res_date);

        CALL updateReservationCostHelper(res_number, res_cost);
    END LOOP;
    CLOSE res_cursor;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS adjustTicket ON PRICE;
CREATE TRIGGER adjustTicket
    AFTER UPDATE
    ON PRICE
    FOR EACH ROW
EXECUTE FUNCTION updateReservationCost();

/*
You should write a trigger, called frequentFlyer, that adjusts the frequent_miles program
of a customer when the customer buys a ticket. The customer is automatically enrolled in the
airline’s frequent miles program with which the customer has traveled most legs. In the case of
a tie, the airline that has collected the most fare from the customer is selected. A second tie is
resolved in favor of the frequent miles program of the airline whose sale fires the trigger, if this
airline is part of the tie; otherwise the second tie is resolved as follows. The airline currently in
the customer record is selected if it is part of the tie, else an airline among the tie is randomly
selected.
*/
CREATE OR REPLACE FUNCTION calculateTotalFare(aid integer, cust_id integer)
    RETURNS INTEGER AS
$$
DECLARE
    res_dt_cursor CURSOR FOR SELECT DATE(reservation_date) AS res_dt, DATE(flight_date) AS flight_dt, leg, airline_id, low_price, high_price
                  FROM reservation NATURAL JOIN reservation_detail NATURAL JOIN flight NATURAL JOIN price
                  WHERE cid = cust_id AND airline_id = aid;
    res_dt_rec RECORD;
    total_fare integer = 0;
    leg_cost integer = 0;
BEGIN
    OPEN res_dt_cursor;
    LOOP
        FETCH res_dt_cursor INTO res_dt_rec;
        IF NOT FOUND THEN
            EXIT;
        END IF;
        IF (res_dt_rec.flight_dt = res_dt_rec.res_dt) THEN
            leg_cost := res_dt_rec.high_price;
        END IF;
        IF (res_dt_rec.flight_dt != res_dt_rec.res_dt) THEN
            leg_cost = res_dt_rec.low_price;
        END IF;
        total_fare = total_fare + leg_cost;
    END LOOP;
    CLOSE res_dt_cursor;
    RETURN total_fare;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE adjustFrequentMilesHelper(airline_abbrev varchar(10), customer_id integer)
AS
$$
BEGIN
    UPDATE customer SET frequent_miles = airline_abbrev WHERE cid = customer_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION adjustFrequentMiles(customer_id integer, res_num integer)
    RETURNS TRIGGER AS
$$
DECLARE
    airline_abbrev varchar(10);
    aid integer;
    _r record;
    total_fr integer;
BEGIN
    DROP TABLE IF EXISTS frequent_airlines;
    CREATE TEMPORARY TABLE frequent_airlines AS
    SELECT *
    FROM (SELECT airline_id, airline_abbreviation, COUNT(leg) AS number_of_legs, DENSE_RANK() OVER (ORDER BY COUNT(leg) DESC) a_rank
          FROM customer NATURAL JOIN reservation NATURAL JOIN reservation_detail NATURAL JOIN flight NATURAL JOIN airline
          WHERE cid = 1
          GROUP BY airline_id, airline_abbreviation
          ORDER BY a_rank) AS airline_by_rank;

    DROP TABLE IF EXISTS top_airline;
    CREATE TEMPORARY TABLE top_airline AS
    SELECT *
    FROM frequent_airlines
    WHERE a_rank = 1;

    IF (SELECT COUNT(*) FROM top_airline) = 1 THEN  -- If there are no ties in the airlines used by the customer (ranked by number of legs per airline)...
    -- Adjust the frequent_miles attribute of the reservation table where cid = customer_id to be the airline_abbreviation attribute of top_airline.
        CALL adjustFrequentMilesHelper((SELECT airline_abbreviation FROM top_airline), customer_id);
        RETURN NEW;
    END IF;

    IF (SELECT COUNT(*) FROM top_airline) > 1 THEN  -- In the event of a tie...
        ALTER TABLE top_airline
        ADD COLUMN total_fare INTEGER;

        SELECT * from top_airline;

        FOR _r in (SELECT * FROM top_airline) LOOP
            total_fr = calculateTotalFare(_r.airline_id, customer_id);
            UPDATE top_airline SET total_fare = total_fr WHERE top_airline.airline_id = _r.airline_id;
        END LOOP;

        DROP TABLE IF EXISTS highest_total_fare;
        CREATE TEMPORARY TABLE highest_total_fare AS
        SELECT *
        FROM (SELECT *, DENSE_RANK() OVER (ORDER BY total_fare DESC) fare_rank
              FROM top_airline
              ORDER BY fare_rank) AS htf
        WHERE fare_rank = 1;

        IF (SELECT COUNT(*) FROM highest_total_fare) = 1 THEN
            CALL adjustFrequentMilesHelper((SELECT airline_abbreviation FROM highest_total_fare), customer_id);
            RETURN NEW;
        END IF;

        IF (SELECT COUNT(*) FROM highest_total_fare) > 1 THEN -- Second tie...
            -- Resolved in favor of the frequent miles program of the airline whose sale fires the trigger, if this
            -- airline is part of the tie.
            IF (SELECT COUNT(*) FROM (SELECT DISTINCT airline_id FROM reservation_detail NATURAL JOIN flight NATURAL JOIN airline
                WHERE reservation_number = res_num) AS ct) = 1 THEN
                -- Get the airline that triggered the sale.
                SELECT airline_id FROM reservation_detail NATURAL JOIN flight NATURAL JOIN airline WHERE reservation_number = res_num
                AND leg = 1 INTO aid;

                IF EXISTS (SELECT airline_id FROM highest_total_fare WHERE airline_id = aid) THEN
                    CALL adjustFrequentMilesHelper((SELECT airline_abbreviation FROM highest_total_fare WHERE airline_id = aid), customer_id);
                    RETURN NEW;
                END IF;
            END IF;

            SELECT (SELECT frequent_miles FROM customer WHERE cid = customer_ID) INTO airline_abbrev;
            IF EXISTS (SELECT * FROM highest_total_fare WHERE airline_abbreviation = airline_abbrev) THEN
                CALL adjustFrequentMilesHelper((SELECT airline_abbreviation FROM highest_total_fare WHERE airline_abbreviation = airline_abbrev), customer_id);
                ELSE
                CALL adjustFrequentMilesHelper((SELECT airline_abbreviation FROM (SELECT *
                                                                                  FROM highest_total_fare
                                                                                  ORDER BY random()
                                                                                  LIMIT 1) AS tp), customer_id);
                RETURN NEW;
            END IF;
        END IF;
    END IF;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS frequentFlyer ON RESERVATION;
CREATE TRIGGER frequentFlyer
    AFTER UPDATE
    ON RESERVATION
    WHEN (ticketed = 'true')
EXECUTE FUNCTION adjustFrequentMiles(NEW.cid, NEW.reservation_number);






