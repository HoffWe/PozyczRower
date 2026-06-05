package AI2.View.Rent;

import AI2.Model.Bike;
import AI2.Model.Client;
import AI2.Model.Rent;
import AI2.Service.RentService;
import AI2.Util.LanguageManager;
import AI2.View.Components.AppButton;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DateTimePicker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AddRentPanel extends JPanel {

    private final RentService rentService;

    private JComboBox<Client> clientComboBox;

    private JComboBox<Bike> bikeComboBox;

    private DateTimePicker startDatePicker;

    private DateTimePicker returnDatePicker;

    private JButton addButton;


    public AddRentPanel(RentService rentService) {

        this.rentService = rentService;

        initializeComponents();

        buildLayout();

        registerListeners();
    }

    private void initializeComponents() {

        setBackground(Color.WHITE);

        clientComboBox = new JComboBox<>();

        bikeComboBox = new JComboBox<>();

        startDatePicker = new DateTimePicker();

        returnDatePicker = new DateTimePicker();

        Dimension fieldSize =
                new Dimension(
                        280,
                        35
                );

        clientComboBox.setPreferredSize(
                fieldSize
        );

        bikeComboBox.setPreferredSize(
                fieldSize
        );

        startDatePicker.setPreferredSize(
                fieldSize
        );

        returnDatePicker.setPreferredSize(
                fieldSize
        );

        addButton =
                new AppButton(
                        LanguageManager.getString(
                                "rent.nameAdd"
                        )
                );
    }

    private void buildLayout() {

        setLayout(
                new BorderLayout(
                        10,
                        10
                )
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        JLabel title =
                new JLabel(
                        LanguageManager.getString(
                                "rent.nameAdd"
                        ),
                        SwingConstants.CENTER
                );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        add(
                title,
                BorderLayout.NORTH
        );

        JPanel formPanel =
                new JPanel(
                        new GridBagLayout()
                );

        formPanel.setBackground(
                Color.WHITE
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        10,
                        10,
                        10,
                        10
                );

        gbc.anchor =
                GridBagConstraints.WEST;

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(
                new JLabel(
                        LanguageManager.getString(
                                "client.name"
                        )
                ),
                gbc
        );

        gbc.gridx = 1;

        formPanel.add(
                clientComboBox,
                gbc
        );

        gbc.gridx = 0;
        gbc.gridy++;

        formPanel.add(
                new JLabel(
                        LanguageManager.getString(
                                "bike.name"
                        )
                ),
                gbc
        );

        gbc.gridx = 1;

        formPanel.add(
                bikeComboBox,
                gbc
        );

        gbc.gridx = 0;
        gbc.gridy++;

        formPanel.add(
                new JLabel(
                        LanguageManager.getString(
                                "date.startDate"
                        )
                ),
                gbc
        );

        gbc.gridx = 1;

        formPanel.add(
                startDatePicker,
                gbc
        );

        gbc.gridx = 0;
        gbc.gridy++;

        formPanel.add(
                new JLabel(
                        LanguageManager.getString(
                                "date.endDate"
                        )
                ),
                gbc
        );

        gbc.gridx = 1;

        formPanel.add(
                returnDatePicker,
                gbc
        );

        add(
                formPanel,
                BorderLayout.CENTER
        );

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        buttonPanel.setBackground(
                Color.WHITE
        );

        buttonPanel.add(
                addButton
        );

        add(
                buttonPanel,
                BorderLayout.SOUTH
        );
    }

    private void registerListeners() {

        addButton.addActionListener(e -> {

            try {

                Client client =
                        (Client)
                                clientComboBox
                                        .getSelectedItem();

                Bike bike =
                        (Bike)
                                bikeComboBox
                                        .getSelectedItem();

                if (client == null) {

                    throw new IllegalArgumentException(
                            LanguageManager.getString(
                                    "error.clientNotSelected"
                            )
                    );
                }

                if (bike == null) {

                    throw new IllegalArgumentException(
                            LanguageManager.getString(
                                    "error.bikeNotSelected"
                            )
                    );
                }

                if (startDatePicker.getDateTimeStrict() == null ||
                        returnDatePicker.getDateTimeStrict() == null) {

                    throw new IllegalArgumentException(
                            LanguageManager.getString(
                                    "error.dateRequired"
                            )
                    );
                }

                LocalDateTime startDate =
                        startDatePicker
                                .getDateTimeStrict();

                LocalDateTime returnDate =
                        returnDatePicker
                                .getDateTimeStrict();

                rentService.addRent(
                        new Rent(
                                bike.getBikeId(),
                                client.getId(),
                                startDate,
                                returnDate
                        )
                );

                JOptionPane.showMessageDialog(
                        this,
                        LanguageManager.getString(
                                "rent.added"
                        )
                );

                SwingUtilities
                        .getWindowAncestor(this)
                        .dispose();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        LanguageManager.getString(
                                "error.title"
                        ),
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}