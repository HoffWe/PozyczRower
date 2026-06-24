package AI2.View.Client;

import AI2.Model.Client;
import AI2.Service.BikeModelService;
import AI2.Service.BikeService;
import AI2.Service.BikeTypeService;
import AI2.Service.ClientService;
import AI2.Service.RentService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseListPanel;
import AI2.View.Components.AppButton;
import AI2.View.Rent.AddRentPanel;
import AI2.View.ViewModel.ClientViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel zarządzania klientami.
 * ID klientów NIE jest wyświetlane w tabeli – przechowywane w {@code rowIds}.
 *
 *  @author Światosław Matsopa
 */
public class ClientPanel extends BaseListPanel {

    private final ClientService clientService;
    private final RentService rentService;
    private final BikeService bikeService;
    private final BikeModelService bikeModelService;
    private final BikeTypeService bikeTypeService;

    private JButton rentButton;
    private JButton showRentsButton;

    public ClientPanel(ClientService clientService, RentService rentService,
                       BikeService bikeService, BikeModelService bikeModelService,
                       BikeTypeService bikeTypeService) {
        this.clientService = clientService;
        this.rentService = rentService;
        this.bikeService = bikeService;
        this.bikeModelService = bikeModelService;
        this.bikeTypeService = bikeTypeService;
        loadData();
    }
    @Override
    protected void initExtraComponents() {
        rentButton = new AppButton(LanguageManager.getString("button.rent"));
        showRentsButton = new AppButton(LanguageManager.getString("button.showRents"));
        showRentsButton.setPreferredSize(new Dimension(180, 40));
        rentButton.setEnabled(false);
        showRentsButton.setEnabled(false);
    }

    @Override
    protected void buildExtraButtons(JPanel buttonPanel) {
        buttonPanel.add(rentButton);
        buttonPanel.add(showRentsButton);
    }

    @Override
    protected void onSelectionChanged(boolean selected) {
        rentButton.setEnabled(selected);
        showRentsButton.setEnabled(selected);
    }

    @Override
    protected void refreshLanguageTexts() {
        rentButton.setText(LanguageManager.getString("button.rent"));
        showRentsButton.setText(LanguageManager.getString("button.showRents"));
    }

    @Override
    protected void initExtraListeners() {
        rentButton.addActionListener(e -> onRent());
        showRentsButton.addActionListener(e -> onShowRents());
    }

    @Override
    protected String getTitleKey() {
        return "client.management";
    }

    @Override
    protected String[] getColumnNames() {
        return new String[]{
                LanguageManager.getString("client.firstName"),
                LanguageManager.getString("client.lastName"),
                LanguageManager.getString("client.evidence"),
                LanguageManager.getString("client.description")
        };
    }

    @Override
    public void loadData() {
        String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";

        List<Client> clients = clientService.getAllClients();
        if (!query.isEmpty()) {
            clients = clients.stream()
                    .filter(c ->
                            contains(c.getName(), query) ||
                            contains(c.getSurname(),query) ||
                            contains(c.getEvidence(),query) ||
                            contains(c.getOpis(),query))
                    .collect(Collectors.toList());
        }

        clearTable();
        for (Client c : clients) {
            addRow(c.getId(), new ClientViewModel(c).toRow());
        }
    }

    private static boolean contains(String field, String query) {
        return field != null && field.toLowerCase().contains(query);
    }

    @Override
    protected void filterTable(String query) {
        loadData();
    }

    @Override
    protected void onAdd() {
        openDialog(
                LanguageManager.getString("client.nameAdd"),
                new AddClientPanel(clientService, this),
                480, 400
        );
    }

    @Override
    protected void onEdit(int row) {
        int id = getSelectedId();
        if (id == -1) return;

        Client client = clientService.getClientById(id);
        if (client == null) {
            JOptionPane.showMessageDialog(this,
                    LanguageManager.getString("error.title"));
            return;
        }
        openDialog(
                LanguageManager.getString("client.editTitle"),
                new EditClientPanel(clientService, client, this),
                480, 400
        );
    }

    @Override
    protected void onDelete(int row) {
        int id = getSelectedId();
        if (id == -1) return;
        int result = JOptionPane.showConfirmDialog(
                this,
                LanguageManager.getString("client.deleteConfirm"),
                LanguageManager.getString("button.delete"),
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            try {
                if (rentService.clientHasActiveRentals(id)) {
                    throw new IllegalStateException(
                            LanguageManager.getString("error.client.hasRents"));
                }
                clientService.removeClient(id);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onRent() {
        int id = getSelectedId();
        if (id == -1) return;

        Client client = clientService.getClientById(id);
        if (client == null) return;

        openDialog(
                LanguageManager.getString("rent.nameAdd"),
                new AddRentPanel(rentService, clientService, bikeService,
                        bikeModelService, bikeTypeService, client),
                580, 600
        );
    }


    /**
     * Otwiera dialog z listą wypożyczeń zaznaczonego klienta.
     *
     */
    private void onShowRents() {
        int id = getSelectedId();
        if (id == -1) return;

        Client client = clientService.getClientById(id);
        if (client == null) return;

        Window owner = javax.swing.SwingUtilities.getWindowAncestor(this);
        ClientRentsDialog dlg = new ClientRentsDialog(
                owner, client, rentService, bikeService, bikeModelService,
                bikeTypeService, clientService);
        dlg.setVisible(true);
    }
}
