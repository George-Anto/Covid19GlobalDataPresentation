import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataSource {

    private final String myFile;
    private BufferedReader reader;

    private DataSource() {
        myFile = "resources/data.csv";
        reader = null;
    }

    private static final class DataSourceHolder {
        private static final DataSource dataSource = new DataSource();
    }

    public static DataSource getInstance() {
        return DataSourceHolder.dataSource;
    }

    public Country getCountryDataAggregated(String aCountry) throws URISyntaxException, IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://covid2019-api.herokuapp.com/v2/country/" + aCountry))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("1) Status: " + response.statusCode() + ", " + response.body());

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        Country country =  gson.fromJson(response.body(), Country.class);
        country.setTypeOfData("aggregated");
        return country;
    }

    private List<String[]> getCountryDataForMonthFromCSV(String aCountry, String month, String year) throws IOException {

        String currentCountry = aCountry.substring(0, 1).toUpperCase() + aCountry.substring(1).toLowerCase();
        String date = month + "/" + year;
        List<String[]> countryDataForAMonth = new ArrayList<>();
        String line;
        String[] row;
        boolean doneFindingResults = false;
        boolean atLeastOneResultsFound = false;

        try {
            reader = new BufferedReader(new FileReader(myFile));

            while((line = reader.readLine()) != null) {

                if(line.contains(date) && line.contains(currentCountry)){
                    atLeastOneResultsFound = true;
                    row = line.split(",");
                    countryDataForAMonth.add(row);
                } else {
                    if(atLeastOneResultsFound) {
                        doneFindingResults = true;
                    }
                }

                if(doneFindingResults) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }
        return countryDataForAMonth;
    }

    public Country getCountryDataMonthly(String aCountry, String month, String year) throws IOException {

        String theMonth = month;
        if (theMonth.length() == 1) theMonth = "0" + theMonth;
        List<String[]> aCountrysDataForAMonth = this.getCountryDataForMonthFromCSV(aCountry, theMonth, year);

//        String location = aCountrysDataForAMonth.get(0)[6];
        String location = aCountry;
//        String month = aCountrysDataForAMonth.get(0)[2];
//        String year = aCountrysDataForAMonth.get(0)[3];
        int confirmed = 0, deaths = 0, recovered = 0, active = 0;

        for(String[] aCountrysDataForADay: aCountrysDataForAMonth) {
            confirmed += Integer.parseInt(aCountrysDataForADay[4]);
            deaths += Integer.parseInt(aCountrysDataForADay[5]);
        }

//        for(String[] aCountrysDataForADay: aCountrysDataForAMonth) {
//            for(String data: aCountrysDataForADay) {
//                System.out.print(data + ", ");
//            }
//            System.out.println();
//        }

        Data data = new Data(location, confirmed, deaths, recovered, active);
        data.setMonth(month);
        data.setYear(year);

        long queryTimestamp = new Timestamp(System.currentTimeMillis()).getTime();

        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        String queryDate = format.format(new Date());

        Country country = new Country(data, queryDate, queryTimestamp);
        country.setTypeOfData("monthly");

        return country;
    }
}
