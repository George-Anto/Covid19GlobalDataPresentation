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

//The instance of this class that is created in the Menu class is responsible for getting the data of the api
//or the csv and create and return a Country object that contains the data the user requested
public class DataSource {

    private final String myFile;
    private BufferedReader reader;

    //the constructor is private
    private DataSource() {
        myFile = "resources/data.csv";
        reader = null;
    }

    //This static class holds the one and only instance of the DataSource class
    private static final class DataSourceHolder {
        private static final DataSource dataSource = new DataSource();
    }

    //Only one instance of the class is constructed and every time this method is called it will
    //return that instance to the caller (singleton)
    public static DataSource getInstance() {
        return DataSourceHolder.dataSource;
    }

    //This method receives a name of a country and creates and returns an object (instance of the Country class)
    //This object holds data regarding the COVID-19 pandemic and the impact to this country (deaths, confirmed cases)
    public Country getCountryDataAggregated(String aCountry) throws URISyntaxException, IOException, InterruptedException {

        //A http request is sent to the api,at the endpoint for the country the user requested
        //and a response is sent back to us
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://covid2019-api.herokuapp.com/v2/country/" + aCountry))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        //We use the gson library to create a Java object out of the json response
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        //The object that is created is an instance of the Country class
        Country country =  gson.fromJson(response.body(), Country.class);
        //A property called type is set to specify that this country object has the aggregated results for a country
        //If the country's name is null, we did not get any results, so we set the type property accordingly
        if (country.getCountryName() == null) {
            country.setTypeOfData("No Data");
        } else {
            country.setTypeOfData("aggregated");
        }
        //We return the country object to the caller
        return country;
    }

    //A helper method that reads data from the csv we use to get the data for the countries by month
    //It returns a list that in each node of the list there is a String array of all the comma separated values
    //that represents the data of one day for a specific country
    //THe list have as many nodes as the days of that month
    private List<String[]> getCountryDataForMonthFromCSV(String aCountry, String month, String year) throws IOException {

        //Make the user's input first letter capital and all the other lower case
        String currentCountry = aCountry.substring(0, 1).toUpperCase() + aCountry.substring(1).toLowerCase();
        //Construct the date as in the csv file
        String date = month + "/" + year;
        //The list where we add all the data that match the user input
        List<String[]> countryDataForAMonth = new ArrayList<>();
        String line;
        String[] row;
        //Helper variables to detect when there are no other relevant data, so we stop searching the csv
        boolean doneFindingResults = false;
        boolean atLeastOneResultsFound = false;

        try {
            //load the csv to the BufferedReader
            reader = new BufferedReader(new FileReader(myFile));

            //Read the csv line by line
            while((line = reader.readLine()) != null) {
                //If the current line contains the country and the month we are looking for,
                //write that line to an array and add that array to the list
                if(line.contains(date) && line.contains(currentCountry)){
                    atLeastOneResultsFound = true;
                    row = line.split(",");
                    countryDataForAMonth.add(row);
                } else {
                    //if we have found a result, and then we found an irrelevant line, this means that
                    //we will not find any other result because the month we are looking for is finished,
                    //and we have already written all the data to the list
                    if(atLeastOneResultsFound) {
                        doneFindingResults = true;
                    }
                }
                //so if this variable is true we stop reading the csv
                if(doneFindingResults) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }
        //return the list of arrays to the caller
        return countryDataForAMonth;
    }

    //This method receives a name of a country and also a month and a year to create and return an object
    //containing the data for a country for a specific month
    public Country getCountryDataMonthly(String aCountry, String month, String year) throws IOException {

        String theMonth = month;
        //Modify the month to read the proper data
        if (theMonth.length() == 1) theMonth = "0" + theMonth;
        //Create a list containing the data of each day of the given month from calling the above helper method
        List<String[]> aCountrysDataForAMonth = this.getCountryDataForMonthFromCSV(aCountry, theMonth, year);

        //If the list is empty we create and early return an object with the type set appropriately
        if (aCountrysDataForAMonth.size() == 0) {
            Country noDataForCountry = new Country(null, "", 0);
            noDataForCountry.setTypeOfData("No Data");
            return noDataForCountry;
        }
        //Now we manually extract the data out of the list we have, and we will create an object out of these data

        String location = aCountry;
        int recovered = 0, active = 0;

        //Create 3 lists that we will fill with the data for deaths, confirmed cases, and cumulative
        //cases of each day of the month
        List<Double> totalCumulativeNumber = new ArrayList<>();
        List<Integer> confirmedList = new ArrayList<>();
        List<Integer> deathsList = new ArrayList<>();

        //we fill these lists with the data
        for(String[] aCountrysDataForADay: aCountrysDataForAMonth) {
            confirmedList.add(Integer.parseInt(aCountrysDataForADay[4]));
            deathsList.add(Integer.parseInt(aCountrysDataForADay[5]));
            if (aCountrysDataForADay.length >= 12) {
                totalCumulativeNumber.add(Double.parseDouble(aCountrysDataForADay[11]));
            }
        }
        //We create a stream to add the data for each day to a sum for the confirmed cases of the whole month
        int confirmed = confirmedList
                .stream()
                .reduce(0, Integer::sum);
        //And the same for the deaths
        int deaths = deathsList
                .stream()
                .reduce(0, Integer::sum);
        //And a stream to find the mean of the month regarding the cumulative number of active cases for the given month
        double cumulativeNumber = totalCumulativeNumber
                .stream()
                .collect(Collectors.averagingDouble(number-> number));

        //We create the data object and fill it with its data
        Data data = new Data(location, confirmed, deaths, recovered, active);
        data.setAverageCumulativeNumberOfCasesFor2Weeks(cumulativeNumber);
        data.setMonth(month);
        data.setYear(year);

        //We create a timestamp
        long queryTimestamp = new Timestamp(System.currentTimeMillis()).getTime();

        //And the date of the search
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        String queryDate = format.format(new Date());

        //We create the country object and fill it with its data,
        //and we set the type, so we can separate it from the aggregated type,
        //and know what kind of data is folding
        Country country = new Country(data, queryDate, queryTimestamp);
        country.setTypeOfData("monthly");
        //We return it to the caller
        return country;
    }
}
