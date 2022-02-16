import com.google.gson.Gson;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Database {

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/covid_cases_db";
    static final String USER = "root";
    static final String PASS = "";
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;

    public void connectDB() {
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected Successfully...");
        } catch (Exception e) {
            System.out.println("Error while Connecting . . . ");
        }
    }

    public void InsertDB(Block block) {
        //Άνοιγμα σύνδεσης
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
//            String query = "INSERT INTO table (v1, v2, v3, v4, v5) VALUES (?,?,?,?,?,?) ";

            pst = conn.prepareStatement(query);
            System.out.println("point 5");
            //pst.setInt(1,x);
            pst.executeUpdate();
            System.out.println("Successfully inserted...");
            pst.close();
        } catch (Exception e) {
            System.out.println("Error while loading");
        }
    }

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
            if (hash == null) {
                hash = "0";
            }
        } catch (Exception e) {
            System.out.println("Error while getting hash");
        }
        return hash;
    }

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
