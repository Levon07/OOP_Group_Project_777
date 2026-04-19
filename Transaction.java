package payment_system;

import java.util.UUID;

public class Transaction {

    private final String transactionId;
    private final TransactionType type;
    private final CryptoCurrency currency;
    private final double amount;
    private final Wallet fromWallet;   // null for DEPOSIT
    private final Wallet toWallet;     // null for WITHDRAWAL

    public Transaction(TransactionType type, CryptoCurrency currency,
                       double amount, Wallet fromWallet, Wallet toWallet) {
        this.transactionId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.type          = type;
        this.currency      = currency;
        this.amount        = amount;
        this.fromWallet    = fromWallet;
        this.toWallet      = toWallet;
    }

    public String getTransactionId()    { return transactionId; }
    public TransactionType getType()    { return type; }
    public CryptoCurrency getCurrency() { return currency; }
    public double getAmount()           { return amount; }
    public Wallet getFromWallet()       { return fromWallet; }
    public Wallet getToWallet()         { return toWallet; }

    @Override
    public String toString() {
        String from = (fromWallet != null) ? fromWallet.getOwner().getUsername() : "EXTERNAL";
        String to   = (toWallet   != null) ? toWallet.getOwner().getUsername()   : "EXTERNAL";
        return "[" + transactionId + "] " + type
                + " | " + amount + " " + currency.name()
                + " | From: " + from + " → To: " + to;
    }
}
