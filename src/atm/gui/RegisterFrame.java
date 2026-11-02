package atm.gui;

import atm.service.ATMService;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public final class RegisterFrame extends JPanel {
    private static final long serialVersionUID = 1L;
    private final ATMFrame frame; private final ATMService service;
    private final JTextField name = new JTextField(18), number = new JTextField(18), opening = new JTextField("0.00", 18), overdraft = new JTextField("0.00", 18);
    private final JPasswordField pin = new JPasswordField(18), confirm = new JPasswordField(18);
    private final JComboBox<String> type = new JComboBox<>(new String[] { "Savings", "Current" });
    public RegisterFrame(ATMFrame frame, ATMService service) {
        this.frame = frame; this.service = service; setLayout(new BorderLayout()); setBackground(Theme.NAVY);
        JPanel form = Theme.panel(); form.setLayout(new GridBagLayout()); GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(6, 8, 6, 8); c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2; c.gridx = 0; c.gridy = 0; form.add(Theme.heading("Open a new account"), c); c.gridwidth = 1;
        addRow(form, c, "Full name", name); addRow(form, c, "Account number", number); addRow(form, c, "Account type", type); addRow(form, c, "Opening balance", opening); addRow(form, c, "Overdraft (current only)", overdraft); addRow(form, c, "Choose PIN", pin); addRow(form, c, "Confirm PIN", confirm);
        JButton create = Theme.button("CREATE ACCOUNT"); create.addActionListener(event -> create()); c.gridy++; c.gridx = 0; c.gridwidth = 2; form.add(create, c);
        JButton back = Theme.button("BACK TO LOGIN"); back.addActionListener(event -> frame.showLogin()); c.gridy++; form.add(back, c); add(form, BorderLayout.CENTER);
    }
    private void addRow(JPanel panel, GridBagConstraints c, String label, java.awt.Component field) { c.gridy++; c.gridx = 0; panel.add(Theme.label(label), c); c.gridx = 1; panel.add(field, c); }
    private void create() {
        try {
            if (!java.util.Arrays.equals(pin.getPassword(), confirm.getPassword())) throw new IllegalArgumentException("The PIN confirmation does not match.");
            service.register(number.getText().trim(), name.getText().trim(), (String) type.getSelectedItem(), pin.getPassword(), new BigDecimal(opening.getText().trim()), new BigDecimal(overdraft.getText().trim()));
            javax.swing.JOptionPane.showMessageDialog(this, "Account created. You can now log in.", "Registration", javax.swing.JOptionPane.INFORMATION_MESSAGE); frame.showLogin();
        } catch (RuntimeException exception) { javax.swing.JOptionPane.showMessageDialog(this, exception.getMessage(), "Registration", javax.swing.JOptionPane.ERROR_MESSAGE); }
    }
    public void clear() { name.setText(""); number.setText(""); opening.setText("0.00"); overdraft.setText("0.00"); pin.setText(""); confirm.setText(""); type.setSelectedIndex(0); }
}
