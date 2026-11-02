package atm.service;

import java.math.BigDecimal;

/** Service contract for monetary operations, implemented by {@link ATMService}. */
public interface TransactionOperations {
    void deposit(String accountNumber, BigDecimal amount);
    void withdraw(String accountNumber, BigDecimal amount);
    void transfer(String fromAccount, String toAccount, BigDecimal amount);
}
