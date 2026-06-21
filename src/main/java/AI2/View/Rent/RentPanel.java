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
 * wypożyczeń – przeterminowane wskakują na górę i są zaznaczane na czerwono
 * bez konieczności ręcznego odświeżania.
 *
 * @author Tomasz Piłat
 */
public class RentPanel extends BaseListPanel {

    /** Interwał automatycznego sprawdzania statusów wypożyczeń (w milisekundach). */
    private static final int STATUS_CHECK_INTERVAL_MS = 60_000;

    /** Serwis wypożyczeń. */
    private final RentService rentService;

    /** Serwis klientów. */
    private final ClientService clientService;

    /** Serwis rowerów. */
    private final BikeService bikeService;

    /** Serwis modeli rowerów. */
    private final BikeModelService bikeModelService;

    /** Serwis typów rowerów. */
    private final BikeTypeService bikeTypeService;

    /** Przycisk zakończenia wypożyczenia. */
    private JButton endButton;

    /**
     * Zbiór indeksów wierszy tabeli odpowiadających wypożyczeniom OVERDUE.
     * Używany przez renderer do kolorowania tych wierszy na czerwono.
     */
    private final Set<Integer> overdueRows = new HashSet<>();

    /**
     * Timer Swing uruchamiany co {@value #STATUS_CHECK_INTERVAL_MS} ms.
     * Sprawdza statusy na EDT – bezpieczne dla Swing.
     * Zatrzymywany gdy panel zostaje usunięty z hierarchii komponentów.
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

    /**
     * Uruchamia timer automatycznego odświeżania gdy panel zostaje
     * dodany do hierarchii komponentów.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        statusTimer.start();
    }

    /**
     * Zatrzymuje timer gdy panel zostaje usunięty z hierarchii komponentów.
     */
    @Override
    public void removeNotify() {
        statusTimer.stop();
        super.removeNotify();
    }

    /** {@inheritDoc} */
    @Override
    protected void initExtraComponents() {
        endButton = new AppButton(LanguageManager.getString("button.end"));
        endButton.setEnabled(false);

        // Renderer kolorujący wiersze OVERDUE na czerwono
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setForeground(overdueRows.contains(row)
                            ? Color.RED
                            : t.getForeground());
                }
                return c;
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    protected void buildExtraButtons(JPanel buttonPanel) {
        buttonPanel.add(endButton);
    }

    /** {@inheritDoc} */
    @Override
    protected void onSelectionChanged(boolean selected) {
        endButton.setEnabled(selected);
    }

    /** {@inheritDoc} */
    @Override
    protected void refreshLanguageTexts() {
        endButton.setText(LanguageManager.getString("button.end"));
    }


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

        List<Rent> rents = rentService.getAllRents();
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

        // OVERDUE na górze, reszta bez zmian kolejności
        rents = new ArrayList<>(rents);
        rents.sort((a, b) -> {
            boolean aOver = a.getStatus() == RentStatus.OVERDUE;
            boolean bOver = b.getStatus() == RentStatus.OVERDUE;
            if (aOver == bOver) return 0;
            return aOver ? -1 : 1;
        });

        overdueRows.clear();
        clearTable();
        int rowIdx = 0;
        for (Rent r : rents) {
            if (r.getStatus() == RentStatus.OVERDUE) overdueRows.add(rowIdx);
            Client client = clientService.getClientById(r.getClientId());
            Bike bike = bikeService.getBikeById(r.getBikeId());
            BikeModel bikeModel = bike != null
                    ? bikeModelService.getBikeModelById(bike.getBikeModelId()) : null;
            RentViewModel vm = new RentViewModel(r, client, bike, bikeModel);
            addRow(vm.getRentId(), vm.toRow());
            rowIdx++;
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void filterTable(String query) {
        loadData();
    }

    /**
     * Otwiera formularz dodawania nowego wypożyczenia.
     * Po zapisie automatycznie odświeża listę wypożyczeń.
     *
     * @author Tomasz Piłat
     */
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
     * Otwiera formularz edycji wypożyczenia. Sprawdza status – SCHEDULED i ACTIVE
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
                        bikeTypeService, this, rent),
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
        if (notes == null) return;   // użytkownik anulował
        try {
            rentService.endRent(rentId, notes);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Usuwa wypożyczenie po sprawdzeniu że jest w statusie SCHEDULED.
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
    }
}
