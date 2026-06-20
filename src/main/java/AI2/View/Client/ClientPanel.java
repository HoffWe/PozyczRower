package AI2.View.Client;

import AI2.Model.Client;
import AI2.Service.ClientService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseListPanel;
import AI2.View.ViewModel.ClientViewModel;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel zarządzania klientami.
 * ID klientów NIE jest wyświetlane w tabeli – przechowywane w {@code rowIds}.
 */
public class ClientPanel extends BaseListPanel {

    private final ClientService clientService;

    public ClientPanel(ClientService clientService) {
        this.clientService = clientService;
        loadData();
    }

    // ----------------------------------------------------------------
    // BaseListPanel – implementacja metod abstrakcyjnych
    // ----------------------------------------------------------------

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
                            contains(c.getName(),     query) ||
                            contains(c.getSurname(),  query) ||
                            contains(c.getEvidence(), query) ||
                            contains(c.getOpis(),     query))
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
            clientService.removeClient(id);
            loadData();
        }
    }
}
