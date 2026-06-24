package AI2.View.Client;

import AI2.Enums.RentStatus;
import AI2.Model.Bike;
import AI2.Model.BikeModel;
import AI2.Model.Client;
import AI2.Model.Rent;
import AI2.Service.BikeModelService;
import AI2.Service.BikeService;
import AI2.Service.BikeTypeService;
import AI2.Service.ClientService;
import AI2.Service.RentService;
import AI2.Util.LanguageManager;
import AI2.View.Components.AppButton;
import AI2.View.Rent.AddRentPanel;
import AI2.View.Rent.EditRentPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * Okno z lista wypozyczen wybranego klienta.
 *
 *
 * @author Światosław Matsopa
 *
 *
 */
public class ClientRentsDialog extends JDialog {

    /** Format wyswietlania daty w tabeli */
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Statusy oznaczajace zakonczone wypozyczenie */
    private static final Set<RentStatus> FINISHED_STATUSES =
            Set.of(RentStatus.FINISHED);

    /** Kolor dla przeterminowanych wierszy. */
    private static final Color COLOR_OVERDUE = Color.RED;

    /** Kolor dla aktywnych i oczekujacych wierszy. */
    private static final Color COLOR_GREEN = new Color(0, 150, 0);

    private final RentService      rentService;
    private final BikeService      bikeService;
    private final BikeModelService bikeModelService;
    private final BikeTypeService  bikeTypeService;
    private final ClientService    clientService;
    private final Client           client;

    private List<Rent>              allRents;
    private final DefaultTableModel tableModel;
    private final List<Integer>     rowIds      = new ArrayList<>();
    private final List<Integer>     overdueRows = new ArrayList<>();
    private final List<Integer>     greenRows   = new ArrayList<>();

    private JTable table;
    private JButton addButton;
    private JButton editButton;
    private JButton confirmButton;
    private JButton cancelButton;
    private JButton endButton;
    private JCheckBox showFinishedBox;

