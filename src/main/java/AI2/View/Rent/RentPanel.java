package AI2.View.Rent;

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
import AI2.View.Abstract.BaseListPanel;
import AI2.View.Components.AppButton;
import AI2.View.ViewModel.RentViewModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Panel zarządzania wypożyczeniami.
 * Dane pobierane z serwisu. ID NIE jest wyświetlane w tabeli.
 * Co {@value #STATUS_CHECK_INTERVAL_MS} ms automatycznie sprawdzany jest status
 * wypożyczeń – przeterminowane wskakują na górę i są zaznaczane na czerwono,
 * oczekujące na zielono – bez konieczności ręcznego odświeżania.
 *
 * @author Tomasz Piłat
 */
public class RentPanel extends BaseListPanel {

    /** Interwał automatycznego sprawdzania statusów wypożyczeń (w milisekundach). */
    private static final int STATUS_CHECK_INTERVAL_MS = 60_000;

    /** Sentinel – "brak filtra statusu". */
    private static final Object STATUS_ALL = "ALL";

    /** Kolor zielony dla wierszy PENDING. */
    private static final Color COLOR_PENDING = new Color(0, 150, 0);

    // ----------------------------------------------------------------
    // Serwisy
    // ----------------------------------------------------------------

    private final RentService rentService;
    private final ClientService clientService;
    private final BikeService bikeService;
    private final BikeModelService bikeModelService;
    private final BikeTypeService bikeTypeService;

    // ----------------------------------------------------------------
    // Komponenty
    // ----------------------------------------------------------------

    /** Przycisk zakończenia wypożyczenia. */
    private JButton endButton;

    /** Przycisk potwierdzenia wypożyczenia (PENDING → ACTIVE). */
    private JButton confirmButton;

    /** Przycisk anulowania wypożyczenia (SCHEDULED/PENDING → CANCELLED). */
    private JButton cancelButton;

    /** Combo box filtrowania wg statusu. */
    private JComboBox<Object> statusFilterCombo;

    /**
     * Zbiór indeksów wierszy OVERDUE – kolorowane na czerwono.
     */
    private final Set<Integer> overdueRows = new HashSet<>();

    /**
     * Zbiór indeksów wierszy PENDING – kolorowane na zielono.
     */
    private final Set<Integer> pendingRows = new HashSet<>();

    /**
     * Timer Swing uruchamiany co {@value #STATUS_CHECK_INTERVAL_MS} ms.
     */
    private final Timer statusTimer;

    /**
     * Tworzy panel zarządzania wypożyczeniami.
     *
     * @param rentService      serwis wypożyczeń
     * @param clientService    serwis klientów
     * @param bikeService      serwis rowerów
     * @param bikeModelService serwis modeli rowerów
     * @param bikeTypeService  serwis typów rowerów
     * @author Tomasz Piłat
     */
    public RentPanel(RentService rentService, ClientService clientService,
                     BikeService bikeService, BikeModelService bikeModelService,
                     BikeTypeService bikeTypeService) {
        this.rentService      = rentService;
        this.clientService    = clientService;
        this.bikeService      = bikeService;
        this.bikeModelService = bikeModelService;
        this.bikeTypeService  = bikeTypeService;

        statusTimer = new Timer(STATUS_CHECK_INTERVAL_MS, e -> {
            if (rentService.updateStatuses()) {
                loadData();
            }
        });
        statusTimer.setInitialDelay(STATUS_CHECK_INTERVAL_MS);

        loadData();
    }

    /** Uruchamia timer gdy panel dodany do hierarchii. */
    @Override
    public void addNotify() {
        super.addNotify();
        statusTimer.start();
    }

    /** Zatrzymuje timer gdy panel usunięty z hierarchii. */
    @Override
    public void removeNotify() {
        statusTimer.stop();
        super.removeNotify();
    }

    // ----------------------------------------------------------------
    // BaseListPanel – inicjalizacja
    // ----------------------------------------------------------------

    /** {@inheritDoc} */
    @Override
    protected void initExtraComponents() {
        endButton = new AppButton(LanguageManager.getString("button.end"));
        confirmButton = new AppButton(LanguageManager.getString("button.confirm"));
        cancelButton = new AppButton(LanguageManager.getString("button.cancel"));

        endButton.setEnabled(false);
        confirmButton.setEnabled(false);
        cancelButton.setEnabled(false);

        // --- Filtr statusu ---
        statusFilterCombo = new JComboBox<>();
        statusFilterCombo.addItem(STATUS_ALL);
        for (RentStatus s : RentStatus.values()) {
            statusFilterCombo.addItem(s);
        }
        statusFilterCombo.setPreferredSize(new Dimension(160, 28));
        statusFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusFilterCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (STATUS_ALL.equals(value)) {
                    setText(LanguageManager.getString("rent.filter.all"));
                } else if (value instanceof RentStatus) {
                    setText(((RentStatus) value).getDisplayName());
                }
                return this;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);

                if (overdueRows.contains(row)) {
                    c.setForeground(Color.decode("#E11D48")); // Użycie naszej czerwieni COLOR_DANGER
                } else if (pendingRows.contains(row)) {
                    c.setForeground(COLOR_PENDING);
                } else {
                    c.setForeground(isSelected ? t.getSelectionForeground() : t.getForeground());
                }

                return c;
            }
        });    }

    /** {@inheritDoc} */
    @Override
    protected JPanel buildFilterBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(LanguageManager.getString("rent.filter.status"));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(label);
        panel.add(statusFilterCombo);
        return panel;
    }

    /** {@inheritDoc} */
    @Override
    protected void buildExtraButtons(JPanel buttonPanel) {
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(endButton);
    }

    /** {@inheritDoc} */
    @Override
    protected void onSelectionChanged(boolean selected) {
        if (!selected) {
            endButton.setEnabled(false);
            confirmButton.setEnabled(false);
            cancelButton.setEnabled(false);
            return;
        }
        int id = getSelectedId();
        try {
            Rent rent = rentService.getRentByID(id);
            RentStatus s = rent.getStatus();
            endButton.setEnabled(s == RentStatus.ACTIVE || s == RentStatus.OVERDUE
                    || s == RentStatus.PENDING);
            confirmButton.setEnabled(s == RentStatus.PENDING);
            cancelButton.setEnabled(s == RentStatus.PENDING || s == RentStatus.SCHEDULED);
        } catch (Exception ex) {
            endButton.setEnabled(false);
            confirmButton.setEnabled(false);
            cancelButton.setEnabled(false);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void refreshLanguageTexts() {
        endButton.setText(LanguageManager.getString("button.end"));
        confirmButton.setText(LanguageManager.getString("button.confirm"));
        cancelButton.setText(LanguageManager.getString("button.cancel"));
        if (statusFilterCombo != null) statusFilterCombo.repaint();
    }

    // ----------------------------------------------------------------
    // BaseListPanel – metody abstrakcyjne
    // ----------------------------------------------------------------

    /** {@inheritDoc} */
    @Override
    protected String getTitleKey() { return "rent.management"; }

    /** {@inheritDoc} */
    @Override
    protected String[] getColumnNames() {
        return new String[]{
                LanguageManager.getString("client.name"),
                LanguageManager.getString("bike.name"),
                LanguageManager.getString("date.startDate"),
                LanguageManager.getString("date.endDate"),
                LanguageManager.getString("rent.status.name"),
                LanguageManager.getString("rent.notes")
        };
    }

    /** {@inheritDoc} */
    @Override
    public void loadData() {
        rentService.updateStatuses();
        String query = searchField != null ? searchField.getText().trim() : "";

        // Wybrany filtr statusu
        Object sel = statusFilterCombo != null ? statusFilterCombo.getSelectedItem() : STATUS_ALL;
        RentStatus filterStatus = (sel instanceof RentStatus) ? (RentStatus) sel : null;

        List<Rent> rents = rentService.getAllRents();

        // Filtruj wg statusu
        if (filterStatus != null) {
            final RentStatus fs = filterStatus;
            rents = rents.stream()
                    .filter(r -> r.getStatus() == fs)
                    .toList();
        }

        // Filtruj wg tekstu wyszukiwania
        if (!query.isEmpty()) {
            String lower = query.toLowerCase();
            rents = rents.stream()
                    .filter(r -> {
                        Client    c  = clientService.getClientById(r.getClientId());
                        Bike      b  = bikeService.getBikeById(r.getBikeId());
                        BikeModel bm = b != null
                                ? bikeModelService.getBikeModelById(b.getBikeModelId()) : null;
                        String clientName = c != null
                                ? (c.getName() + " " + c.getSurname()).toLowerCase() : "";
                        String bikeInfo = bm != null
                                ? (bm.getBrand() + " " + bm.getModel()).toLowerCase() : "";
                        String status = r.getStatus() != null
                                ? r.getStatus().getDisplayName().toLowerCase() : "";
                        return clientName.contains(lower)
                                || bikeInfo.contains(lower)
                                || status.contains(lower);
                    })
                    .toList();
        }

        // OVERDUE na górze, potem PENDING, reszta bez zmian
        rents = new ArrayList<>(rents);
        rents.sort((a, b) -> {
            int pa = priority(a.getStatus());
            int pb = priority(b.getStatus());
            return Integer.compare(pa, pb);
        });

        overdueRows.clear();
        pendingRows.clear();
        clearTable();
        int rowIdx = 0;
        for (Rent r : rents) {
            if (r.getStatus() == RentStatus.OVERDUE)  overdueRows.add(rowIdx);
            if (r.getStatus() == RentStatus.PENDING)  pendingRows.add(rowIdx);
            Client client = clientService.getClientById(r.getClientId());
            Bike bike = bikeService.getBikeById(r.getBikeId());
            BikeModel bikeModel = bike != null
                    ? bikeModelService.getBikeModelById(bike.getBikeModelId()) : null;
            RentViewModel vm = new RentViewModel(r, client, bike, bikeModel);
            addRow(vm.getRentId(), vm.toRow());
            rowIdx++;
        }
    }

    /**
     * Zwraca priorytet sortowania dla danego statusu (niższy = wyżej).
     */
    private int priority(RentStatus s) {
        if (s == RentStatus.OVERDUE)  return 0;
        if (s == RentStatus.PENDING)  return 1;
        return 2;
    }

    /** {@inheritDoc} */
    @Override
    protected void filterTable(String query) {
        loadData();
    }

    // ----------------------------------------------------------------
    // Akcje
    // ----------------------------------------------------------------

    /** {@inheritDoc} */
    @Override
    protected void onAdd() {
        openDialog(
                LanguageManager.getString("rent.nameAdd"),
                new AddRentPanel(rentService, clientService, bikeService,
                        bikeModelService, bikeTypeService, this::loadData),
                580, 600
        );
    }

    /**
     * Otwiera formularz edycji wypożyczenia. Sprawdza status – SCHEDULED, PENDING i ACTIVE
     * mogą być edytowane; pozostałe są zablokowane.
     *
     * @param row wybrany wiersz tabeli
     * @author Tomasz Piłat
     */
    @Override
    protected void onEdit(int row) {
        int id = getSelectedId();
        if (id == -1) return;

        Rent rent;
        try {
            rent = rentService.getRentByID(id);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        openDialog(
                LanguageManager.getString("rent.editTitle"),
                new EditRentPanel(rentService, bikeService, bikeModelService,
                        bikeTypeService, this::loadData, rent),
                560, 510
        );
    }

    /**
     * Kończy wypożyczenie – pyta o uwagi, ustawia status FINISHED i zwalnia rower.
     *
     * @param rentId identyfikator wypożyczenia
     * @author Tomasz Piłat
     */
    private void onEnd(int rentId) {
        String notes = JOptionPane.showInputDialog(
                this,
                LanguageManager.getString("rent.returnNotes"),
                LanguageManager.getString("button.end"),
                JOptionPane.PLAIN_MESSAGE);
        if (notes == null) return;
        try {
            rentService.endRent(rentId, notes);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Potwierdza wypożyczenie (PENDING → ACTIVE).
     *
     * @param rentId identyfikator wypożyczenia
     * @author Tomasz Piłat
     */
    private void onConfirm(int rentId) {
        try {
            rentService.confirmRent(rentId);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Anuluje wypożyczenie (SCHEDULED/PENDING → CANCELLED).
     *
     * @param rentId identyfikator wypożyczenia
     * @author Tomasz Piłat
     */
    private void onCancel(int rentId) {
        int result = JOptionPane.showConfirmDialog(
                this,
                LanguageManager.getString("rent.cancelConfirm"),
                LanguageManager.getString("button.cancel"),
                JOptionPane.YES_NO_OPTION
        );
        if (result != JOptionPane.YES_OPTION) return;
        try {
            rentService.cancelRent(rentId);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Usuwa wypożyczenie – tylko dla statusu SCHEDULED.
     *
     * @param row wybrany wiersz tabeli
     * @author Tomasz Piłat
     */
    @Override
    protected void onDelete(int row) {
        int id = getSelectedId();
        if (id == -1) return;

        Rent rent;
        try {
            rent = rentService.getRentByID(id);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (rent.getStatus() != RentStatus.SCHEDULED) {
            JOptionPane.showMessageDialog(this,
                    LanguageManager.getString("error.rent.onlyScheduled"),
                    LanguageManager.getString("error.title"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                LanguageManager.getString("rent.deleteConfirm"),
                LanguageManager.getString("button.delete"),
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            try {
                rentService.removeRent(id);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void initExtraListeners() {
        endButton.addActionListener(e -> {
            int id = getSelectedId();
            if (id != -1) onEnd(id);
        });
        confirmButton.addActionListener(e -> {
            int id = getSelectedId();
            if (id != -1) onConfirm(id);
        });
        cancelButton.addActionListener(e -> {
            int id = getSelectedId();
            if (id != -1) onCancel(id);
        });
        statusFilterCombo.addActionListener(e -> loadData());
    }
}
