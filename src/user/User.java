package user;

import java.io.*;
import java.util.HashMap;

/**
 * The User object holds all account info, including the account info for
 * logging into the password manager and all Internet account info.
 *
 * This class is updated to store all user data in separate CSV files.
 */
public class User {
    private Account account;
    private HashMap<String, InternetAccount> internetAccounts;

    // Set your specific path for the CSV files
    public static final String USER_CSV_FILE = "D:\\3rd semester\\eclipse\\PasswordManager\\src\\data\\users.csv";
    public static final String ACCOUNT_CSV_FILE_PREFIX = "D:\\3rd semester\\eclipse\\PasswordManager\\src\\data\\"; // To be used with username

    // Constructor: initializes the user account and loads Internet account info from the user's CSV file
    public User(Account account) {
        this.account = account;
        this.internetAccounts = new HashMap<>();
        loadInternetAccounts();
    }

    // Load internet accounts from the user's CSV file
    private void loadInternetAccounts() {
        File csvFile = new File(ACCOUNT_CSV_FILE_PREFIX + account.getUserName() + "_accounts.csv");
        if (!csvFile.exists()) {
            System.out.println("Account CSV file not found for user: " + account.getUserName() + ". Creating a new one...");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 3) {
                    String domain = fields[0];
                    String username = fields[1];
                    String password = fields[2];
                    internetAccounts.put(domain, new InternetAccount(domain, username, password));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the InternetAccount corresponding to the input domain.
     *
     * @param domain the domain of the internet account
     * @return the InternetAccount associated with the domain, or null if not found
     */
    public InternetAccount getInternetAccount(String domain) {
        return internetAccounts.get(domain);
    }

    /**
     * Returns this User's Account.
     *
     * @return the user's account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Returns a HashMap of InternetAccounts.
     *
     * @return a HashMap containing the user's internet accounts
     */
    public HashMap<String, InternetAccount> getInternetAccounts() {
        return internetAccounts;
    }

    /**
     * Adds an InternetAccount to the HashMap and writes the data to the user's CSV file.
     *
     * @param internetAccount the InternetAccount to be added
     */
    public void addInternetAccount(InternetAccount internetAccount) {
        if (internetAccount == null) {
            System.err.println("Cannot add null InternetAccount.");
            return;
        }

        internetAccounts.put(internetAccount.getDomain(), internetAccount);
        saveInternetAccountToCSV(internetAccount);
    }

    // Helper method to save an individual Internet account to the user's CSV file
    private void saveInternetAccountToCSV(InternetAccount internetAccount) {
        try (FileWriter writer = new FileWriter(ACCOUNT_CSV_FILE_PREFIX + account.getUserName() + "_accounts.csv", true)) { // Append mode
            String line = String.format("%s,%s,%s%n", 
                                         internetAccount.getDomain(), 
                                         internetAccount.getUserName(), 
                                         internetAccount.getPassword());
            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves all internet accounts to the user's CSV file.
     */
    public void saveInternetAccounts() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNT_CSV_FILE_PREFIX + account.getUserName() + "_accounts.csv", false))) { // Overwrite mode
            for (InternetAccount internetAccount : internetAccounts.values()) {
                writer.write(String.format("%s,%s,%s%n", 
                                           internetAccount.getDomain(), 
                                           internetAccount.getUserName(), 
                                           internetAccount.getPassword()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}