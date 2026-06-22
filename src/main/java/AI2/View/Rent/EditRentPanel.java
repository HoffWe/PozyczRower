package AI2.View.Rent;

import AI2.Enums.RentStatus;
import AI2.Model.Bike;
import AI2.Model.BikeModel;
import AI2.Model.BikeType;
import AI2.Model.Rent;
import AI2.Service.BikeModelService;
import AI2.Service.BikeService;
import AI2.Service.BikeTypeService;
import AI2.Service.RentService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseFormPanel;
import com.github.lgooddatepicker.components.DateTimePicker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

/**
 * Panel formularza edycji wypożyczenia z logiką warunkową wg statusu:
 * <ul>
 *   <li>SCHEDULED – edytowalne: model roweru, daty; klient jest zablokowany</li>
 *   <li>ACTIVE – edytowalna tylko data zakończenia</li>
 * </ul>
 *
 * @author Tomasz Piłat
 */
public class EditRentPanel extends BaseFormPanel {

    /** Serwis wypożyczeń. */
    private final RentService rentService;

    /** Serwis rowerów. */
    private final BikeService bikeService;

    /** Serwis modeli rowerów. */
    private final BikeModelService bikeModelService;

    /** Serwis typów rowerów. */
    private final BikeTypeService bikeTypeService;

    /** Akcja po pomyślnym zapisie (np. odświeżenie listy nadrzędnej). */
    private final Runnable onSuccess;

    /** Edytowane wypożyczenie. */
    private final Rent rent;

    /** Etykieta wyświetlająca klienta (tylko do odczytu). */
    private JLabel clientLabel;

    /** Selektor roweru (tylko dla SCHEDULED). */
    private JTextField bikeDisplayField;
    private JButton    selectBikeBtn;
    private JPanel     bikeSelectorPanel;
    private Bike       selectedBike;

    /** Wybór dat. */
    private DateTimePicker startDatePicker;
    private DateTimePicker returnDatePicker;

    /** Panel z presetami czasu trwania. */
    private JPanel presetPanel;

    /** Uwagi do wypożyczenia. */
    private JTextArea notesArea;

    /**
     * Tworzy panel edycji wypożyczenia.
     *
     * @param rentService      serwis wypożyczeń
     * @param bikeService      serwis rowerów
     * @param bikeModelService serwis modeli rowerów
     * @param bikeTypeService  serwis typów rowerów
     * @param onSuccess        akcja po pomyślnym zapisie (np. {@code parentPanel::loadData})
     * @param rent             wypożyczenie do edycji
     * @author Tomasz Piłat
     */
    public EditRentPanel(RentService rentService, BikeService bikeService,
                         BikeModelService bikeModelService, BikeTypeService bikeTypeService,
                         Runnable onSuccess, Rent rent) {
        this.rentService      = rentService;
        this.bikeService      = bikeService;
        this.bikeModelService = bikeModelService;
        this.bikeTypeService  = bikeTypeService;
        this.onSuccess        = onSuccess;
        this.rent             = rent;
        init();
    }

    /** {@inheritDoc} */
    @Override
    protected String getTitleKey() { return "rent.editTitle"; }

    /** {@inheritDoc} */
    @Override
    protected String getSubmitButtonKey() { return "button.save"; }

    /** {@inheritDoc} */
    @Override
    protected void initFormComponents() {
        Dimension size = defaultFieldSize();

        // Klient – zawsze tylko do odczytu
        clientLabel = new JLabel();
        clientLabel.setPreferredSize(size);

        // Rower – selektor (tylko dla SCHEDULED)
        Bike currentBike = bikeService.getBikeById(rent.getBikeId());
        selectedBike = currentBike;

        bikeDisplayField = new JTextField(bikeLabel(currentBike));
        bikeDisplayField.setEditable(false);
        bikeDisplayField.setBackground(Color.WHITE);
        bikeDisplayField.setPreferredSize(new Dimension(200, 35));

        selectBikeBtn = new JButton("+");
        selectBikeBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        selectBikeBtn.setPreferredSize(new Dimension(40, 35));
        selectBikeBtn.setMargin(new Insets(0, 0, 0, 0));
        selectBikeBtn.setFocusPainted(false);
        selectBikeBtn.addActionListener(e -> openBikeDialog());

        bikeSelectorPanel = new JPanel(new BorderLayout(4, 0));
        bikeSelectorPanel.setBackground(Color.WHITE);
        bikeSelectorPanel.setPreferredSize(new Dimension(280, 35));
        bikeSelectorPanel.add(bikeDisplayField, BorderLayout.CENTER);
        bikeSelectorPanel.add(selectBikeBtn,    BorderLayout.EAST);

        // Daty
        startDatePicker  = new DateTimePicker(); startDatePicker.setPreferredSize(size);
        returnDatePicker = new DateTimePicker(); returnDatePicker.setPreferredSize(size);

        if (rent.getRentDate()   != null) startDatePicker.setDateTimeStrict(rent.getRentDate());
        if (rent.getReturnTime() != null) returnDatePicker.setDateTimeStrict(rent.getReturnTime());

        // Zablokowania wg statusu
        boolean isScheduled  = rent.getStatus() == RentStatus.SCHEDULED;
        boolean isActive     = rent.getStatus() == RentStatus.ACTIVE;
        boolean isPending    = rent.getStatus() == RentStatus.PENDING;
        boolean canEditDates = isScheduled || isActive || isPending;

        selectBikeBtn.setEnabled(isScheduled);
        startDatePicker.setEnabled(isScheduled);

        // Dla ACTIVE i PENDING tylko data zakończenia edytowalna; dla pozostałych statusów – zablokowane
        returnDatePicker.setEnabled(canEditDates);

        // Presety czasu trwania (dostępne dla SCHEDULED i ACTIVE)
        presetPanel = buildPresetPanel(canEditDates);

        // Uwagi – zawsze edytowalne niezależnie od statusu
        notesArea = new JTextArea(3, 20);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setText(rent.getNotes());
    }

