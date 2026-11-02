package atm;

import atm.gui.ATMFrame;
import atm.service.ATMService;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Application entry point. Run with --console-demo for the optional Core Java console exercise. */
public final class Main {
    public static void main(String[] args) {
        if (args.length > 0 && "--console-demo".equals(args[0])) { runConsoleConceptDemo(); return; }
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) { }
            ATMFrame frame = new ATMFrame(new ATMService()); frame.showLogin(); frame.setVisible(true);
        });
    }
    /* Separate from the Swing program: demonstrates Scanner, arrays, loop and switch for evaluation. */
    private static void runConsoleConceptDemo() {
        boolean[] pinAttempts = new boolean[3]; int count = 0;
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                System.out.print("Console demo: 1 record attempt, 2 show attempts, 0 exit: ");
                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1" -> { if (count < pinAttempts.length) { System.out.print("Was the sample attempt correct (y/n)? "); pinAttempts[count++] = "y".equalsIgnoreCase(scanner.nextLine().trim()); } else System.out.println("Array is full."); }
                    case "2" -> { for (int i = 0; i < count; i++) System.out.println("Attempt " + (i + 1) + ": " + (pinAttempts[i] ? "correct" : "incorrect")); }
                    case "0" -> running = false;
                    default -> System.out.println("Choose 0, 1, or 2.");
                }
            }
        }
    }
}
