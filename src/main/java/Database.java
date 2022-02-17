import com.google.gson.Gson;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

//The main class for the connection with the database. As database, we use mySQL in MariaDB engine.
// We use JDBC API for connecting, issuing queries and handling results.
//For every action with database, we create the connection giving the following credentials, and we open the
//connection, we use the PrepareStatement objects to issue our statements
public class Database {

    //They are following credentials for the connection and initialization of the connection - statement - results.
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/covid_cases_db";
    static final String USER = "root";
    static final String PASS = "";
    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;

    //The method that we use to create a connection with the database
    public void connectDB() {
        try {
            Class.forName(JDBC_DRIVER);
//            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
//            System.out.println("Connected Successfully...");
        } catch (Exception e) {
            System.out.println("Error while Connecting . . . ");
        }
    }

    //the main method for the inserting all the block values in the current block position of the chain
    public void InsertDB(Block block) {
        connectDB();
        try {
            String hash = block.getHash();
            String previous_hash = block.getPreviousHash();
            int nonce = block.getNonce();
            long query_date = block.getCountryData().getQueryTimestamp();

            Country countryData = block.getCountryData();
            Gson gson = new Gson();
            String countryDataJson = gson.toJson(countryData);

            String type = block.getCountryData().getTypeOfData();
            String country = block.getCountryData().getCountryName();

            //If the countryData object that is inside the block is of type aggregated, then
            //there will be no month and year data to parseInt from, so we mark them with the value -1
            //and store them with that value in the database
            int year = -1;
            if (block.getCountryData().getYear() != null) {
                year = Integer.parseInt(block.getCountryData().getYear());
            }

            int month = -1;
            if (block.getCountryData().getMonth() != null) {
                month = Integer.parseInt(block.getCountryData().getMonth());
            }

            int deaths = block.getCountryData().getDeaths();
            int confirmed = block.getCountryData().getConfirmed();
            double monthCumulativeNumber = block.getCountryData().getAverageCumulativeNumberOfCasesFor2Weeks();

            String query = "INSERT INTO new_covid_blockchain (hash, previous_hash, nonce, query_date, countryData, type, country, year, month, deaths, confirmed, monthCumulativeNumber) " +
                    "VALUES ('"+ hash +"', '"+ previous_hash +"', '"+ nonce +"', '"+ query_date +"',  '"+ countryDataJson +"', '"+ type +"', '"+ country +"', '"+ year +"', '"+ month +"', '"+ deaths +"', '"+ confirmed +"', '"+ monthCumulativeNumber +"')";

            pst = conn.prepareStatement(query);
            pst.executeUpdate();
            //System.out.println("Successfully inserted...");
            pst.close();
        } catch (Exception e) {
            System.out.println("Error while loading");
        }
    }

    //the method that is getting the last hash key from the latest inserted block in the chain
    public String getLastHashFromDB() {
        connectDB();
        String hash = "0";
        try {
            String query = "SELECT hash FROM new_covid_blockchain ORDER BY id DESC LIMIT 1;";
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery(query);
            while (rs.next()) {
                hash = rs.getString("hash");
            }
            pst.close();
            //If there is no block saved in the database, it means that the current block is the first one
            //in the chain so, the field that refers to the previous hash will be zero as in the provided code
            if (hash == null) {
                hash = "0";
            }
        } catch (Exception e) {
            System.out.println("Error while getting hash");
        }
        return hash;
    }

    //method that is printing the total / aggregated results for a country
    public void aggregateStatsPerCountry() {
        System.out.println("Write a country: ");
        String usersChoice = new Scanner(System.in).nextLine();

        connectDB();
        try {
            String query = "SELECT * FROM new_covid_blockchain WHERE (country = '"+usersChoice+"' AND type = 'aggregated') LIMIT 1";
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery(query);
            System.out.println();
            System.out.println("--------------------------- " + usersChoice + " total stats  ---------------------------");
            boolean dataFound = false;
            while (rs.next()) {
                dataFound = true;
                String country = rs.getString("country");
                int deaths = rs.getInt("deaths");
                int confirmed = rs.getInt("confirmed");
                System.out.println("For the country: " + country + "\tThe casualties are: " + deaths + " \tOf total cases confirmed: " + confirmed);
            }
            if (!dataFound) System.out.println("No DATA found");
            System.out.println("-----------------------------------------------------------------------------\n");
            pst.close();
        } catch (Exception e) {
            System.out.println("Error while loading data for the " + usersChoice + "...");
        }
    }

