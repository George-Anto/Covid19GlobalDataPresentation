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
import java.util.stream.Collectors;

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

//        System.out.println("1) Status: " + response.statusCode() + ", " + response.body());

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        Country country =  gson.fromJson(response.body(), Country.class);
        if (country.getCountryName() == null) {
            country.setTypeOfData("No Data");
        } else {
            country.setTypeOfData("aggregated");
        }
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

        if (aCountrysDataForAMonth.size() == 0) {
            Country noDataForCountry = new Country(null, "", 0);
            noDataForCountry.setTypeOfData("No Data");
            return noDataForCountry;
        }

        String location = aCountry;
        int recovered = 0, active = 0;

        List<Double> totalCumulativeNumber = new ArrayList<>();
        List<Integer> confirmedList = new ArrayList<>();
        List<Integer> deathsList = new ArrayList<>();

        //Print the lines of the csv that are stored in the list<String[]> aCountrysDataForAMonth
        //For debugging purposes
//        for(String[] aCountrysDataForADay: aCountrysDataForAMonth) {
//            for(String data: aCountrysDataForADay) {
//                System.out.print(data + ", ");
//            }
//            System.out.println();
//        }

        for(String[] aCountrysDataForADay: aCountrysDataForAMonth) {
            confirmedList.add(Integer.parseInt(aCountrysDataForADay[4]));
            deathsList.add(Integer.parseInt(aCountrysDataForADay[5]));
            if (aCountrysDataForADay.length >= 12) {
                totalCumulativeNumber.add(Double.parseDouble(aCountrysDataForADay[11]));
            }
        }

        int confirmed = confirmedList
                .stream()
                .reduce(0, Integer::sum);

        int deaths = deathsList
                .stream()
                .reduce(0, Integer::sum);

        double cumulativeNumber = totalCumulativeNumber
                .stream()
                .collect(Collectors.averagingDouble(number-> number));

        Data data = new Data(location, confirmed, deaths, recovered, active);
        data.setAverageCumulativeNumberOfCasesFor2Weeks(cumulativeNumber);
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
