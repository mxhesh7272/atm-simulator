package atm.service;

import atm.model.BankAccount;
import atm.model.Customer;
import atm.model.CurrentAccount;
import atm.model.SavingsAccount;
import atm.model.Transaction;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/** GUI-facing facade; keeps Swing code free of banking rules. */
public class ATMService implements TransactionOperations {
    private final Bank bank;
    public ATMService() { this(new Bank()); }
    public ATMService(Bank bank) { this.bank = bank; }
    public void register(String number, String name, String type, char[] pin, BigDecimal opening, BigDecimal overdraft) {
        try {
            PinSecurity.validate(pin);
            BankAccount account = "Current".equalsIgnoreCase(type)
                    ? new CurrentAccount(number, new Customer(name).getName(), PinSecurity.hash(pin), opening, overdraft)
                    : new SavingsAccount(number, new Customer(name).getName(), PinSecurity.hash(pin), opening);
            bank.addAccount(account);
        } finally { Arrays.fill(pin, '\0'); }
    }
    public BankAccount login(String account, char[] pin) { try { return bank.authenticate(account, pin); } finally { Arrays.fill(pin, '\0'); } }
    public boolean adminLogin(char[] pin) { try { return bank.authenticateAdmin(pin); } finally { Arrays.fill(pin, '\0'); } }
    @Override public void deposit(String accountNumber, BigDecimal amount) { bank.deposit(accountNumber, amount); }
    @Override public void withdraw(String accountNumber, BigDecimal amount) { bank.withdraw(accountNumber, amount); }
    @Override public void transfer(String fromAccount, String toAccount, BigDecimal amount) { bank.transfer(fromAccount, toAccount, amount); }
    public void changePin(String account, char[] oldPin, char[] newPin) { try { bank.changePin(account, oldPin, newPin); } finally { Arrays.fill(oldPin, '\0'); Arrays.fill(newPin, '\0'); } }
    public List<BankAccount> accounts() { return bank.getAccounts(); }
    public List<Transaction> transactions(String account, int limit) { return bank.getTransactions(account, limit); }
    public List<Transaction> transactions(String account) { return bank.getTransactions(account); }
    public List<Transaction> searchTransactions(String query) { return bank.searchTransactions(query); }
    public boolean isLocked(String accountNumber) { return bank.isLocked(accountNumber); }
}
