import com.google.gson.Gson;

import java.sql.*;

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
            System.out.println("point 1");
            Class.forName(JDBC_DRIVER);
            System.out.println("point 2");
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected Successfully...");
        } catch (Exception e) {
            System.out.println("Error while Connecting . . . ");
            System.out.println(e);
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
//            System.out.println(hash+" "+previous_hash+" "+nonce+" "+query_date+" "+countryData+" "+
//                    type+" "+country+" "+year+" "+month+" "+deaths+" "+confirmed+" "+monthCumulativeNumber);
            System.out.println("point 3");
            System.out.println("point 4");
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
            System.out.println(e);
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
            System.out.println(e);
        }
        return hash;
    }
}
