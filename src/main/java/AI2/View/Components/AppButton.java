package AI2.View.Components;

import javax.swing.*;
import java.awt.*;

public class AppButton extends JButton {


    public AppButton(String text) {
        super(text);
    }

    public AppButton(String text, Color actionColor) {
        super(text);
        styleOutlinedButton(actionColor);
    }

    private void styleOutlinedButton(Color color) {

        setFont(new Font("Segoe UI", Font.BOLD, 14));

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setPreferredSize(new Dimension(120, 40));

        setForeground(color);

        setBackground(new Color(0, 0, 0, 0));
        setOpaque(false);
        setFocusPainted(false);

        putClientProperty("JButton.buttonType", "roundRect");
        putClientProperty("JButton.borderColor", color);
        putClientProperty("JButton.focusedBorderColor", color);
        putClientProperty("JButton.hoverBorderColor", color.darker());
    }
}