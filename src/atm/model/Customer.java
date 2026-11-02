package atm.model;

/** Small value object retained separately from account credentials. */
public final class Customer {
    private final String name;
    public Customer(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Customer name is required.");
        this.name = name.trim();
    }
    public String getName() { return name; }
}