    //method that is printing the unique values for a specific
    //month from every country in the blockchain descending ordered by deaths
    public void everyCountryStatsPerOneMonth() {
        int usersChoice = getMonthInput();

        connectDB();
        try {
            String query = "SELECT DISTINCT country,deaths,confirmed FROM new_covid_blockchain WHERE (month = '"+usersChoice+"' AND type = 'monthly') ORDER BY deaths DESC";
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery(query);
            System.out.println();
            System.out.println("--------------------------- Metrics of all countries for month: " + usersChoice + "  ---------------------------");
            boolean dataFound = false;
            while (rs.next()) {
                dataFound = true;
                String country = rs.getString("country");
                int deaths = rs.getInt("deaths");
                int confirmed = rs.getInt("confirmed");
                System.out.println("For the country: " + country + "\tThe casualties are: " + deaths + " \tOf total cases confirmed: " + confirmed);
            }
            if (!dataFound) System.out.println("No DATA found");
            System.out.println("-----------------------------------------------------------------------------\n");
            pst.close();
        } catch (Exception e) {
            System.out.println("Error while loading data for the " + usersChoice + " month...");
        }
    }

    //method that is printing for a unique country every register month in the blockchain
    //descending ordered by months
    public void oneCountryStatsPerEveryMonth() {
        System.out.println("Write a country: ");
        String usersChoice = new Scanner(System.in).nextLine();

        connectDB();
        try {
            String query = "SELECT DISTINCT country,month,deaths,confirmed FROM new_covid_blockchain WHERE (country = '"+usersChoice+"' AND type = 'monthly') ORDER BY month DESC";
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery(query);
            System.out.println();
            System.out.println("---------------------------  " + usersChoice + " per every month stats  ---------------------------");
            boolean dataFound = false;
            while (rs.next()) {
                dataFound = true;
                int month = rs.getInt("month");
                String country = rs.getString("country");
                int deaths = rs.getInt("deaths");
                int confirmed = rs.getInt("confirmed");
                System.out.println("For the month: " + month + "\tFor the country: " + country + "\tThe casualties are: " + deaths + " \tOf total cases confirmed: " + confirmed);
            }
            if (!dataFound) System.out.println("No DATA found");
            System.out.println("-----------------------------------------------------------------------------\n");
            pst.close();
        } catch (Exception e) {
            System.out.println("Error while loading data for the " + usersChoice + " country...");
        }
    }

    //method that is printing for a month the country with the max deaths
    public void oneMonthMaxDeaths() {
        int usersChoice = getMonthInput();

        connectDB();
        try {
            String query = "SELECT * FROM new_covid_blockchain WHERE (month = '"+usersChoice+"' AND type = 'monthly') ORDER BY deaths DESC LIMIT 1";
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery(query);
            System.out.println();
            System.out.println("------------------------ Country with max deaths for month: " + usersChoice + "  ------------------------");
            boolean dataFound = false;
            while (rs.next()) {
                dataFound = true;
                String country = rs.getString("country");
                int deaths = rs.getInt("deaths");
                int confirmed = rs.getInt("confirmed");
                System.out.println("For the country: " + country + "\tThe casualties are: " + deaths + " \tOf total cases confirmed: " + confirmed);
            }
            if (!dataFound) System.out.println("No DATA found");
            System.out.println("----------------------------------------------------------------------------------");
            pst.close();
        } catch (Exception e) {
            System.out.println("Error while loading data for the " + usersChoice + " month...");
        }
    }

