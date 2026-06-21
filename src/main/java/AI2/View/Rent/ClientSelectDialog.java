package AI2.View.Rent;

import AI2.Model.Client;
import AI2.Service.ClientService;
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
import java.util.stream.Collectors;

/**
 * Okno dialogowe wyboru klienta z wyszukiwarką.
 * Filtruje po imieniu, nazwisku i numerze dowodu w czasie rzeczywistym.
 *
 * @author Tomasz Piłat
 */
public class ClientSelectDialog extends JDialog {

    /** Wybrany klient (null = anulowano). */
    private Client selectedClient = null;

    /** Pełna lista klientów (niezmodyfikowana). */
    private final List<Client> allClients;

    /** Lista klientów aktualnie widocznych w tabeli (po filtrze). */
    private final List<Client> visibleClients = new ArrayList<>();

    /** Tabela wyświetlająca klientów. */
    private final JTable table;

    /** Model tabeli. */
    private final DefaultTableModel tableModel;

    /**
     * Tworzy okno dialogowe wyboru klienta.
     *
     * @param owner         okno nadrzędne
     * @param clientService serwis klientów
     * @author Tomasz Piłat
     */
    public ClientSelectDialog(Window owner, ClientService clientService) {
        super(owner, LanguageManager.getString("dialog.select.client"),
                ModalityType.APPLICATION_MODAL);

        allClients = clientService.getAllClients();

        tableModel = new DefaultTableModel(new String[]{
                LanguageManager.getString("client.firstName"),
                LanguageManager.getString("client.lastName"),
                LanguageManager.getString("client.evidence")
        }, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) confirmSelection();
            }
        });

        // Wyszukiwarka
        SearchPanel searchPanel = new SearchPanel();
        JTextField searchField  = searchPanel.getSearchField();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filter(searchField.getText()); }
            public void removeUpdate(DocumentEvent e)  { filter(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) { filter(searchField.getText()); }
        });

        JButton selectButton = new AppButton(LanguageManager.getString("button.select"));
        selectButton.addActionListener(e -> confirmSelection());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(selectButton);

        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(searchPanel,           BorderLayout.NORTH);
        add(new JScrollPane(table),BorderLayout.CENTER);
        add(south,                 BorderLayout.SOUTH);

        // Załaduj pełną listę
        filter("");

        setSize(460, 400);
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    /**
     * Filtruje tabelę po zadanym zapytaniu (imię, nazwisko lub numer dowodu).
     *
     * @param query tekst wyszukiwania
     * @author Tomasz Piłat
     */
    private void filter(String query) {
        String q = query.trim().toLowerCase();
        tableModel.setRowCount(0);
        visibleClients.clear();

        List<Client> filtered = q.isEmpty() ? allClients : allClients.stream()
                .filter(c ->
                        contains(c.getName(),     q) ||
                        contains(c.getSurname(),  q) ||
                        contains(c.getEvidence(), q))
                .collect(Collectors.toList());

        for (Client c : filtered) {
            visibleClients.add(c);
            tableModel.addRow(new Object[]{ c.getName(), c.getSurname(), c.getEvidence() });
        }
    }

    private static boolean contains(String field, String q) {
        return field != null && field.toLowerCase().contains(q);
    }

    private void confirmSelection() {
        int row = table.getSelectedRow();
        if (row >= 0 && row < visibleClients.size()) {
            selectedClient = visibleClients.get(row);
            dispose();
        }
    }

    /** Zwraca wybranego klienta lub {@code null} jeśli anulowano. */
    public Client getSelectedClient() { return selectedClient; }
}
