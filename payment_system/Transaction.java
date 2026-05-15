package payment_system;

import java.util.UUID;

/**
 * Represents a cryptocurrency transaction in the payment system.
 * <p>
 * A transaction stores information about the transaction type,
 * cryptocurrency, amount, sender wallet, and receiver wallet.
 * Each transaction is assigned a unique transaction ID.
 * </p>
 *
 * @author Movses
 */
public class Transaction {

    /** Unique identifier of the transaction. */
    private final String transactionId;

    /** Type of the transaction. */
    private final TransactionType type;

    /** Cryptocurrency involved in the transaction. */
    private final CryptoCurrency currency;

    /** Amount transferred in the transaction. */
    private final double amount;

    /** Source wallet of the transaction. */
    private final Wallet fromWallet;

    /** Destination wallet of the transaction. */
    private final Wallet toWallet;

    /**
     * Constructs a new Transaction object.
     *
     * @param type the transaction type
     * @param currency the cryptocurrency used
     * @param amount the amount transferred
     * @param fromWallet the sender wallet
     * @param toWallet the receiver wallet
     */
    public Transaction(TransactionType type, CryptoCurrency currency,
                       double amount, Wallet fromWallet, Wallet toWallet) {

        this.transactionId = UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        this.type = type;
        this.currency = currency;
        this.amount = amount;
        this.fromWallet = fromWallet;
        this.toWallet = toWallet;
    }

    /**
     * Returns the transaction ID.
     *
     * @return the transaction ID
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Returns the transaction type.
     *
     * @return the transaction type
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Returns the cryptocurrency used in the transaction.
     *
     * @return the cryptocurrency
     */
    public CryptoCurrency getCurrency() {
        return currency;
    }

    /**
     * Returns the transaction amount.
     *
     * @return the amount transferred
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Returns the sender wallet.
     *
     * @return the source wallet
     */
    public Wallet getFromWallet() {
        return fromWallet;
    }

    /**
     * Returns the receiver wallet.
     *
     * @return the destination wallet
     */
    public Wallet getToWallet() {
        return toWallet;
    }

    /**
     * Returns a formatted string representation of the transaction.
     *
     * @return transaction details as a string
     */
    @Override
    public String toString() {

        String from = (fromWallet != null)
                ? fromWallet.getOwner().getUsername()
                : "EXTERNAL";

        String to = (toWallet != null)
                ? toWallet.getOwner().getUsername()
                : "EXTERNAL";

        return "[" + transactionId + "] " + type
                + " | " + String.format("%.4f", amount)
                + " " + currency.name()
                + " | From: " + from + " → To: " + to;
    }
}