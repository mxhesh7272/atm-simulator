package atm.gui;

import atm.model.BankAccount;
import atm.service.ATMService;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public final class TransactionFrame extends JPanel {
    private static final long serialVersionUID = 1L;
    private final ATMFrame frame; private final ATMService service; private BankAccount account; private String operation;
    private final JLabel title = Theme.heading(""), recipientLabel = Theme.label("Recipient account");
    private final JTextField amount = new JTextField(18), recipient = new JTextField(18);
    public TransactionFrame(ATMFrame frame, ATMService service) {
        this.frame = frame; this.service = service; setLayout(new BorderLayout()); setBackground(Theme.NAVY); JPanel form = Theme.panel(); form.setLayout(new GridBagLayout()); GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(9, 8, 9, 8); c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; form.add(title, c); c.gridwidth = 1; c.gridy++; c.gridx = 0; form.add(Theme.label("Amount (₹)"), c); c.gridx = 1; form.add(amount, c); c.gridy++; c.gridx = 0; form.add(recipientLabel, c); c.gridx = 1; form.add(recipient, c);
        JButton submit = Theme.button("CONFIRM"); submit.addActionListener(event -> submit()); c.gridy++; c.gridx = 0; c.gridwidth = 2; form.add(submit, c); JButton back = Theme.button("CANCEL"); back.addActionListener(event -> frame.showCustomerDashboard()); c.gridy++; form.add(back, c); add(form, BorderLayout.CENTER);
    }
    public void configure(String operation, BankAccount account) { this.operation = operation; this.account = account; title.setText(operation + " — " + account.getAccountNumber()); recipientLabel.setVisible("TRANSFER".equals(operation)); recipient.setVisible("TRANSFER".equals(operation)); amount.setText(""); recipient.setText(""); }
    private void submit() { try { BigDecimal value = new BigDecimal(amount.getText().trim()); switch (operation) { case "DEPOSIT" -> service.deposit(account.getAccountNumber(), value); case "WITHDRAW" -> service.withdraw(account.getAccountNumber(), value); case "TRANSFER" -> service.transfer(account.getAccountNumber(), recipient.getText().trim(), value); default -> throw new IllegalStateException("Unknown transaction action."); } javax.swing.JOptionPane.showMessageDialog(this, operation + " completed successfully."); frame.showCustomer(account); } catch (RuntimeException exception) { javax.swing.JOptionPane.showMessageDialog(this, exception.getMessage(), operation, javax.swing.JOptionPane.ERROR_MESSAGE); } }
}