    /**
     * Tworzy dialog wyswietlajacy wypozyczenia klienta.
     *
     * @param owner okno nadrzedne
     * @param client klient ktorego wypozyczenia wyswietlamy
     * @param rentService serwis wypozyczen
     * @param bikeService serwis rowerow
     * @param bikeModelService serwis modeli rowerow
     * @param bikeTypeService serwis typow rowerow
     * @param clientService serwis klientow
     * @author Światosław Matsopa
     */
    public ClientRentsDialog(Window owner, Client client,
                             RentService rentService,
                             BikeService bikeService,
                             BikeModelService bikeModelService,
                             BikeTypeService bikeTypeService,
                             ClientService clientService) {
        super(owner,
              LanguageManager.getString("client.rents")
                      + ": " + client.getName() + " " + client.getSurname(),
              ModalityType.APPLICATION_MODAL);

        this.client= client;
        this.rentService= rentService;
        this.bikeService= bikeService;
        this.bikeModelService= bikeModelService;
        this.bikeTypeService= bikeTypeService;
        this.clientService= clientService;

        tableModel = new DefaultTableModel(new String[]{
                LanguageManager.getString("bike.name"),
                LanguageManager.getString("date.startDate"),
                LanguageManager.getString("date.endDate"),
                LanguageManager.getString("rent.status.name")
        }, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if (overdueRows.contains(row)) c.setForeground(COLOR_OVERDUE);
                    else if (greenRows.contains(row)) c.setForeground(COLOR_GREEN);
                    else c.setForeground(t.getForeground());
                }
                return c;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtonStates();
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) onEdit();
            }
        });

        showFinishedBox = new JCheckBox(
                LanguageManager.getString("client.rents.showFinished"), false);
        showFinishedBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showFinishedBox.setBackground(Color.WHITE);
        showFinishedBox.addActionListener(e -> rebuildTable(showFinishedBox.isSelected()));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(showFinishedBox);

        addButton = new AppButton(LanguageManager.getString("button.add"));
        editButton = new AppButton(LanguageManager.getString("button.edit"));
        confirmButton = new AppButton(LanguageManager.getString("button.confirm"));
        cancelButton = new AppButton(LanguageManager.getString("button.cancel"));
        endButton = new AppButton(LanguageManager.getString("button.end"));

        editButton.setEnabled(false);
        confirmButton.setEnabled(false);
        cancelButton.setEnabled(false);
        endButton.setEnabled(false);

        addButton.addActionListener(e -> onAdd());
        editButton.addActionListener(e -> onEdit());
        confirmButton.addActionListener(e -> onConfirm());
        cancelButton.addActionListener(e -> onCancel());
        endButton.addActionListener(e -> onEnd());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(endButton);

        setLayout(new BorderLayout(8, 8));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(topPanel,               BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel,            BorderLayout.SOUTH);

        rebuildTable(false);

        setSize(820, 460);
        setResizable(true);
        setLocationRelativeTo(owner);
    }
    /**
     * Aktualizuje stan przycisków na podstawie statusu zaznaczonego wiersza.
     * Brak zaznaczenia wyłącza wszystkie przyciski akcji.
     *
     * @author Rafał Wojciechowski
     */
    private void updateButtonStates() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= rowIds.size()) {
            editButton.setEnabled(false);
            confirmButton.setEnabled(false);
            cancelButton.setEnabled(false);
            endButton.setEnabled(false);
            return;
        }
        try {
            Rent rent = rentService.getRentByID(rowIds.get(row));
            RentStatus s = rent.getStatus();
            editButton.setEnabled(true);
            confirmButton.setEnabled(s == RentStatus.PENDING);
            cancelButton.setEnabled(s == RentStatus.PENDING || s == RentStatus.SCHEDULED);
            endButton.setEnabled(s == RentStatus.ACTIVE || s == RentStatus.OVERDUE
                    || s == RentStatus.PENDING);
        } catch (Exception ex) {
            editButton.setEnabled(false);
            confirmButton.setEnabled(false);
            cancelButton.setEnabled(false);
            endButton.setEnabled(false);
        }
    }

    /** Otwiera formularz dodawania nowego wypozyczenia dla tego klienta */
    private void onAdd() {
        JDialog dialog = new JDialog(this,
                LanguageManager.getString("rent.nameAdd"), true);
        AddRentPanel panel = new AddRentPanel(
                rentService, clientService, bikeService, bikeModelService, bikeTypeService,
                () -> reloadAndRebuild(showFinishedBox.isSelected()), client);
        dialog.setContentPane(panel);
        dialog.setSize(580, 600);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Otwiera formularz edycji zaznaczonego wypożyczenia.
     * Nie robi nic gdy żaden wiersz nie jest zaznaczony.
     *
     * @author Tomasz Piłat
     */
    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= rowIds.size()) return;

        Rent rent;
        try {
            rent = rentService.getRentByID(rowIds.get(row));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this,
                LanguageManager.getString("rent.editTitle"), true);
        EditRentPanel panel = new EditRentPanel(
                rentService, bikeService, bikeModelService, bikeTypeService,
                () -> reloadAndRebuild(showFinishedBox.isSelected()), rent);
        dialog.setContentPane(panel);
        dialog.setSize(560, 510);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Potwierdza zaznaczone wypozyczenie - zmienia status PENDING na ACTIVE.
     *
     * @author Tomasz Piłat
     */
    private void onConfirm() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= rowIds.size()) return;
        try {
            rentService.confirmRent(rowIds.get(row));
            reloadAndRebuild(showFinishedBox.isSelected());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Anuluje zaznaczone wypozyczenie po potwierdzeniu przez uzytkownika */
    private void onCancel() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= rowIds.size()) return;
        int result = JOptionPane.showConfirmDialog(this,
                LanguageManager.getString("rent.cancelConfirm"),
                LanguageManager.getString("button.cancel"),
                JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.YES_OPTION) return;
        try {
            rentService.cancelRent(rowIds.get(row));
            reloadAndRebuild(showFinishedBox.isSelected());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Kończy wypożyczenie – pyta o uwagi, ustawia status FINISHED i zwalnia rower.
     * Wywołanie bez zaznaczonego wiersza nie powoduje żadnego efektu.
     *
     * @throws RuntimeException jeśli wypożyczenie nie istnieje lub jest już zakończone
     * @author Tomasz Piłat
     */
    private void onEnd() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= rowIds.size()) return;
        String notes = JOptionPane.showInputDialog(this,
                LanguageManager.getString("rent.returnNotes"),
                LanguageManager.getString("button.end"),
                JOptionPane.PLAIN_MESSAGE);
        if (notes == null) return;
        try {
            rentService.endRent(rowIds.get(row), notes);
            reloadAndRebuild(showFinishedBox.isSelected());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Pobiera swieże dane z serwisu i przebudowuje tabele */
    private void reloadAndRebuild(boolean showFinished) {
        allRents = rentService.findClientRents(client.getId());
        rebuildTable(showFinished);
    }

    /**
     * Przebudowuje zawartość tabeli na podstawie listy wypożyczeń.
     * Koloruje wiersze OVERDUE na czerwono, PENDING na zielono.
     * Resetuje stan przycisków po każdej przebudowie.
     *
     * @param showFinished {@code true} – wyświetla wszystkie statusy;
     *                     {@code false} – ukrywa zakończone ({@link RentStatus#FINISHED})
     * @author Rafał Wojciechowski
     */
    private void rebuildTable(boolean showFinished) {
        if (allRents == null) {
            allRents = rentService.findClientRents(client.getId());
        }

        tableModel.setRowCount(0);
        rowIds.clear();
        overdueRows.clear();
        greenRows.clear();

        List<Rent> visible = allRents.stream()
                .filter(r -> showFinished || !FINISHED_STATUSES.contains(r.getStatus()))
                .collect(Collectors.toList());

        int rowIdx = 0;
        for (Rent r : visible) {
            RentStatus s = r.getStatus();
            if (s == RentStatus.OVERDUE)                            overdueRows.add(rowIdx);
            if (s == RentStatus.PENDING)  greenRows.add(rowIdx);

            String bikeInfo = "ID:" + r.getBikeId();
            try {
                Bike b = bikeService.getBikeById(r.getBikeId());
                if (b != null && bikeModelService != null) {
                    BikeModel bm = bikeModelService.getBikeModelById(b.getBikeModelId());
                    if (bm != null) {
                        bikeInfo = bm.getBrand() + " " + bm.getModel()
                                + "  " + b.getWheelSize() + "\"";
                    }
                }
            } catch (Exception ignored) {}
            rowIds.add(r.getId());
            tableModel.addRow(new Object[]{
                    bikeInfo,
                    r.getRentDate()   != null ? r.getRentDate().format(FMT)   : "-",
                    r.getReturnTime() != null ? r.getReturnTime().format(FMT) : "-",
                    s != null ? s.getDisplayName() : "-"
            });
            rowIdx++;
        }
        updateButtonStates();
    }
}
