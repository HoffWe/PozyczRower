package AI2.View.Abstract;

import AI2.Util.LanguageChangeListener;
import AI2.Util.LanguageManager;
import AI2.View.Components.AppButton;
import AI2.View.Components.SearchPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstrakcyjna klasa bazowa dla paneli z listą (tabelą).
 * <p>
 * Wzorzec Template Method: podklasy implementują tylko to, co specyficzne
 * (nazwy kolumn, ładowanie danych, reakcje na przyciski). Cała mechanika
 * układu, tabeli, wyszukiwarki i przycisków jest tutaj.
 */
public abstract class BaseListPanel extends JPanel implements LanguageChangeListener {

    protected DefaultTableModel tableModel;
    protected JTable table;
    protected JTextField searchField;
    protected JButton addButton;
    protected JButton editButton;
    protected JButton deleteButton;

    private JLabel titleLabel;

    /**
     * IDs odpowiadające wierszom tabeli (równoległa lista).
     * Używane do pobrania ID zaznaczonego rekordu bez wyświetlania go.
     */
    protected final List<Integer> rowIds = new ArrayList<>();

    /**
     * Konstruktor nie wywołuje {@code loadData()} – podklasa musi wywołać
     * {@code loadData()} samodzielnie na końcu swojego konstruktora,
     * po ustawieniu własnych pól (serwisów itp.).
     */
    protected BaseListPanel() {
        LanguageManager.addListener(this);
        initComponents();
        buildLayout();
        registerListeners();
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(getColumnNames(), 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        addButton = new AppButton(LanguageManager.getString("button.add"));
        editButton = new AppButton(LanguageManager.getString("button.edit"));
        deleteButton = new AppButton(LanguageManager.getString("button.delete"));
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        initExtraComponents();
    }

    /** Podklasy mogą nadpisać, aby dodać dodatkowe komponenty (np. przycisk "Zakończ"). */
    protected void initExtraComponents() {}

    private void buildLayout() {
        setLayout(new BorderLayout(15, 15));
//        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        titleLabel = new JLabel(LanguageManager.getString(getTitleKey()), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
//        centerPanel.setBackground(Color.WHITE);

        SearchPanel searchPanel = new SearchPanel();
        searchField = searchPanel.getSearchField();

        JPanel filterBar = buildFilterBar();
        if (filterBar != null) {
            JPanel topBar = new JPanel(new BorderLayout(0, 4));
//            topBar.setBackground(Color.WHITE);
            topBar.add(searchPanel, BorderLayout.NORTH);
            topBar.add(filterBar,   BorderLayout.SOUTH);
            centerPanel.add(topBar, BorderLayout.NORTH);
        } else {
            centerPanel.add(searchPanel, BorderLayout.NORTH);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
//        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buildExtraButtons(buttonPanel);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /** Podklasy mogą nadpisać, aby wstawić dodatkowe przyciski między Edit a Delete. */
    protected void buildExtraButtons(JPanel buttonPanel) {}

    /**
     * Podklasy mogą nadpisać, aby zwrócić panel filtrów wyświetlany
     * między wyszukiwarką a tabelą. Domyślnie zwraca {@code null} (brak paska filtrów).
     *
     * @return panel filtrów lub {@code null}
     */
    protected JPanel buildFilterBar() { return null; }

    /** Podklasy mogą nadpisać, aby zarejestrować listenery dla własnych przycisków. */
    protected void initExtraListeners() {}

    private void registerListeners() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean sel = table.getSelectedRow() != -1;
                editButton.setEnabled(sel);
                deleteButton.setEnabled(sel);
                onSelectionChanged(sel);
            }
        });

        initExtraListeners();

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    editButton.doClick();
                }
            }
        });

        addButton.addActionListener(e -> onAdd());
        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) onEdit(row);
        });
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) onDelete(row);
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filterTable(searchField.getText()); }
            public void removeUpdate(DocumentEvent e)  { filterTable(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) { filterTable(searchField.getText()); }
        });
    }

    /** Wywoływane, gdy zmienia się zaznaczenie. Podklasy mogą reagować na zmiany stanu przycisków. */
    protected void onSelectionChanged(boolean selected) {}

    /**
     * Zwraca ID zaznaczonego wiersza z listy rowIds.
     * @return ID lub -1 gdy nic nie zaznaczone
     */
    protected int getSelectedId() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= rowIds.size()) return -1;
        return rowIds.get(row);
    }

    /** Czyści tabelę i listę ID (przed przeładowaniem danych). */
    protected void clearTable() {
        tableModel.setRowCount(0);
        rowIds.clear();
    }

    /** Dodaje wiersz do tabeli i odpowiadające mu ID do rowIds. */
    protected void addRow(int id, Object[] rowData) {
        rowIds.add(id);
        tableModel.addRow(rowData);
    }

    /** Filtruje tabelę po tekście wyszukiwania. Podklasy nadpisują wg potrzeb. */
    protected void filterTable(String query) {
        loadData();
    }

    @Override
    public void onLanguageChanged() {
        titleLabel.setText(LanguageManager.getString(getTitleKey()));
        addButton.setText(LanguageManager.getString("button.add"));
        editButton.setText(LanguageManager.getString("button.edit"));
        deleteButton.setText(LanguageManager.getString("button.delete"));
        updateColumnHeaders();
        refreshLanguageTexts();
        loadData();
    }

    private void updateColumnHeaders() {
        String[] cols = getColumnNames();
        for (int i = 0; i < cols.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderValue(cols[i]);
        }
        table.getTableHeader().repaint();
    }

    /** Podklasy nadpisują, aby odświeżyć dodatkowe teksty zależne od języka. */
    protected void refreshLanguageTexts() {}

    protected void openDialog(String title, JPanel panel, int width, int height) {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                title,
                true
        );
        dialog.setContentPane(panel);
        dialog.setSize(width, height);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /** Klucz i18n tytułu panelu, np. "client.management". */
    protected abstract String getTitleKey();

    /** Nazwy kolumn tabeli (i18n). */
    protected abstract String[] getColumnNames();

    /** Ładuje / odświeża dane w tabeli. */
    public abstract void loadData();

    protected abstract void onAdd();
    protected abstract void onEdit(int selectedRow);
    protected abstract void onDelete(int selectedRow);
}
