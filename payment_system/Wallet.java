package payment_system;

import payment_system.exceptions.InsufficientFundsException;
import payment_system.exceptions.InvalidAmountException;

public class Wallet {

    private static final int MAX_TRANSACTIONS = 1000;

    private final User owner;
    private final double[] balances;          // indexed by CryptoCurrency.ordinal()
    private final Transaction[] transactions;
    private int transactionCount = 0;

    public Wallet(User owner) {
        this.owner        = owner;
        this.balances     = new double[CryptoCurrency.values().length]; // all 0.0 by default
        this.transactions = new Transaction[MAX_TRANSACTIONS];
    }

    public User getOwner() { return owner; }

    public double getBalance(CryptoCurrency currency) {
        return balances[currency.ordinal()];
    }

    // ── Deposit ───────────────────────────────────────────────────────────────
    public void deposit(CryptoCurrency currency, double amount)
            throws InvalidAmountException {
        if (amount <= 0)
            throw new InvalidAmountException("Deposit amount must be positive. Got: " + amount);

        balances[currency.ordinal()] += amount;
        addTransaction(new Transaction(TransactionType.DEPOSIT, currency, amount, null, this));

        System.out.println("Deposited " + amount + " " + currency.name()
                + " to " + owner.getUsername() + ". New balance: " + balances[currency.ordinal()]);
    }

    // ── Withdraw ──────────────────────────────────────────────────────────────
    public void withdraw(CryptoCurrency currency, double amount)
            throws InvalidAmountException, InsufficientFundsException {
        if (amount <= 0)
            throw new InvalidAmountException("Withdrawal amount must be positive. Got: " + amount);
        if (balances[currency.ordinal()] < amount)
            throw new InsufficientFundsException("Insufficient " + currency.name()
                    + ". Balance: " + balances[currency.ordinal()] + ", required: " + amount);

        balances[currency.ordinal()] -= amount;
        addTransaction(new Transaction(TransactionType.WITHDRAWAL, currency, amount, this, null));

        System.out.println("Withdrew " + amount + " " + currency.name()
                + " from " + owner.getUsername() + ". Remaining: " + balances[currency.ordinal()]);
    }

    // ── Transfer ──────────────────────────────────────────────────────────────
    public void transfer(Wallet toWallet, CryptoCurrency currency, double amount)
            throws InvalidAmountException, InsufficientFundsException {
        if (amount <= 0)
            throw new InvalidAmountException("Transfer amount must be positive. Got: " + amount);
        if (balances[currency.ordinal()] < amount)
            throw new InsufficientFundsException("Insufficient " + currency.name()
                    + ". Balance: " + balances[currency.ordinal()] + ", required: " + amount);

        balances[currency.ordinal()]           -= amount;
        toWallet.balances[currency.ordinal()]  += amount;

        Transaction tx = new Transaction(TransactionType.TRANSFER, currency, amount, this, toWallet);
        this.addTransaction(tx);
        toWallet.addTransaction(tx);

        System.out.println("Transferred " + amount + " " + currency.name()
                + " from " + owner.getUsername()
                + " to " + toWallet.getOwner().getUsername());
    }

    // ── Print ─────────────────────────────────────────────────────────────────
    public void printBalances() {
        System.out.println("--- Balances for " + owner.getUsername() + " ---");
        CryptoCurrency[] coins = CryptoCurrency.values();
        for (int i = 0; i < coins.length; i++) {
            System.out.println("  " + coins[i].name() + ": " + balances[i]);
        }
    }

    public void printTransactionHistory() {
        System.out.println("--- Transaction History for " + owner.getUsername() + " ---");
        if (transactionCount == 0) {
            System.out.println("  No transactions yet.");
        } else {
            for (int i = 0; i < transactionCount; i++) {
                System.out.println("  " + transactions[i]);
            }
        }
    }

    // ── Internal helper ───────────────────────────────────────────────────────
    private void addTransaction(Transaction tx) {
        if (transactionCount < MAX_TRANSACTIONS) {
            transactions[transactionCount] = tx;
            transactionCount++;
        }
    }
}
