import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Menu {

    private final DataSource dataSource = DataSource.getInstance();

    public void showMenu() throws IOException, InterruptedException {

        String usersChoice;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("Press 1 to search for the aggregated covid-19 data during " +
                        "the course of the pandemic for a desired country.");
                System.out.println("Press 2 to search for a particular month's data for a given country " +
                        "(current only available for the months of 2020).");
                System.out.println("Press 3 to see some statistics for the countries that you already searched for.");
                System.out.println("Press 4 or type exit terminate the program.\n");

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
                    System.out.println("This feature is not ready yet.\n");
                }

                if (usersChoice.equals("4") || usersChoice.equals("exit")) {
                    System.out.println("The program will be terminated after all the searched data are\n" +
                            "saved in their corresponding blocks and in the database.");
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
        System.out.println("Month given: " + aMonth);
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
}