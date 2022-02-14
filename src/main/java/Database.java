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
        //Examples on how to get the individual fields of the block on pass the into the insert
//        block.getCountryData().getCountryName();
//        block.getHash();
//        block.getCountryData().getDeaths();
//        block.getPreviousHash();
        //Άνοιγμα σύνδεσης
        connectDB();
        try {
            System.out.println("Name: " + block.getCountryData().getCountryName());
            System.out.println(block.getHash() + " " + block.getPreviousHash()+ " " + block.getNonce());
//            String hash = block.getHash();
//            String previousHash = block.getPreviousHash();
//            int nonce = block.getNonce();
//            long timestamp = block.getCountryData().getQueryTimestamp();
//            Country countryData = block.getCountryData();
//            String type = block.getCountryData().getTypeOfData();
//            String countryName = block.getCountryData().getCountryName();
//            int year = Integer.parseInt(block.getCountryData().getYear());
//            int month = Integer.parseInt(block.getCountryData().getMonth());
//            int deaths = block.getCountryData().getDeaths();
//            int confirmed = block.getCountryData().getConfirmed();
//            double monthCumulativeNumber = block.getCountryData().getAverageCumulativeNumberOfCasesFor2Weeks();
            //System.out.println("Edw ftanei pantws 1");
            //String query = "INSERT INTO covid_blockchain (hash, previous_hash, nonce, query_date, data, type, country, year, month, deaths, recover, cases) VALUES (1,2,3,current_timestamp(),5,6,'Greece',8,9,10,11,12)";
//            String query = "INSERT INTO new_covid_blockchain (hash, previous_hash, nonce, query_date, data, type, country, year, month, deaths, recover, cases)" +
//                    " VALUES (hash,previousHash,nonce,timestamp,countryData,type,countryName,year,month,deaths,confirmed,monthCumulativeNumber)";
            System.out.println("point 3");
            String query = "INSERT INTO new_covid_blockchain (hash, previous_hash, nonce, query_date, countryData, type, country, year, month, deaths, confirmed, monthCumulativeNumber) VALUES (1,2,3,current_timestamp(),5,6,'Greece',8,9,10,11,12)";
            System.out.println("point 4");
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
}
