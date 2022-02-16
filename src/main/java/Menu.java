import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Menu {

    private final DataSource dataSource = DataSource.getInstance();

    public void showMenu() throws IOException, InterruptedException {

        Miner.getInstance().start();

        String usersChoice;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("\n------------ Main Menu -----------------");
                System.out.println("Press 1 to search for the aggregated covid-19 data during " +
                        "the course of the pandemic for a desired country.");
                System.out.println("Press 2 to search for a particular month's data for a given country " +
                        "(current only available for the months of 2020).");
                System.out.println("Press 3 to see some statistics for the countries that you already searched for.");
                System.out.println("Press 4 or type exit terminate the program.");
                System.out.println("-----------------------------------------");

                System.out.println("Your choice: ");
                usersChoice = scanner.nextLine();

                while (!usersChoice.equals("1") && !usersChoice.equals("2") && !usersChoice.equals("3") &&
                        !usersChoice.equals("4") && !usersChoice.equals("exit")) {
                    System.out.println("Please enter a valid choice: ");
                    usersChoice = new Scanner(System.in).nextLine();
                }

                if (usersChoice.equals("1")) {
                    printAggregatedCountryResults();
                }

                if (usersChoice.equals("2")) {
                    printMonthlyCountryResults();
                }

                if (usersChoice.equals("3")) {
                    showStats();
                }

                if (usersChoice.equals("4") || usersChoice.equals("exit")) {
                    System.out.println("The program will be terminated after all the searched data are\n" +
                            "saved in their corresponding blocks and in the database.");
                    Miner.getInstance().programIsTerminated();
                    break;
                }

            } catch (URISyntaxException | NullPointerException e) {
                System.out.println("Country input must be only one word and not contain special characters.\n");
            }
        }
    }

    private void printAggregatedCountryResults() throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Country: ");
        String aCountryInput = new Scanner(System.in).nextLine();

        dataSource
                .getCountryDataAggregated(aCountryInput)
                .printCountryData();
    }

    private void printMonthlyCountryResults() throws IOException {
        System.out.println("Give the country and the month of the year you wish to see results for...");
        System.out.println("Country: ");
        String aCountryInput = new Scanner(System.in).nextLine();
        String aMonth;
        int aMonthInt = -1;
        do {
            System.out.println("Month: ");
            aMonth = new Scanner(System.in).nextLine();
            try {
                aMonthInt = Integer.parseInt(aMonth);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number between 1 and 12.");
                continue;
            }
            if (aMonthInt < 1 || aMonthInt > 12) {
                System.out.println("Please enter a number between 1 and 12.");
            }
        } while (aMonthInt < 1 || aMonthInt > 12);
        String aYear;
        System.out.println("Year: (Data available only for 2020 for the moment)");
        do {
            aYear = new Scanner(System.in).nextLine();
            if (!aYear.equals("2020")) System.out.println("Data available only for 2020 for the moment.\nYear: ");
        }while (!aYear.equals("2020"));

        dataSource
                .getCountryDataMonthly(aCountryInput, aMonth, aYear)
                .printCountryData();
    }

    private void showStats() {
        Database database = new Database();

        while (true) {
            System.out.println("\n------------ Specific Statistics per month / country -----------------");
            System.out.println("Press 31 to print the aggregated stats for a specific country.");
            System.out.println("Press 32 to print the metrics of all countries for a specific month.");
            System.out.println("Press 33 to print the monthly metrics for a specific country.");
            System.out.println("Press 34 to print the country with the max deaths for a given month.");
            System.out.println("Press 35 to print the month with the max deaths for a given country.");

            System.out.println("\n----------- Top 10 Statistics aggregated / months / countries --------");
            System.out.println("Press 36 to print the top ten countries with the most aggregated deaths.");
            System.out.println("Press 37 to print the top ten months per country with the most monthly deaths.");
            System.out.println("Press 38 to print the top ten months per country with the most monthly confirmed cases.");

            System.out.println("\n--------------- Back to Main Menu ----------------");
            System.out.println("Press 0 or write 'back' to go back to the Main Menu");

            System.out.println("Choose a statistic category: ");
            String usersChoice = new Scanner(System.in).nextLine();

            while (!usersChoice.equals("31") && !usersChoice.equals("32") && !usersChoice.equals("33") &&
                    !usersChoice.equals("34") && !usersChoice.equals("35") && !usersChoice.equals("36") &&
                    !usersChoice.equals("37") && !usersChoice.equals("38") && !usersChoice.equals("0") &&
                    !usersChoice.equals("back")) {
                System.out.println("Please enter a valid choice: ");
                usersChoice = new Scanner(System.in).nextLine();
            }

            if (usersChoice.equals("31")) database.aggregateStatsPerCountry();

            if (usersChoice.equals("32")) database.everyCountryStatsPerOneMonth();

            if (usersChoice.equals("33")) database.oneCountryStatsPerEveryMonth();

            if (usersChoice.equals("34")) database.oneMonthMaxDeaths();

            if (usersChoice.equals("35")) database.oneCountryMaxDeathsInAMonth();

            if (usersChoice.equals("36")) database.topTenAggregated();

            if (usersChoice.equals("37")) database.topTenMonthDeaths();

            if (usersChoice.equals("38")) database.topTenMonthConfirmedCases();

            if (usersChoice.equals("0") || usersChoice.equals("back")) break;
        }
    }
}
