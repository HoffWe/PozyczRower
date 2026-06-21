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
import java.util.List;

/**
 * Panel zarządzania wypożyczeniami.
 * Dane pobierane z serwisu. ID NIE jest wyświetlane w tabeli.
 *
 * @author Tomasz Piłat
 */
public class RentPanel extends BaseListPanel {

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
        loadData();
    }

    /** {@inheritDoc} */
    @Override
    protected void initExtraComponents() {
        endButton = new AppButton(LanguageManager.getString("button.end"));
        endButton.setEnabled(false);
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
                LanguageManager.getString("rent.status.name")
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

        clearTable();
        for (Rent r : rents) {
            Client client = clientService.getClientById(r.getClientId());
            Bike bike = bikeService.getBikeById(r.getBikeId());
            BikeModel bikeModel = bike != null
                    ? bikeModelService.getBikeModelById(bike.getBikeModelId()) : null;
            RentViewModel vm = new RentViewModel(r, client, bike, bikeModel);
            addRow(vm.getRentId(), vm.toRow());
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

        RentStatus status = rent.getStatus();
        if (status != RentStatus.SCHEDULED && status != RentStatus.ACTIVE) {
            JOptionPane.showMessageDialog(this,
                    LanguageManager.getString("error.rent.cannotEdit"),
                    LanguageManager.getString("error.title"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        openDialog(
                LanguageManager.getString("rent.editTitle"),
                new EditRentPanel(rentService, bikeService, bikeModelService,
                        bikeTypeService, this, rent),
                560, 360
        );
    }

    /**
     * Kończy wypożyczenie (ustawia status FINISHED, zwalnia rower).
     *
     * @param rentId identyfikator wypożyczenia
     * @author Tomasz Piłat
     */
    private void onEnd(int rentId) {
        try {
            rentService.endRent(rentId);
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
