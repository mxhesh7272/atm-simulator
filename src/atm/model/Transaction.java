package atm.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Immutable audit entry. Amounts are positive; direction is recorded by type. */
public final class Transaction {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd MMM uuuu HH:mm");
    private final String id;
    private final LocalDateTime timestamp;
    private final String accountNumber;
    private final String type;
    private final BigDecimal amount;
    private final String relatedAccount;
    private final String description;
    public Transaction(String id, LocalDateTime timestamp, String accountNumber, String type,
            BigDecimal amount, String relatedAccount, String description) {
        this.id = id; this.timestamp = timestamp; this.accountNumber = accountNumber; this.type = type;
        this.amount = amount; this.relatedAccount = relatedAccount == null ? "" : relatedAccount;
        this.description = description == null ? "" : description;
    }
    public String getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getAccountNumber() { return accountNumber; }
    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getRelatedAccount() { return relatedAccount; }
    public String getDescription() { return description; }
    public String getDisplayDate() { return timestamp.format(DISPLAY_FORMAT); }
}
