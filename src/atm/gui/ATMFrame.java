package atm.gui;

import atm.model.BankAccount;
import atm.service.ATMService;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/** Main application window using CardLayout rather than opening many windows. */
public final class ATMFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final ATMService service;
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final LoginFrame login;
    private final RegisterFrame registration;
    private final DashboardFrame dashboard;
    private final TransactionFrame transaction;
    private final AdminFrame admin;
    public ATMFrame(ATMService service) {
        super("Java ATM Simulator");
        this.service = service;
        login = new LoginFrame(this, service); registration = new RegisterFrame(this, service);
        dashboard = new DashboardFrame(this, service); transaction = new TransactionFrame(this, service);
        admin = new AdminFrame(this, service);
        root.add(login, "LOGIN"); root.add(registration, "REGISTER"); root.add(dashboard, "DASHBOARD");
        root.add(transaction, "TRANSACTION"); root.add(admin, "ADMIN");
        setContentPane(root); setSize(940, 620); setMinimumSize(getSize()); setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() { @Override public void windowClosing(java.awt.event.WindowEvent event) { safeExit(); } });
    }
    public void showLogin() { login.clear(); cards.show(root, "LOGIN"); }
    public void showRegistration() { registration.clear(); cards.show(root, "REGISTER"); }
    public void showCustomer(BankAccount account) { dashboard.setAccount(account); cards.show(root, "DASHBOARD"); }
    public void showTransaction(String operation, BankAccount account) { transaction.configure(operation, account); cards.show(root, "TRANSACTION"); }
    public void showAdmin() { admin.refresh(); cards.show(root, "ADMIN"); }
    public void showCustomerDashboard() { cards.show(root, "DASHBOARD"); }
    public ATMService service() { return service; }
    public void safeExit() { if (javax.swing.JOptionPane.showConfirmDialog(this, "Exit the ATM simulator?", "Confirm exit", javax.swing.JOptionPane.YES_NO_OPTION) == javax.swing.JOptionPane.YES_OPTION) dispose(); }
}
