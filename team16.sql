-- Database creation:
--hw5-db.sql
DROP SCHEMA PUBLIC CASCADE;
CREATE SCHEMA PUBLIC;
--Q1
DROP TABLE IF EXISTS AIRLINE CASCADE;
DROP TABLE IF EXISTS FLIGHT CASCADE;
DROP TABLE IF EXISTS PLANE CASCADE;
DROP TABLE IF EXISTS PRICE CASCADE;
DROP TABLE IF EXISTS CUSTOMER CASCADE;
DROP TABLE IF EXISTS RESERVATION CASCADE;
DROP TABLE IF EXISTS RESERVATION_DETAIL CASCADE;
DROP TABLE IF EXISTS OURTIMESTAMP CASCADE;
DROP DOMAIN IF EXISTS EMAIL_DOMAIN CASCADE;

--Note: This is a simplified email domain and is not intended to exhaustively check for all requirements of an email
CREATE DOMAIN EMAIL_DOMAIN AS varchar(30)
    CHECK ( value ~ '^[a-zA-Z0-9.!#$%&''*+\/=?^_`{|}~\-]+@(?:[a-zA-Z0-9\-]+\.)+[a-zA-Z0-9\-]+$' );

CREATE TABLE AIRLINE (
  airline_id            integer,
  airline_name          varchar(50)     NOT NULL,
  airline_abbreviation  varchar(10)     NOT NULL,
  year_founded          integer         NOT NULL,
  CONSTRAINT AIRLINE_PK PRIMARY KEY (airline_id),
  CONSTRAINT AIRLINE_UQ1 UNIQUE (airline_name),
  CONSTRAINT AIRLINE_UQ2 UNIQUE (airline_abbreviation)
);

CREATE TABLE PLANE (
    plane_type      char(4),
    manufacturer    varchar(10)     NOT NULL,
    plane_capacity  integer         NOT NULL,
    last_service    date            NOT NULL,
    year            integer         NOT NULL,
    owner_id        integer         NOT NULL,
    CONSTRAINT PLANE_PK PRIMARY KEY (plane_type,owner_id),
    CONSTRAINT PLANE_FK FOREIGN KEY (owner_id) REFERENCES AIRLINE(airline_id)
);

CREATE TABLE FLIGHT (
    flight_number   integer,
    airline_id      integer     NOT NULL,
    plane_type      char(4)     NOT NULL,
    departure_city  char(3)     NOT NULL,
    arrival_city    char(3)     NOT NULL,
    departure_time  varchar(4)  NOT NULL,
    arrival_time    varchar(4)  NOT NULL,
    weekly_schedule varchar(7)  NOT NULL,
    CONSTRAINT FLIGHT_PK PRIMARY KEY (flight_number),
    CONSTRAINT FLIGHT_FK1 FOREIGN KEY (plane_type,airline_id) REFERENCES PLANE(plane_type,owner_id),
    CONSTRAINT FLIGHT_FK2 FOREIGN KEY (airline_id) REFERENCES AIRLINE(airline_id),
    CONSTRAINT FLIGHT_UQ UNIQUE (departure_city, arrival_city)
);

CREATE TABLE PRICE (
    departure_city  char(3),
    arrival_city    char(3),
    airline_id      integer,
    high_price      integer     NOT NULL,
    low_price       integer     NOT NULL,
    CONSTRAINT PRICE_PK PRIMARY KEY (departure_city, arrival_city),
    CONSTRAINT PRICE_FK FOREIGN KEY (airline_id) REFERENCES AIRLINE(airline_id),
    CONSTRAINT PRICE_CHECK_HIGH CHECK (high_price >= 0),
    CONSTRAINT PRICE_CHECK_LOW CHECK (low_price >= 0)
);

