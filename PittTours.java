import java.util.Properties;
import java.sql.*;
import java.io.*;
import java.util.Scanner;
import java.util.Arrays;
import java.text.SimpleDateFormat;
//import java.util.Date;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Iterator;
@SuppressWarnings({"unchecked", "deprecation"})
public class PittTours{
    static String url;
    static Properties props;
    static BufferedReader br; //for reading file
    static Scanner sc; //for reading user input
    static Console console; //for password masking
    static Connection c;

    public static void main(String[] args) throws SQLException, ClassNotFoundException, FileNotFoundException, IOException{
        Class.forName("org.postgresql.Driver");
        url = "jdbc:postgresql://localhost:5432/";

        props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "G@rd@n99331");
        c = DriverManager.getConnection(url, props);

        sc = new Scanner(System.in);
        console = System.console();
        boolean continuePrompt = true;
        while(continuePrompt){
            baseOptions();
            String menuOption = sc.nextLine();
            switch(menuOption){
                case "1":
                    boolean isAdmin = checkAdmin();
                    if(isAdmin){
                        boolean adminPrompt = true;
                        while(adminPrompt)
                        {
                            adminUserOptions();
                            String adminOption = sc.nextLine();
                            switch(adminOption){
                                case "1":
                                    eraseDatabase();
                                    break;
                                case "2":
                                    loadAirline();
                                    break;
                                case "3":
                                    loadSchedule();
                                    break;
                                case "4":
                                    loadPricing();
                                    break;
                                case "5":
                                    loadPlane();
                                    break;
                                case "6":
                                    generatePassengerManifest();
                                    break;
                                case "7":
                                    updateTimestamp();
                                    break;
                                case "8":
                                    boolean customerPrompt = true;
                                    while(customerPrompt){
                                        customerOptions();
                                        String customerOption = sc.nextLine();
                                        switch(customerOption){
                                            case "1":
                                                task1(sc, c);
                                                break;
                                            case "2":
                                                task2(sc, c);
                                                break;
                                            case "3":
                                                task3(sc, c);
                                                break;
                                            case "4":
                                                task4(sc, c);
                                                break;
                                            case "5":
                                                task5(sc, c);
                                                break;
                                            case "6":
                                                task6(sc, c);
                                                break;
                                            case "7":
                                                task7(sc, c);
                                                break;
                                            case "8":
                                                task8(sc, c);
                                                break;
                                            case "9":
                                                task9(sc, c);
                                                break;
                                            case "10":
                                                task10(sc, c);
                                                break;
                                            case "11":
                                                task11(sc, c);
                                                break;
                                            case "12":
                                                task12(sc, c);
                                                break;
                                            case "13":
                                                task13(c);
                                                break;
                                            case "14":
                                                customerPrompt = false;
                                                break;
                                            case "15":
                                                exitProgram();
                                                break;
                                            default:
                                                System.out.println("Not a menu option, prompting again...");
                                        }
                                    }
                                    break;
                                case "9":
                                    adminPrompt = false; //go back to base level
                                    break;
                                case "10":
                                    adminPrompt = false;
                                    exitProgram();
                                    break;
                                default:
                                    System.out.println("Not a menu option, prompting again...");
                            }
                        }
                    }
                    break;
                case "2":
                    runCustomerUI(url, props);
                    break;
                case "3":
                    continuePrompt = false;
                    exitProgram();
                    break;
                default:
                    System.out.println("Not a menu option, prompting again...");
            }
        }
    }

    public static void baseOptions()
    {
        System.out.println("<~------------------Welcome------to------Pitt-------Tours------------------~>");
        System.out.println("|| Please select one of the following options:                             ||");
        System.out.println("|| ======================================================================= ||");
        System.out.println("|| (1): Login as Admin                                                     ||");
        System.out.println("|| (2): Access Customer Menu                                               ||");
        System.out.println("|| (3): Exit the Program                                                   ||");
        System.out.println("|| ======================================================================= ||");
        System.out.println("|| NOTE: Please be wary of erroneous input! Try to enter viable choices!   ||");
        System.out.println("<~-------------------------------------------------------------------------~>");

        System.out.print("                            ||Enter your option||: ");
    }

    public static void adminUserOptions(){

        System.out.println("<~------------------Administrator------Menu-------Options------------------~>");
        System.out.println("|| Please select one of the following options:                             ||");
        System.out.println("|| ======================================================================= ||");
        System.out.println("|| (1): Erase the Database                                                 ||");
        System.out.println("|| (2): Load Airline Information                                           ||");
        System.out.println("|| (3): Load Schedule Information                                          ||");
        System.out.println("|| (4): Load Pricing Information                                           ||");
        System.out.println("|| (5): Load Plane Information                                             ||");
        System.out.println("|| (6): Generate Passenger Manifest for Specific Flight on a Given Day     ||");
        System.out.println("|| (7): Update the Current Timestamp                                       ||");
        System.out.println("|| (8): Display more options                                               ||");
        System.out.println("|| (9): Go back one level                                                  ||");
        System.out.println("|| (10): Exit the Program                                                  ||");
        System.out.println("|| ======================================================================= ||");
        System.out.println("|| NOTE: Please be wary of erroneous input! Try to enter viable choices!   ||");
        System.out.println("<~-------------------------------------------------------------------------~>");

        System.out.print("                            ||Enter your option||: ");
    }

    public static void customerOptions(){
        System.out.println("<~--------------------Customer-------Menu-------Options--------------------~>");
        System.out.println("|| Please select one of the following options:                             ||");
        System.out.println("|| ======================================================================= ||");
        System.out.println("|| (1): Add Customer                                                       ||");
        System.out.println("|| (2): Show Customer Information (given a Customer Name)                  ||");
        System.out.println("|| (3): Find Price for Flights between Two Cities                          ||");
        System.out.println("|| (4): Find all Routes between Two Cities                                 ||");
        System.out.println("|| (5): Find all Routes between Two Cities of a Given Airline              ||");
        System.out.println("|| (6): Find all Routes with Available Seats Between Two Cities on a Date  ||");
        System.out.println("|| (7): Add Reservation                                                    ||");
        System.out.println("|| (8): Delete Reservation                                                 ||");
        System.out.println("|| (9): Show Reservation Information (given a Reservation Number)          ||");
        System.out.println("|| (10): Buy Ticket from Existing Reservation                              ||");
        System.out.println("|| (11): Find the Top-k Customers for each Airline                         ||");
        System.out.println("|| (12): Find the Top-k Traveled Customers for each Airline                ||");
        System.out.println("|| (13): Rank the Airlines based on Customer Satisfaction                  ||");
        System.out.println("|| (14): Go back one level                                                 ||");
        System.out.println("|| (15): Exit the Program                                                  ||");
        System.out.println("|| ======================================================================= ||");
        System.out.println("|| NOTE: Please be wary of erroneous input! Try to enter viable choices!   ||");
        System.out.println("<~-------------------------------------------------------------------------~>");

        System.out.print("                            ||Enter your option||: ");
    }

    public static boolean checkAdmin() throws SQLException, ClassNotFoundException, FileNotFoundException, IOException{
        String username = "", password = "";
        System.out.print("Username: ");
        username = sc.nextLine();
        // System.out.print("Password: ");
        password = new String(console.readPassword("Password: "));
        boolean loggedIn = false;

        Connection conn = DriverManager.getConnection(url, props);
        String sql = "SELECT * FROM USERS WHERE username = '" + username + "' and password = '" + password + "'";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            String user_type = rs.getString("user_type");
            if(user_type.equals("admin")){
                loggedIn = true;
                System.out.println("Successfully logged in");
            }else{
                System.out.println("Error. User " + username + " is not an administrator.");
            }

        } else {
            System.out.println("Username and/or password not recognized");
        }
        return loggedIn;
    }
    // Admin options here
    //----------------------------
    // Ask the user to verify deletion of all the data.
    // Simply delete all the tuples of all the tables in the database
    public static void eraseDatabase() throws SQLException, ClassNotFoundException
    {
        System.out.print("Are you sure you want to delete all data? (Enter Y for Yes and N for No): ");
        String input = sc.nextLine();
        if(input.equalsIgnoreCase("Y")){
            Connection conn = DriverManager.getConnection(url, props);
            PreparedStatement stmt = conn.prepareStatement("TRUNCATE TABLE AIRLINE CASCADE");
            stmt.executeUpdate();
            stmt = conn.prepareStatement("TRUNCATE TABLE PLANE CASCADE");
            stmt.executeUpdate();
            stmt = conn.prepareStatement("TRUNCATE TABLE FLIGHT CASCADE");
            stmt.executeUpdate();
            stmt = conn.prepareStatement("TRUNCATE TABLE PRICE CASCADE");
            stmt.executeUpdate();
            stmt = conn.prepareStatement("TRUNCATE TABLE CUSTOMER CASCADE");
            stmt.executeUpdate();
            stmt = conn.prepareStatement("TRUNCATE TABLE RESERVATION CASCADE");
            stmt.executeUpdate();
            stmt = conn.prepareStatement("TRUNCATE TABLE RESERVATION_DETAIL CASCADE");
            stmt.executeUpdate();
            stmt = conn.prepareStatement("TRUNCATE TABLE OURTIMESTAMP CASCADE");
            stmt.executeUpdate();
            // Statement st = conn.createStatement();
            // st.execute("TRUNCATE TABLE AIRLINE CASCADE"); //truncate table accounts for rows in case there is a FK to it
            // st.execute("TRUNCATE TABLE PLANE CASCADE");
            // st.execute("TRUNCATE TABLE FLIGHT CASCADE");
            // st.execute("TRUNCATE TABLE PRICE CASCADE");
            // st.execute("TRUNCATE TABLE CUSTOMER CASCADE");
            // st.execute("TRUNCATE TABLE RESERVATION CASCADE");
            // st.execute("TRUNCATE TABLE RESERVATION_DETAIL CASCADE");
            // st.execute("TRUNCATE TABLE OURTIMESTAMP CASCADE");

            System.out.println("Database successfully cleared.\nNow prompting for another choice:");
        }
        else{
            System.out.println("Database not cleared, continuing to prompt...");
        }
    }
    // Ask the user to supply the filename where the airline information is stored.
    // Load the information from the specified file into the appropriate table(s)
    // Question: Account for edge cases? Such as entering a redundant Airline ID??
    public static void loadAirline() throws SQLException, FileNotFoundException, IOException{
        System.out.print("                       ||Enter the name of the file||: ");
        String fname = sc.nextLine();
        File airline = new File(fname);
        if(!airline.exists() && !airline.isDirectory()){
            System.out.println("File not found!!\nNow prompting for another option...");
            return;
        }

        br = new BufferedReader(new FileReader(fname));
        while(br.ready()){
            String[] contents = br.readLine().split("\\t");
            StringBuilder query = new StringBuilder("INSERT INTO Airline values("); //sb concatenate on char array rather than creating new instance
            StringBuilder sub_sb = new StringBuilder();
            for(int i = 1; i < contents.length-1; i++){
                sub_sb.append("'" + contents[i] + "', ");

            }
            query.append(contents[0] + ", " + sub_sb.toString() + contents[contents.length - 1] + ")");
            Connection conn = DriverManager.getConnection(url, props);
            PreparedStatement stmt = conn.prepareStatement(query.toString());
            // Statement st = conn.createStatement();
            try{
                stmt.executeUpdate();
                // st.execute(query.toString()); //insert into Airline table
                System.out.println("Successsfully inserted " + query.toString());
            }
            catch(SQLException sql){
                System.out.println("SQL Error");
                while (sql != null) {
                    System.out.println("Message = " + sql.getMessage());
                    System.out.println("SQLState = "+ sql.getSQLState());
                    System.out.println("SQL Code = "+ sql.getErrorCode());
                    sql = sql.getNextException();
                }
            }
        }
    }

    // Ask the user to supply the filename where the schedule information is stored.
    // Load the information from the specified file into the appropriate table(s).
    public static void loadSchedule() throws SQLException, FileNotFoundException, IOException{
        System.out.print("                       ||Enter the name of the file||: ");
        String fname = sc.nextLine();
        File schedule = new File(fname);
        if(!schedule.exists() && !schedule.isDirectory()){
            System.out.println("File not found!!\nNow prompting for another option...");
            return;
        }

        br = new BufferedReader(new FileReader(fname));
        while(br.ready()){
            String[] contents = br.readLine().split("\\t");
            StringBuilder query = new StringBuilder("INSERT INTO Flight values("); //sb concatenate on char array rather than creating new instance
            query.append(contents[0] + ", " + contents[1] + ", ");
            for(int i = 2; i < contents.length; i++){
                if(i != contents.length - 1){
                    query.append("'" + contents[i] + "', ");
                }else{
                    query.append("'" + contents[i] + "')");
                }
            }
            Connection conn = DriverManager.getConnection(url, props);
            // Statement st = conn.createStatement();
            PreparedStatement stmt = conn.prepareStatement(query.toString());
            try{
                stmt.executeUpdate();
                // st.execute(query.toString()); //insert into Flight table
                System.out.println("Successsfully inserted " + query.toString());
            }
            catch(SQLException sql){
                System.out.println("SQL Error");
                while (sql != null) {
                    System.out.println("Message = " + sql.getMessage());
                    System.out.println("SQLState = "+ sql.getSQLState());
                    System.out.println("SQL Code = "+ sql.getErrorCode());
                    sql = sql.getNextException();
                }
            }

        }
    }
    // Ask the user to choose between L (Load pricing information) and C (change the price of an
    // existing flight).

    // If the user chooses C, then ask the user to supply the departure city, arrival city, high price
    // and low price. Your program needs to update the prices for the flight specified by the
    // departure city and arrival city that the user enters.
    // If the user chooses L, then ask the user to supply the filename where the pricing information
    // is stored. Load the information from the specified file into the appropriate table(s).
    // Question: If the corresponding FK is not in the table, what should we do??
    public static void loadPricing() throws SQLException, FileNotFoundException, IOException{
        System.out.print("Would you like to 'Load pricing information' or 'Change the price of an existing flight'?\nEnter L to Load and C to Change: ");
        String input = sc.nextLine();
        input = input.toUpperCase();
        switch(input){
            case "L":
                System.out.print("                       ||Enter the name of the file||: ");
                String fname = sc.nextLine();
                File load_file = new File(fname);
                if(!load_file.exists() && !load_file.isDirectory()){
                    System.out.println("File not found!!\nNow prompting for another option...");
                    return;
                }

                br = new BufferedReader(new FileReader(fname));
                while(br.ready()){
                    String[] contents = br.readLine().split("\\t");
                    StringBuilder query = new StringBuilder("INSERT INTO Price values("); //sb concatenate on char array rather than creating new instance
                    query.append("'" + contents[0] + "', '" + contents[1] + "', ");
                    for(int i = 2; i < contents.length; i++){
                        if(i != contents.length - 1){
                            query.append(contents[i] + ", ");
                        }else{
                            query.append(contents[i] + ")");
                        }
                    }
                    Connection conn = DriverManager.getConnection(url, props);

                    PreparedStatement stmt = conn.prepareStatement(query.toString());
                    // Statement st = conn.createStatement();
                    try{
                        stmt.executeUpdate();
                        // st.execute(query.toString()); //insert into Price table
                        System.out.println("Successsfully inserted " + query.toString());
                    }
                    catch(SQLException sql){
                        System.out.println("SQL Error");
                        while (sql != null) {
                            System.out.println("Message = " + sql.getMessage());
                            System.out.println("SQLState = "+ sql.getSQLState());
                            System.out.println("SQL Code = "+ sql.getErrorCode());
                            sql = sql.getNextException();
                        }
                    }
                }

                break;
            case "C":
                String deptCity = "", arrivalCity = "", highPrice = "", lowPrice = "";
                System.out.print("Enter the Departure City: ");
                deptCity = "'" + sc.nextLine() + "'";
                System.out.print("Enter the Arrival City: ");
                arrivalCity = "'" + sc.nextLine() + "'";
                System.out.print("Enter the new High Price: ");
                highPrice = sc.nextLine();
                try{
                    long parsed = Long.parseLong(highPrice);
                }catch(NumberFormatException nfe){
                    System.out.println("Non-numeric input entered for high price!!");
                    return;
                }
                System.out.print("Enter the new Low Price: ");
                lowPrice = sc.nextLine();
                try{
                    long parsed = Long.parseLong(lowPrice);
                }catch(NumberFormatException nfe){
                    System.out.println("Non-numeric input entered for low price!!");
                    return;
                }
                Connection conn = DriverManager.getConnection(url, props);

                // Statement st = conn.createStatement();
                StringBuilder query = new StringBuilder();
                query.append("UPDATE Price SET high_price = ");
                query.append(highPrice + ", low_price = ");
                query.append(lowPrice + " WHERE departure_city = " + deptCity + " and arrival_city = " + arrivalCity);
                PreparedStatement stmt = conn.prepareStatement(query.toString());
                try{
                    stmt.executeUpdate(); //update tuple in Price table
                    // st.execute(query.toString()); //update tuple in Price table
                    System.out.println("Successsfully updated " + query.toString());
                }
                catch(SQLException sql){
                    System.out.println("SQL Error");
                    while (sql != null) {
                        System.out.println("Message = " + sql.getMessage());
                        System.out.println("SQLState = "+ sql.getSQLState());
                        System.out.println("SQL Code = "+ sql.getErrorCode());
                        sql = sql.getNextException();
                    }
                }

                break;
            default:
                System.out.println("Invalid option!!!");
        }
    }
    // Ask the user to supply the filename where the plane information is stored.
    // Load the information from the specified file into the appropriate table(s)
    public static void loadPlane() throws SQLException, FileNotFoundException, IOException{
        System.out.print("                       ||Enter the name of the file||: ");
        String fname = sc.nextLine();
        File plane = new File(fname);
        if(!plane.exists() && !plane.isDirectory()){
            System.out.println("File not found!!\nNow prompting for another option...");
            return;
        }

        br = new BufferedReader(new FileReader(fname));
        while(br.ready()){
            String[] contents = br.readLine().split("\\t");
            StringBuilder query = new StringBuilder("INSERT INTO Plane values("); //sb concatenate on char array rather than creating new instance
            // query.append(contents[0] + ", " + contents[1] + ", ");
            int i;
            for(i = 0; i < contents.length; i++){
                if(i < 2 || isValidDate(contents[i])){
                    query.append("'" + contents[i] + "', ");
                }
                else if(i != contents.length - 1){
                    query.append(contents[i] + ", ");
                }
                else{
                    query.append(contents[i] + ")");
                }
            }
            Connection conn = DriverManager.getConnection(url, props);
            PreparedStatement stmt = conn.prepareStatement(query.toString());
            // Statement st = conn.createStatement();
            try{
                stmt.executeUpdate();
                // st.execute(query.toString()); //insert into Plane table
                System.out.println("Successsfully inserted " + query.toString());
            }
            catch(SQLException sql){
                System.out.println("SQL Error");
                while (sql != null) {
                    System.out.println("Message = " + sql.getMessage());
                    System.out.println("SQLState = "+ sql.getSQLState());
                    System.out.println("SQL Code = "+ sql.getErrorCode());
                    sql = sql.getNextException();
                }
            }

        }

    }

    // Ask the user to supply the flight number and the date.
    // Search the database to locate those passengers who bought tickets for the given flight and
    // the given date. Print the passenger list (salutation, first name, last name).
    public static void generatePassengerManifest() throws SQLException, FileNotFoundException, IOException{
        String flightNum = "", date = "", unparsed_date = "";
        System.out.print("Enter the Flight Number: ");
        flightNum = sc.nextLine();
        try{
            long parsed = Long.parseLong(flightNum);
        }catch(NumberFormatException nfe){
            System.out.println("Non-numeric input entered for flight number!!");
            return;
        }
        System.out.print("Enter the Date [mm/dd/yyyy]: ");
        unparsed_date = sc.nextLine();
        date = "'" + unparsed_date + "'";


        if(isValidDate(unparsed_date)){
            Connection conn = DriverManager.getConnection(url, props);

            // Statement st = conn.createStatement();
            ArrayList<Triplet> passengerList = new ArrayList<Triplet>();
            String query = "SELECT salutation, first_name, last_name\n"
                            + "FROM reservation_detail rd INNER JOIN reservation r ON\n"
                            + "rd.reservation_number = r.reservation_number\n"
                            + "INNER JOIN Flight f ON f.flight_number = rd.flight_number\n"
                            + "INNER JOIN Customer c ON c.cid = r.cid\n"
                            + "AND f.flight_number = " + flightNum + "\n"
                            + "AND rd.flight_date = TO_DATE(" + date + ", 'MM/DD/YYYY')\n"
                            + "WHERE r.ticketed = true";

            /*
            First join on the reservation_detail and reservation table to get reservations with
            flight date and to easily compare if a passenger has been ticketed.

            Then join on the flight to get the corresponding flights.

            Finally join on the customer table to get the name

            Doesn't work here for some reason???
            */
            /*

            WORKS ON POSTGRES:
            SELECT first_name || ' ' || last_name as Passenger
            FROM reservation_detail rd INNER JOIN reservation r ON
            rd.reservation_number = r.reservation_number
            INNER JOIN Flight f ON f.flight_number = rd.flight_number
            INNER JOIN Customer c ON c.cid = r.cid
            and f.flight_number = 1 AND rd.flight_date = TO_DATE('11/02/2020', 'MM/DD/YYYY')
            WHERE r.ticketed = true;

            ACTUAL:
            SELECT first_name, last_name
            FROM reservation_detail rd INNER JOIN reservation r ON
            rd.reservation_number = r.reservation_number
            INNER JOIN Flight f ON f.flight_number = rd.flight_number
            INNER JOIN Customer c ON c.cid = r.cid
            and f.flight_number = 1 AND rd.flight_date = TO_DATE('11/02/2020', 'MM/DD/YYYY')
            WHERE r.ticketed = true;
            */
            PreparedStatement stmt = conn.prepareStatement(query.toString());
            ResultSet res = stmt.executeQuery();
            // ResultSet res = st.executeQuery(query);
            String salutation;
            String fname, lname;
            while (res.next()) {
                salutation = res.getString("salutation");
                fname = res.getString("first_name");
                lname = res.getString("last_name");
                passengerList.add(new Triplet(salutation, fname, lname));
            }
            for(Triplet t : passengerList){
                System.out.println(t);
            }
        }
        else{
            System.out.println("Invalid input for date!");
        }
    }
    // Ask the user to supply a date and time to be set as the current timestamp (c timestamp) in
    // OurTimestamp table.
    public static void updateTimestamp() throws SQLException{
        String date_time = "", unparsed_date_time = "";
        System.out.print("Please supply the Date and Time to be set as the current timestamp (MM/DD/YYYY HH:MM): ");
        unparsed_date_time = sc.nextLine();
        date_time = "'" + unparsed_date_time + "'";
        if(isValidDatetime(unparsed_date_time)){
            Connection conn = DriverManager.getConnection(url, props);
            // Statement st = conn.createStatement();
            StringBuilder query = new StringBuilder("INSERT INTO OurTimestamp values(");
            query.append(date_time + ")");
            PreparedStatement stmt = conn.prepareStatement("TRUNCATE TABLE OURTIMESTAMP CASCADE");
            stmt.executeUpdate();
            // st.execute("TRUNCATE TABLE OURTIMESTAMP CASCADE");
            PreparedStatement stmt2 = conn.prepareStatement(query.toString());
            stmt2.executeUpdate();
            // st.execute(query.toString()); //insert into OurTimestamp table
            System.out.println("Successfully added " + query.toString());
        }else{
            System.out.println("Invalid input for date!");
        }


    }
    public static boolean isValidDate(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
        boolean flag = true;

        try{
           dateFormat.parse(date);
        }catch(Exception e){
           flag = false;
        }
      return flag;
    }
    public static boolean isValidDatetime(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy hh:mm");
        boolean flag = true;

        try{
           dateFormat.parse(date);
        }catch(Exception e){
           flag = false;
        }
      return flag;
    }
    //----------------------------
    //End Admin options
    public static void exitProgram()
    {
        System.out.println("\n<~-------------------Exiting----------Pitt--------Tours--------------------~>");
        System.out.println("||=========================================================================||");
        System.out.println("||                          Terminating Program...                         ||");
        System.out.println("||                               Goodbye!                                  ||");
        System.out.println("||=========================================================================||");
        System.out.println("<~-------------------------------------------------------------------------~>\n");

        System.exit(0);
    }



    public static void task1(Scanner input, Connection conn){

        try{
            String salutation = "";
            String fname = "";
            String lname = "";
            String street = "";
            String city = "";
            String state = "";
            String phone = "";
            String email = "";
            String cc = "";
            String expiration = "";
            String freqMiles = "";

            System.out.print("\nEnter a salutation (Mr, Mrs, Ms, Dr, etc): ");
            salutation=input.nextLine();
            System.out.print("\nEnter your first name: ");
            fname=input.nextLine();
            System.out.print("\nEnter your last name: ");
            lname=input.nextLine();
            System.out.print("\nEnter your street: ");
            street=input.nextLine();
            System.out.print("\nEnter your city: ");
            city=input.nextLine();
            System.out.print("\nEnter your state (two letter abbreviation): ");
            state=input.nextLine();
            System.out.print("\nEnter your phone number (Do not separate the numbers by anything): ");
            phone=input.nextLine();
            System.out.print("\nEnter your email address: ");
            email=input.nextLine();
            System.out.print("\nEnter your credit card number (no spaces): ");
            cc=input.nextLine();
            System.out.print("\nEnter your credit card's expiration date (yyyy-mm-dd): ");
            expiration=input.nextLine();
            String y = expiration.split("-")[0];
            String m = expiration.split("-")[1];
            String d = expiration.split("-")[2];

            if(expiration.length() != 10){
                System.out.println("\nFormat Error: Follow date format of yyyy-mm-dd");
                return;
            }else if(y.length() != 4){
                System.out.println("\nFormat Error: The year should have four digits");
                return;
            }else if(m.length() !=2 || d.length() !=2){
                System.out.println("\nFormat Error: The month and day should both have two digits");
                return;
            }

            if(Integer.parseInt(m) > 12 || Integer.parseInt(m) < 1){
                System.out.println("\nFormat Error: Invalid month.");
                return;
            }
            if(Integer.parseInt(d) > 31 || Integer.parseInt(d) < 0){
                System.out.println("\nFormat Error: Invalid day.");
                return;
            }

            System.out.print("\nWhat airline do you fly with the most (use the three letter abbreviation)? : ");
            freqMiles=input.nextLine();

            PreparedStatement sameName = conn.prepareStatement("SELECT * FROM CUSTOMER WHERE first_name = ? AND last_name = ?");
            sameName.setString(1, fname);
            sameName.setString(2, lname);

            ResultSet sameNameCur = sameName.executeQuery();
            if(sameNameCur.next()){
                System.out.println("\nCustomer '"+fname+" "+lname+"' already exists. Please use a different name.");
                return;
            }

            Statement maxCid = conn.createStatement();
            ResultSet cidCur = maxCid.executeQuery("SELECT MAX(cid) FROM CUSTOMER");
            cidCur.next();
            int newCid = cidCur.getInt(1)+1;

            int day = Integer.parseInt(d);
            int mon = Integer.parseInt(m)-1;
            int year = Integer.parseInt(y) - 1900;//due to the API
            Date expirationDate = new Date(year, mon, day);

            PreparedStatement stmt = conn.prepareStatement("INSERT INTO CUSTOMER VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setInt(1, newCid);
            stmt.setString(2, salutation);
            stmt.setString(3, fname);
            stmt.setString(4, lname);
            stmt.setString(5, cc);
            stmt.setString(6, street);
            stmt.setDate(7, expirationDate);
            stmt.setString(8, city);
            stmt.setString(9, state);
            stmt.setString(10, phone);
            stmt.setString(11, email);
            stmt.setString(12, freqMiles);

            stmt.executeUpdate();

            System.out.println("\nYour customer ID number is "+newCid+".");

        } catch(SQLException e1){
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = "+ e1.getSQLState());
                System.out.println("SQL Code = "+ e1.getErrorCode());
                e1 = e1.getNextException();
            }
        } catch(ArrayIndexOutOfBoundsException e2){
            System.out.println("\nFormat Error: Please only use the '-' character to separate the year, month, and day");
            return;
        }
    }

    public static void task2(Scanner input, Connection conn){
        String fname = "";
        String lname = "";
        System.out.print("\nEnter the customer's first name: ");
        fname = input.nextLine();
        System.out.print("\nEnter the customer's last name: ");
        lname = input.nextLine();

        try{

            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CUSTOMER WHERE first_name = ? AND last_name = ?;");
            stmt.setString(1, fname);
            stmt.setString(2, lname);

            ResultSet cur = stmt.executeQuery();

            boolean isInResult = cur.next();
            if(isInResult){
                if(cur.isLast()){
                    System.out.println("\nHere is "+fname+" "+lname+"'s information: ");
                    System.out.println("\nCID - "+cur.getString(1)+"\n"+
                                       "Salutation - "+cur.getString(2)+"\n"+
                                       "First Name - "+cur.getString(3)+"\n"+
                                       "Last Name - "+cur.getString(4)+"\n"+
                                       "Credit Card Number - "+cur.getString(5)+"\n"+
                                       "Street - "+cur.getString(6)+"\n"+
                                       "Credit Card Expiration Date - "+cur.getString(7)+"\n"+
                                       "City - "+cur.getString(8)+"\n"+
                                       "State - "+cur.getString(9)+"\n"+
                                       "Phone Number - "+cur.getString(10)+"\n"+
                                       "Email - "+cur.getString(11)+"\n"+
                                       "Frequent Airline - "+cur.getString(12));

                }
                else{
                    //This should never run given the contraints of task1
                    System.out.println("\nError: More than one customer with the name '"+fname+" "+lname+"'");
                }
            }
            else{
                System.out.println("\nError: There are no customers with the name '"+fname+" "+lname+"'");
            }

        }catch(SQLException e1){
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = "+ e1.getSQLState());
                System.out.println("SQL Code = "+ e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }

    }

    public static void task3(Scanner input, Connection conn){
        try{
            System.out.print("\nEnter the three letter abbreviation of the city you would be departing from: ");
            String cityA = input.nextLine().toUpperCase();
            if(cityA.length() != 3){
                System.out.println("\nFormat Error: That is not a three letter abbreviation.");
                return;
            }

            System.out.print("\nEnter the three letter abbreviation of the city you would be arriving in: ");
            String cityB = input.nextLine().toUpperCase();
            if(cityB.length() != 3){
                System.out.println("\nFormat Error: That is not a three letter abbreviation.");
                return;
            }


            ArrayList<String> departCities = new ArrayList<String>();
            ArrayList<String> arriveCities = new ArrayList<String>();
            HashMap<String, Integer> highPrices = new HashMap<String, Integer>();
            HashMap<String, Integer> lowPrices = new HashMap<String, Integer>();


            PreparedStatement stmt = conn.prepareStatement("SELECT departure_city, arrival_city, high_price, low_price FROM PRICE");
            ResultSet cur = stmt.executeQuery();
            while(cur.next()){
                departCities.add(cur.getString(1));
                arriveCities.add(cur.getString(2));

                String trip = cur.getString(1)+"-"+cur.getString(2);
                highPrices.put(trip, cur.getInt(3));
                lowPrices.put(trip, cur.getInt(4));
            }

            if(!departCities.contains(cityA)){
                System.out.println("\nError: There are no flights listed as leaving from "+cityA+".");
                return;
            }
            if(!arriveCities.contains(cityB)){
                System.out.println("\nError: There are no flights listed as arriving in "+cityA+".");
                return;
            }
            System.out.println();

            String userTrip = cityA+"-"+cityB;
            String userTripReverse = cityB+"-"+cityA;

            Graph graph = new Graph(departCities.size());
            for(int i = 0; i<departCities.size(); i++){
                graph.addEdge(departCities.get(i), arriveCities.get(i));
            }

            HashMap<ArrayList<String>, Integer> pathHighPrices = new HashMap<ArrayList<String>, Integer>();
            HashMap<ArrayList<String>, Integer> pathLowPrices = new HashMap<ArrayList<String>, Integer>();

            ArrayList<ArrayList<String>> paths = graph.genAllPaths(cityA, cityB);
            ArrayList<ArrayList<String>> revPaths = graph.genAllPaths(cityB, cityA);

            //find high and low prices for all paths
            for(ArrayList<String> path : paths){
                int high = 0;
                int low = 0;
                String str = "";
                for(int i = 0; i<path.size()-1; i++){
                    String stop = path.get(i)+"-"+path.get(i+1);
                    str+=path.get(i)+" -> ";
                    high += highPrices.get(stop);
                    low += lowPrices.get(stop);
                }
                str += path.get(path.size()-1);
                System.out.println("For the flight "+str+" the high price is $"+high+" and the low price is $"+low+".");

                pathHighPrices.put(path, high);
                pathLowPrices.put(path, low);
            }

            System.out.println();

            for(ArrayList<String> path : revPaths){
                int high = 0;
                int low = 0;
                String str = "";
                for(int i = 0; i<path.size()-1; i++){
                    String stop = path.get(i)+"-"+path.get(i+1);
                    str+=path.get(i)+" -> ";
                    high += highPrices.get(stop);
                    low += lowPrices.get(stop);
                }
                str += path.get(path.size()-1);
                System.out.println("For the return flight "+str+" the high price is $"+high+" and the low price is $"+low+".");

                pathHighPrices.put(path, high);
                pathLowPrices.put(path, low);
            }

            System.out.println();

            //For each forward path, for each return path, if return path does not cause loop then calculate round trip price
            boolean atLeastOneRoundTrip = false;
            for(ArrayList<String> path : paths){
                for(ArrayList<String> retPath : revPaths){
                    boolean loop = false;
                    for(String dest : path){
                        if(retPath.contains(dest) && !dest.equals(cityA) && !dest.equals(cityB)){
                            loop = true;
                        }
                    }

                    if(!loop){
                        atLeastOneRoundTrip = true;
                        int roundTripHigh = pathHighPrices.get(path)+pathHighPrices.get(retPath);
                        int roundTripLow = pathLowPrices.get(path)+pathLowPrices.get(retPath);
                        String str = "";
                        for(int i = 0; i<path.size()-1; i++){
                            str += path.get(i)+" -> ";
                        }
                        for(int i = 0; i<retPath.size()-1; i++){
                            str += retPath.get(i)+" -> ";
                        }
                        str += retPath.get(retPath.size()-1);
                        System.out.println("For the round trip "+str+" the high price is $"+roundTripHigh+" and the low price is $"+roundTripLow+".");
                    }
                    loop = false;
                }
            }

            if(!atLeastOneRoundTrip){
                System.out.println("Error: No round trips could be found that do not cause loops.");
            }

        }catch(SQLException e1){
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = "+ e1.getSQLState());
                System.out.println("SQL Code = "+ e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    //need fake data to test thoroughly
    public static void task4(Scanner input, Connection conn){

        try{
            System.out.print("\nEnter the three letter abbreviation of the city you would be departing from: ");
            String cityA = input.nextLine().toUpperCase();
            if(cityA.length() != 3){
                System.out.println("\nFormat Error: That is not a three letter abbreviation.");
                return;
            }

            System.out.print("\nEnter the three letter abbreviation of the city you would be arriving in: ");
            String cityB = input.nextLine().toUpperCase();
            if(cityB.length() != 3){
                System.out.println("\nFormat Error: That is not a three letter abbreviation.");
                return;
            }

            String directQuery = "SELECT flight_number, departure_time, arrival_time "+
                                 "FROM FLIGHT "+
                                 "WHERE departure_city = ? AND arrival_city = ?;";

            PreparedStatement stmt = conn.prepareStatement(directQuery);
            stmt.setString(1, cityA);
            stmt.setString(2, cityB);
            ResultSet cur = stmt.executeQuery();

            ///////////////////////////////Trivial Direct Flights///////////////////////////////
            boolean atLeastOneDirect = false;
            while(cur.next()){
                String fNum = ""+cur.getInt(1);
                String dTime = ""+cur.getInt(2);
                String aTime = ""+cur.getInt(3);

                String spaces = "";
                for(int i = 0; i<13-fNum.length(); i++){
                    spaces+=" ";
                }
                System.out.println("\nThis is a direct flight. "+cityA+" -> "+cityB);
                System.out.println("Flight Number\tDeparture City\tDeparture Time\tArrival Time");
                System.out.println(fNum+spaces+"\t"+cityA+"           \t"+dTime+"         \t"+aTime+"\n");
                atLeastOneDirect = true;
            }
            if(!atLeastOneDirect){
                System.out.println("\nThere are no direct flights between "+cityA+" and "+cityB+".");
            }
            ////////////////////////////////////////////////////////////////////////////////////

            String connectQuery = "SELECT flight_number, departure_city, arrival_city "+
                                  "FROM FLIGHT "+
                                  "WHERE departure_city = ? OR arrival_city = ?;";

            HashMap<String, Integer> numToTrip = new HashMap<String, Integer>();

            stmt = conn.prepareStatement(connectQuery);
            stmt.setString(1, cityA);
            stmt.setString(2, cityB);
            cur = stmt.executeQuery();
            ResultSet cur2 = stmt.executeQuery();
            ArrayList<String> vertices = new ArrayList<String>();
            while(cur.next()){
                int fNum = cur.getInt(1);
                String dCity = cur.getString(2);
                String aCity = cur.getString(3);
                numToTrip.put(dCity+" -> "+aCity, fNum);
                if(!vertices.contains(dCity)){
                    vertices.add(dCity);
                }
                if(!vertices.contains(aCity)){
                    vertices.add(aCity);
                }
            }

            //populate graph
            Graph graph = new Graph(vertices.size());
            while(cur2.next()){
                String dCity = cur2.getString(2);
                String aCity = cur2.getString(3);
                graph.addEdge(dCity, aCity);
            }

            //generate paths that only include 1 connecting flight (length of three)
            ArrayList<ArrayList<String>> paths = graph.genAllPaths(cityA, cityB);
            if(paths.size() == 0){
                System.out.println("\nThere are no direct connecting flights between "+cityA+" and "+cityB+".");
                return;
            }

            for(int i = 0; i<paths.size(); i++){
                if(paths.get(i).size() != 3){
                    paths.remove(i);
                }
            }

            //for each path, find the two flight numbers involved, then query for their weekly schedules
            String scheduleQuery = "SELECT weekly_schedule, departure_time, arrival_time "+
                                   "FROM FLIGHT "+
                                   "WHERE flight_number = ?;";
            stmt = conn.prepareStatement(scheduleQuery);
            for(ArrayList<String> path : paths){
                String f1 = path.get(0)+" -> "+path.get(1);
                String f2 = path.get(1)+" -> "+path.get(2);
                String fNum1 = ""+numToTrip.get(f1);
                String fNum2 = ""+numToTrip.get(f2);
                String sched1 = "";
                String sched2 = "";
                int departOrigin = -1;
                int arriveCon = -1;
                int departCon = -1;
                int arriveDest = -1;

                stmt.setInt(1, Integer.parseInt(fNum1));
                cur = stmt.executeQuery();
                while(cur.next()){
                    sched1 = cur.getString(1);
                    departOrigin = cur.getInt(2);
                    arriveCon = cur.getInt(3);
                }

                stmt.setInt(1, Integer.parseInt(fNum2));
                cur = stmt.executeQuery();
                while(cur.next()){
                    sched2 = cur.getString(1);
                    departCon = cur.getInt(2);
                    arriveDest = cur.getInt(3);
                }

                //Check if the schedules are compatible
                boolean similar = false;
                for(int i = 0; i<7; i++){
                    if(sched1.charAt(i) != '-' && sched2.charAt(i) != '-'){
                        similar = true;
                        break;
                    }
                }

                //if the flights have similar flight schedules and
                //arrive at the connecting flight at least 1 hour before connecting flight departs
                //Assuming origin flights and connecting have to be in the same day.
                if(similar && departCon >= arriveCon + 100){
                    System.out.println("\nFor flight "+path.get(0)+" -> "+path.get(1)+" -> "+path.get(2));
                    System.out.println("Flight Number\tDeparture City\tDeparture Time\tArrival Time");

                    String spaces1 = "";
                    for(int i = 0; i<13-fNum1.length(); i++){
                        spaces1+=" ";
                    }
                    System.out.println(fNum1+spaces1+"\t"+path.get(0)+"           \t"+departOrigin+"         \t"+arriveCon);

                    String spaces2 = "";
                    for(int i = 0; i<13-fNum2.length(); i++){
                        spaces2+=" ";
                    }

                    System.out.println(fNum2+spaces2+"\t"+path.get(1)+"           \t"+departCon+"         \t"+arriveDest+"\n");
                }

            }

        }catch(SQLException e1){
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = "+ e1.getSQLState());
                System.out.println("SQL Code = "+ e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }

    }

    public static void task5(Scanner input, Connection conn){
        try{
            System.out.print("\nEnter the three letter abbreviation of the city you would be departing from: ");
            String cityA = input.nextLine().toUpperCase();
            if(cityA.length() != 3){
                System.out.println("\nFormat Error: That is not a three letter abbreviation.");
                return;
            }

            System.out.print("\nEnter the three letter abbreviation of the city you would be arriving in: ");
            String cityB = input.nextLine().toUpperCase();
            if(cityB.length() != 3){
                System.out.println("\nFormat Error: That is not a three letter abbreviation.");
                return;
            }


            System.out.print("\nEnter the preferred airline name: ");
            String airlineName = input.nextLine();

            //check existence of input airline
            PreparedStatement stmt = conn.prepareStatement("SELECT airline_name FROM AIRLINE WHERE airline_name = ?;");
            stmt.setString(1, airlineName);
            ResultSet cur = stmt.executeQuery();
            if(!cur.next()){
                System.out.println("\nError: That airline does not exist");
            }


            String directQuery = "SELECT DISTINCT flight_number, departure_time, arrival_time, airline_id "+
                                 "FROM FLIGHT NATURAL JOIN AIRLINE "+
                                 "WHERE departure_city = ? AND arrival_city = ? AND airline_name = ?;";

            stmt = conn.prepareStatement(directQuery);
            stmt.setString(1, cityA);
            stmt.setString(2, cityB);
            stmt.setString(3, airlineName);
            cur = stmt.executeQuery();

            ///////////////////////////////Trivial Direct Flights///////////////////////////////
            boolean atLeastOneDirect = false;
            while(cur.next()){
                String fNum = ""+cur.getInt(1);
                String dTime = ""+cur.getInt(2);
                String aTime = ""+cur.getInt(3);
                String airID = ""+cur.getInt(4);
                String spaces = "";
                for(int i = 0; i<13-fNum.length(); i++){
                    spaces+=" ";
                }
                String spaces2 = "";
                for(int i = 0; i<10-airID.length(); i++){
                    spaces2+=" ";
                }
                System.out.println("\nThis is a direct flight. "+cityA+" -> "+cityB);
                System.out.println("Airline ID\tFlight Number\tDeparture City\tDeparture Time\tArrival Time");
                System.out.println(airID+spaces2+"\t"+fNum+spaces+"\t"+cityA+"           \t"+dTime+"         \t"+aTime+"\n");
                atLeastOneDirect = true;
            }
            if(!atLeastOneDirect){
                System.out.println("\nThere are no direct flights between "+cityA+" and "+cityB+".");
            }
            ////////////////////////////////////////////////////////////////////////////////////

            String connectQuery = "SELECT DISTINCT flight_number, departure_city, arrival_city "+
                                  "FROM FLIGHT NATURAL JOIN AIRLINE "+
                                  "WHERE (departure_city = ? OR arrival_city = ?) AND airline_name = ?;";

            HashMap<String, Integer> numToTrip = new HashMap<String, Integer>();

            stmt = conn.prepareStatement(connectQuery);
            stmt.setString(1, cityA);
            stmt.setString(2, cityB);
            stmt.setString(3, airlineName);
            cur = stmt.executeQuery();
            ResultSet cur2 = stmt.executeQuery();
            ArrayList<String> vertices = new ArrayList<String>();
            while(cur.next()){
                int fNum = cur.getInt(1);
                String dCity = cur.getString(2);
                String aCity = cur.getString(3);
                numToTrip.put(dCity+" -> "+aCity, fNum);
                if(!vertices.contains(dCity)){
                    vertices.add(dCity);
                }
                if(!vertices.contains(aCity)){
                    vertices.add(aCity);
                }
            }

            //populate graph
            Graph graph = new Graph(vertices.size());
            while(cur2.next()){
                String dCity = cur2.getString(2);
                String aCity = cur2.getString(3);
                graph.addEdge(dCity, aCity);
            }

            //generate paths that only include 1 connecting flight (length of three)
            ArrayList<ArrayList<String>> paths = graph.genAllPaths(cityA, cityB);
            if(paths.size() == 0){
                System.out.println("\nThere are no direct connecting flights between "+cityA+" and "+cityB+".");
                return;
            }
            for(int i = 0; i<paths.size(); i++){
                if(paths.get(i).size() != 3){
                    paths.remove(i);
                }
            }

            //for each path, find the two flight numbers involved, then query for their weekly schedules
            String scheduleQuery = "SELECT weekly_schedule, departure_time, arrival_time, airline_id "+
                                   "FROM FLIGHT "+
                                   "WHERE flight_number = ?;";
            stmt = conn.prepareStatement(scheduleQuery);
            for(ArrayList<String> path : paths){
                String f1 = path.get(0)+" -> "+path.get(1);
                String f2 = path.get(1)+" -> "+path.get(2);
                String fNum1 = ""+numToTrip.get(f1);
                String fNum2 = ""+numToTrip.get(f2);
                String sched1 = "";
                String sched2 = "";
                int departOrigin = -1;
                int arriveCon = -1;
                int departCon = -1;
                int arriveDest = -1;
                String airline1 = "";
                String airline2 = "";
                stmt.setInt(1, Integer.parseInt(fNum1));
                cur = stmt.executeQuery();
                while(cur.next()){
                    sched1 = cur.getString(1);
                    departOrigin = cur.getInt(2);
                    arriveCon = cur.getInt(3);
                    airline1 = ""+cur.getInt(4);
                }

                stmt.setInt(1, Integer.parseInt(fNum2));
                cur = stmt.executeQuery();
                while(cur.next()){
                    sched2 = cur.getString(1);
                    departCon = cur.getInt(2);
                    arriveDest = cur.getInt(3);
                    airline2 = ""+cur.getInt(4);
                }

                //Check if the schedules are compatible
                boolean similar = false;
                for(int i = 0; i<7; i++){
                    if(sched1.charAt(i) != '-' && sched2.charAt(i) != '-'){
                        similar = true;
                        break;
                    }
                }

                //if the flights have similar flight schedules and
                //arrive at the connecting flight at least 1 hour before connecting flight departs
                //Assuming origin flights and connecting have to be in the same day.
                if(similar && departCon >= arriveCon + 100){
                    System.out.println("\nFor flight "+path.get(0)+" -> "+path.get(1)+" -> "+path.get(2));
                    System.out.println("Airline ID\tFlight Number\tDeparture City\tDeparture Time\tArrival Time");

                    String spaces1 = "";
                    for(int i = 0; i<13-fNum1.length(); i++){
                        spaces1+=" ";
                    }
                    String airSpace1 = "";
                    for(int i = 0; i<10-airline1.length();i++){
                        airSpace1+=" ";
                    }
                    System.out.println(airline1+airSpace1+"\t"+fNum1+spaces1+"\t"+path.get(0)+"           \t"+departOrigin+"         \t"+arriveCon);

                    String spaces2 = "";
                    for(int i = 0; i<13-fNum2.length(); i++){
                        spaces2+=" ";
                    }
                    String airSpace2 = "";
                    for(int i = 0; i<10-airline2.length();i++){
                        airSpace2+=" ";
                    }
                    System.out.println(airline2+airSpace2+"\t"+fNum2+spaces2+"\t"+path.get(1)+"           \t"+departCon+"         \t"+arriveDest+"\n");
                }

            }

        }catch(SQLException e1){
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = "+ e1.getSQLState());
                System.out.println("SQL Code = "+ e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    public static void task6(Scanner input, Connection conn){

        try{

            System.out.print("\nEnter the city you would be departing from: ");
            String cityA = input.nextLine().toUpperCase();
            System.out.print("\nEnter the city you would be arriving at: ");
            String cityB = input.nextLine().toUpperCase();
            System.out.print("\nEnter the date you are trying to view (yyyy-mm-dd): ");
            String givenDate = input.nextLine();

            //parse out timestamp
            int year = Integer.parseInt(givenDate.split("-")[0])-1900;
            int month = Integer.parseInt(givenDate.split("-")[1])-1;
            int day = Integer.parseInt(givenDate.split("-")[2]);

            if(givenDate.length() != 10){
                System.out.println("\nFormat Error: Follow date format of yyyy-mm-dd");
                return;
            }else if(givenDate.split("-")[0].length() != 4){
                System.out.println("\nFormat Error: The year should have four digits");
                return;
            }else if(givenDate.split("-")[1].length() !=2 || givenDate.split("-")[2].length() !=2){
                System.out.println("\nFormat Error: The month and day should both have two digits");
                return;
            }

            if(month > 11 || month < 0){
                System.out.println("\nFormat Error: Invalid month.");
                return;
            }

            if(day > 31 || day < 1){
                System.out.println("\nFormat Error: Invalid day.");
            }

            Timestamp flightDate = new Timestamp(year, month, day, 0, 0, 0, 0);

            //Direct Flight Query
            String directQuery = "SELECT flight_number, departure_city, arrival_city, departure_time, arrival_time "+
                                  "FROM (SELECT DISTINCT flight_number, departure_city, arrival_city, departure_time, arrival_time, isplanefull(flight_number) AS full "+
                                        "FROM RESERVATION_DETAIL NATURAL JOIN FLIGHT NATURAL JOIN PLANE "+
                                        "WHERE flight_date = ? AND departure_city = ? AND arrival_city = ?) AS large "+
                                  "WHERE large.full = false;";

            PreparedStatement stmt = conn.prepareStatement(directQuery);
            stmt.setTimestamp(1, flightDate);
            stmt.setString(2, cityA);
            stmt.setString(3, cityB);
            ResultSet cur = stmt.executeQuery();

            ///////////////////////////////Trivial Direct Flights///////////////////////////////
            boolean atLeastOneDirect = false;
            while(cur.next()){
                String fNum = ""+cur.getInt(1);
                String dTime = ""+cur.getInt(4);
                String aTime = ""+cur.getInt(5);
                String spaces = "";
                for(int i = 0; i<13-fNum.length(); i++){
                    spaces+=" ";
                }

                System.out.println("\nThis is a direct flight. "+cityA+" -> "+cityB);
                System.out.println("Flight Number\tDeparture City\tDeparture Time\tArrival Time");
                System.out.println(fNum+spaces+"\t"+cityA+"           \t"+dTime+"         \t"+aTime+"\n");
                atLeastOneDirect = true;
            }
            if(!atLeastOneDirect){
                System.out.println("\nThere are no direct flights between "+cityA+" and "+cityB+" on "+flightDate.toString().split(" ")[0]+".");
            }
            ////////////////////////////////////////////////////////////////////////////////////

            //Connecting flight query
            String getFlightNumberQuery = "SELECT flight_number, departure_city, arrival_city, departure_time, arrival_time "+
                                          "FROM (SELECT DISTINCT flight_number, departure_city, arrival_city, departure_time, arrival_time, isplanefull(flight_number) AS full "+
                                                "FROM RESERVATION_DETAIL NATURAL JOIN FLIGHT NATURAL JOIN PLANE "+
                                                "WHERE flight_date = ? AND (departure_city = ? OR arrival_city = ?)) AS large "+
                                          "WHERE large.full = false;";

            stmt = conn.prepareStatement(getFlightNumberQuery);
            stmt.setTimestamp(1, flightDate);
            stmt.setString(2, cityA);
            stmt.setString(3, cityB);
            cur = stmt.executeQuery();
            ArrayList<String> vertices = new ArrayList<String>();
            HashMap<String, Integer> tripToNum = new HashMap<String,Integer>();
            while(cur.next()){
                int fNum = cur.getInt(1);
                String dCity = cur.getString(2);
                String aCity = cur.getString(5);
                tripToNum.put(dCity+" -> "+aCity, fNum);
                if(!vertices.contains(dCity)){
                    vertices.add(dCity);
                }
                if(!vertices.contains(aCity)){
                    vertices.add(aCity);
                }
            }

            Graph graph = new Graph(vertices.size());
            cur = stmt.executeQuery();
            while(cur.next()){
                graph.addEdge(cur.getString(2), cur.getString(5));
            }

            //Filter for only connecting flights
            ArrayList<ArrayList<String>> paths = graph.genAllPaths(cityA, cityB);
            for(int i = 0; i<paths.size(); i++){
                if(paths.get(i).size() != 3){
                    paths.remove(i);
                }
            }

            //check schedules
            //for each path, find the two flight numbers involved, then query for their weekly schedules
            String scheduleQuery = "SELECT weekly_schedule, departure_time, arrival_time "+
                                   "FROM FLIGHT "+
                                   "WHERE flight_number = ?;";
            stmt = conn.prepareStatement(scheduleQuery);
            for(ArrayList<String> path : paths){
                String f1 = path.get(0)+" -> "+path.get(1);
                String f2 = path.get(1)+" -> "+path.get(2);
                String fNum1 = ""+tripToNum.get(f1);
                String fNum2 = ""+tripToNum.get(f2);
                String sched1 = "";
                String sched2 = "";
                int departOrigin = -1;
                int arriveCon = -1;
                int departCon = -1;
                int arriveDest = -1;

                stmt.setInt(1, Integer.parseInt(fNum1));
                cur = stmt.executeQuery();
                while(cur.next()){
                    sched1 = cur.getString(1);
                    departOrigin = cur.getInt(2);
                    arriveCon = cur.getInt(3);
                }

                stmt.setInt(1, Integer.parseInt(fNum2));
                cur = stmt.executeQuery();
                while(cur.next()){
                    sched2 = cur.getString(1);
                    departCon = cur.getInt(2);
                    arriveDest = cur.getInt(3);
                }

                //Check if the schedules are compatible
                boolean similar = false;
                for(int i = 0; i<7; i++){
                    if(sched1.charAt(i) != '-' && sched2.charAt(i) != '-'){
                        similar = true;
                        break;
                    }
                }

                //if the flights have similar flight schedules and
                //arrive at the connecting flight at least 1 hour before connecting flight departs
                //Assuming origin flights and connecting have to be in the same day.
                if(similar && departCon >= arriveCon + 100){
                    System.out.println("\nFor flight "+path.get(0)+" -> "+path.get(1)+" -> "+path.get(2));
                    System.out.println("Airline ID\tFlight Number\tDeparture City\tDeparture Time\tArrival Time");

                    String spaces1 = "";
                    for(int i = 0; i<13-fNum1.length(); i++){
                        spaces1+=" ";
                    }

                    System.out.println(fNum1+spaces1+"\t"+path.get(0)+"           \t"+departOrigin+"         \t"+arriveCon);

                    String spaces2 = "";
                    for(int i = 0; i<13-fNum2.length(); i++){
                        spaces2+=" ";
                    }

                    System.out.println(fNum2+spaces2+"\t"+path.get(1)+"           \t"+departCon+"         \t"+arriveDest+"\n");
                }
            }

        }catch(SQLException e1){
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = "+ e1.getSQLState());
                System.out.println("SQL Code = "+ e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }catch(ArrayIndexOutOfBoundsException e2){
            System.out.println("\nFormat Error: Please only use the '-' character to separate the year, month, and day");
            return;
        }catch(NumberFormatException e3){
            System.out.println("\nFormat Error: Please enter a valid number");
        }

    }


    public static void task7(Scanner input, Connection conn){

        try{

            System.out.print("\nHow many flights are a part of this reservation? ");
            int numFlights = Integer.parseInt(input.nextLine());
            if(numFlights < 1 || numFlights > 4){
                System.out.println("Error: That is not a valid number of flights");
                return;
            }
            ArrayList<String> resInfo = new ArrayList<String>();

            for(int i = 0; i<numFlights; i++){

                System.out.print("\nFor leg "+(i+1)+" What is the flight number? ");
                int flightNum = Integer.parseInt(input.nextLine());


                //Make flight checks


                PreparedStatement st = conn.prepareStatement("SELECT flight_number FROM FLIGHT WHERE flight_number = ?");
                st.setInt(1, flightNum);

                ResultSet testCur = st.executeQuery();
                if(testCur.next()){
                    if(!testCur.isLast()){
                        //this should never run. if it does, then there is a problem with the FLIGHT table's schema
                        System.out.println("\nConstraint Error: There are more than one flight with the same flight number");
                        return;
                    }
                }else{
                    System.out.println("\nError: That flight number does not exist");
                    return;
                }


                //check if seats are available
                st = conn.prepareStatement("SELECT * FROM "+
                                           "FLIGHT NATURAL JOIN PLANE "+
                                           "WHERE flight_number = ? AND plane_capacity > (SELECT COUNT(reservation_number) "+
                                                                                         "FROM RESERVATION_DETAIL "+
                                                                                         "WHERE flight_number = ?)");
                st.setInt(1, flightNum);
                st.setInt(2, flightNum);

                testCur = st.executeQuery();
                if(!testCur.next()){
                    System.out.println("\nError: That flight is full.");
                    return;
                }


                System.out.print("\n\t What is the departure date for that flight (yyyy-mm-dd)? ");
                String date = input.nextLine();
                String y = date.split("-")[0];
                String m = date.split("-")[1];
                String d = date.split("-")[2];

                if(date.length() != 10){
                    System.out.println("\nFormat Error: Follow date format of yyyy-mm-dd");
                    return;
                }else if(y.length() != 4){
                    System.out.println("\nFormat Error: The year should have four digits");
                    return;
                }else if(m.length() !=2 || d.length() !=2){
                    System.out.println("\nFormat Error: The month and day should both have two digits");
                    return;
                }

                if(Integer.parseInt(m) > 12 || Integer.parseInt(m) < 1){
                    System.out.println("\nFormat Error: Invalid month.");
                    return;
                }
                if(Integer.parseInt(d) < 1 || Integer.parseInt(d) > 31){
                    System.out.println("\nFormat Error: Invalid day.");
                    return;
                }

                Timestamp departDate = new Timestamp(Integer.parseInt(y)-1900, Integer.parseInt(m)-1, Integer.parseInt(d), 0, 0, 0, 0);

                resInfo.add(""+flightNum+" "+departDate.getTime()+" "+(i+1));

            }




            PreparedStatement stmt = conn.prepareStatement("SELECT MAX(reservation_number) FROM RESERVATION");
            ResultSet cur = stmt.executeQuery();
            cur.next();
            int newResNum = cur.getInt(1)+1;


            //Need to ask for customer info and insert into reservation table
            System.out.print("\nPlease enter your first name: ");
            String fName = input.nextLine();
            System.out.print("\nPlease enter your last name: ");
            String lName = input.nextLine();

            stmt = conn.prepareStatement("SELECT cid, credit_card_num FROM CUSTOMER WHERE first_name = ? AND last_name = ?;");
            stmt.setString(1, fName);
            stmt.setString(2, lName);
            cur = stmt.executeQuery();
            if(!cur.next()){
                System.out.println("\nIt does not appear that "+fName+" "+lName+" is in the system.");
                System.out.println("Redirecting...");
                task1(input, conn);
            }

            cur = stmt.executeQuery();
            cur.next();
            int cid = cur.getInt(1);
            String credit = cur.getString(2);
            boolean ticketed = false;

            stmt = conn.prepareStatement("SELECT c_timestamp FROM OURTIMESTAMP");
            cur = stmt.executeQuery();
            cur.next();
            Timestamp reservationDate = cur.getTimestamp(1);

            //Need to calculate cost
            int cost = 0;
            for(String flight : resInfo){

                String[] info = flight.split(" ");
                int flightNum = Integer.parseInt(info[0]);
                long milli = Long.parseLong(info[1]);
                int leg = Integer.parseInt(info[2]);

                stmt = conn.prepareStatement("SELECT high_price, low_price "+
                                             "FROM FLIGHT NATURAL JOIN AIRLINE NATURAL JOIN PRICE "+
                                             "WHERE flight_number = ?");
                stmt.setInt(1, flightNum);
                cur = stmt.executeQuery();
                cur.next();
                int high = cur.getInt(1);
                int low = cur.getInt(2);

                Timestamp departDate = new Timestamp(milli);

                if(departDate.toString().split(" ")[0].equals(reservationDate.toString().split(" ")[0])){
                    cost+=high;
                }else{
                    cost+=low;
                }

            }


            conn.setAutoCommit(false);
            Savepoint svpnt = conn.setSavepoint("init");

            stmt = conn.prepareStatement("INSERT INTO RESERVATION VALUES(?, ?, ?, ?, ?, ?)");
            stmt.setInt(1, newResNum);
            stmt.setInt(2, cid);
            stmt.setInt(3, cost);
            stmt.setString(4, credit);
            stmt.setTimestamp(5, reservationDate);
            stmt.setBoolean(6, ticketed);
            stmt.executeUpdate();
            boolean fullFlights = false;
            for(String flight : resInfo){
                String[] info = flight.split(" ");
                int flightNum = Integer.parseInt(info[0]);
                long milli = Long.parseLong(info[1]);
                int leg = Integer.parseInt(info[2]);

                Timestamp departDate = new Timestamp(milli);

                stmt = conn.prepareStatement("SELECT isplanefull(?) FROM FLIGHT WHERE flight_number=?");
                stmt.setInt(1, flightNum);
                stmt.setInt(2, flightNum);
                ResultSet temp = stmt.executeQuery();
                conn.commit();
                if(temp.next()){
                    if(temp.getBoolean(1) == true){
                        fullFlights = true;
                    }
                }

                stmt = conn.prepareStatement("INSERT INTO RESERVATION_DETAIL VALUES(?, ?, ?, ?)");
                stmt.setInt(1, newResNum);
                stmt.setInt(2, flightNum);
                stmt.setTimestamp(3, departDate);
                stmt.setInt(4, leg);
                stmt.executeUpdate();
            }
            conn.commit();

            if(fullFlights){
                conn.rollback(svpnt);
                System.out.println("\nError: One or more of the flights in this reservation is full.");
                return;
            }

            System.out.println("\nSuccess! Your reservation number is "+newResNum);

        }catch(SQLException e1){
            System.out.println("SQL Error");
            //conn.rollback(svpnt);
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = "+ e1.getSQLState());
                System.out.println("SQL Code = "+ e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }catch(NumberFormatException e2){
            System.out.println("\nFormat Error: Please enter a valid number");
        }catch(ArrayIndexOutOfBoundsException e3){
            System.out.println("\nFormat Error: Please only use the '-' character to separate the year, month, and day");
            return;
        }

    }

    public static void task8(Scanner input, Connection conn){

        try{
            int resNumber = -1;
            System.out.print("\nEnter the reservation number for the reservation you would like to delete: ");
            resNumber = Integer.parseInt(input.nextLine());

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM RESERVATION_DETAIL WHERE reservation_number = ?");
            stmt.setInt(1, resNumber);
            stmt.executeUpdate();

            stmt = conn.prepareStatement("DELETE FROM RESERVATION WHERE reservation_number = ?");
            stmt.setInt(1, resNumber);
            stmt.executeUpdate();

            //The rest of this task should be handled by the trigger Lexi is making.

        }catch(SQLException e1){
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = "+ e1.getSQLState());
                System.out.println("SQL Code = "+ e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }catch(NumberFormatException e2){
            System.out.println("\nFormat Error: Please enter a valid reservation number");
        }
    }

    public static void task9(Scanner input, Connection conn){

        try{
            int resNumber = -1;
            System.out.print("\nEnter the reservation number of the reservation you would like to view: ");
            resNumber = Integer.parseInt(input.nextLine());
            PreparedStatement stmt = conn.prepareStatement("SELECT flight_number, departure_city, arrival_city, departure_time, arrival_time,leg, flight_date "+
                                                            "FROM FLIGHT NATURAL JOIN "+
                                                                        "(SELECT flight_number, leg, flight_date "+
                                                                         "FROM RESERVATION_DETAIL "+
                                                                         "WHERE reservation_number = ? "+
                                                                         "ORDER BY leg DESC) AS RES");
            stmt.setInt(1, resNumber);

            ResultSet cur = stmt.executeQuery();
            boolean atLeastOneTuple = false;
            while(cur.next()){
                atLeastOneTuple = true;
                String flightNum = ""+cur.getInt(1);
                String departCity = cur.getString(2);
                String arriveCity = cur.getString(3);
                String departTime = cur.getString(4);
                String arriveTime = cur.getString(5);
                String legNum = ""+cur.getInt(6);
                String flightDate = cur.getTimestamp(7).toString().split(" ")[0];
                System.out.print("\nLeg "+legNum+": On "+flightDate+" departs from "+departCity+" at "+departTime+" and arrives in "+arriveCity+" at "+arriveTime+".");
            }

            if(!atLeastOneTuple){
                System.out.println("\nError: That reservation number does not exist");
            }

            System.out.println();

        }catch(SQLException e1){
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = "+ e1.getSQLState());
                System.out.println("SQL Code = "+ e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }catch(NumberFormatException e2){
            System.out.println("\nFormat Error: Please enter a valid reservation number");
        }

    }

    public static void task10(Scanner input, Connection conn){

        try{

            int resNumber = -1;
            System.out.print("\nEnter the reservation number of the reservation you would like to pay for: ");
            resNumber = Integer.parseInt(input.nextLine());

            PreparedStatement stmt = conn.prepareStatement("SELECT ticketed "+
                                                           "FROM RESERVATION "+
                                                           "WHERE reservation_number = ?");
            stmt.setInt(1, resNumber);

            ResultSet cur = stmt.executeQuery();
            if(cur.next()){
                boolean paid = cur.getBoolean(1);
                if(paid){
                    System.out.println("\nThat reservation has already been purchased.");
                }else{
                    stmt = conn.prepareStatement("UPDATE RESERVATION "+
                                                 "SET ticketed = true "+
                                                 "WHERE reservation_number = ?");
                    stmt.setInt(1, resNumber);
                    stmt.executeUpdate();
                    System.out.println("\nYou have purchased reservation "+resNumber+".");
                }
            }else{
                System.out.println("\nError: That reservation number doesn't exist");
            }

        }catch(SQLException e1){
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = "+ e1.getSQLState());
                System.out.println("SQL Code = "+ e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }catch(NumberFormatException e2){
            System.out.println("\nFormat Error: Please enter a valid reservation number");
        }

    }

    public static void task11(Scanner input, Connection conn){

        try{
            int numCust = -1;
            System.out.print("\nHow many customers would you like to view? ");
            numCust = Integer.parseInt(input.nextLine());

            String customerQuery = "SELECT first_name || ' ' || last_name "+
                                   "FROM CUSTOMER NATURAL JOIN (SELECT cid, SUM(cost) AS total_cost "+
                                                                "FROM AIRLINE NATURAL JOIN (SELECT DISTINCT cid, cost, airline_id "+
                                                                                            "FROM FLIGHT NATURAL JOIN (SELECT cid, cost, flight_number "+
                                                                                                                      "FROM RESERVATION NATURAL JOIN RESERVATION_DETAIL "+
                                                                                                                      "WHERE ticketed = true) AS res) AS air "+
                                                                "WHERE airline_name = ? "+
                                                                "GROUP BY cid "+
                                                                "ORDER BY total_cost DESC "+
                                                                "FETCH FIRST ? ROWS ONLY) AS cust";
            String airlineQuery = "SELECT airline_name "+
                                  "FROM AIRLINE";
            PreparedStatement stmt = conn.prepareStatement(airlineQuery);
            ResultSet cur1 = stmt.executeQuery();

            while(cur1.next()){

                String airline = cur1.getString(1);
                stmt = conn.prepareStatement(customerQuery);
                stmt.setString(1, airline);
                stmt.setInt(2, numCust);

                ResultSet cur2 = stmt.executeQuery();
                System.out.println("\nAirline: "+airline);
                while(cur2.next()){
                    System.out.println(cur2.getString(1));
                }
            }

        }catch(SQLException e1){
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = "+ e1.getSQLState());
                System.out.println("SQL Code = "+ e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }catch(NumberFormatException e2){
            System.out.println("\nFormat Error: Please enter a valid number of customers");
        }

    }

    public static void task12(Scanner input, Connection conn){
        try{
            int numCust = -1;
            System.out.print("\nHow many customers would you like to view? ");
            numCust = Integer.parseInt(input.nextLine());

            String customerQuery = "SELECT first_name || ' ' || last_name "+
                                   "FROM CUSTOMER NATURAL JOIN (SELECT cid, COUNT(leg) AS num_leg "+
                                                                "FROM AIRLINE NATURAL JOIN (SELECT cid, airline_id, leg "+
                                                                                            "FROM FLIGHT NATURAL JOIN (SELECT cid, flight_number, leg "+
                                                                                                                      "FROM RESERVATION NATURAL JOIN RESERVATION_DETAIL "+
                                                                                                                      "WHERE ticketed = true) AS res) AS air "+
                                                                "WHERE airline_name = ? "+
                                                                "GROUP BY cid "+
                                                                "ORDER BY num_leg DESC "+
                                                                "FETCH FIRST ? ROWS ONLY) AS cust";
            String airlineQuery = "SELECT airline_name "+
                                  "FROM AIRLINE";
            PreparedStatement stmt = conn.prepareStatement(airlineQuery);
            ResultSet cur1 = stmt.executeQuery();

            while(cur1.next()){

                String airline = cur1.getString(1);
                stmt = conn.prepareStatement(customerQuery);
                stmt.setString(1, airline);
                stmt.setInt(2, numCust);

                ResultSet cur2 = stmt.executeQuery();
                System.out.println("\nAirline: "+airline);
                while(cur2.next()){
                    System.out.println(cur2.getString(1));
                }
            }

        }catch(SQLException e1){
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = "+ e1.getSQLState());
                System.out.println("SQL Code = "+ e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }catch(NumberFormatException e2){
            System.out.println("\nFormat Error: Please enter a valid number of customers");
        }
    }

    public static void task13(Connection conn){
        try{


            String rankQuery =  "SELECT DENSE_RANK () OVER (ORDER BY num_cust DESC) AS air_rank, airline_name "+
                                "FROM AIRLINE NATURAL JOIN (SELECT COUNT(DISTINCT cid) AS num_cust, airline_id "+
                                                            "FROM FLIGHT NATURAL JOIN (SELECT cid, flight_number "+
                                                                                      "FROM RESERVATION NATURAL JOIN RESERVATION_DETAIL "+
                                                                                      "WHERE ticketed = true) AS res "+
                                                             "GROUP BY airline_id) AS air ";
           PreparedStatement stmt = conn.prepareStatement(rankQuery);

           ResultSet cur = stmt.executeQuery();

           while(cur.next()){
               int rank = cur.getInt(1);
               String name = cur.getString(2);
               System.out.println(rank+") "+name);
           }

        }catch(SQLException e1){
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = "+ e1.getSQLState());
                System.out.println("SQL Code = "+ e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

///////////////////////////////////////////////////////////////////////////////
//Everything below is only for the main menu and running the application.
    public static void printFirstCustomerMenu(){
        System.out.println();
        System.out.println("---------- Customer Menu ----------");
        System.out.println("1: Add customer");
        System.out.println("2: Show customer info, given customer name");
        System.out.println("3: Find price for flights between two cities");
        System.out.println("4: Find all routes between two cities");
        System.out.println("5: Find all routes between two cities of a given airline");
        System.out.println("6: Find all routes with available seats between two cities on a given date");
        System.out.println("7: Add reservation");
        System.out.println("8: Delete reservation");
        System.out.println("9: Show reservation info, given reservation number");
        System.out.println("10: Buy ticket from existing reservation");
        System.out.println("11: Find the top-k customers for each airline");
        System.out.println("12: Find the top-k traveled customers for each airline");
        System.out.println("13: Rank the airlines based on customer satisfaction");
        System.out.println("Type 'quit' to close program.");
        System.out.println();
        System.out.print("\nPlease select an option: ");
    }

    public static void pressAnyButtonToContinue(Scanner input){
        System.out.print("\nPress enter to continue...");
        input.nextLine();
        System.out.println();
    }

    public static void runCustomerUI(String url, Properties props){
        Scanner input = null;
        Connection conn = null;
        try{
            input = new Scanner(System.in);
            //Class.forName("org.postgresql.Driver");
            //String url = "jdbc:postgresql://localhost/postgres";
            //Properties props = new Properties();
            //props.setProperty("user", "postgres");
            //props.setProperty("password", "5506");
            conn = DriverManager.getConnection(url, props);
        } catch(Exception e){
            e.printStackTrace();
        }

        String selection = "";
        while(true){
            printFirstCustomerMenu();
            selection = input.nextLine();
            System.out.println();
            if(selection.equals("quit")){
                System.out.println("\nClosing application. Have a nice day!");
                break;
            }
            else if(selection.equals("1")){
                task1(input, conn);
                pressAnyButtonToContinue(input);
            }
            else if(selection.equals("2")){
                task2(input, conn);
                pressAnyButtonToContinue(input);
            }
            else if(selection.equals("3")){
                task3(input, conn);
                pressAnyButtonToContinue(input);
            }
            else if(selection.equals("4")){
                task4(input, conn);
                pressAnyButtonToContinue(input);
            }
            else if(selection.equals("5")){
                task5(input, conn);
                pressAnyButtonToContinue(input);
            }
            else if(selection.equals("6")){
                task6(input, conn);
                pressAnyButtonToContinue(input);
            }
            else if(selection.equals("7")){
                task7(input, conn);
            }
            else if(selection.equals("8")){
                task8(input, conn);
                pressAnyButtonToContinue(input);
            }
            else if(selection.equals("9")){
                task9(input, conn);
                pressAnyButtonToContinue(input);
            }
            else if(selection.equals("10")){
                task10(input, conn);
                pressAnyButtonToContinue(input);
            }
            else if(selection.equals("11")){
                task11(input, conn);
                pressAnyButtonToContinue(input);
            }
            else if(selection.equals("12")){
                task12(input, conn);
                pressAnyButtonToContinue(input);
            }
            else if(selection.equals("13")){
                task13(conn);
                pressAnyButtonToContinue(input);
            }
            else{
                System.out.println("\nEnter a valid selection or type 'quit' to close the application.\n");
                pressAnyButtonToContinue(input);
            }

        }//end while
        //application closes... No code beyond this line
    }

}
class Triplet{
    String a, b, c;

    public Triplet(String a, String b, String c){
        this.a = a;
        this.b = b;
        this.c = c;
    }
    public String toString(){
        return "(" + a + ", " + b + ", " + c + ")";
    }
}

class Graph{
    private int numV;
    private HashMap<String, LinkedList<String>> adjMat;
    private ArrayList<ArrayList<String>> allPathsSrcDest;

    public Graph(int v){
        numV = v;
        adjMat = new HashMap<String, LinkedList<String>>(numV);
    }

    public void addEdge(String v, String w){
        if(!adjMat.containsKey(v)){
            adjMat.put(v, new LinkedList<String>());
        }
        if(!adjMat.containsKey(w)){
            adjMat.put(w, new LinkedList<String>());
        }
        adjMat.get(v).add(w);
    }

    public void bfs(String source, String dest){

        HashMap<String, Boolean> visited = new HashMap<String, Boolean>(numV);
        //fill with false
        for(String k : adjMat.keySet()){
            visited.put(k, false);
        }
        LinkedList<String> queue = new LinkedList<String>();

        visited.put(source, true);
        queue.add(source);

        while (queue.size() != 0){
            source = queue.poll();

            Iterator<String> it = adjMat.get(source).listIterator();
            while (it.hasNext()){
                String n = it.next();
                //Edge is source -> n
                System.out.println(source+" -> "+n);
                if (!visited.get(n)){
                    visited.replace(n, true);
                    queue.add(n);
                    if(n.equals(dest)){
                        return;
                    }
                }
            }//end while 2
        }//end while 1
    }//end bfs

    public ArrayList<ArrayList<String>> genAllPaths(String s, String d){
        HashMap<String, Boolean> isVisited = new HashMap<String, Boolean>(numV);
        for(String k : adjMat.keySet()){
            isVisited.put(k, false);
        }
        ArrayList<String> pathList = new ArrayList<String>();
        pathList.add(s);

        allPathsSrcDest = new ArrayList<ArrayList<String>>();
        allPathsSrcDest = dfsHelp(s, d, isVisited, pathList);
        return allPathsSrcDest;
    }

    private ArrayList<ArrayList<String>> dfsHelp(String u, String d, HashMap<String, Boolean> isVisited, ArrayList<String> localPathList){

        if (u.equals(d)) {
            ArrayList<String> copy = new ArrayList<String>();
            for(String s : localPathList){
                copy.add(s);
            }
            allPathsSrcDest.add(copy);
            return allPathsSrcDest;
        }

        isVisited.replace(u, true);

        for (String i : adjMat.get(u)){
            if (!isVisited.get(i)) {
                localPathList.add(i);
                dfsHelp(i, d, isVisited, localPathList);
                localPathList.remove(i);
            }
        }
        isVisited.replace(u, false);
        return allPathsSrcDest;
    }
}
