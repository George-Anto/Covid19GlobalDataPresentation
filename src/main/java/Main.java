import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        DataSource dataSource = DataSource.getInstance();

        System.out.println("Country: ");
        String aCountryInput = new Scanner(System.in).nextLine();

        Country aCountry = dataSource.getCountryDataAggregated(aCountryInput);

        aCountry.printCountryData();

        System.out.println("Give the country and the month of the year you wish to see results for...");
        System.out.println("Country: ");
        aCountryInput = new Scanner(System.in).nextLine();
        System.out.println("Month: ");
        String aMonth = new Scanner(System.in).nextLine();
        System.out.println("Year: ");
        String aYear = new Scanner(System.in).nextLine();

        Country anotherCountry = dataSource.getCountryDataMonthly(aCountryInput, aMonth, aYear);
        anotherCountry.printCountryData();
    }
}
