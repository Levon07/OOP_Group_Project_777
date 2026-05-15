package payment_system;

import java.util.ArrayList;
import payment_system.exceptions.InsufficientFundsException;
import payment_system.exceptions.InvalidAmountException;

/**
 * Represents a cryptocurrency wallet owned by a user.
 * <p>
 * A wallet stores balances for multiple cryptocurrencies
 * and keeps a history of transactions such as deposits,
 * withdrawals, and transfers.
 * </p>
 *
 * @author Movses Tonyan, Davit Chilingaryan, Levon Nazinyan
 */
public class Wallet {

    /** The owner of the wallet. */
    private final User owner;

    /** Array storing balances for each cryptocurrency. */
    private final double[] balances;

    /** List storing wallet transactions. */
    private final ArrayList<Transaction> transactions;

    /**
     * Constructs a new wallet for a specific user.
     *
     * @param owner the owner of the wallet
     */
    public Wallet(User owner) {
        this.owner = owner;
        this.balances = new double[CryptoCurrency.values().length];
        this.transactions = new ArrayList<>();
    }

    /**
     * Returns the owner of the wallet.
     *
     * @return the wallet owner
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Returns the balance of a specific cryptocurrency.
     *
     * @param currency the cryptocurrency
     * @return the balance of the given cryptocurrency
     */
    public double getBalance(CryptoCurrency currency) {
        return balances[currency.ordinal()];
    }

    /**
     * Prints the full transaction history of the wallet.
     * If no transactions exist, prints a message.
     */
    public void printTransactionHistory() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }

        System.out.println("\n===== TRANSACTION HISTORY =====");
        for (Transaction t : transactions) {
            System.out.println(t);
        }
    }

    /**
     * Prints all cryptocurrency balances in the wallet.
     */
    public void printBalances() {
        System.out.println("\n===== WALLET BALANCES =====");

        for (CryptoCurrency currency : CryptoCurrency.values()) {
            System.out.println(currency + ": " + balances[currency.ordinal()]);
        }
    }

    /**
     * Deposits an amount into the wallet.
     *
     * @param currency the cryptocurrency to deposit
     * @param amount the amount to deposit
     * @throws InvalidAmountException if the amount is not positive
     */
    public void deposit(CryptoCurrency currency, double amount)
            throws InvalidAmountException {

        if (amount <= 0)
            throw new InvalidAmountException("Deposit must be positive");

        balances[currency.ordinal()] += amount;

        addTransaction(new Transaction(
                TransactionType.DEPOSIT,
                currency,
                amount,
                null,
                this
        ));
    }

    /**
     * Withdraws an amount from the wallet.
     *
     * @param currency the cryptocurrency to withdraw
     * @param amount the amount to withdraw
     * @throws InvalidAmountException if the amount is not positive
     * @throws InsufficientFundsException if balance is insufficient
     */
    public void withdraw(CryptoCurrency currency, double amount)
            throws InvalidAmountException, InsufficientFundsException {

        if (amount <= 0)
            throw new InvalidAmountException("Withdrawal must be positive");

        if (balances[currency.ordinal()] < amount)
            throw new InsufficientFundsException("Not enough balance");

        balances[currency.ordinal()] -= amount;

        addTransaction(new Transaction(
                TransactionType.WITHDRAWAL,
                currency,
                amount,
                this,
                null
        ));
    }

    /**
     * Transfers cryptocurrency from this wallet to another wallet.
     *
     * @param toWallet the destination wallet
     * @param currency the cryptocurrency to transfer
     * @param amount the amount to transfer
     * @throws InvalidAmountException if the amount is not positive
     * @throws InsufficientFundsException if balance is insufficient
     */
    public void transfer(Wallet toWallet, CryptoCurrency currency, double amount)
            throws InvalidAmountException, InsufficientFundsException {

        if (amount <= 0)
            throw new InvalidAmountException("Transfer must be positive");

        if (balances[currency.ordinal()] < amount)
            throw new InsufficientFundsException("Not enough balance");

        balances[currency.ordinal()] -= amount;
        toWallet.balances[currency.ordinal()] += amount;

        Transaction tx = new Transaction(
                TransactionType.TRANSFER,
                currency,
                amount,
                this,
                toWallet
        );

        this.addTransaction(tx);
        toWallet.addTransaction(tx);
    }

    /**
     * Adds a transaction to the wallet history.
     *
     * @param tx the transaction to add
     */


    private void addTransaction(Transaction tx) {
        transactions.add(tx);
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }
}