    /** {@inheritDoc} */
    @Override
    protected void buildForm(JPanel formPanel, GridBagConstraints gbc) {
        addFormRow(formPanel, gbc, "client.name",    clientLabel);
        addFormRow(formPanel, gbc, "bike.name",      bikeSelectorPanel);
        addFormRow(formPanel, gbc, "date.startDate", startDatePicker);
        addFormRow(formPanel, gbc, "rent.duration",  presetPanel);
        addFormRow(formPanel, gbc, "date.endDate",   returnDatePicker);

        // Uwagi – fill=BOTH + weighty, żeby GridBag przydzielił wysokość dla JScrollPane
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        addFormRow(formPanel, gbc, "rent.notes",     new JScrollPane(notesArea));
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.0;
    }

    /** {@inheritDoc} */
    @Override
    protected void onSubmit() {
        try {
            boolean isScheduled  = rent.getStatus() == RentStatus.SCHEDULED;
            boolean isActive     = rent.getStatus() == RentStatus.ACTIVE;
            boolean isPending    = rent.getStatus() == RentStatus.PENDING;
            boolean canEditDates = isScheduled || isActive || isPending;

            if (canEditDates) {
                // Pełna edycja dat i roweru
                if (returnDatePicker.getDateTimeStrict() == null) {
                    throw new IllegalArgumentException(
                            LanguageManager.getString("error.dateRequired"));
                }
                if (isScheduled) {
                    if (startDatePicker.getDateTimeStrict() == null) {
                        throw new IllegalArgumentException(
                                LanguageManager.getString("error.dateRequired"));
                    }
                    rent.setRentDate(startDatePicker.getDateTimeStrict());
                    if (selectedBike != null) {
                        rent.setBikeId(selectedBike.getBikeId());
                    }
                }
                rent.setReturnTime(returnDatePicker.getDateTimeStrict());
                rent.setNotes(notesArea.getText().trim());
                rentService.updateRent(rent);
            } else {
                // Wyłącznie aktualizacja uwag (status FINISHED / OVERDUE / CLOSED)
                rentService.updateNotes(rent.getId(), notesArea.getText().trim());
            }

            showSuccess("rent.updated");
            if (onSuccess != null) onSuccess.run();
            closeDialog();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Otwiera okno dialogowe wyboru roweru.
     * Przekazuje daty z formularza, aby dialog pokazał tylko rowery dostępne w tym okresie.
     *
     * @author Tomasz Piłat
     */
    private void openBikeDialog() {
        if (startDatePicker.getDateTimeStrict() == null
                || returnDatePicker.getDateTimeStrict() == null) {
            showError(LanguageManager.getString("error.rent.selectDatesFirst"));
            return;
        }
        Window owner = SwingUtilities.getWindowAncestor(this);
        BikeSelectDialog dlg = new BikeSelectDialog(owner, bikeService,
                bikeModelService, bikeTypeService, rentService,
                startDatePicker.getDateTimeStrict(),
                returnDatePicker.getDateTimeStrict());
        dlg.setVisible(true);
        Bike b = dlg.getSelectedBike();
        if (b != null) {
            selectedBike = b;
            bikeDisplayField.setText(bikeLabel(b));
        }
    }

    /**
     * Buduje czytelny opis roweru na podstawie modelu i typu.
     *
     * @param bike rower
     * @return tekstowy opis roweru
     * @author Tomasz Piłat
     */
    private String bikeLabel(Bike bike) {
        if (bike == null) return LanguageManager.getString("dialog.none.selected");
        BikeModel bm = bikeModelService.getBikeModelById(bike.getBikeModelId());
        BikeType  bt = bikeTypeService.getBikeTypeById(bike.getBikeTypeId());
        String model = bm != null ? bm.getBrand() + " " + bm.getModel() : "?";
        String type  = bt != null ? bt.getDisplayName() : "?";
        return model + "  [" + type + "]";
    }

    /**
     * Buduje panel z przyciskami presetów czasu trwania.
     *
     * @param enabled czy presety mają być aktywne
     * @return panel z presetami
     * @author Tomasz Piłat
     */
    private JPanel buildPresetPanel(boolean enabled) {
        long[]   minutes = {120, 240, 1440, 4320, 10080, 20160};
        String[] keys    = {
            "rent.preset.2h", "rent.preset.4h",
            "rent.preset.1day", "rent.preset.3days",
            "rent.preset.7days", "rent.preset.14days"
        };

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panel.setBackground(Color.WHITE);
        ButtonGroup group = new ButtonGroup();

        for (int i = 0; i < minutes.length; i++) {
            JToggleButton btn = new JToggleButton(LanguageManager.getString(keys[i]));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btn.setPreferredSize(new Dimension(72, 30));
            btn.setFocusPainted(false);
            btn.setEnabled(enabled);
            final long m = minutes[i];
            btn.addActionListener(e -> applyPreset(m));
            group.add(btn);
            panel.add(btn);
        }
        return panel;
    }

    /**
     * Ustawia datę zwrotu = data startu + wybrany czas trwania.
     *
     * @param minutes liczba minut
     * @author Tomasz Piłat
     */
    private void applyPreset(long minutes) {
        LocalDateTime start = startDatePicker.getDateTimeStrict();
        if (start == null) return;
        returnDatePicker.setDateTimeStrict(start.plusMinutes(minutes));
    }
}