--Assuming salutation can be NULL as many people don't use salutations on online forms
--Assuming last_name can be NULL as not everyone has a last name, like Cher
--Assuming phone is optional (can be NULL) but email is required
--Assuming that duplicate first_name and last_name pairs are possible since cid will be unique
--Assuming that email addresses should be unique in the table since multiple customers shouldn't sign up with
---the same email
CREATE TABLE CUSTOMER (
    cid                 integer,
    salutation          varchar(3),
    first_name          varchar(30)     NOT NULL,
    last_name           varchar(30),
    credit_card_num     varchar(16)     NOT NULL,
    credit_card_expire  date            NOT NULL,
    street              varchar(30)     NOT NULL,
    city                varchar(30)     NOT NULL,
    state               varchar(2)      NOT NULL,
    phone               varchar(10),
    email               EMAIL_DOMAIN    NOT NULL,
    frequent_miles      varchar(10),
    CONSTRAINT CUSTOMER_PK PRIMARY KEY (cid),
    CONSTRAINT CUSTOMER_FK FOREIGN KEY (frequent_miles) REFERENCES AIRLINE(airline_abbreviation),
    CONSTRAINT CUSTOMER_CCN CHECK (credit_card_num ~ '\d{16}'),
    CONSTRAINT CUSTOMER_UQ1 UNIQUE (credit_card_num),
    CONSTRAINT CUSTOMER_UQ2 UNIQUE (email)
);

--Assuming that a customer can make multiple reservations, i.e., cid and credit_card_num are not unique here
---since multiple reservations will have unique reservation_numbers
CREATE TABLE RESERVATION (
  reservation_number    integer,
  cid                   integer     NOT NULL,
  cost                  decimal     NOT NULL,
  credit_card_num       varchar(16) NOT NULL,
  reservation_date      timestamp   NOT NULL,
  ticketed              boolean     NOT NULL    DEFAULT FALSE,
  CONSTRAINT RESERVATION_PK PRIMARY KEY (reservation_number),
  CONSTRAINT RESERVATION_FK1 FOREIGN KEY (cid) REFERENCES CUSTOMER(cid),
  CONSTRAINT RESERVATION_FK2 FOREIGN KEY (credit_card_num) REFERENCES CUSTOMER(credit_card_num),
  CONSTRAINT RESERVATION_COST CHECK (cost >= 0)
);

CREATE TABLE RESERVATION_DETAIL (
  reservation_number    integer,
  flight_number         integer     NOT NULL,
  flight_date           timestamp   NOT NULL,
  leg                   integer,
  CONSTRAINT RESERVATION_DETAIL_PK PRIMARY KEY (reservation_number, leg),
  CONSTRAINT RESERVATION_DETAIL_FK1 FOREIGN KEY (reservation_number) REFERENCES RESERVATION(reservation_number) ON DELETE CASCADE,
  CONSTRAINT RESERVATION_DETAIL_FK2 FOREIGN KEY (flight_number) REFERENCES FLIGHT(flight_number),
  CONSTRAINT RESERVATION_DETAIL_CHECK_LEG CHECK (leg > 0)
);

-- The c_timestamp is initialized once using INSERT and updated subsequently
CREATE TABLE OURTIMESTAMP (
    c_timestamp     timestamp,
    CONSTRAINT OURTIMESTAMP_PK PRIMARY KEY (c_timestamp)
);
-- Functions from hw5-query sample solutions:
-- Q4 makeReservation Procedure

-- Q4 Helper Functions
-- Check if the reservation exit and if the flight exist
CREATE OR REPLACE FUNCTION validateReservationInfo(reservation_num integer, flight_num integer)
    RETURNS BOOLEAN AS
$$
DECLARE
    reservation_exist BOOLEAN := FALSE;
    flight_exist      BOOLEAN := FALSE;
    result            BOOLEAN := FALSE;
BEGIN
    SELECT (reservation_number = reservation_num)
    INTO reservation_exist
    FROM reservation
    WHERE reservation_number = reservation_num;

    SELECT (flight_number = flight_num)
    INTO flight_exist
    FROM flight
    WHERE flight_number = flight_num;

    IF (reservation_exist IS NULL OR flight_exist IS NULL) THEN
        result := FALSE;
    ELSE
        result := reservation_exist AND flight_exist;
    END IF;

    RETURN result;
END;
$$ LANGUAGE plpgsql;

--TEST: Check this function is working as expected

--SELECT validateReservationInfo(1, 1); -- should return true
--SELECT validateReservationInfo(1, 4); -- should return false

-- Get a letter if there is a flight or '-' if there isn't one
CREATE OR REPLACE FUNCTION getDayLetterFromSchedule(departure_date date, flight_num integer)
    RETURNS VARCHAR AS
$$
DECLARE
    day_of_week integer;
    weekly      varchar(7);
    day         varchar(1);
