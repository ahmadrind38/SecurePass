package user;

/**
 * This class represents an account with a username and password.
 * It includes methods to retrieve and change the password.
 */
public class Account {

    private String username;
    private String password;

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * @return Returns the username of this Account.
     */
    public String getUserName() {
        return this.username;
    }

    /**
     * @return Returns the password of this Account.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Changes the password for this Account.
     * @param password the new password
     */
    public void changePassword(String password) {
        this.password = password;
    }
}