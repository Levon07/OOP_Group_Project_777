package payment_system;
import payment_system.exceptions.InvalidAmountException;
public class Wallet {

    // TODO: figure out MAX_TRANSACTIONS value, using 1000 for now
    private static final int MAX_TRANSACTIONS = 1000;

    private final User owner;
    private final double[] balances;
    private final Transaction[] transactions;
    private int transactionCount = 0;

    public Wallet(User owner) {
        this.owner        = owner;
        this.balances     = new double[CryptoCurrency.values().length];
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
    // not finally done yet


    // ── Transfer ──────────────────────────────────────────────────────────────
    // not finally done yet

    // ── Print ─────────────────────────────────────────────────────────────────
    public void printBalances() {
        System.out.println("--- Balances for " + owner.getUsername() + " ---");
        CryptoCurrency[] coins = CryptoCurrency.values();
        for (int i = 0; i < coins.length; i++) {
            System.out.println("  " + coins[i].name() + ": " + balances[i]);
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
