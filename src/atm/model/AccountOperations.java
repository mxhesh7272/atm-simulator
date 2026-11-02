package atm.model;

import java.math.BigDecimal;

/** Interface used by account types to demonstrate polymorphic account actions. */
public interface AccountOperations {
    void deposit(BigDecimal amount);
    void withdraw(BigDecimal amount);
}
