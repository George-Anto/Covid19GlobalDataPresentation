import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class DataSource {

    private final static String myFile = "resources/data.csv";
    private static BufferedReader reader = null;

    public static Country getCountryData(String aCountry) throws URISyntaxException, IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://covid2019-api.herokuapp.com/v2/country/" + aCountry))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("1) Status: " + response.statusCode() + ", " + response.body());

        Gson gson = new Gson();

        return gson.fromJson(response.body(), Country.class);
    }

    public static List<String[]> getCountryDataForAMonth(String aCountry, String month, String year) throws IOException {

        String currentCountry = aCountry.substring(0, 1).toUpperCase() + aCountry.substring(1).toLowerCase();
        String date = "/" + month + "/" + year;
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
}
