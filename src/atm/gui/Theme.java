package atm.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Shared navy/cyan Swing styling. */
final class Theme {
    static final Color NAVY = new Color(11, 27, 52);
    static final Color PANEL = new Color(19, 44, 78);
    static final Color CYAN = new Color(35, 211, 238);
    static final Color WHITE = new Color(235, 244, 255);
    static final Font TITLE = new Font("SansSerif", Font.BOLD, 28);
    static final Font HEADING = new Font("SansSerif", Font.BOLD, 18);
    private Theme() { }
    static JPanel panel() { JPanel panel = new JPanel(); panel.setBackground(NAVY); panel.setBorder(BorderFactory.createEmptyBorder(24, 34, 24, 34)); return panel; }
    static JLabel label(String text) { JLabel label = new JLabel(text); label.setForeground(WHITE); label.setFont(new Font("SansSerif", Font.PLAIN, 14)); return label; }
    static JLabel heading(String text) { JLabel label = label(text); label.setFont(HEADING); label.setForeground(CYAN); return label; }
    static JButton button(String text) { JButton button = new JButton(text); button.setBackground(CYAN); button.setForeground(NAVY); button.setFont(new Font("SansSerif", Font.BOLD, 13)); button.setFocusPainted(false); button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); button.setPreferredSize(new Dimension(150, 34)); return button; }
    static void align(Component component) { component.setForeground(WHITE); }
}
