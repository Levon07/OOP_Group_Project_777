package payment_system;

import payment_system.exceptions.InsufficientFundsException;
import payment_system.exceptions.InvalidAmountException;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Graphical user interface for a simplified Crypto Wallet application.
 *
 * Main features include:
 *
 *     User login and registration
 *     Dashboard with balance overview
 *     Transaction history table
 *     Basic in-memory user management
 *
 * @author Movses Tonyan, Davit Chilingaryan, Levon Nazinyan
 */

public class CryptoWalletGUI extends JFrame {

    // ── Color palette ─────────────────────────────────────────────────────────
    private static final Color BG_DARK      = new Color(245, 247, 250); // Main background
    private static final Color BG_PANEL     = new Color(255, 255, 255); // Panels/cards
    private static final Color BG_CARD      = new Color(248, 250, 252); // Secondary cards

    private static final Color ACCENT       = new Color(59, 130, 246);  // Blue accent
    private static final Color ACCENT2      = new Color(37, 99, 235);   // Darker blue

    private static final Color SUCCESS      = new Color(34, 197, 94);   // Green
    private static final Color DANGER       = new Color(239, 68, 68);   // Red

    private static final Color TEXT_PRIMARY = new Color(31, 41, 55);    // Dark gray text
    private static final Color TEXT_MUTED   = new Color(107, 114, 128); // Muted gray text

    private static final Color BORDER_COLOR = new Color(229, 231, 235); // Light border

    // ── Data ──────────────────────────────────────────────────────────────────
    private static final int MAX_USERS = 100;
    private final User[] users = new User[MAX_USERS];
    private int userCount = 0;
    private User currentUser = null;

    // ── Card layout ───────────────────────────────────────────────────────────
    private CardLayout cardLayout;
    private JPanel cardContainer;

    // ── Login panel fields ────────────────────────────────────────────────────
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;

    // ── Register panel fields ─────────────────────────────────────────────────
    private JTextField regUsernameField;
    private JTextField regEmailField;
    private JPasswordField regPasswordField;
    private JPasswordField regConfirmField;

    // ── Dashboard components ──────────────────────────────────────────────────
    private JLabel welcomeLabel;
    private JPanel balanceCardsPanel;
    private JTable txTable;
    private DefaultTableModel txTableModel;
    private JLabel statusLabel;


    /**
     * Constructs the Crypto Wallet GUI window, initializes the frame,
     * sets up default UI properties, seeds demo users, and builds the interface.
     *
     * Demo users are added for testing purposes:
     *
     *     Movses (admin)
     *     Davit (admin)
     *     Levon (admin)
     *
     *
     * The method then initializes all UI components and makes the window visible.
     */
    public CryptoWalletGUI() {
        super("Crypto Wallet");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 650);
        setMinimumSize(new Dimension(800, 580));
        setLocationRelativeTo(null);

        // Seed demo users
        addUser(new User("Movses", "movses_tonyan@edu.aua.am", "admin"));
        addUser(new User("Davit",  "davit_chilingaryan@edu.aua.am",  "admin"));
        addUser(new User("Levon",  "levon_nazinyan@edu.aua.am",  "admin"));

        buildUI();
        setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // UI CONSTRUCTION
    // ══════════════════════════════════════════════════════════════════════════


    /**
     * Builds the main user interface structure of the application.
     *
     * This method initializes the root container using a {@link CardLayout}
     * and sets up the two primary application screens:
     * authentication (login/register) and the user dashboard.
     *
     *
     * The authentication screen is shown by default.
     */
    private void buildUI() {
        getContentPane().setBackground(BG_DARK);
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setOpaque(false);

        cardContainer.add(buildAuthPanel(), "AUTH");
        cardContainer.add(buildDashboardPanel(), "DASHBOARD");

        add(cardContainer);
        cardLayout.show(cardContainer, "AUTH");
    }

    // ── Auth panel (login + register tabs) ────────────────────────────────────

