# Ελληνικά

# Παρουσίαση Παγκόσμιων Δεδομένων για Covid-19

## Απλή Java με χρήση τρίτου API και τεχνικές πολυνηματικού προγραμματισμού

Εφαρμογή κονσόλας σε Java η οποία διαχειρίζεται δεδομένα Covid-19 για όλες τις χώρες του κόσμου και τα καταγράφει 
σε ένα blockchain με την χρήση παράλληλων τεχνικών προγραμματισμού.

### Βασικές λειτουργίες της εφαρμογής
1. Αναζήτηση αποτελεσμάτων ανά χώρα
2. Αναζήτηση μηνιαίων αποτελεσμάτων για συγκεκριμένο έτος ανά χώρα
3. Προβολή στατιστικών για όσες χώρες έχει προηγηθεί αναζήτηση (τα στατιστικά
παράγονται από τα δεδομένα της Βάσης Δεδομέων - MySQL, οπότε δεν τα αποθηκεύουμε αφού τα
υπολογίσουμε)

Κάθε αναζήτηση αποτελεσμάτων από το χρήστη καταγράφεται (όπως αναφέρθηκε) στο blockchain, σε
διαφορετικά blocks, τα οποία θα αποθηκεύονται στην βάση μας. 
Σε κάθε block καταγράφετε εκτός από τα δεδομένα της αναζήτησης του χρήστη, και ένα
timestamp, του πότε έγινε η εν λόγω αναζήτηση. Επίσης, η παραγωγή του κάθε block
«δαπανά» 30-60 δευτερόλεπτα στον υπολογιστή μας και ως εκ τούτου υλοποιείται με τη χρήση Threads.

# English

# Presentation of Global Data on Covid-19

## Plain Java using third party API and multi-threaded programming techniques

Java console application that manages and logs Covid-19 data for all countries in the world
on a blockchain using parallel programming techniques.

### Basic Functions of the Application
1. Search results by country
2. Search monthly results for a specific year by country
3. View statistics for other countries previously searched (the statistics
are produced from the data of the Database - MySQL, so we do not store them after
calculation)

Every search result by the user is recorded (as stated) on the blockchain, in
different blocks, which will be stored in our database.
In each block we store in addition to the data of the user's search, and one
timestamp, of when the search in question took place. Also, the production of each block
"spends" 30-60 seconds on our computer and is therefore used using Threads.
