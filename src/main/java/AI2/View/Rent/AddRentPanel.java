package AI2.View.Rent;

import AI2.Model.Bike;
import AI2.Model.Client;
import AI2.Model.Rent;
import AI2.Service.BikeService;
import AI2.Service.ClientService;
import AI2.Service.RentService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseFormPanel;
import com.github.lgooddatepicker.components.DateTimePicker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

/**
 * Panel formularza dodawania nowego wypożyczenia.
 */
public class AddRentPanel extends BaseFormPanel {

    private final RentService   rentService;
    private final BikeService    bikeService;
    private final ClientService clientService;
    private final RentPanel     parentPanel;

    private JComboBox<Client> clientComboBox;
    private JComboBox<Bike>   bikeComboBox;
    private DateTimePicker    startDatePicker;
    private DateTimePicker    returnDatePicker;

    public AddRentPanel(RentService rentService, ClientService clientService,
                        RentPanel parentPanel, BikeService bikeService) {
        this.rentService   = rentService;
        this.clientService = clientService;
        this.parentPanel   = parentPanel;
        this.bikeService   = bikeService;
        init();
    }

    // ----------------------------------------------------------------
    // BaseFormPanel
    // ----------------------------------------------------------------

    @Override
    protected String getTitleKey() { return "rent.nameAdd"; }

    @Override
    protected String getSubmitButtonKey() { return "button.add"; }

    @Override
    protected void initFormComponents() {
        Dimension size = defaultFieldSize();

        clientComboBox = new JComboBox<>();
        clientComboBox.setPreferredSize(size);

        bikeComboBox = new JComboBox<>();
        bikeComboBox.setPreferredSize(size);

        startDatePicker  = new DateTimePicker(); startDatePicker.setPreferredSize(size);
        returnDatePicker = new DateTimePicker(); returnDatePicker.setPreferredSize(size);

        // Załaduj klientów
        for (Client c : clientService.getAllClients()) {
            clientComboBox.addItem(c);
        }
        for  (Bike b : bikeService.getAllBikes()) {
            bikeComboBox.addItem(b);
        }
    }

    @Override
    protected void buildForm(JPanel formPanel, GridBagConstraints gbc) {
        addFormRow(formPanel, gbc, "client.name",clientComboBox);
        addFormRow(formPanel, gbc, "bike.name",bikeComboBox);
        addFormRow(formPanel, gbc, "date.startDate",startDatePicker);
        addFormRow(formPanel, gbc, "date.endDate",returnDatePicker);
    }

    @Override
    protected void onSubmit() {
        try {
            Client client = (Client) clientComboBox.getSelectedItem();
            Bike bike=(Bike)bikeComboBox.getSelectedItem();

            if (client == null) {
                throw new IllegalArgumentException(
                        LanguageManager.getString("error.clientNotSelected"));
            }
            if (bike == null) {
                throw new IllegalArgumentException(
                        LanguageManager.getString("error.bikeNotSelected"));
            }
            if (startDatePicker.getDateTimeStrict() == null
                    || returnDatePicker.getDateTimeStrict() == null) {
                throw new IllegalArgumentException(
                        LanguageManager.getString("error.dateRequired"));
            }

            LocalDateTime start  = startDatePicker.getDateTimeStrict();
            LocalDateTime end    = returnDatePicker.getDateTimeStrict();

            rentService.addRent(new Rent(bike.getBikeId(), client.getId(), start, end));
            showSuccess("rent.added");
            parentPanel.loadData();
            closeDialog();

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }
}
