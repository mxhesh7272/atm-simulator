package atm.service;

import atm.exception.InvalidAccountException;
import atm.model.BankAccount;
import atm.model.Transaction;
import atm.storage.FileManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** In-memory bank domain model backed by the local file manager. */
public class Bank {
    public static final int MAX_PIN_ATTEMPTS = 3;
    /* PBKDF2 hash of documented demo admin PIN. The PIN itself is not stored in code. */
    private static final String ADMIN_PIN_HASH = "120000:yti6Z6rECGy4GFEtDsaxyA==:2sjPQZ58/M2ije0Ssp5Ielhj2vo5gS1pAcDjGqEto8I=";
    private final FileManager fileManager;
    private final Map<String, BankAccount> accounts = new HashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();
    private final Map<String, Integer> failedAttempts = new HashMap<>();

    public Bank() { this(new FileManager()); }
    public Bank(FileManager fileManager) {
        this.fileManager = fileManager;
        for (BankAccount account : fileManager.loadAccounts()) accounts.put(account.getAccountNumber(), account);
        transactions.addAll(fileManager.loadTransactions());
        failedAttempts.putAll(fileManager.loadAttempts());
    }
    public synchronized void addAccount(BankAccount account) {
        if (accounts.containsKey(account.getAccountNumber())) throw new InvalidAccountException("That account number is already registered.");
        accounts.put(account.getAccountNumber(), account);
        try { fileManager.saveAccounts(accounts.values()); }
        catch (RuntimeException exception) { accounts.remove(account.getAccountNumber()); throw exception; }
    }
    public synchronized BankAccount authenticate(String accountNumber, char[] pin) {
        BankAccount account = accounts.get(accountNumber == null ? "" : accountNumber.trim());
        if (account == null) return null;
        if (isLocked(accountNumber)) throw new IllegalStateException("Account is locked after three incorrect PIN attempts.");
        if (!PinSecurity.verify(pin, account.getPinHash())) {
            failedAttempts.put(accountNumber, failedAttempts.getOrDefault(accountNumber, 0) + 1);
            fileManager.saveAttempts(failedAttempts);
            if (isLocked(accountNumber)) throw new IllegalStateException("Account is now locked after three incorrect PIN attempts.");
            throw new IllegalArgumentException("Incorrect PIN. " + (MAX_PIN_ATTEMPTS - failedAttempts.get(accountNumber)) + " attempt(s) remaining.");
        }
        failedAttempts.remove(accountNumber);
        fileManager.saveAttempts(failedAttempts);
        return account;
    }
    public boolean authenticateAdmin(char[] pin) { return PinSecurity.verify(pin, ADMIN_PIN_HASH); }
    public synchronized boolean isLocked(String accountNumber) { return failedAttempts.getOrDefault(accountNumber, 0) >= MAX_PIN_ATTEMPTS; }
    public synchronized BankAccount getAccount(String accountNumber) {
        BankAccount account = accounts.get(accountNumber);
        if (account == null) throw new InvalidAccountException("Account number was not found.");
        return account;
    }
    public synchronized List<BankAccount> getAccounts() {
        List<BankAccount> result = new ArrayList<>(accounts.values());
        result.sort(Comparator.comparing(BankAccount::getAccountNumber));
        return result;
    }
    public synchronized List<Transaction> getTransactions(String accountNumber, int limit) {
        List<Transaction> result = new ArrayList<>();
        Iterator<Transaction> iterator = transactions.iterator();
        while (iterator.hasNext()) { // Iterator is intentional: it is a visible Core Java example.
            Transaction transaction = iterator.next();
            if (accountNumber == null || accountNumber.equals(transaction.getAccountNumber())) result.add(transaction);
        }
        result.sort(Comparator.comparing(Transaction::getTimestamp).reversed());
        return limit > 0 && result.size() > limit ? new ArrayList<>(result.subList(0, limit)) : result;
    }
    /** Overload for callers that need all records for an account. */
    public List<Transaction> getTransactions(String accountNumber) { return getTransactions(accountNumber, 0); }
    public synchronized List<Transaction> searchTransactions(String query) {
        String needle = query == null ? "" : query.trim().toLowerCase();
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            String haystack = (transaction.getAccountNumber() + " " + transaction.getType() + " " + transaction.getRelatedAccount() + " " + transaction.getDescription()).toLowerCase();
            if (haystack.contains(needle)) result.add(transaction);
        }
        result.sort(Comparator.comparing(Transaction::getTimestamp).reversed());
        return result;
    }
    public synchronized void deposit(String accountNumber, BigDecimal amount) {
        BankAccount account = getAccount(accountNumber); BigDecimal money = money(amount);
        account.deposit(money); Transaction transaction = entry(accountNumber, "DEPOSIT", money, "", "Cash deposit");
        transactions.add(transaction);
        try { saveFinancialState(); } catch (RuntimeException exception) { transactions.remove(transaction); account.withdraw(money); throw exception; }
    }
    public synchronized void withdraw(String accountNumber, BigDecimal amount) {
        BankAccount account = getAccount(accountNumber); BigDecimal money = money(amount);
        account.withdraw(money); Transaction transaction = entry(accountNumber, "WITHDRAWAL", money, "", "Cash withdrawal");
        transactions.add(transaction);
        try { saveFinancialState(); } catch (RuntimeException exception) { transactions.remove(transaction); account.deposit(money); throw exception; }
    }
    public synchronized void transfer(String fromAccount, String toAccount, BigDecimal amount) {
        if (fromAccount == null || fromAccount.equals(toAccount)) throw new IllegalArgumentException("Choose a different registered recipient account.");
        BankAccount sender = getAccount(fromAccount); BankAccount recipient = getAccount(toAccount); BigDecimal money = money(amount);
        sender.withdraw(money); recipient.deposit(money);
        Transaction debit = entry(fromAccount, "TRANSFER OUT", money, toAccount, "Transfer to " + toAccount);
        Transaction credit = entry(toAccount, "TRANSFER IN", money, fromAccount, "Transfer from " + fromAccount);
        transactions.add(debit); transactions.add(credit);
        try { saveFinancialState(); }
        catch (RuntimeException exception) { transactions.remove(debit); transactions.remove(credit); recipient.withdraw(money); sender.deposit(money); throw exception; }
    }
    public synchronized void changePin(String accountNumber, char[] oldPin, char[] newPin) {
        BankAccount account = getAccount(accountNumber);
        if (!PinSecurity.verify(oldPin, account.getPinHash())) throw new IllegalArgumentException("Current PIN is incorrect.");
        PinSecurity.validate(newPin); String previous = account.getPinHash(); account.setPinHash(PinSecurity.hash(newPin));
        try { fileManager.saveAccounts(accounts.values()); } catch (RuntimeException exception) { account.setPinHash(previous); throw exception; }
    }
    private Transaction entry(String account, String type, BigDecimal amount, String related, String description) {
        return new Transaction(UUID.randomUUID().toString(), LocalDateTime.now(), account, type, amount, related, description);
    }
    private BigDecimal money(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be greater than zero.");
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
    private void saveFinancialState() { fileManager.saveAccounts(accounts.values()); fileManager.saveTransactions(transactions); }
}
