import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

//        System.out.println("Country: ");
//        String chosenCountry = new Scanner(System.in).nextLine();
//
//        Country aCountry = DataSource.getCountryData(chosenCountry);
//
//        System.out.println("2) Country: " + aCountry.getCountryName() + ", Deaths: " + aCountry.getDeaths() +
//                ", confirmed: " + aCountry.getConfirmed() + ", current date: " + aCountry.getQueryDate() +
//                ", timestamp: " + aCountry.getQueryTimestamp());

        System.out.println("Give the country and the month of the year you wish to see results for...");
        System.out.println("Country: ");
        String aCountry = new Scanner(System.in).nextLine();
        System.out.println("Month: ");
        String aMonth = new Scanner(System.in).nextLine();
        System.out.println("Year: ");
        String aYear = new Scanner(System.in).nextLine();

//        List<String[]> aCountrysDataForAMonth = DataSource.getCountryDataForAMonth("afGhanistan", "11", "2020");
        List<String[]> aCountrysDataForAMonth = DataSource.getCountryDataForAMonth(aCountry, aMonth, aYear);

        for(String[] aCountrysDataForADay: aCountrysDataForAMonth) {
            for(String data: aCountrysDataForADay) {
                System.out.print(data + ", ");
            }
            System.out.println();
        }
    }
}
