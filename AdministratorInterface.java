import java.util.Properties;
import java.sql.*;
import java.io.*;
import java.util.Scanner;
import java.util.Arrays;
import java.text.SimpleDateFormat;  
import java.util.Date; 
import java.util.ArrayList;
public class AdministratorInterface {
    static String url;
    static Properties props;
    static BufferedReader br; //for reading file
    static Scanner sc; //for reading user input 
    public static void main(String[] args) throws SQLException, ClassNotFoundException, FileNotFoundException, IOException{
        Class.forName("org.postgresql.Driver");
        url = "jdbc:postgresql://localhost:5432/";

        props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "G@rd@n99331");

        sc = new Scanner(System.in);

        boolean continuePrompt = true;
        while(continuePrompt)
        {
            menuOptions();
            String menuOption = sc.nextLine();
            switch(menuOption){
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
                    continuePrompt = false;
                    exitProgram();
                default:
                    System.out.println("Not a menu option, prompting again...");
                    
                    
            }
        }
    }

    public static void menuOptions()
    {
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
        System.out.println("|| (8): Exit the Program                                                   ||");
        System.out.println("|| ======================================================================= ||");        
        System.out.println("|| NOTE: Please be wary of erroneous input! Try to enter viable choices!   ||");
        System.out.println("<~-------------------------------------------------------------------------~>");

        System.out.print("                            ||Enter your option||: ");

        
    }
    // Ask the user to verify deletion of all the data.
    // Simply delete all the tuples of all the tables in the database
    public static void eraseDatabase() throws SQLException, ClassNotFoundException
    {
        System.out.print("Are you sure you want to delete all data? (Enter Y for Yes and N for No): ");
        String input = sc.nextLine();
        if(input.equalsIgnoreCase("Y")){
            Connection conn = DriverManager.getConnection(url, props);
            Statement st = conn.createStatement();
            st.execute("TRUNCATE TABLE AIRLINE CASCADE"); //truncate table accounts for rows in case there is a FK to it
            st.execute("TRUNCATE TABLE PLANE CASCADE");
            st.execute("TRUNCATE TABLE FLIGHT CASCADE");
            st.execute("TRUNCATE TABLE PRICE CASCADE");
            st.execute("TRUNCATE TABLE CUSTOMER CASCADE");
            st.execute("TRUNCATE TABLE RESERVATION CASCADE");
            st.execute("TRUNCATE TABLE RESERVATION_DETAIL CASCADE");
            st.execute("TRUNCATE TABLE OURTIMESTAMP CASCADE");

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
            Statement st = conn.createStatement(); 
            try{
                st.execute(query.toString()); //insert into Airline table
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
            Statement st = conn.createStatement(); 
            try{
                st.execute(query.toString()); //insert into Flight table
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
                    Statement st = conn.createStatement(); 
                    try{
                        st.execute(query.toString()); //insert into Price table
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
                System.out.print("Enter the new Low Price: ");
                lowPrice = sc.nextLine();

                Connection conn = DriverManager.getConnection(url, props);

                Statement st = conn.createStatement();
                StringBuilder query = new StringBuilder();
                query.append("UPDATE Price SET high_price = ");
                query.append(highPrice + ", low_price = ");
                query.append(lowPrice + " WHERE departure_city = " + deptCity + " and arrival_city = " + arrivalCity);
                
                try{
                    st.execute(query.toString()); //update tuple in Price table                    
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
            Statement st = conn.createStatement(); 
            try{
                st.execute(query.toString()); //insert into Plane table
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
        String flightNum = "", date = "";
        System.out.print("Enter the Flight Number: ");
        flightNum = sc.nextLine();
        System.out.print("Enter the Date [mm/dd/yyyy]: ");
        date = "'" + sc.nextLine() + "'";

        Connection conn = DriverManager.getConnection(url, props);

        Statement st = conn.createStatement();
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
        ResultSet res = st.executeQuery(query);
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
    // Ask the user to supply a date and time to be set as the current timestamp (c timestamp) in
    // OurTimestamp table.
    public static void updateTimestamp() throws SQLException{
        String date_time = "";
        System.out.print("Please supply the Date and Time to be set as the current timestamp (MM/DD/YYYY HH:MM): ");
        date_time = "'" + sc.nextLine() + "'";
        Connection conn = DriverManager.getConnection(url, props);

        Statement st = conn.createStatement();
        StringBuilder query = new StringBuilder("INSERT INTO OurTimestamp values(");
        query.append(date_time + ")");
        st.execute("TRUNCATE TABLE OURTIMESTAMP CASCADE");
        st.execute(query.toString()); //insert into OurTimestamp table
        System.out.println("Successfully added " + query.toString());
        //TO DO
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

    public static void exitProgram()
    {

        System.out.println("\n<~---------------Exiting--------Administrator-------Program----------------~>");
        System.out.println("||=========================================================================||");
        System.out.println("||                          Terminating Program...                         ||");
        System.out.println("||                               Goodbye!                                  ||");
        System.out.println("||=========================================================================||");
        System.out.println("<~-------------------------------------------------------------------------~>\n");                           
        
        System.exit(0);
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
