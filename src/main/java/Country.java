import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Country {

    private Data data;
    @SerializedName("dt")
    //@Expose
    private String queryDate;
    @SerializedName("ts")
    private int queryTimestamp;

    public String getQueryDate() {
        return queryDate;
    }

    public int getQueryTimestamp() {
        return queryTimestamp;
    }

    public String getCountryName() {
        return data.getLocation();
    }

    public int getConfirmed() {
        return data.getConfirmed();
    }

    public int getDeaths() {
        return data.getDeaths();
    }

    public int getRecovered() {
        return data.getRecovered();
    }

    public int getActive() {
        return data.getActive();
    }
}