    //method that is printing for one country the month with the max deaths
    public void oneCountryMaxDeathsInAMonth() {
        System.out.println("Write a country: ");
        String usersChoice = new Scanner(System.in).nextLine();

        connectDB();
        try {
            String query = "SELECT * FROM new_covid_blockchain WHERE (country = '"+usersChoice+"' AND type = 'monthly') ORDER BY deaths DESC LIMIT 1";
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery(query);
            System.out.println();
            System.out.println("---------------------------  Month with max casualties for " + usersChoice + "  ---------------------------");
            boolean dataFound = false;
            while (rs.next()) {
                dataFound = true;
                int month = rs.getInt("month");
                String country = rs.getString("country");
                int deaths = rs.getInt("deaths");
                System.out.println("For the month: " + month + "\tFor the country: " + country + "\tTotal casualties: " + deaths);
            }
            if (!dataFound) System.out.println("No DATA found");
            System.out.println("------------------------------------------------------------------------------------\n");
            pst.close();
        } catch (Exception e) {
            System.out.println("Error while loading data for the " + usersChoice + " country...");
        }
    }

    //method that is printing the unique top ten aggregated stats for a country ordered descending by deaths
    public void topTenAggregated() {
        connectDB();
        try {
            String query = "SELECT DISTINCT country,deaths,confirmed FROM new_covid_blockchain WHERE (type = 'aggregated') ORDER BY deaths DESC LIMIT 10";
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery(query);
            System.out.println();
            System.out.println("----------------------- top 10 countries with most aggregated casualties ---------------------");
            boolean dataFound = false;
            while (rs.next()) {
                dataFound = true;
                String country = rs.getString("country");
                int deaths = rs.getInt("deaths");
                System.out.println("\tFor the country: " + country + "\tThe casualties are: " + deaths);
            }
            if (!dataFound) System.out.println("No DATA found");
            System.out.println("-------------------------------------------------------------------------\n");
            pst.close();
        } catch (Exception e) {
            System.out.println("Error while loading data...");
        }
    }

    //method that is printing the unique top ten aggregated stats for a country ordered descending by deaths
    public void topTenMonthDeaths() {
        connectDB();
        try {
            String query = "SELECT DISTINCT country,month,deaths,confirmed FROM new_covid_blockchain WHERE (type = 'monthly') ORDER BY deaths DESC LIMIT 10";
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery(query);
            System.out.println();
            System.out.println("----------------------- top 10 months per country with the most monthly casualties ---------------------");
            boolean dataFound = false;
            while (rs.next()) {
                dataFound = true;
                int month = rs.getInt("month");
                String country = rs.getString("country");
                int deaths = rs.getInt("deaths");
                System.out.println("\tFor the month: " + month + "\tFor the country: " + country + "\tThe casualties are: " + deaths);
            }
            if (!dataFound) System.out.println("No DATA found");
            System.out.println("-------------------------------------------------------------------------\n");
            pst.close();
        } catch (Exception e) {
            System.out.println("Error while loading data...");
        }
    }

    //method that is printing the unique top ten aggregated stats for a country ordered descending by confirmed cases
    public void topTenMonthConfirmedCases() {
        connectDB();
        try {
            String query = "SELECT DISTINCT country,month,confirmed FROM new_covid_blockchain WHERE (type = 'monthly') ORDER BY confirmed DESC LIMIT 10";
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery(query);
            System.out.println();
            System.out.println("----------------------- top 10 months per country with the most monthly confirmed cases ---------------------");
            boolean dataFound = false;
            while (rs.next()) {
                dataFound = true;
                int month = rs.getInt("month");
                String country = rs.getString("country");
                int confirmed = rs.getInt("confirmed");
                System.out.println("\tFor the month: " + month + "\tFor the country: " + country + "\tThe confirmed cases are: " + confirmed);
            }
            if (!dataFound) System.out.println("No DATA found");
            System.out.println("-------------------------------------------------------------------------\n");
            pst.close();
        } catch (Exception e) {
            System.out.println("Error while loading data...");
        }
    }

    //method that is used from the previous methods to confirm that the user is giving valid month input
    //this means that the input is a number between 1 nad 12
    private int getMonthInput() {
        int usersChoice = -1;
        do {
            try {
                System.out.println("Give a month: ");
                usersChoice = new Scanner(System.in).nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number between 1 and 12.");
                continue;
            }
            if (usersChoice < 1 || usersChoice > 12) System.out.println("Please enter a number between 1 and 12.");
        }while (usersChoice < 1 || usersChoice > 12);
        return usersChoice;
    }
}
