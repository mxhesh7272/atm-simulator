package atm.gui;

import atm.model.BankAccount;
import atm.service.ATMService;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public final class LoginFrame extends JPanel {
    private static final long serialVersionUID = 1L;
    private final ATMFrame frame; private final ATMService service;
    private final JTextField number = new JTextField(20); private final JPasswordField pin = new JPasswordField(20);
    public LoginFrame(ATMFrame frame, ATMService service) {
        this.frame = frame; this.service = service; setLayout(new BorderLayout()); setBackground(Theme.NAVY);
        JPanel form = Theme.panel(); form.setLayout(new GridBagLayout()); GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(8, 8, 8, 8); c.fill = GridBagConstraints.HORIZONTAL;
        JLabel title = Theme.heading("JAVA ATM BANK"); title.setFont(Theme.TITLE); title.setHorizontalAlignment(SwingConstants.CENTER); c.gridx = 0; c.gridy = 0; c.gridwidth = 2; form.add(title, c);
        c.gridy++; form.add(Theme.label("Secure educational banking simulator"), c); c.gridwidth = 1;
        c.gridy++; c.gridx = 0; form.add(Theme.label("Account number"), c); c.gridx = 1; form.add(number, c);
        c.gridy++; c.gridx = 0; form.add(Theme.label("PIN"), c); c.gridx = 1; form.add(pin, c);
        JButton customer = Theme.button("CUSTOMER LOGIN"); customer.addActionListener(event -> customerLogin()); c.gridy++; c.gridx = 0; c.gridwidth = 2; form.add(customer, c);
        JButton admin = Theme.button("ADMIN LOGIN"); admin.addActionListener(event -> adminLogin()); c.gridy++; form.add(admin, c);
        JButton register = Theme.button("CREATE ACCOUNT"); register.addActionListener(event -> frame.showRegistration()); c.gridy++; form.add(register, c);
        add(form, BorderLayout.CENTER);
    }
    private void customerLogin() { try { BankAccount account = service.login(number.getText().trim(), pin.getPassword()); if (account == null) throw new IllegalArgumentException("Account number was not found."); frame.showCustomer(account); } catch (RuntimeException exception) { showError(exception.getMessage() == null ? "Login was not successful." : exception.getMessage()); } finally { Arrays.fill(pin.getPassword(), '\0'); pin.setText(""); } }
    private void adminLogin() { if (service.adminLogin(pin.getPassword())) frame.showAdmin(); else showError("Invalid administrator PIN."); pin.setText(""); }
    private void showError(String message) { javax.swing.JOptionPane.showMessageDialog(this, message, "Login", javax.swing.JOptionPane.ERROR_MESSAGE); }
    public void clear() { number.setText(""); pin.setText(""); }
}
