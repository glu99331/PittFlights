import java.sql.*;
import java.util.Properties;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Iterator;

public class CustomerUI{
    public CustomerUI(){}

    private class Graph{
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

    public void task1(Scanner input, Connection conn){

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

    public void task2(Scanner input, Connection conn){
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

    public void task3(Scanner input, Connection conn){
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
    public void task4(Scanner input, Connection conn){

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

    public void task5(Scanner input, Connection conn){
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

    public void task6(Scanner input, Connection conn){

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

    //not sure how to test becuase don't know which customer is adding reservation
    //Adding a unique reservation number to
    public void task7(Scanner input, Connection conn){

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

            stmt = conn.prepareStatement("INSERT INTO RESERVATION VALUES(?, ?, ?, ?, ?, ?)");
            stmt.setInt(1, newResNum);
            stmt.setInt(2, cid);
            stmt.setInt(3, cost);
            stmt.setString(4, credit);
            stmt.setTimestamp(5, reservationDate);
            stmt.setBoolean(6, ticketed);
            stmt.executeUpdate();

            for(String flight : resInfo){
                String[] info = flight.split(" ");
                int flightNum = Integer.parseInt(info[0]);
                long milli = Long.parseLong(info[1]);
                int leg = Integer.parseInt(info[2]);

                Timestamp departDate = new Timestamp(milli);

                stmt = conn.prepareStatement("INSERT INTO RESERVATION_DETAIL VALUES(?, ?, ?, ?)");
                stmt.setInt(1, newResNum);
                stmt.setInt(2, flightNum);
                stmt.setTimestamp(3, departDate);
                stmt.setInt(4, leg);
                stmt.executeUpdate();
            }

            System.out.println("\nSuccess! Your reservation number is "+newResNum);

        }catch(SQLException e1){
            System.out.println("SQL Error");
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

    public void task8(Scanner input, Connection conn){

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

    public void task9(Scanner input, Connection conn){

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

    public void task10(Scanner input, Connection conn){

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

    public void task11(Scanner input, Connection conn){

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

    public void task12(Scanner input, Connection conn){
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

    public void task13(Connection conn){
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
    public void printFirstCustomerMenu(){
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

    public void pressAnyButtonToContinue(Scanner input){
        System.out.print("\nPress enter to continue...");
        input.nextLine();
        System.out.println();
    }

    public void runUI(){
        Scanner input = null;
        Connection conn = null;
        try{
            input = new Scanner(System.in);
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost/postgres";
            Properties props = new Properties();
            props.setProperty("user", "postgres");
            props.setProperty("password", "5506");
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

    public static void main(String args[]){
        CustomerUI c = new CustomerUI();
        c.runUI();
    }
}
