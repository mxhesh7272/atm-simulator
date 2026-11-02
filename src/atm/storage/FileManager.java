package atm.storage;

import atm.exception.StorageException;
import atm.model.BankAccount;
import atm.model.CurrentAccount;
import atm.model.SavingsAccount;
import atm.model.Transaction;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Local TSV persistence with Base64 text fields and atomic replace writes. */
public class FileManager {
    private final Path dataDirectory;
    private final Path accountsFile;
    private final Path transactionsFile;
    private final Path attemptsFile;

    public FileManager() { this(Path.of("data")); }
    public FileManager(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        accountsFile = dataDirectory.resolve("accounts.txt");
        transactionsFile = dataDirectory.resolve("transactions.txt");
        attemptsFile = dataDirectory.resolve("login_attempts.txt");
        ensureFiles();
    }

    public List<BankAccount> loadAccounts() {
        List<BankAccount> accounts = new ArrayList<>();
        for (String line : readLines(accountsFile)) {
            try {
                String[] p = line.split("\\|", -1);
                if (p.length != 6) continue;
                String name = decode(p[2]);
                if ("Savings".equals(p[1])) accounts.add(new SavingsAccount(p[0], name, p[3], new BigDecimal(p[4])));
                else if ("Current".equals(p[1])) accounts.add(new CurrentAccount(p[0], name, p[3], new BigDecimal(p[4]), new BigDecimal(p[5])));
            } catch (RuntimeException ignored) { /* skip a malformed record, retain usable data */ }
        }
        return accounts;
    }

    public List<Transaction> loadTransactions() {
        List<Transaction> result = new ArrayList<>();
        for (String line : readLines(transactionsFile)) {
            try {
                String[] p = line.split("\\|", -1);
                if (p.length == 7) result.add(new Transaction(p[0], LocalDateTime.parse(p[1]), p[2], p[3],
                        new BigDecimal(p[4]), decode(p[5]), decode(p[6])));
            } catch (RuntimeException ignored) { /* skip malformed transaction record */ }
        }
        return result;
    }

    public Map<String, Integer> loadAttempts() {
        Map<String, Integer> result = new HashMap<>();
        for (String line : readLines(attemptsFile)) {
            String[] p = line.split("\\|", -1);
            try { if (p.length == 2) result.put(p[0], Integer.parseInt(p[1])); } catch (NumberFormatException ignored) { }
        }
        return result;
    }

    public void saveAccounts(Iterable<BankAccount> accounts) {
        List<String> lines = new ArrayList<>();
        for (BankAccount account : accounts) {
            String overdraft = account instanceof CurrentAccount current ? current.getOverdraftLimit().toPlainString() : "0";
            lines.add(account.getAccountNumber() + "|" + account.getAccountType() + "|" + encode(account.getCustomerName())
                    + "|" + account.getPinHash() + "|" + account.getBalance().toPlainString() + "|" + overdraft);
        }
        writeAtomically(accountsFile, lines);
    }
    public void saveTransactions(Iterable<Transaction> transactions) {
        List<String> lines = new ArrayList<>();
        for (Transaction transaction : transactions) lines.add(transaction.getId() + "|" + transaction.getTimestamp()
                + "|" + transaction.getAccountNumber() + "|" + transaction.getType() + "|" + transaction.getAmount().toPlainString()
                + "|" + encode(transaction.getRelatedAccount()) + "|" + encode(transaction.getDescription()));
        writeAtomically(transactionsFile, lines);
    }
    public void saveAttempts(Map<String, Integer> attempts) {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : attempts.entrySet()) lines.add(entry.getKey() + "|" + entry.getValue());
        writeAtomically(attemptsFile, lines);
    }
    private void ensureFiles() {
        try { Files.createDirectories(dataDirectory); for (Path file : List.of(accountsFile, transactionsFile, attemptsFile)) if (!Files.exists(file)) Files.createFile(file); }
        catch (IOException exception) { throw new StorageException("Cannot prepare the data directory.", exception); }
    }
    private List<String> readLines(Path file) { try { return Files.readAllLines(file, StandardCharsets.UTF_8); } catch (IOException exception) { throw new StorageException("Cannot read " + file.getFileName(), exception); } }
    private void writeAtomically(Path target, List<String> lines) {
        try {
            Path temp = Files.createTempFile(dataDirectory, target.getFileName().toString(), ".tmp");
            Files.write(temp, lines, StandardCharsets.UTF_8);
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException exception) { throw new StorageException("Cannot safely save " + target.getFileName(), exception); }
    }
    private String encode(String value) { return Base64.getUrlEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8)); }
    private String decode(String value) { return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8); }
}
