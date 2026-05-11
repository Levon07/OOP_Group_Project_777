package payment_system;

import payment_system.exceptions.InsufficientFundsException;
import payment_system.exceptions.InvalidAmountException;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final int MAX_USERS = 100;
    private static final User[] users = new User[MAX_USERS];
    private static int userCount = 0;
    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("   CRYPTO WALLET MANAGEMENT SYSTEM");
        System.out.println("===========================================\n");

        // Initialize with some demo users
        addUser(new User("Movses", "movses_tonyan@edu.aua.am"));
        addUser(new User("Davit",   "davit_chilingaryan@edu.aua.am"));
        addUser(new User("Levon", "levon_nazinyan@edu.aua.am"));

        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = showLoginMenu();
            } else {
                running = showMainMenu();
            }
        }

        scanner.close();
        System.out.println("\n===========================================");
        System.out.println("     Thank you for using Crypto Wallet!");
        System.out.println("===========================================");
    }

    // ══════════════════════════════════════════════════════════════════════
    // LOGIN MENU
    // ══════════════════════════════════════════════════════════════════════
    private static boolean showLoginMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           LOGIN / REGISTER             ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("1. Login");
        System.out.println("2. Register New User");
        System.out.println("3. View All Users");
        System.out.println("4. Exit");
        System.out.print("\nEnter choice: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                handleLogin();
                break;
            case 2:
                handleRegister();
                break;
            case 3:
                listAllUsers();
                break;
            case 4:
                return false;
            default:
                System.out.println("❌ Invalid choice. Please try again.");
        }
        return true;
    }

    private static void handleLogin() {
        System.out.print("\nEnter username: ");
        String username = scanner.nextLine().trim();

        User foundUser = findUserByUsername(username);
        if (foundUser != null) {
            currentUser = foundUser;
            System.out.println("✅ Welcome back, " + currentUser.getUsername() + "!");
        } else {
            System.out.println("❌ User not found. Please register first.");
        }
    }

    private static void handleRegister() {
        System.out.print("\nEnter username: ");
        String username = scanner.nextLine().trim();

        if (findUserByUsername(username) != null) {
            System.out.println("❌ Username already exists. Please login instead.");
            return;
        }

        if (userCount >= MAX_USERS) {
            System.out.println("❌ User limit reached. Cannot register more users.");
            return;
        }

        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        addUser(new User(username, email));
        System.out.println("✅ Registration successful! You can now login.");
    }

    private static void listAllUsers() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           REGISTERED USERS             ║");
        System.out.println("╚════════════════════════════════════════╝");

        if (userCount == 0) {
            System.out.println("No users registered yet.");
        } else {
            for (int i = 0; i < userCount; ++i) {
                System.out.println((i + 1) + ". " + users[i]);
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // MAIN MENU
    // ══════════════════════════════════════════════════════════════════════
    private static boolean showMainMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         MAIN MENU - " + String.format("%-18s", currentUser.getUsername()) + "║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("1. Balance Operations");
        System.out.println("2. Transaction Operations");
        System.out.println("3. Transaction History");
        System.out.println("4. Logout");
        System.out.println("5. Exit");
        System.out.print("\nEnter choice: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                showBalanceMenu();
                break;
            case 2:
                showTransactionMenu();
                break;
            case 3:
                currentUser.getWallet().printTransactionHistory();
                break;
            case 4:
                currentUser = null;
                System.out.println("✅ Logged out successfully.");
                break;
            case 5:
                return false;
            default:
                System.out.println("❌ Invalid choice. Please try again.");
        }
        return true;
    }

    // ══════════════════════════════════════════════════════════════════════
    // BALANCE MENU
    // ══════════════════════════════════════════════════════════════════════
    private static void showBalanceMenu() {
        boolean inBalanceMenu = true;
        while (inBalanceMenu) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║         BALANCE OPERATIONS             ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("1. Add Funds (Deposit)");
            System.out.println("2. Check Balance");
            System.out.println("3. Back to Main Menu");
            System.out.print("\nEnter choice: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    handleDeposit();
                    break;
                case 2:
                    currentUser.getWallet().printBalances();
                    break;
                case 3:
                    inBalanceMenu = false;
                    break;
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
        }
    }

    private static void handleDeposit() {
        CryptoCurrency currency = selectCryptoCurrency();
        if (currency == null) return;

        System.out.print("Enter amount to deposit: ");
        double amount = getDoubleInput();

        try {
            currentUser.getWallet().deposit(currency, amount);
            System.out.println("✅ Deposit successful!");
        } catch (InvalidAmountException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // TRANSACTION MENU
    // ══════════════════════════════════════════════════════════════════════
    private static void showTransactionMenu() {
        boolean inTransactionMenu = true;
        while (inTransactionMenu) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║       TRANSACTION OPERATIONS           ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("1. Withdraw Funds");
            System.out.println("2. Transfer to Another User");
            System.out.println("3. Back to Main Menu");
            System.out.print("\nEnter choice: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    handleWithdraw();
                    break;
                case 2:
                    handleTransfer();
                    break;
                case 3:
                    inTransactionMenu = false;
                    break;
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
        }
    }

    private static void handleWithdraw() {
        CryptoCurrency currency = selectCryptoCurrency();
        if (currency == null) return;

        System.out.print("Enter amount to withdraw: ");
        double amount = getDoubleInput();

        try {
            currentUser.getWallet().withdraw(currency, amount);
            System.out.println("✅ Withdrawal successful!");
        } catch (InvalidAmountException | InsufficientFundsException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void handleTransfer() {
        System.out.print("\nEnter recipient username: ");
        String recipientUsername = scanner.nextLine().trim();

        User recipient = findUserByUsername(recipientUsername);

        if (recipient == null) {
            System.out.println("❌ Recipient not found.");
            return;
        }

        if (recipientUsername.equals(currentUser.getUsername())) {
            System.out.println("❌ Cannot transfer to yourself.");
            return;
        }

        CryptoCurrency currency = selectCryptoCurrency();
        if (currency == null) return;

        System.out.print("Enter amount to transfer: ");
        double amount = getDoubleInput();

        try {
            currentUser.getWallet().transfer(recipient.getWallet(), currency, amount);
            System.out.println("✅ Transfer successful!");
        } catch (InvalidAmountException | InsufficientFundsException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ══════════════════════════════════════════════════════════════════════

    private static void addUser(User user) {
        users[userCount] = user;
        userCount++;
    }

    /**
     * Find a user by username using linear search through the users array.
     * @param username the username to search for
     * @return User object if found, null otherwise
     */
    private static User findUserByUsername(String username) {
        for (int i = 0; i < userCount; ++i) {
            if (users[i].getUsername().equals(username)) {
                return users[i];
            }
        }
        return null;
    }

    private static CryptoCurrency selectCryptoCurrency() {
        System.out.println("\nSelect Cryptocurrency:");
        CryptoCurrency[] currencies = CryptoCurrency.values();
        for (int i = 0; i < currencies.length; ++i) {
            System.out.println((i + 1) + ". " + currencies[i].toString());
        }
        System.out.print("Enter choice: ");

        int choice = getIntInput();
        if (choice >= 1 && choice <= currencies.length) {
            return currencies[choice - 1];
        } else {
            System.out.println("❌ Invalid choice.");
            return null;
        }
    }

    private static int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid input. Please enter a number: ");
            }
        }
    }

    private static double getDoubleInput() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid input. Please enter a valid number: ");
            }
        }
    }
}
