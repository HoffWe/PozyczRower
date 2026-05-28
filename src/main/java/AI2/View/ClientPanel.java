package AI2.View;

import AI2.Model.Client;
import AI2.Repository.ClientRepository;
import AI2.Service.ClientFileService;
import AI2.Service.ClientService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 *
 *
 *
 * @author Sviatoslav Matsopa
 */
public class ClientPanel extends JPanel {

    private final ClientService clientService;
    private final ClientRepository clientRepository;
    private final ClientFileService clientFileService;

    private final JTextField nameField;
    private final JTextField surnameField;
    private final JTextField evidenceField;
    private final JTextField opisField;

    private final DefaultTableModel tableModel;
    private final JTable table;

    public ClientPanel(ClientService clientService, ClientRepository clientRepository) {
        this.clientService = clientService;
        this.clientRepository = clientRepository;
        this.clientFileService = new ClientFileService();
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Dane klienta"));

        formPanel.add(new JLabel("name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("surname:"));
        surnameField = new JTextField();
        formPanel.add(surnameField);

        formPanel.add(new JLabel("evidence:"));
        evidenceField = new JTextField();
        formPanel.add(evidenceField);

        formPanel.add(new JLabel("Opis:"));
        opisField = new JTextField();
        formPanel.add(opisField);

        JButton addButton = new JButton("Add client");
        addButton.addActionListener(e -> dodajclient());
        formPanel.add(addButton);

        JButton deleteButton = new JButton("Delete client");
        deleteButton.addActionListener(e -> usunKlienta());
        formPanel.add(deleteButton);

        JButton saveButton = new JButton("Zapisz do pliku");
        saveButton.addActionListener(e -> {
            clientFileService.saveToFile(clientRepository);
            JOptionPane.showMessageDialog(this, "Zapisano!");
        });
        formPanel.add(saveButton);

        JButton loadButton = new JButton("Wczytaj z pliku");
        loadButton.addActionListener(e -> {
            clientFileService.loadFromFile(clientRepository);
            odswiezTabele();
            JOptionPane.showMessageDialog(this, "Wczytano!");
        });
        formPanel.add(loadButton);

        add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("name");
        tableModel.addColumn("surname");
        tableModel.addColumn("evidence");
        tableModel.addColumn("Opis");

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                nameField.setText(tableModel.getValueAt(row, 1).toString());
                surnameField.setText(tableModel.getValueAt(row, 2).toString());
                evidenceField.setText(tableModel.getValueAt(row, 3).toString());
                opisField.setText(tableModel.getValueAt(row, 4).toString());
            }
        });
    }

    private void dodajclient() {
        String name = nameField.getText();
        String surname = surnameField.getText();
        String evidence = evidenceField.getText();
        String opis = opisField.getText();

        if (name.isEmpty() || surname.isEmpty() || evidence.isEmpty()) {
            JOptionPane.showMessageDialog(this, "write name, surname and evidence!");
            return;
        }

        clientService.addClient(name, surname, evidence, opis);
        odswiezTabele();

        nameField.setText("");
        surnameField.setText("");
        evidenceField.setText("");
        opisField.setText("");
    }

    private void usunKlienta() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Wybierz klienta z tabeli!");
            return;
        }

        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        clientService.removeClient(id);
        odswiezTabele();
    }

    public void odswiezTabele() {
        tableModel.setRowCount(0);
        List<Client> lista = clientService.getAllClients();
        for (int i = 0; i < lista.size(); i++) {
            Client c = lista.get(i);
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getSurname(),
                    c.getEvidence(),
                    c.getOpis()
            });
        }
    }
}