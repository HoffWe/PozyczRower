package AI2.View.Components;

import javax.swing.*;
import java.awt.*;

public class AppButton extends JButton {

    public AppButton(String text) {

        super(text);

        setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        setFocusPainted(false);

        setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        setPreferredSize(
                new Dimension(
                        120,
                        40
                )
        );
    }
}