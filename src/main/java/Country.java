import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Country {

    @Expose
    private final Data data;
    @SerializedName("dt")
    @Expose
    private final String queryDate;
    @SerializedName("ts")
    @Expose
    private final long queryTimestamp;
    private String typeOfData; // If data by country (total): "aggregated", if data by month: "monthly"

    public Country(Data data, String queryDate, long queryTimestamp) {
        this.data = data;
        this.queryDate = queryDate;
        this.queryTimestamp = queryTimestamp;
    }

    public String getQueryDate() { return queryDate; }
    public long getQueryTimestamp() { return queryTimestamp; }
    public String getTypeOfData() { return typeOfData; }
    public String getCountryName() { return data.getLocation(); }
    public int getConfirmed() { return data.getConfirmed(); }
    public int getDeaths() { return data.getDeaths(); }
    public int getRecovered() { return data.getRecovered(); }
    public int getActive() { return data.getActive(); }
    public String getMonth() { return data.getMonth(); }
    public String getYear() { return data.getYear(); }

    public void setTypeOfData(String typeOfData) { this.typeOfData = typeOfData; }

    public void printCountryData() {
        if (this.getTypeOfData().equals( "aggregated")) {
            System.out.println("2) Country: " + this.getCountryName() + ", Deaths: " + this.getDeaths() +
                    ", confirmed: " + this.getConfirmed() + ", current date: " + this.getQueryDate() +
                    ", timestamp: " + this.getQueryTimestamp() + ", type of query: " + this.getTypeOfData());
        }
        else if(this.getTypeOfData().equals( "monthly")) {
            System.out.println("2) Country: " + this.getCountryName() + ", Deaths: " + this.getDeaths() +
                    ", confirmed: " + this.getConfirmed() + ", current date: " + this.getQueryDate() +
                    ", timestamp: " + this.getQueryTimestamp() + ", type of query: " + this.getTypeOfData() +
                    ", for the month: " + this.getMonth() + " of the year: " + this.getYear());
        }
        else System.out.println("Wrong type of data.");
    }
}