    /**
     * Constructs the authentication panel containing login and registration
     * interfaces inside a tab-like layout.
     *
     * The panel includes:
     *
     *     Application branding (logo and subtitle)
     *     Tab buttons for switching between Login and Register forms
     *     Inner {@link CardLayout} container for form switching
     *
     * The login form is shown by default when the panel is first loaded.
     *
     * @return the fully constructed authentication panel
     */
    private JPanel buildAuthPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG_DARK);

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(BG_PANEL);
        box.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(36, 40, 36, 40)));
        box.setPreferredSize(new Dimension(420, 520));

        // Logo / title
        JLabel logo = new JLabel("◈ CryptoWallet", SwingConstants.CENTER);
        logo.setFont(new Font("SansSerif", Font.BOLD, 26));
        logo.setForeground(ACCENT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(logo);
        box.add(Box.createVerticalStrut(6));

        JLabel sub = new JLabel("Secure Digital Asset Management", SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(sub);
        box.add(Box.createVerticalStrut(24));

        // Tab buttons
        JPanel tabRow = new JPanel(new GridLayout(1, 2, 4, 0));
        tabRow.setOpaque(false);
        tabRow.setMaximumSize(new Dimension(340, 36));
        tabRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginTab    = tabButton("Login");
        JButton registerTab = tabButton("Register");
        tabRow.add(loginTab);
        tabRow.add(registerTab);
        box.add(tabRow);
        box.add(Box.createVerticalStrut(20));

        // Inner card for login/register forms
        CardLayout innerCard = new CardLayout();
        JPanel innerPanel = new JPanel(innerCard);
        innerPanel.setOpaque(false);
        innerPanel.setMaximumSize(new Dimension(340, 320));
        innerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        innerPanel.add(buildLoginForm(), "LOGIN");
        innerPanel.add(buildRegisterForm(), "REGISTER");

        loginTab.addActionListener(e -> {
            innerCard.show(innerPanel, "LOGIN");
            loginTab.setBackground(ACCENT);
            loginTab.setForeground(BG_DARK);
            registerTab.setBackground(BG_CARD);
            registerTab.setForeground(TEXT_PRIMARY);
        });
        registerTab.addActionListener(e -> {
            innerCard.show(innerPanel, "REGISTER");
            registerTab.setBackground(ACCENT);
            registerTab.setForeground(BG_DARK);
            loginTab.setBackground(BG_CARD);
            loginTab.setForeground(TEXT_PRIMARY);
        });

        // Activate login tab by default
        loginTab.doClick();

        box.add(innerPanel);

        outer.add(box);
        return outer;
    }


    /**
     * Constructs the authentication panel containing login and registration
     * interfaces inside a tab-like layout.
     *
     * The panel includes:
     *
     *     Application branding (logo and subtitle)
     *     Tab buttons for switching between Login and Register forms
     *     Inner {@link CardLayout} container for form switching
     *
     * The login form is shown by default when the panel is first loaded.
     *
     * @return the fully constructed authentication panel
     */
    private JPanel buildLoginForm() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        loginUsernameField = styledTextField("Username");
        loginPasswordField = styledPasswordField("Password");

        p.add(fieldLabel("Username"));
        p.add(Box.createVerticalStrut(4));
        p.add(loginUsernameField);
        p.add(Box.createVerticalStrut(12));
        p.add(fieldLabel("Password"));
        p.add(Box.createVerticalStrut(4));
        p.add(loginPasswordField);
        p.add(Box.createVerticalStrut(20));

        JButton loginBtn = accentButton("Login →", ACCENT);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> doLogin());
        loginPasswordField.addActionListener(e -> doLogin());
        p.add(loginBtn);

        return p;
    }

    /**
     * Builds the registration form UI for creating a new user account.
     *
     * The form includes fields for username, email, password, and password confirmation.
     * It performs no validation itself; validation is handled in {@code doRegister()}.
     *
     * @return a JPanel containing the styled registration form
     */
    private JPanel buildRegisterForm() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        regUsernameField = styledTextField("Choose a username");
        regEmailField    = styledTextField("you@example.com");
        regPasswordField = styledPasswordField("Min 6 characters");
        regConfirmField  = styledPasswordField("Repeat password");

        p.add(fieldLabel("Username"));
        p.add(Box.createVerticalStrut(3));
        p.add(regUsernameField);
        p.add(Box.createVerticalStrut(8));
        p.add(fieldLabel("Email"));
        p.add(Box.createVerticalStrut(3));
        p.add(regEmailField);
        p.add(Box.createVerticalStrut(8));
        p.add(fieldLabel("Password"));
        p.add(Box.createVerticalStrut(3));
        p.add(regPasswordField);
        p.add(Box.createVerticalStrut(8));
        p.add(fieldLabel("Confirm Password"));
        p.add(Box.createVerticalStrut(3));
        p.add(regConfirmField);
        p.add(Box.createVerticalStrut(14));

        JButton regBtn = accentButton("Create Account", ACCENT2);
        regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        regBtn.addActionListener(e -> doRegister());
        p.add(regBtn);

        return p;
    }

    // ── Dashboard panel ───────────────────────────────────────────────────────

    /**
     * Constructs the main dashboard panel displayed after successful login.
     *
     * The dashboard is composed of three main sections:
     *
     *     A sidebar with wallet operations and logout
     *     A top bar with user info and status messages
     *     Main content area with balance cards and transaction history
     *
     * @return the fully constructed dashboard panel
     */

    private JPanel buildDashboardPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);

        // Left sidebar
        root.add(buildSidebar(), BorderLayout.WEST);

        // Main area
        JPanel main = new JPanel(new BorderLayout(0, 12));
        main.setBackground(BG_DARK);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        welcomeLabel = new JLabel("Welcome back!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        welcomeLabel.setForeground(TEXT_PRIMARY);
        topBar.add(welcomeLabel, BorderLayout.WEST);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        statusLabel.setForeground(SUCCESS);
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        topBar.add(statusLabel, BorderLayout.EAST);
        main.add(topBar, BorderLayout.NORTH);

        // Balance cards
        balanceCardsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        balanceCardsPanel.setOpaque(false);
        balanceCardsPanel.setPreferredSize(new Dimension(0, 110));
        main.add(balanceCardsPanel, BorderLayout.CENTER);

        // Transaction history table
        main.add(buildTxPanel(), BorderLayout.SOUTH);

        root.add(main, BorderLayout.CENTER);
        return root;
    }


    /**
     * Builds the left sidebar navigation panel for wallet operations.
     *
     * Provides quick actions such as deposit, withdraw, transfer, and logout.
     * Each action triggers an operation dialog or session control.
     *
     *
     * @return a JPanel representing the application sidebar
     */
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_PANEL);
        sidebar.setBorder(new EmptyBorder(24, 16, 24, 16));
        sidebar.setPreferredSize(new Dimension(200, 0));

        JLabel brand = new JLabel("◈ CryptoWallet");
        brand.setFont(new Font("SansSerif", Font.BOLD, 16));
        brand.setForeground(ACCENT);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(30));

        String[] labels = {"Deposit", "Withdraw", "Transfer"};
        Color[]  colors = {SUCCESS, DANGER, ACCENT2};
        String[] icons  = {"↓  ", "↑  ", "⇄  "};
        for (int i = 0; i < labels.length; i++) {
            final int idx = i;
            JButton btn = sidebarButton(icons[i] + labels[i], colors[i]);
            btn.addActionListener(e -> openOperationDialog(labels[idx]));
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(8));
        }

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = sidebarButton("⎋  Logout", DANGER);
        logoutBtn.addActionListener(e -> doLogout());
        sidebar.add(logoutBtn);

        return sidebar;
    }

    /**
     * Builds the transaction history panel containing a read-only table of user transactions.
     *
     * Displays all wallet transactions including deposits, withdrawals, and transfers.
     * Transactions are shown in reverse chronological order.
     *
     *
     * @return a JPanel containing the transaction history table
     */
    private JPanel buildTxPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(0, 300));

        JLabel title = new JLabel("Transaction History");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(TEXT_PRIMARY);
        p.add(title, BorderLayout.NORTH);

        String[] cols = {"ID", "Type", "Currency", "Amount", "From", "To"};
        txTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        txTable = new JTable(txTableModel);
        styleTable(txTable);

        JScrollPane scroll = new JScrollPane(txTable);
        scroll.setBackground(BG_CARD);
        scroll.setBorder(new LineBorder(BORDER_COLOR, 1));
        scroll.getViewport().setBackground(BG_CARD);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ACTIONS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Handles user login by validating credentials against stored users.
     * <p>
     * On successful authentication, sets the current user and switches to the dashboard view.
     * Displays error messages for invalid input, unknown users, or incorrect passwords.
     * </p>
     */
    private void doLogin() {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        User found = findUser(username);
        if (found == null) {
            showError("User \"" + username + "\" not found.");
            return;
        }
        if (!found.checkPassword(password)) {
            showError("Incorrect password.");
            loginPasswordField.setText("");
            return;
        }

        currentUser = found;
        loginUsernameField.setText("");
        loginPasswordField.setText("");
        refreshDashboard();
        cardLayout.show(cardContainer, "DASHBOARD");
    }

    /**
     * Handles new user registration by validating input fields and creating a new account.
     * <p>
     * Ensures username uniqueness, password rules, and email format validity.
     * On success, adds the user to the system and clears the form.
     * </p>
     */
    private void doRegister() {
        String username = regUsernameField.getText().trim();
        String email    = regEmailField.getText().trim();
        String password = new String(regPasswordField.getPassword());
        String confirm  = new String(regConfirmField.getPassword());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("All fields are required.");
            return;
        }
        if (username.length() < 3) {
            showError("Username must be at least 3 characters.");
            return;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }
        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }
        if (!email.contains("@")) {
            showError("Please enter a valid email address.");
            return;
        }
        if (findUser(username) != null) {
            showError("Username \"" + username + "\" is already taken.");
            return;
        }
        if (userCount >= MAX_USERS) {
            showError("User limit reached.");
            return;
        }

        addUser(new User(username, email, password));
        regUsernameField.setText("");
        regEmailField.setText("");
        regPasswordField.setText("");
        regConfirmField.setText("");

        JOptionPane.showMessageDialog(this,
                "Account created! You can now log in.",
                "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Logs out the current user and returns to the authentication screen.
     * <p>
     * Clears the active session and resets the UI state.
     * </p>
     */
    private void doLogout() {
        currentUser = null;
        cardLayout.show(cardContainer, "AUTH");
    }

    /**
     * Opens a modal dialog for wallet operations such as deposit, withdraw, or transfer.
     * <p>
     * The dialog dynamically adapts its fields based on the selected operation type.
     * Performs validation and executes wallet operations on confirmation.
     * </p>
     *
     * @param type the operation type ("Deposit", "Withdraw", or "Transfer")
     */
    private void openOperationDialog(String type) {
        JDialog dlg = new JDialog(this, type, true);
        dlg.setSize(380, type.equals("Transfer") ? 320 : 260);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(BG_PANEL);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_PANEL);
        p.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel(type + " Funds");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(title);
        p.add(Box.createVerticalStrut(16));

        // Currency selector
        p.add(fieldLabel("Cryptocurrency"));
        p.add(Box.createVerticalStrut(4));
        JComboBox<CryptoCurrency> currencyBox = new JComboBox<>(CryptoCurrency.values());
        styleCombo(currencyBox);
        currencyBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(currencyBox);
        p.add(Box.createVerticalStrut(12));

        // Recipient (only for Transfer)
        JTextField recipientField = null;
        if (type.equals("Transfer")) {
            p.add(fieldLabel("Recipient Username"));
            p.add(Box.createVerticalStrut(4));
            recipientField = styledTextField("Enter username");
            recipientField.setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(recipientField);
            p.add(Box.createVerticalStrut(12));
        }

        // Amount
        p.add(fieldLabel("Amount"));
        p.add(Box.createVerticalStrut(4));
        JTextField amountField = styledTextField("0.0000");
        amountField.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(amountField);
        p.add(Box.createVerticalStrut(20));

        // Confirm button
        Color btnColor = type.equals("Deposit") ? SUCCESS : type.equals("Withdraw") ? DANGER : ACCENT2;
        JButton confirmBtn = accentButton("Confirm " + type, btnColor);
        confirmBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        final JTextField recipFinal = recipientField;
        confirmBtn.addActionListener(e -> {
            double amount;
            try {
                amount = Double.parseDouble(amountField.getText().trim());
            } catch (NumberFormatException ex) {
                showError("Please enter a valid number.");
                return;
            }

            CryptoCurrency currency = (CryptoCurrency) currencyBox.getSelectedItem();

            try {
                switch (type) {
                    case "Deposit":
                        currentUser.getWallet().deposit(currency, amount);
                        break;
                    case "Withdraw":
                        currentUser.getWallet().withdraw(currency, amount);
                        break;
                    case "Transfer":
                        String rec = recipFinal.getText().trim();
                        if (rec.isEmpty()) { showError("Please enter a recipient."); return; }
                        if (rec.equals(currentUser.getUsername())) { showError("Cannot transfer to yourself."); return; }
                        User recipient = findUser(rec);
                        if (recipient == null) { showError("User \"" + rec + "\" not found."); return; }
                        currentUser.getWallet().transfer(recipient.getWallet(), currency, amount);
                        break;
                }
                refreshDashboard();
                setStatus("✅ " + type + " of " + String.format("%.4f", amount) + " " + currency.name() + " successful!");
                dlg.dispose();
            } catch (InvalidAmountException | InsufficientFundsException ex) {
                showError(ex.getMessage());
            }
        });

        p.add(confirmBtn);
        dlg.add(p);
        dlg.setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // REFRESH DASHBOARD
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Refreshes the dashboard UI to reflect the current user's wallet state.
     * <p>
     * Updates balance cards, welcome label, status messages, and transaction history table.
     * Called after any wallet operation or login event.
     * </p>
     */
    private void refreshDashboard() {
        if (currentUser == null) return;

        welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "  |  " + currentUser.getEmail());

        // Balance cards
        balanceCardsPanel.removeAll();
        CryptoCurrency[] coins = CryptoCurrency.values();
        Color[] cardColors = {new Color(245, 158, 11), new Color(99, 179, 237), new Color(107, 114, 128), new Color(139, 92, 246)};
        for (int i = 0; i < coins.length; i++) {
            double bal = currentUser.getWallet().getBalance(coins[i]);
            balanceCardsPanel.add(buildBalanceCard(coins[i], bal, cardColors[i]));
        }
        balanceCardsPanel.revalidate();
        balanceCardsPanel.repaint();

        // Transaction table
        txTableModel.setRowCount(0);
        Wallet w = currentUser.getWallet();
        for (int i = w.getTransactionCount() - 1; i >= 0; i--) {
            Transaction tx = w.getTransactions()[i];
            String from = tx.getFromWallet() != null ? tx.getFromWallet().getOwner().getUsername() : "EXTERNAL";
            String to   = tx.getToWallet()   != null ? tx.getToWallet().getOwner().getUsername()   : "EXTERNAL";
            txTableModel.addRow(new Object[]{
                tx.getTransactionId(),
                tx.getType().name(),
                tx.getCurrency().name(),
                String.format("%.4f", tx.getAmount()),
                from,
                to
            });
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════════════════════


    /**
     * Creates a visual card component displaying cryptocurrency balance information.
     *
     * @param coin the cryptocurrency type
     * @param balance the current balance
     * @param accent the accent color used for styling
     * @return a styled JPanel representing a balance card
     */
    private JPanel buildBalanceCard(CryptoCurrency coin, double balance, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(accent.darker(), 1, true),
                new EmptyBorder(12, 14, 12, 14)));

        JLabel name = new JLabel(coin.name());
        name.setFont(new Font("SansSerif", Font.BOLD, 13));
        name.setForeground(accent);
        card.add(name);

        JLabel full = new JLabel(coin.getFullName());
        full.setFont(new Font("SansSerif", Font.PLAIN, 11));
        full.setForeground(TEXT_MUTED);
        card.add(full);

        card.add(Box.createVerticalStrut(8));

        JLabel bal = new JLabel(String.format("%.4f", balance));
        bal.setFont(new Font("Monospaced", Font.BOLD, 18));
        bal.setForeground(TEXT_PRIMARY);
        card.add(bal);

        return card;
    }

    /**
     * Adds a new user to the in-memory user store if capacity allows.
     *
     * @param u the user to be added
     */
    private void addUser(User u) {
        if (userCount < MAX_USERS) users[userCount++] = u;
    }

    /**
     * Searches for a user by username in the in-memory user array.
     *
     * @param username the username to search for
     * @return the matching User object, or null if not found
     */
    private User findUser(String username) {
        for (int i = 0; i < userCount; i++) {
            if (users[i].getUsername().equals(username)) return users[i];
        }
        return null;
    }

    /**
     * Displays an error message dialog to the user.
     *
     * @param msg the error message to display
     */
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Creates a styled label used for form field descriptions.
     *
     * @param text the label text
     * @return a styled JLabel component
     */
    private void setStatus(String msg) {
        statusLabel.setText(msg);
        Timer t = new Timer(4000, e -> statusLabel.setText(" "));
        t.setRepeats(false);
        t.start();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // STYLED COMPONENT FACTORIES
    // ══════════════════════════════════════════════════════════════════════════

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField styledTextField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setBackground(BG_CARD);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        f.setToolTipText(placeholder);
        return f;
    }

    private JPasswordField styledPasswordField(String placeholder) {
        JPasswordField f = new JPasswordField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setBackground(BG_CARD);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        f.setToolTipText(placeholder);
        return f;
    }

    private JButton accentButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(10, 22, 10, 22));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.brighter()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    private JButton tabButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(BG_CARD);
        b.setForeground(TEXT_PRIMARY);
        b.setBorder(new EmptyBorder(8, 0, 8, 0));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton sidebarButton(String text, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 14));
        b.setBackground(BG_PANEL);
        b.setForeground(fg);
        b.setBorder(new EmptyBorder(10, 8, 10, 8));
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(BG_CARD); }
            public void mouseExited(MouseEvent e)  { b.setBackground(BG_PANEL); }
        });
        return b;
    }

    private void styleTable(JTable table) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(new Font("Monospaced", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);
        table.setSelectionBackground(ACCENT.darker());
        table.setSelectionForeground(TEXT_PRIMARY);
        table.getTableHeader().setBackground(BG_PANEL);
        table.getTableHeader().setForeground(TEXT_MUTED);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBorder(new LineBorder(BORDER_COLOR, 1));
    }

    private void styleCombo(JComboBox<?> box) {
        box.setBackground(BG_CARD);
        box.setForeground(TEXT_PRIMARY);
        box.setFont(new Font("SansSerif", Font.PLAIN, 13));
        box.setBorder(new LineBorder(BORDER_COLOR, 1));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ENTRY POINT
    // ══════════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(CryptoWalletGUI::new);
    }
}
