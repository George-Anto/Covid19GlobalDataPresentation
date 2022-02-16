import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//The objects that are created of this class contain the data that the user requested
//These objects contain the aggregated data for a country or monthly, as per user request
//When the data are aggregated, the object is created through the gson library and the fields of
//the object are set from the json that the api http call returns
//If the type is monthly, the data are drawn from the csv file located in resources folder of the project
//so, in that case we create the object manually with the constructor and set the corresponding fields ourselves
public class Country {

    @Expose
    private final Data data;
    @SerializedName("dt")
    @Expose
    private final String queryDate;
    @SerializedName("ts")
    @Expose
    private final long queryTimestamp;
    //If data by country (total): "aggregated", if data by month: "monthly"
    private String typeOfData;

    public Country(Data data, String queryDate, long queryTimestamp) {
        this.data = data;
        this.queryDate = queryDate;
        this.queryTimestamp = queryTimestamp;
    }
    //Getters
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
    //Setter
    public void setTypeOfData(String typeOfData) { this.typeOfData = typeOfData; }

    //This method prints the most relevant information of the country object that is calling it
    public void printCountryData() {
        //If the type is aggregated, the following fields will be presented to the user
        if (this.getTypeOfData().equals( "aggregated")) {
            System.out.println("Getting aggregated results for country: " + this.getCountryName() + "\n" +
                    "Total covid cases confirmed: " + this.getConfirmed() + "\n" +
                    "Total deaths due to the pandemic: " + this.getDeaths() + "\n");

            //The thread that is in charge of mining the block that will
            //store the country data and then save it to the database
            //We call the one and only instance of the Miner class and pass it the country object
            //that is shown to the user, so it will be stored in the block and then the database
            Miner.getInstance().writeCountryToBlock(this);
        }
        //If the type is monthly, then the information shown are the following
        else if(this.getTypeOfData().equals( "monthly")) {
            System.out.println("Getting monthly results for country: " + this.getCountryName() + "\n" +
                    "Month displayed: " + this.getMonth() + "/" + this.getYear() + "\n" +
                    "Covid cases confirmed: " + this.getConfirmed() + "\n" +
                    "Average cumulative number of confirmed cases per 100.000 citizens for the month: " +
                    String.format("%.2f", this.getAverageCumulativeNumberOfCasesFor2Weeks()) + "\n" +
                    "Deaths this month due to the pandemic: " + this.getDeaths() + "\n");

            //We do the same here, we did not put the call of this method outside the if clause
            //because we will not call it if the type is neither aggregated nor monthly
            Miner.getInstance().writeCountryToBlock(this);

        }
        //Else if the type is neither type of the above, the object does not contain valid data
        else System.out.println("We could not find any data for your input.\n");
    }

    //This method returns a string that contains all the data of the current object
    //It is relevant for the mining of the block that the thread is doing before the
    //mined block that contains the data is stored to the database
    @Override
    public String toString() {
        return this.data.toString();
    }
}
