package atm.model;

import java.math.BigDecimal;
import atm.exception.InsufficientFundsException;

public class CurrentAccount extends BankAccount {

    private final BigDecimal overdraftLimit;

    public CurrentAccount(
            String accountNumber,
            String customerName,
            String pinHash,
            BigDecimal initialBalance,
            BigDecimal overdraftLimit) {

        super(
            accountNumber,
            customerName,
            pinHash,
            initialBalance
        );

        if (overdraftLimit == null ||
                overdraftLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException(
                "Overdraft limit cannot be negative."
            );
        }

        this.overdraftLimit = overdraftLimit;
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    @Override
    public void withdraw(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "Withdrawal amount must be greater than zero."
            );
        }

        BigDecimal availableFunds = getBalance().add(overdraftLimit);

        if (availableFunds.compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                "Insufficient funds, including overdraft."
            );
        }

        deductBalance(amount);
    }

    @Override
    public String getAccountType() {
        return "Current";
    }
}