BEGIN
    SELECT EXTRACT(dow FROM departure_date) INTO day_of_week;

    SELECT weekly_schedule
    INTO weekly
    FROM FLIGHT AS F
    WHERE F.flight_number = flight_num;

    --CAUTION: substring function is one-index based and not zero
    SELECT substring(weekly from (day_of_week + 1) for 1) INTO day;

    RETURN day;
END;
$$ language plpgsql;

--TEST: Check this function is working as expected
--SELECT getDayLetterFromSchedule(TO_DATE('2020-11-03', 'YYYY-MM-DD'), 1); -- should return T
--SELECT getDayLetterFromSchedule(TO_DATE('2020-11-01', 'YYYY-MM-DD'), 1); -- should return S

-- Calculate the departure time based on the date and the flight schedule
CREATE OR REPLACE FUNCTION getCalculatedDepartureDate(departure_date date, flight_num integer)
    RETURNS timestamp AS
$$
DECLARE
    flight_time varchar(5);
BEGIN
    SELECT (substring(DEPT_TABLE.departure_time from 1 for 2) || ':' ||
            substring(DEPT_TABLE.departure_time from 3 for 2))
    INTO flight_time
    FROM (SELECT departure_time
          FROM FLIGHT AS F
          WHERE F.flight_number = flight_num) AS DEPT_TABLE;

    RETURN to_timestamp(departure_date || ' ' || flight_time, 'YYYY-MM-DD HH24:MI');
END;
$$ language plpgsql;

--TEST: Check this function is working as expected
--SELECT getCalculatedDepartureDate(TO_DATE('2020-11-03', 'YYYY-MM-DD'), 1); -- should return M

-- Q4 makeReservation Procedure
CREATE OR REPLACE PROCEDURE makeReservation(reservation_num integer, flight_num integer, departure_date date,
                                            leg_trip integer)
AS
$$
DECLARE
    information_valid      BOOLEAN := FALSE;
    calculated_flight_date timestamp;
    day                    varchar(1);
BEGIN

    -- make sure arguments are valid
    information_valid = validateReservationInfo(reservation_num, flight_num);

    IF (NOT information_valid) THEN
        RAISE EXCEPTION 'reservation number and/or flight number not valid';
    END IF;

    -- get the letter day from flight schedule corresponding to customer desired departure
    day = getDayLetterFromSchedule(departure_date, flight_num);

    IF day = '-' THEN
        RAISE EXCEPTION 'no available flights on desired departure day';
    END IF;

    -- check flight schedule to get the exact flight_date
    calculated_flight_date = getCalculatedDepartureDate(departure_date, flight_num);

    -- make the reservation
    INSERT INTO RESERVATION_DETAIL (reservation_number, flight_number, flight_date, leg)
    VALUES (reservation_num, flight_num, calculated_flight_date, leg_trip);
END;
$$ LANGUAGE plpgsql;

--Triggers, functions, and store procedures (new triggers and procedures from Milestone 2 are at the bottom):
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

