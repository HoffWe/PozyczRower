package AI2.View.Client;

import AI2.Service.ClientService;
import AI2.Util.LanguageManager;
import AI2.View.Components.AppButton;

import javax.swing.*;
import java.awt.*;

public class AddClientPanel extends JPanel {

    private final ClientService clientService;
    private final ClientPanel clientPanel;
    private JTextField nameField;
    private JTextField surnameField;
    private JTextField evidenceField;
    private JTextField opisField;
    private JButton addButton;

    public AddClientPanel(ClientService clientService, ClientPanel clientPanel) {
        this.clientService = clientService;
        this.clientPanel = clientPanel;
        initializeComponents();
        buildLayout();
        registerListeners();
    }

    private void initializeComponents() {

        setBackground(Color.WHITE);

        Dimension fieldSize = new Dimension(280, 35);

        nameField = new JTextField();
        nameField.setPreferredSize(fieldSize);
        surnameField = new JTextField();
        surnameField.setPreferredSize(fieldSize);
        evidenceField = new JTextField();
        evidenceField.setPreferredSize(fieldSize);
        opisField = new JTextField();
        opisField.setPreferredSize(fieldSize);
        addButton = new AppButton(
                LanguageManager.getString("client.nameAdd")
        );
    }

    private void buildLayout() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel(
                LanguageManager.getString("client.nameAdd"),
                SwingConstants.CENTER
        );

        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(
                new JLabel(LanguageManager.getString("client.firstName")),
                gbc
        );

        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(
                new JLabel(LanguageManager.getString("client.lastName")),
                gbc
        );

        gbc.gridx = 1;
        formPanel.add(surnameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        formPanel.add(
                new JLabel(LanguageManager.getString("client.evidence")),
                gbc
        );

        gbc.gridx = 1;

        formPanel.add(evidenceField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        formPanel.add(
                new JLabel(LanguageManager.getString("client.description")),
                gbc
        );

        gbc.gridx = 1;
        formPanel.add(opisField, gbc);
        add(formPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void registerListeners() {

        addButton.addActionListener(e -> {

            try {

                String name = nameField.getText().trim();
                String surname = surnameField.getText().trim();
                String evidence = evidenceField.getText().trim();
                String opis = opisField.getText().trim();
                clientService.addClient(name, surname, evidence, opis);
                JOptionPane.showMessageDialog(
                        this,
                        LanguageManager.getString("client.added")
                );

                clientPanel.refreshTable();
                SwingUtilities.getWindowAncestor(this).dispose();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}