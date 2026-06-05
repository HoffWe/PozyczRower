package AI2.View.Components;

import javax.swing.*;
import java.awt.*;

public class SearchPanel extends JPanel {

    private final JTextField searchField;

    public SearchPanel() {

        setLayout(
                new FlowLayout(
                        FlowLayout.LEFT
                )
        );

        setBackground(
                Color.WHITE
        );

        JLabel searchIcon =
                new JLabel("🔍");

        searchField =
                new JTextField(25);

        searchField.setPreferredSize(
                new Dimension(
                        250,
                        35
                )
        );

        add(searchIcon);

        add(searchField);
    }

    public JTextField getSearchField() {

        return searchField;
    }
}