--INSERT INTO plane (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
--VALUES ('t001', 'Plane 01', 1, '2020-12-12', 2020, 3);
--INSERT INTO plane (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
--VALUES ('t002', 'Plane 02', 2, '2020-12-12', 2020, 3);
--INSERT INTO plane (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
--VALUES ('t003', 'Plane 03', 3, '2020-12-12', 2020, 3);
--UPDATE flight
--SET plane_type = 't001'
--WHERE flight_number = 3;

--INSERT INTO RESERVATION_DETAIL (reservation_number, flight_number, flight_date, leg)
--VALUES (2, 3, TO_TIMESTAMP('11-05-2020 14:15', 'MM-DD-YYYY HH24:MI'), 3);

--SELECT getNumberOfSeats(3, TO_TIMESTAMP('11-05-2020 14:15', 'MM-DD-YYYY HH24:MI')::timestamp without time zone);
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
/*INSERT INTO plane (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
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
COMMIT;*/

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

CREATE OR REPLACE FUNCTION adjustFrequentMiles()
    RETURNS TRIGGER AS
$$
DECLARE
    airline_abbrev varchar(10);
    aid integer;
    _r record;
    total_fr integer;
BEGIN
    DROP TABLE IF EXISTS frequent_airlines; -- Rank the airlines by the number of legs traveled per airline in the customer's record.
    CREATE TEMPORARY TABLE frequent_airlines AS
    SELECT *
    FROM (SELECT airline_id, airline_abbreviation, COUNT(leg) AS number_of_legs, DENSE_RANK() OVER (ORDER BY COUNT(leg) DESC) a_rank
          FROM customer NATURAL JOIN reservation NATURAL JOIN reservation_detail NATURAL JOIN flight NATURAL JOIN airline
          WHERE cid = NEW.cid
          GROUP BY airline_id, airline_abbreviation
          ORDER BY a_rank) AS airline_by_rank;

    DROP TABLE IF EXISTS top_airline;   -- Get the "top" airline from the previous ranking.
    CREATE TEMPORARY TABLE top_airline AS
    SELECT *
    FROM frequent_airlines
    WHERE a_rank = 1;

    IF (SELECT COUNT(*) FROM top_airline) = 1 THEN -- If a tie is not present from the ranking, update the frequent_miles attribute in the customer table.
        CALL adjustFrequentMilesHelper((SELECT airline_abbreviation FROM top_airline), NEW.cid);
        RETURN NEW;
    END IF;

    IF (SELECT COUNT(*) FROM top_airline) > 1 THEN  -- First tie:
        ALTER TABLE top_airline
        ADD COLUMN total_fare INTEGER;  -- Add an attribute into the top_airline temporary table to track the total fare collected
                                        -- from the customer for each airline.

        FOR _r in (SELECT * FROM top_airline) LOOP  -- Calculate the total fare collected by that airline and add it to the temporary table.
            total_fr = calculateTotalFare(_r.airline_id, NEW.cid);
            UPDATE top_airline SET total_fare = total_fr WHERE top_airline.airline_id = _r.airline_id;
        END LOOP;

        DROP TABLE IF EXISTS highest_total_fare;    -- Rank the airlines in the first tie by the highest total fare collected.
        CREATE TEMPORARY TABLE highest_total_fare AS
        SELECT *
        FROM (SELECT *, DENSE_RANK() OVER (ORDER BY total_fare DESC) fare_rank
              FROM top_airline
              ORDER BY fare_rank) AS htf
        WHERE fare_rank = 1;

        IF (SELECT COUNT(*) FROM highest_total_fare) = 1 THEN   -- If no tie is present from the first breaking, update like before:
            CALL adjustFrequentMilesHelper((SELECT airline_abbreviation FROM highest_total_fare), NEW.cid);
            RETURN NEW;
        END IF;

        IF (SELECT COUNT(*) FROM highest_total_fare) > 1 THEN -- Second tie:
            -- Resolved in favor of the frequent miles program of the airline whose sale fires the trigger, if this
            -- airline is part of the tie.
            IF (SELECT COUNT(*) FROM (SELECT DISTINCT airline_id FROM reservation_detail NATURAL JOIN flight NATURAL JOIN airline
                WHERE reservation_number = NEW.reservation_number) AS ct) = 1 THEN
                -- Get the airline that triggered the sale (can only be true if one airline exists among the legs for that reservation
                -- (checked in the IF statement directly above):
                SELECT airline_id FROM reservation_detail NATURAL JOIN flight NATURAL JOIN airline WHERE reservation_number = NEW.reservation_number
                AND leg = 1 INTO aid;

                IF EXISTS (SELECT airline_id FROM highest_total_fare WHERE airline_id = aid) THEN -- Check if that airline exists within the tie.
                    CALL adjustFrequentMilesHelper((SELECT airline_abbreviation FROM highest_total_fare WHERE airline_id = aid), NEW.cid);
                    RETURN NEW;
                END IF;
            END IF;

            SELECT (SELECT frequent_miles FROM customer WHERE cid = NEW.cid) INTO airline_abbrev;
            IF EXISTS (SELECT * FROM highest_total_fare WHERE airline_abbreviation = airline_abbrev) THEN
                -- Otherwise, select a random airline from the tie
                -- to update the customer table with:
                CALL adjustFrequentMilesHelper((SELECT airline_abbreviation FROM highest_total_fare WHERE airline_abbreviation = airline_abbrev), NEW.cid);
                ELSE
                CALL adjustFrequentMilesHelper((SELECT airline_abbreviation FROM (SELECT *
                                                                                  FROM highest_total_fare
                                                                                  ORDER BY random()
                                                                                  LIMIT 1) AS tp), NEW.cid);
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
    FOR EACH ROW
    WHEN (NEW.ticketed = 'true')
EXECUTE FUNCTION adjustFrequentMiles();






