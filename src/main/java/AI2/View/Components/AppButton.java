package AI2.View.Components;

import javax.swing.*;
import java.awt.*;

public class AppButton extends JButton {

    public AppButton(String text) {
        super(text);

        // 1. Zwiększona, pogrubiona czcionka
        setFont(new Font("Segoe UI", Font.BOLD, 14));

        // 2. Kursor w kształcie łapki po najechaniu
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 3. TO TA LINIJKA ODPOWIADA ZA DUŻY ROZMIAR! (Szerokość 120, Wysokość 40)
        setPreferredSize(new Dimension(120, 40));

        // 4. Usuwamy brzydką ramkę po kliknięciu (tzw. focus ring)
        setFocusPainted(false);

        // 5. Opcjonalnie: wymuszenie ładnych, miękkich rogów z FlatLafa
        putClientProperty("JButton.buttonType", "roundRect");
    }
}