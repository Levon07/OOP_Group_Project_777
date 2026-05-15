package payment_system;

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

    /** Maximum number of transactions stored in the wallet history. */
    private static final int MAX_TRANSACTIONS = 1000;

    /** The owner of the wallet. */
    private final User owner;

    /** Array storing balances for each cryptocurrency. */
    private final double[] balances;

    /** Array storing wallet transactions. */
    private final Transaction[] transactions;

    /** Current number of stored transactions. */
    private int transactionCount = 0;

    /**
     * Constructs a new wallet for a specific user.
     *
     * @param owner the owner of the wallet
     */
    public Wallet(User owner) {
        this.owner = owner;
        this.balances = new double[CryptoCurrency.values().length];
        this.transactions = new Transaction[MAX_TRANSACTIONS];
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
     * Deposits an amount into the wallet.
     *
     * @param currency the cryptocurrency to deposit
     * @param amount the amount to deposit
     * @throws InvalidAmountException if the amount is not positive
     */
    public void deposit(CryptoCurrency currency, double amount)
            throws InvalidAmountException {

        if (amount <= 0)
            throw new InvalidAmountException(
                    "Deposit amount must be positive. Got: " + amount);

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
     * @throws InsufficientFundsException if the balance is insufficient
     */
    public void withdraw(CryptoCurrency currency, double amount)
            throws InvalidAmountException, InsufficientFundsException {

        if (amount <= 0)
            throw new InvalidAmountException(
                    "Withdrawal amount must be positive. Got: " + amount);

        if (balances[currency.ordinal()] < amount)
            throw new InsufficientFundsException(
                    "Insufficient " + currency.name()
                            + ". Balance: " + balances[currency.ordinal()]
                            + ", required: " + amount);

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
     * @throws InsufficientFundsException if the balance is insufficient
     */
    public void transfer(Wallet toWallet, CryptoCurrency currency, double amount)
            throws InvalidAmountException, InsufficientFundsException {

        if (amount <= 0)
            throw new InvalidAmountException(
                    "Transfer amount must be positive. Got: " + amount);

        if (balances[currency.ordinal()] < amount)
            throw new InsufficientFundsException(
                    "Insufficient " + currency.name()
                            + ". Balance: " + balances[currency.ordinal()]
                            + ", required: " + amount);

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
     * Returns the transaction history array.
     *
     * @return the transactions array
     */
    public Transaction[] getTransactions() {
        return transactions;
    }

    /**
     * Returns the number of stored transactions.
     *
     * @return the transaction count
     */
    public int getTransactionCount() {
        return transactionCount;
    }

    /**
     * Adds a transaction to the wallet history.
     *
     * @param tx the transaction to add
     */
    private void addTransaction(Transaction tx) {
        if (transactionCount < MAX_TRANSACTIONS) {
            transactions[transactionCount++] = tx;
        }
    }
}