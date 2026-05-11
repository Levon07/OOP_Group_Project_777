package payment_system;

public enum CryptoCurrency {
    BTC("Bitcoin"),
    ETH("Ethereum"),
    LTC("Litecoin"),
    SOL("Solana");

    private final String fullName;

    CryptoCurrency(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return name() + " (" + fullName + ")";
    }
}
