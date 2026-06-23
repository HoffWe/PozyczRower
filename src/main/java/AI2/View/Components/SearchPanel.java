package AI2.View.Components;

import AI2.Util.LanguageChangeListener;
import AI2.Util.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Pasek wyszukiwania z:
 * <ul>
 *   <li>ikoną lupy</li>
 *   <li>polem tekstowym z szarym placeholderem</li>
 *   <li>przyciskiem ✕ czyszczącym pole (widocznym gdy jest tekst)</li>
 * </ul>
 * Dynamiczne filtrowanie realizowane jest przez DocumentListener w
 * {@link AI2.View.Abstract.BaseListPanel} – SearchPanel jedynie udostępnia pole.
 */
public class SearchPanel extends JPanel implements LanguageChangeListener {

    private final JTextField searchField;
    private final JButton    clearButton;
    private       String     placeholder;

    public SearchPanel() {
        LanguageManager.addListener(this);
        placeholder = LanguageManager.getString("search");

        setLayout(new BorderLayout(4, 0));
//        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        // --- ikona ---
        JLabel icon = new JLabel("🔍");
        icon.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        add(icon, BorderLayout.WEST);

        // --- pole z własnym placeholder'em ---
        searchField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(Color.GRAY);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets ins = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(placeholder, ins.left + 2,
                                  ins.top + fm.getAscent());
                    g2.dispose();
                }
            }
        };
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(300, 35));

        // repaint placeholder przy zysku/stracie focusu
        searchField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { searchField.repaint(); }
            @Override public void focusLost(FocusEvent e)   { searchField.repaint(); }
        });

        // pokaż/ukryj clearButton w miarę pisania
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { updateClear(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { updateClear(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateClear(); }
        });

        add(searchField, BorderLayout.CENTER);

        // --- przycisk czyszczenia ---
        clearButton = new JButton("✕");
        clearButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        clearButton.setFocusPainted(false);
        clearButton.setBorderPainted(false);
        clearButton.setContentAreaFilled(false);
        clearButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearButton.setForeground(Color.GRAY);
        clearButton.setVisible(false);
        clearButton.setPreferredSize(new Dimension(30, 35));
        clearButton.addActionListener(e -> {
            searchField.setText("");
            searchField.requestFocusInWindow();
        });
        add(clearButton, BorderLayout.EAST);
    }

    private void updateClear() {
        clearButton.setVisible(!searchField.getText().isEmpty());
    }

    /** Zwraca referencję do pola tekstowego (używana przez BaseListPanel). */
    public JTextField getSearchField() {
        return searchField;
    }

    /** Czyści pole wyszukiwania programowo. */
    public void clear() {
        searchField.setText("");
    }

    @Override
    public void onLanguageChanged() {
        placeholder = LanguageManager.getString("search");
        searchField.repaint();
    }
}
