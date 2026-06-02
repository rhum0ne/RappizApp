package fr.rhumain.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

public final class AppTheme {

    public static final Color BACKGROUND = new Color(245, 247, 251);
    public static final Color CARD = Color.WHITE;
    public static final Color PRIMARY = new Color(63, 81, 181);
    public static final Color SUCCESS = new Color(46, 204, 113);
    public static final Color WARNING = new Color(245, 166, 35);
    public static final Color TEXT = new Color(45, 52, 54);
    public static final Color MUTED_TEXT = new Color(99, 110, 114);
    public static final Color BORDER = new Color(221, 226, 232);

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    private AppTheme() {
    }

    public static void stylePrimaryButton(JButton button) {
        styleButton(button, PRIMARY, Color.WHITE);
    }

    public static void styleSuccessButton(JButton button) {
        styleButton(button, SUCCESS, Color.WHITE);
    }

    public static void styleSecondaryButton(JButton button) {
        styleButton(button, CARD, PRIMARY);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY),
                new EmptyBorder(9, 18, 9, 18)
        ));
    }

    public static void styleCard(JComponent component) {
        component.setBackground(CARD);
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 16, 14, 16)
        ));
    }

    private static void styleButton(JButton button, Color background, Color foreground) {
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
    }
}
