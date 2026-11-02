package atm.model;

import java.math.BigDecimal;
import atm.exception.InsufficientFundsException;

public class SavingsAccount extends BankAccount {

    public SavingsAccount(
            String accountNumber,
            String customerName,
            String pinHash,
            BigDecimal initialBalance) {

        super(
            accountNumber,
            customerName,
            pinHash,
            initialBalance
        );
    }

    @Override
    public void withdraw(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientFundsException(
                "Withdrawal amount must be greater than zero."
            );
        }

        if (getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                "Insufficient balance."
            );
        }

        deductBalance(amount);
    }

    @Override
    public String getAccountType() {
        return "Savings";
    }
}
