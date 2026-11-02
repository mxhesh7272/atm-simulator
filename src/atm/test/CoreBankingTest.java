package atm.test;

import atm.model.BankAccount;
import atm.service.ATMService;
import atm.service.Bank;
import atm.storage.FileManager;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

/** Dependency-free executable tests for the banking rules and persistence. */
public final class CoreBankingTest {
    public static void main(String[] args) throws Exception {
        Path data = Files.createTempDirectory("atm-simulator-test-");
        ATMService service = new ATMService(new Bank(new FileManager(data)));
        check(service.adminLogin("2468".toCharArray()), "administrator authentication");
        service.register("S1001", "Asha Rao", "Savings", "1234".toCharArray(), new BigDecimal("1000"), BigDecimal.ZERO);
        service.register("C1001", "Dev Shah", "Current", "5678".toCharArray(), new BigDecimal("100"), new BigDecimal("500"));
        check(service.login("S1001", "1234".toCharArray()) != null, "authentication");
        service.deposit("S1001", new BigDecimal("250.50")); service.withdraw("S1001", new BigDecimal("100.50"));
        service.transfer("S1001", "C1001", new BigDecimal("200"));
        check(service.accounts().stream().filter(a -> a.getAccountNumber().equals("S1001")).findFirst().orElseThrow().getBalance().compareTo(new BigDecimal("950.00")) == 0, "balance after deposit, withdrawal and transfer");
        boolean rejected = false; try { service.withdraw("S1001", BigDecimal.ZERO); } catch (IllegalArgumentException expected) { rejected = true; } check(rejected, "zero amount rejection");
        rejected = false; try { service.withdraw("S1001", new BigDecimal("9999")); } catch (RuntimeException expected) { rejected = true; } check(rejected, "insufficient funds rejection");
        service.changePin("S1001", "1234".toCharArray(), "4321".toCharArray()); check(service.login("S1001", "4321".toCharArray()) != null, "PIN change");
        for (int i = 0; i < 3; i++) try { service.login("C1001", "0000".toCharArray()); } catch (RuntimeException expected) { }
        check(service.isLocked("C1001"), "three-attempt lockout");
        ATMService reloaded = new ATMService(new Bank(new FileManager(data))); BankAccount saved = reloaded.login("S1001", "4321".toCharArray());
        check(saved.getBalance().compareTo(new BigDecimal("950.00")) == 0 && reloaded.transactions("S1001", 0).size() == 3, "persistence after restart");
        System.out.println("PASS: CoreBankingTest (all checks completed)");
    }
    private static void check(boolean expression, String name) { if (!expression) throw new AssertionError("Failed: " + name); }
}
