package AI2.View.Rent;

import AI2.Enums.RentStatus;
import AI2.Model.Bike;
import AI2.Model.Client;
import AI2.Model.Rent;
import AI2.Service.BikeService;
import AI2.Service.ClientService;
import AI2.Service.RentService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseListPanel;
import AI2.View.Components.AppButton;
import AI2.View.ViewModel.RentViewModel;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel zarządzania wypożyczeniami.
 * Dane pobierane z serwisu. ID NIE jest wyświetlane w tabeli.
 */
public class RentPanel extends BaseListPanel {

    private final RentService   rentService;
    private final ClientService clientService;
    private final BikeService   bikeService;

    private JButton endButton;

    public RentPanel(RentService rentService, ClientService clientService,
                     BikeService bikeService) {
        this.rentService   = rentService;
        this.clientService = clientService;
        this.bikeService   = bikeService;
        loadData();
    }

    // ----------------------------------------------------------------
    // BaseListPanel – inicjalizacja dodatkowych komponentów
    // ----------------------------------------------------------------

    @Override
    protected void initExtraComponents() {
        endButton = new AppButton(LanguageManager.getString("button.end"));
        endButton.setEnabled(false);
    }

    @Override
    protected void buildExtraButtons(JPanel buttonPanel) {
        buttonPanel.add(endButton);
    }

    @Override
    protected void onSelectionChanged(boolean selected) {
        endButton.setEnabled(selected);
    }

    @Override
    protected void refreshLanguageTexts() {
        endButton.setText(LanguageManager.getString("button.end"));
    }

    // ----------------------------------------------------------------
    // BaseListPanel – metody abstrakcyjne
    // ----------------------------------------------------------------

    @Override
    protected String getTitleKey() {
        return "rent.management";
    }

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

    @Override
    public void loadData() {
        String query = searchField != null ? searchField.getText().trim() : "";

        List<Rent> rents = rentService.getAllRents();
        if (!query.isEmpty()) {
            String lower = query.toLowerCase();
            rents = rents.stream()
                    .filter(r -> {
                        Client c    = clientService.getClientById(r.getClientId());
                        Bike   bike = bikeService.getBikeById(r.getBikeId());
                        String clientName = c != null
                                ? (c.getName() + " " + c.getSurname()).toLowerCase() : "";
                        String bikeInfo   = bike != null
                                ? (bike.getBrand() + " " + bike.getModel()).toLowerCase() : "";
                        String status     = r.getStatus() != null
                                ? r.getStatus().name().toLowerCase() : "";
                        return clientName.contains(lower)
                                || bikeInfo.contains(lower)
                                || status.contains(lower);
                    })
                    .collect(Collectors.toList());
        }

        clearTable();
        for (Rent r : rents) {
            Client client = clientService.getClientById(r.getClientId());
            Bike   bike   = bikeService.getBikeById(r.getBikeId());
            RentViewModel vm = new RentViewModel(r, client, bike);
            addRow(vm.getRentId(), vm.toRow());
        }
    }

    @Override
    protected void filterTable(String query) {
        loadData();
    }

    @Override
    protected void onAdd() {
        openDialog(
                LanguageManager.getString("rent.nameAdd"),
                new AddRentPanel(rentService, clientService,this,bikeService ),
                560, 420
        );
    }

    @Override
    protected void onEdit(int row) {
        // Edycja wypożyczenia – otwiera szczegóły
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

        // Uruchomienie przycisku "Zakończ" jeśli status to ACTIVE
        int choice = JOptionPane.showConfirmDialog(
                this,
                LanguageManager.getString("rent.deleteConfirm"),
                LanguageManager.getString("button.end"),
                JOptionPane.YES_NO_OPTION
        );
        if (choice == JOptionPane.YES_OPTION) {
            onEnd(id);
        }
    }

    private void onEnd(int rentId) {
        try {
            rentService.endRent(rentId);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected void onDelete(int row) {
        int id = getSelectedId();
        if (id == -1) return;

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

    // ----------------------------------------------------------------
    // Listener przycisku "Zakończ" – dodawany w konstruktorze bazy
    // ----------------------------------------------------------------

    @Override
    protected void initExtraListeners() {
        endButton.addActionListener(e -> {
            int id = getSelectedId();
            if (id != -1) onEnd(id);
        });
    }
}
