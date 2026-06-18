package AI2.View.BikeType;

import AI2.DTO.BikeTypeDTO;
import AI2.Service.BikeTypeService;
import AI2.Util.LanguageManager;
import AI2.View.Components.AppButton;

import javax.swing.*;
import java.awt.*;

public class AddBikeTypePanel extends JPanel {

    private final BikeTypeService  bikeTypeService;

    private JTextField nameField;
    private JTextField descriptionField;
    private JButton addButton;

    public AddBikeTypePanel(BikeTypeService bikeTypeService) {
        this.bikeTypeService = bikeTypeService;
        initializeCompoments();
        buildLayout();
        registerListeners();
    }

    private void initializeCompoments() {
        setBackground(Color.WHITE);

        nameField = new JTextField(20);
        descriptionField = new JTextField(20);

        Dimension fieldSize = new Dimension(200, 35);
        nameField.setPreferredSize(fieldSize);
        descriptionField.setPreferredSize(fieldSize);

        addButton = new AppButton(LanguageManager.getString("button.add"));
    }

    private void buildLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        // --- TYTUŁ ---
        JLabel title = new JLabel(LanguageManager.getString("bikeType.nameAdd"), SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // --- FORMULARZ (GridBagLayout) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Marginesy między komórkami
        gbc.anchor = GridBagConstraints.WEST;    // Wyrównanie do lewej
        gbc.fill = GridBagConstraints.HORIZONTAL;// Rozciąganie w poziomie

        // Wiersz 1: Etykieta i Pole dla Nazwy
        gbc.weightx = 0.0; //etykieta ma się nie rozpychać
        gbc.gridx = 0; // Kolumna 0
        gbc.gridy = 0; // Wiersz 0
        formPanel.add(new JLabel(LanguageManager.getString("bikeType.nameField")), gbc);

        gbc.weightx = 1.0;
        gbc.gridx = 1; // Kolumna 1
        formPanel.add(nameField, gbc);

        // Wiersz 2: Etykieta i Pole dla Opisu
        gbc.weightx = 0.0; //etykieta ma się nie rozpychać
        gbc.gridx = 0; // Kolumna 0
        gbc.gridy++;   // Wiersz 1 (zwiększamy y o 1)
        formPanel.add(new JLabel(LanguageManager.getString("bikeType.descriptionField")), gbc);

        gbc.weightx = 1.0; //etykieta ma się nie rozpychać
        gbc.gridx = 1; // Kolumna 1
        formPanel.add(descriptionField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- PRZYCISK ---
        // Używamy FlowLayout.RIGHT, aby przycisk zapisał się po prawej stronie na dole
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void registerListeners() {

        addButton.addActionListener(e -> {
            try{
                String name = nameField.getText();
                String description = descriptionField.getText();

                //wrzucamy do DTO
                BikeTypeDTO newBikeTypeDTO = new BikeTypeDTO(name, description);

                // Próbujemy zapisać. Jeśli nazwa jest pusta lub istnieje,
                // serwis rzuci IllegalArgumentException!
                bikeTypeService.addBikeType(newBikeTypeDTO);

                JOptionPane.showMessageDialog(this, LanguageManager.getString("bikeType.added"));

                SwingUtilities.getWindowAncestor(this).dispose();

            }catch(Exception ex){
                //Jeśli serwis rzucił błąd pokazuuemy go w czerwonym okienku
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        LanguageManager.getString("bikeType.addedError"),
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

}
