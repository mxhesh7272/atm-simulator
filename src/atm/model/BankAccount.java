package atm.model;

import java.math.BigDecimal;

public abstract class BankAccount implements AccountOperations {

    private final String accountNumber;
    private final String customerName;
    private String pinHash;
    private BigDecimal balance;

    public BankAccount(
            String accountNumber,
            String customerName,
            String pinHash,
            BigDecimal initialBalance) {

        if (accountNumber == null || !accountNumber.matches("[A-Za-z0-9-]{4,24}")) {
            throw new IllegalArgumentException("Account number must contain 4-24 letters, digits, or hyphens.");
        }
        if (customerName == null || customerName.trim().isEmpty() || pinHash == null || pinHash.isBlank()
                || initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid account information.");
        }
        this.accountNumber = accountNumber.trim();
        this.customerName = customerName.trim();
        this.pinHash = pinHash;
        this.balance = initialBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getPinHash() {
        return pinHash;
    }

    public void setPinHash(String pinHash) {
        this.pinHash = pinHash;
    }

    @Override
    public final void deposit(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "Deposit amount must be greater than zero."
            );
        }

        balance = balance.add(amount);
    }

    public abstract void withdraw(BigDecimal amount);

    public abstract String getAccountType();

    protected void deductBalance(BigDecimal amount) {
        balance = balance.subtract(amount);
    }
}
