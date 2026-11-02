package atm.gui;

import atm.model.BankAccount;
import atm.model.Transaction;
import atm.service.ATMService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public final class DashboardFrame extends JPanel {
    private static final long serialVersionUID = 1L;
    private final ATMFrame frame; private final ATMService service; private BankAccount account;
    private final JLabel welcome = Theme.heading(""), balance = Theme.heading("");
    public DashboardFrame(ATMFrame frame, ATMService service) {
        this.frame = frame; this.service = service; setLayout(new BorderLayout(12, 12)); setBackground(Theme.NAVY); setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 28, 22, 28));
        JPanel top = new JPanel(new GridLayout(2, 1)); top.setBackground(Theme.PANEL); top.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 18, 16, 18)); top.add(welcome); top.add(balance); add(top, BorderLayout.NORTH);
        JPanel actions = new JPanel(new GridLayout(3, 2, 14, 14)); actions.setBackground(Theme.NAVY);
        action(actions, "DEPOSIT", () -> frame.showTransaction("DEPOSIT", account)); action(actions, "WITHDRAW", () -> frame.showTransaction("WITHDRAW", account));
        action(actions, "TRANSFER", () -> frame.showTransaction("TRANSFER", account)); action(actions, "MINI STATEMENT", () -> statement(5, "Mini statement"));
        action(actions, "FULL HISTORY", () -> statement(0, "Transaction history")); action(actions, "CHANGE PIN", this::changePin);
        add(actions, BorderLayout.CENTER); JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bottom.setBackground(Theme.NAVY); JButton logout = Theme.button("LOGOUT"); logout.addActionListener(event -> frame.showLogin()); bottom.add(logout); JButton exit = Theme.button("EXIT"); exit.addActionListener(event -> frame.safeExit()); bottom.add(exit); add(bottom, BorderLayout.SOUTH);
    }
    private void action(JPanel panel, String text, Runnable task) { JButton button = Theme.button(text); button.addActionListener(event -> task.run()); panel.add(button); }
    public void setAccount(BankAccount account) { this.account = account; refresh(); }
    public void refresh() { if (account != null) { welcome.setText("Welcome, " + account.getCustomerName() + "  |  " + account.getAccountType() + " account #" + account.getAccountNumber()); balance.setText("Available balance: ₹ " + account.getBalance().setScale(2)); } }
    private void statement(int limit, String title) {
        List<Transaction> entries = service.transactions(account.getAccountNumber(), limit); DefaultTableModel model = tableModel(entries); JTable table = new JTable(model);
        JDialog dialog = new JDialog(frame, title, true); dialog.add(new JScrollPane(table)); dialog.setSize(820, 300); dialog.setLocationRelativeTo(frame); dialog.setVisible(true);
    }
    private DefaultTableModel tableModel(List<Transaction> entries) { DefaultTableModel model = new DefaultTableModel(new String[] { "Date", "Type", "Amount", "Related account", "Description" }, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } }; for (Transaction entry : entries) model.addRow(new Object[] { entry.getDisplayDate(), entry.getType(), "₹ " + entry.getAmount().setScale(2), entry.getRelatedAccount(), entry.getDescription() }); return model; }
    private void changePin() {
        JPasswordField oldPin = new JPasswordField(), newPin = new JPasswordField(), confirm = new JPasswordField(); Object[] fields = { "Current PIN", oldPin, "New PIN", newPin, "Confirm new PIN", confirm };
        if (javax.swing.JOptionPane.showConfirmDialog(this, fields, "Change PIN", javax.swing.JOptionPane.OK_CANCEL_OPTION) == javax.swing.JOptionPane.OK_OPTION) try {
            if (!java.util.Arrays.equals(newPin.getPassword(), confirm.getPassword())) throw new IllegalArgumentException("New PIN confirmation does not match.");
            service.changePin(account.getAccountNumber(), oldPin.getPassword(), newPin.getPassword()); javax.swing.JOptionPane.showMessageDialog(this, "PIN changed successfully.");
        } catch (RuntimeException exception) { javax.swing.JOptionPane.showMessageDialog(this, exception.getMessage(), "Change PIN", javax.swing.JOptionPane.ERROR_MESSAGE); }
    }
}
