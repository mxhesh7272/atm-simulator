package atm.gui;

import atm.model.BankAccount;
import atm.model.Transaction;
import atm.service.ATMService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public final class AdminFrame extends JPanel {
    private static final long serialVersionUID = 1L;
    private final ATMService service; private final JTextField accountSearch = new JTextField(18), transactionSearch = new JTextField(18);
    private final DefaultTableModel accounts = locked(new String[] { "Account", "Customer", "Type", "Balance", "Status" }); private final DefaultTableModel transactions = locked(new String[] { "Date", "Account", "Type", "Amount", "Related", "Description" });
    public AdminFrame(ATMFrame frame, ATMService service) {
        this.service = service; setLayout(new BorderLayout()); setBackground(Theme.NAVY); JPanel heading = new JPanel(new FlowLayout(FlowLayout.LEFT)); heading.setBackground(Theme.PANEL); heading.add(Theme.heading("Administrator dashboard — PINs are never displayed")); JButton logout = Theme.button("LOGOUT"); logout.addActionListener(event -> frame.showLogin()); heading.add(logout); add(heading, BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane(); tabs.addTab("Accounts", accountPanel()); tabs.addTab("Transactions", transactionPanel()); add(tabs, BorderLayout.CENTER);
    }
    private JPanel accountPanel() { JPanel panel = new JPanel(new BorderLayout(8, 8)); JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT)); filter.add(new JLabel("Search account/name:")); filter.add(accountSearch); JButton go = new JButton("Search"); go.addActionListener(event -> refreshAccounts()); filter.add(go); panel.add(filter, BorderLayout.NORTH); panel.add(new JScrollPane(new JTable(accounts)), BorderLayout.CENTER); return panel; }
    private JPanel transactionPanel() { JPanel panel = new JPanel(new BorderLayout(8, 8)); JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT)); filter.add(new JLabel("Search account/type/details:")); filter.add(transactionSearch); JButton go = new JButton("Search"); go.addActionListener(event -> refreshTransactions()); filter.add(go); panel.add(filter, BorderLayout.NORTH); panel.add(new JScrollPane(new JTable(transactions)), BorderLayout.CENTER); return panel; }
    private DefaultTableModel locked(String[] columns) { return new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } }; }
    public void refresh() { accountSearch.setText(""); transactionSearch.setText(""); refreshAccounts(); refreshTransactions(); }
    private void refreshAccounts() { accounts.setRowCount(0); String query = accountSearch.getText().trim().toLowerCase(); for (BankAccount account : service.accounts()) if ((account.getAccountNumber() + " " + account.getCustomerName()).toLowerCase().contains(query)) accounts.addRow(new Object[] { account.getAccountNumber(), account.getCustomerName(), account.getAccountType(), "₹ " + account.getBalance().setScale(2), service.isLocked(account.getAccountNumber()) ? "Locked" : "Active" }); }
    private void refreshTransactions() { transactions.setRowCount(0); List<Transaction> result = service.searchTransactions(transactionSearch.getText()); for (Transaction entry : result) transactions.addRow(new Object[] { entry.getDisplayDate(), entry.getAccountNumber(), entry.getType(), "₹ " + entry.getAmount().setScale(2), entry.getRelatedAccount(), entry.getDescription() }); }
}
