package payment_system;

/**
 * Represents a user in the payment system.
 * <p>
 * Each user has a unique ID, username, email, password,
 * and an associated wallet created automatically during registration.
 * </p>
 *
 * @author Movses Tonyan, Levon Nazinyan, Davit Chilingaryan
 */
public class User {

    /** Unique identifier of the user. */
    private final String userId;

    /** Username chosen by the user. */
    private final String username;

    /** Email address of the user. */
    private final String email;

    /** Password used for authentication. */
    private final String password;

    /** Wallet associated with this user. */
    private final Wallet wallet;

    /** Counter used to generate unique user IDs. */
    private static int counter = 1000;

    /**
     * Constructs a new User object.
     *
     * @param username the username of the user
     * @param email the email address of the user
     * @param password the password of the user
     */
    public User(String username, String email, String password) {
        this.userId   = "USR" + (++counter);
        this.username = username;
        this.email    = email;
        this.password = password;
        this.wallet   = new Wallet(this);
    }

    /**
     * Returns the unique user ID.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Returns the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the user's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the wallet associated with the user.
     *
     * @return the user's wallet
     */
    public Wallet getWallet() {
        return wallet;
    }

    /**
     * Checks whether the entered password matches the user's password.
     *
     * @param input the password entered by the user
     * @return true if the password matches, otherwise false
     */
    public boolean checkPassword(String input) {
        return password != null && password.equals(input);
    }

    /**
     * Returns a string representation of the user.
     *
     * @return formatted user information
     */
    @Override
    public String toString() {
        return "User[" + userId + "] " + username + " <" + email + ">";
    }
}