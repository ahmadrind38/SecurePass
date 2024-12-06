package model;

import user.Account;
import user.User;
import user.InternetAccount;

import java.io.*;
import java.util.*;

/**
 * This class is the model and stores all the information by reading the CSV files.
 * This class also manages user accounts and their internet accounts.
 */
public class PasswordManagerModel {
    private Map<String, User> userMap;
    private User currentUser ; // the current user, i.e. whoever logged in

    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final String MAIN_DIRECTORY = ".." + File.separator;
    public static final String SRC_DIRECTORY = "src" + File.separator;
    public static final String VIEW_DIRECTORY = "view/"; // Adjust path as necessary
    public static final String DATA_DIRECTORY = SRC_DIRECTORY + "data" + File.separator;

    // Define CSV file paths
    public static final String USER_CSV_FILE = DATA_DIRECTORY + "users.csv";
    public static final String ACCOUNT_CSV_FILE_PREFIX = DATA_DIRECTORY; // To be used with username

    public PasswordManagerModel() {
        userMap = new HashMap<>();
        loadUserCredentialsFromCSV(); // Load user credentials from CSV on startup
    }

    //=============== Methods ==========================================================================================

    /**
     * Sets the current user when logging in.
     *
     * @param user the user to set as the current user
     */
    public void setUser (User user) {
        this.currentUser  = user;
    }

    /**
     * @return Returns the current user, i.e. whoever is logged in.
     */
    public User getCurrentUser () {
        return currentUser ;
    }

    /**
     * @return Returns the current user's username.
     */
    public String getCurrentUserName() {
        return currentUser  != null ? currentUser .getAccount().getUserName() : null;
    }

    /**
     * @param username the username to look for
     * @return Returns the User that corresponds to the input username.
     */
    public User getUser (String username) {
        return userMap.get(username);
    }

    /**
     * @param username the username to check
     * @return Returns true if there is a User with the input username in the database, false otherwise.
     */
    public boolean hasUser (String username) {
        return userMap.containsKey(username);
    }

    /**
     * @param username        the username to check
     * @param enteredPassword the password entered by the user
     * @return Returns true if the input password matches the database password of the input username, false otherwise.
     */
    public boolean isCorrectPassword(String username, String enteredPassword) {
        if (!hasUser (username)) return false;
        return getUser (username).getAccount().getPassword().equals(enteredPassword);
    }

    /**
     * Adds a new user to the map of users and saves to CSV.
     *
     * @param username the username of the new user
     * @param password the password of the new user
     */
    public void addUser (String username, String password) {
        if (hasUser (username)) {
            System.err.println("User  already exists: " + username);
            return;
        }

        try {
            // Add user to the userMap
            User newUser  = new User(new Account(username, password));
            userMap.put(username, newUser );

            // Append the new user to the users.csv file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_CSV_FILE, true))) {
                // Write header if the file is empty
                if (new File(USER_CSV_FILE).length() == 0) {
                    writer.write("username,password\n");
                }
                writer.write(username + "," + password + "\n");
            }

            // Create a new CSV file for the user's internet accounts
            File accountFile = new File(ACCOUNT_CSV_FILE_PREFIX + username + "_accounts.csv");
            if (accountFile.createNewFile()) {
                System.out.println("Created new account file: " + accountFile.getName());
            } else {
                System.out.println("Account file already exists: " + accountFile.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an Internet account for the current user and saves to CSV.
     *
     * @param domain   the domain of the internet account
     * @param username the username for the internet account
     * @param password the password for the internet account
     */
    public void addInternetAccount(String domain, String username, String password) {
        if (currentUser  != null) {
            currentUser .addInternetAccount(new InternetAccount(domain , username, password));
            saveInternetAccountToCSV(domain, username, password);
        } else {
            System.err.println("No user is currently logged in.");
        }
    }

    /**
     * Saves the internet account information to the user's CSV file.
     *
     * @param domain   the domain of the internet account
     * @param username the username for the internet account
     * @param password the password for the internet account
     */
    private void saveInternetAccountToCSV(String domain, String username, String password) {
        try {
            File accountFile = new File(ACCOUNT_CSV_FILE_PREFIX + currentUser .getAccount().getUserName() + "_accounts.csv");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(accountFile, true))) {
                // Write header if the file is empty
                if (accountFile.length() == 0) {
                    writer.write("domain,username,password\n");
                }
                writer.write(domain + "," + username + "," + password + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads user credentials from a CSV file.
     */
    private void loadUserCredentialsFromCSV() {
        File csvFile = new File(USER_CSV_FILE);
        if (!csvFile.exists()) {
            System.out.println("User  credentials CSV file not found. No users loaded.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials.length >= 2) {
                    String username = credentials[0];
                    String password = credentials[1];
                    userMap.put(username, new User(new Account(username, password)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}