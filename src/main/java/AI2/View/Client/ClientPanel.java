package AI2.View.Client;

import AI2.Model.Client;
import AI2.Service.ClientService;
import AI2.Util.LanguageManager;
import AI2.View.Components.AppButton;
import AI2.View.Components.SearchPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClientPanel extends JPanel {

    private final ClientService clientService;
    private JTable clientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    public ClientPanel(ClientService clientService) {

        this.clientService = clientService;
        initializeComponents();
        buildLayout();
        registerListeners();
        refreshTable();
    }

    private void initializeComponents() {

        tableModel = new DefaultTableModel(
                new String[]{
                        "ID",
                        LanguageManager.getString("client.firstName"),
                        LanguageManager.getString("client.lastName"),
                        LanguageManager.getString("client.evidence"),
                        LanguageManager.getString("client.description")
                }, 0
        );

        clientTable = new JTable(tableModel);
        clientTable.setRowHeight(35);
        clientTable.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );

        clientTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        addButton = new AppButton(
                LanguageManager.getString("button.add")
        );

        editButton = new AppButton(
                LanguageManager.getString("button.edit")
        );

        deleteButton = new AppButton(
                LanguageManager.getString("button.delete")
        );

        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void buildLayout() {

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel(
                LanguageManager.getString("client.management"),
                SwingConstants.CENTER
        );

        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        SearchPanel searchPanel = new SearchPanel();
        searchField = searchPanel.getSearchField();
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(clientTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void registerListeners() {

        clientTable.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {

                        boolean selected = clientTable.getSelectedRow() != -1;
                        editButton.setEnabled(selected);
                        deleteButton.setEnabled(selected);
                    }
                });

        clientTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2) {
                    editButton.doClick();
                }
            }
        });

        addButton.addActionListener(e -> {

            JDialog dialog = new JDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    LanguageManager.getString("client.nameAdd"),
                    true
            );

            dialog.add(new AddClientPanel(clientService, this));
            dialog.setSize(450, 380);
            dialog.setResizable(false);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        editButton.addActionListener(e -> {

            int selectedRow = clientTable.getSelectedRow();
            if (selectedRow == -1) {
                return;
            }

            JOptionPane.showMessageDialog(this, "Edit client row: " + selectedRow);
        });

        deleteButton.addActionListener(e -> {

            int selectedRow = clientTable.getSelectedRow();
            if (selectedRow == -1) {
                return;
            }

            int result = JOptionPane.showConfirmDialog(
                    this,
                    LanguageManager.getString("client.deleteConfirm"),
                    LanguageManager.getString("button.delete"),
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {

                int id = (int) tableModel.getValueAt(selectedRow, 0);
                clientService.removeClient(id);
                refreshTable();
            }
        });
    }

    public void refreshTable() {

        tableModel.setRowCount(0);

        for (Client c : clientService.getAllClients()) {
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