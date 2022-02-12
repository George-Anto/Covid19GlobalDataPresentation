import com.google.gson.annotations.Expose;

public class Data {

    @Expose
    private final String location;
    @Expose
    private final int confirmed;
    @Expose
    private final int deaths;
    @Expose
    private final int recovered;
    @Expose
    private final int active;
    private String month;
    private String year;

    public Data(String location, int confirmed, int deaths, int recovered, int active) {
        this.location = location.substring(0, 1).toUpperCase() + location.substring(1).toLowerCase();
        this.confirmed = confirmed;
        this.deaths = deaths;
        this.recovered = recovered;
        this.active = active;
    }

    public String getLocation() {
        return location;
    }
    public int getConfirmed() {
        return confirmed;
    }
    public int getDeaths() {
        return deaths;
    }
    public int getRecovered() {
        return recovered;
    }
    public int getActive() {
        return active;
    }
    public void setMonth(String month) { this.month = month; }
    public void setYear(String year) { this.year = year; }

    public String getYear() { return year; }
    public String getMonth() { return month; }
}
