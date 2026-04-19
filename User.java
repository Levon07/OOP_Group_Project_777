package payment_system;

public class User {

    private final String userId;
    private final String username;
    private final String email;
    private final Wallet wallet;

    private static int counter = 1000;

    public User(String username, String email) {
        this.userId   = "USR" + (++counter);
        this.username = username;
        this.email    = email;
        this.wallet   = new Wallet(this);
    }

    public String getUserId()  { return userId; }
    public String getUsername(){ return username; }
    public String getEmail()   { return email; }
    public Wallet getWallet()  { return wallet; }

    @Override
    public String toString() {
        return "User[" + userId + "] " + username + " <" + email + ">";
    }
}
