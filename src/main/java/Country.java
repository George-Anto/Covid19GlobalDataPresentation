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
    public double getAverageCumulativeNumberOfCasesFor2Weeks() { return data.getAverageCumulativeNumberOfCasesFor2Weeks(); }

    public void setTypeOfData(String typeOfData) { this.typeOfData = typeOfData; }

    public void printCountryData() {
        if (this.getTypeOfData().equals( "aggregated")) {
            System.out.println("Getting aggregated results for country: " + this.getCountryName() + "\n" +
                    "Total covid cases confirmed: " + this.getConfirmed() + "\n" +
                    "Total deaths due to the pandemic: " + this.getDeaths() + "\n");

            Miner.getInstance().writeCountryToBlock(this);
        }
        else if(this.getTypeOfData().equals( "monthly")) {
            System.out.println("Getting monthly results for country: " + this.getCountryName() + "\n" +
                    "Month displayed: " + this.getMonth() + "/" + this.getYear() + "\n" +
                    "Covid cases confirmed: " + this.getConfirmed() + "\n" +
                    "Average cumulative number of confirmed cases per 100.000 citizens for the month: " +
                    String.format("%.2f", this.getAverageCumulativeNumberOfCasesFor2Weeks()) + "\n" +
                    "Deaths this month due to the pandemic: " + this.getDeaths() + "\n");

            Miner.getInstance().writeCountryToBlock(this);

        }
        else System.out.println("We could not find any data for your input.\n");
    }
}
