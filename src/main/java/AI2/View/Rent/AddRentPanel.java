package AI2.View.Rent;

import AI2.Model.Bike;
import AI2.Model.BikeModel;
import AI2.Model.Client;
import AI2.Model.Rent;
import AI2.Service.BikeModelService;
import AI2.Service.BikeService;
import AI2.Service.BikeTypeService;
import AI2.Service.ClientService;
import AI2.Service.RentService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseFormPanel;
import com.github.lgooddatepicker.components.DateTimePicker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Panel formularza dodawania nowego wypożyczenia.
 * <p>
 * Kolejność pól: klient → data rozpoczęcia → data zwrotu → rowery.
 * Przycisk dodawania roweru jest aktywny dopiero po wybraniu obu dat.
 * Można wybrać wiele rowerów – każdy zostanie zapisany jako osobne wypożyczenie.
 *
 * @author Tomasz Piłat
 */
public class AddRentPanel extends BaseFormPanel {

    /** Serwis wypożyczeń. */
    private final RentService rentService;

    /** Serwis rowerów. */
    private final BikeService bikeService;

    /** Serwis modeli rowerów. */
    private final BikeModelService bikeModelService;

    /** Serwis typów rowerów. */
    private final BikeTypeService bikeTypeService;

    /** Serwis klientów. */
    private final ClientService clientService;

    /** Akcja wykonywana po pomyślnym dodaniu wypożyczenia/wypożyczeń. */
    private final Runnable onSuccess;

    private Client selectedClient;
    private final List<Bike> selectedBikes  = new ArrayList<>();


    private JTextField clientDisplayField;
    private JButton selectClientBtn;
    private JPanel clientSelectorPanel;

    private DateTimePicker startDatePicker;
    private DateTimePicker returnDatePicker;
    private JPanel presetPanel;

    private DefaultListModel<String> bikesListModel;
    private JList<String> bikesList;
    private JButton addBikeBtn;
    private JButton removeBikeBtn;
    private JPanel bikesPanel;

    private JTextArea notesArea;

    /**
     * Konstruktor z panelu wypożyczeń – pełny zestaw serwisów, brak pre-selekcji.
     *
     * @param rentService serwis wypożyczeń
     * @param clientService serwis klientów
     * @param bikeService serwis rowerów
     * @param bikeModelService serwis modeli rowerów
     * @param bikeTypeService serwis typów rowerów
     * @param onSuccess akcja po pomyślnym zapisie
     * @author Tomasz Piłat
     */
    public AddRentPanel(RentService rentService, ClientService clientService,
                        BikeService bikeService, BikeModelService bikeModelService,
                        BikeTypeService bikeTypeService, Runnable onSuccess) {
        this(rentService, clientService, bikeService, bikeModelService, bikeTypeService,
                onSuccess, null, null);
    }

    /**
     * Konstruktor z panelu klientów – klient jest pre-wybrany.
     *
     * @param rentService serwis wypożyczeń
     * @param clientService serwis klientów
     * @param bikeService serwis rowerów
     * @param bikeModelService serwis modeli rowerów
     * @param bikeTypeService serwis typów rowerów
     * @param preClient pre-wybrany klient
     * @author Tomasz Piłat
     */
    public AddRentPanel(RentService rentService, ClientService clientService,
                        BikeService bikeService, BikeModelService bikeModelService,
                        BikeTypeService bikeTypeService, Client preClient) {
        this(rentService, clientService, bikeService, bikeModelService, bikeTypeService,
                () -> {}, preClient, null);
    }

    /**
     * Konstruktor z panelu klientów – klient pre-wybrany, własna akcja po zapisie.
     *
     * @param rentService serwis wypożyczeń
     * @param clientService serwis klientów
     * @param bikeService serwis rowerów
     * @param bikeModelService serwis modeli rowerów
     * @param bikeTypeService serwis typów rowerów
     * @param onSuccess akcja po pomyślnym zapisie
     * @param preClient pre-wybrany klient
     * @author Tomasz Piłat
     */
    public AddRentPanel(RentService rentService, ClientService clientService,
                        BikeService bikeService, BikeModelService bikeModelService,
                        BikeTypeService bikeTypeService, Runnable onSuccess, Client preClient) {
        this(rentService, clientService, bikeService, bikeModelService, bikeTypeService,
                onSuccess, preClient, null);
    }

    /**
     * Konstruktor z panelu rowerów – rower jest pre-wybrany.
     *
     * @param rentService serwis wypożyczeń
     * @param clientService serwis klientów
     * @param bikeService serwis rowerów
     * @param bikeModelService serwis modeli rowerów
     * @param bikeTypeService serwis typów rowerów
     * @param preBike pre-wybrany rower
     * @author Tomasz Piłat
     */
    public AddRentPanel(RentService rentService, ClientService clientService,
                        BikeService bikeService, BikeModelService bikeModelService,
                        BikeTypeService bikeTypeService, Bike preBike) {
        this(rentService, clientService, bikeService, bikeModelService, bikeTypeService,
                () -> {}, null, preBike);
    }

    /**
     * Konstruktor z panelu wypożyczeń – brak pre-selekcji, odświeża RentPanel po zapisie.
     * (Zachowany dla wstecznej kompatybilności.)
     *
     * @param rentService serwis wypożyczeń
     * @param clientService serwis klientów
     * @param parentPanel nadrzędny panel listy wypożyczeń
     * @param bikeService serwis rowerów
     * @author Tomasz Piłat
     */
    public AddRentPanel(RentService rentService, ClientService clientService,
                        RentPanel parentPanel, BikeService bikeService) {
        this(rentService, clientService, bikeService, null, null,
                parentPanel::loadData, null, null);
    }

    /**
     * Konstruktor główny – wspólna logika wszystkich wariantów.
     *
     * @param rentService serwis wypożyczeń
     * @param clientService serwis klientów
     * @param bikeService serwis rowerów
     * @param bikeModelService serwis modeli rowerów (może być {@code null})
     * @param bikeTypeService serwis typów rowerów (może być {@code null})
     * @param onSuccess akcja po pomyślnym zapisie
     * @param preClient pre-wybrany klient (może być {@code null})
     * @param preBike pre-wybrany rower (może być {@code null})
     * @author Tomasz Piłat
     */
    private AddRentPanel(RentService rentService, ClientService clientService,
                         BikeService bikeService, BikeModelService bikeModelService,
                         BikeTypeService bikeTypeService, Runnable onSuccess,
                         Client preClient, Bike preBike) {
        this.rentService = rentService;
        this.clientService = clientService;
        this.bikeService = bikeService;
        this.bikeModelService = bikeModelService;
        this.bikeTypeService = bikeTypeService;
        this.onSuccess = onSuccess;
        this.selectedClient = preClient;
        if (preBike != null) {
            this.selectedBikes.add(preBike);
        }
        init();
    }

    /** {@inheritDoc} */
    @Override
    protected String getTitleKey() { return "rent.nameAdd"; }

    /** {@inheritDoc} */
    @Override
    protected String getSubmitButtonKey() { return "button.add"; }

    /** {@inheritDoc} */
    @Override
    protected void initFormComponents() {
        Dimension pickerSize = defaultFieldSize();

        clientDisplayField = new JTextField();
        clientDisplayField.setEditable(false);
        clientDisplayField.setPreferredSize(new Dimension(220, 35));


        selectClientBtn = makePlusButton();
        selectClientBtn.addActionListener(e -> openClientDialog());
        clientSelectorPanel = buildSelectorPanel(clientDisplayField, selectClientBtn);

        if (selectedClient != null) {
            clientDisplayField.setText(clientLabel(selectedClient));
            selectClientBtn.setEnabled(false);
        } else {
            clientDisplayField.setText(LanguageManager.getString("dialog.none.selected"));
        }


        startDatePicker = new DateTimePicker();
        startDatePicker.setPreferredSize(pickerSize);
        startDatePicker.setDateTimeStrict(LocalDateTime.now());
        returnDatePicker = new DateTimePicker();
        returnDatePicker.setPreferredSize(pickerSize);

        startDatePicker.getDatePicker().addDateChangeListener(e -> onDatesChanged());
        startDatePicker.getTimePicker().addTimeChangeListener(e -> onDatesChanged());
        returnDatePicker.getDatePicker().addDateChangeListener(e -> onDatesChanged());
        returnDatePicker.getTimePicker().addTimeChangeListener(e -> onDatesChanged());


        JButton startCalBtn = startDatePicker.getDatePicker().getComponentToggleCalendarButton();
        JButton returnCalBtn = returnDatePicker.getDatePicker().getComponentToggleCalendarButton();
        JButton startTimeBtn = startDatePicker.getTimePicker().getComponentToggleTimeMenuButton();
        JButton returnTimeBtn = returnDatePicker.getTimePicker().getComponentToggleTimeMenuButton();

        javax.swing.border.Border flatBorder = UIManager.getBorder("Button.border");

        if (startCalBtn != null) {
            startCalBtn.setBorder(flatBorder);
            startCalBtn.putClientProperty("JButton.buttonType", "roundRect");
        }
        if (returnCalBtn != null) {
            returnCalBtn.setBorder(flatBorder);
            returnCalBtn.putClientProperty("JButton.buttonType", "roundRect");
        }
        if (startTimeBtn != null) {
            startTimeBtn.setBorder(flatBorder);
            startTimeBtn.putClientProperty("JButton.buttonType", "roundRect");
        }
        if (returnTimeBtn != null) {
            returnTimeBtn.setBorder(flatBorder);
            returnTimeBtn.putClientProperty("JButton.buttonType", "roundRect");
        }
        presetPanel = buildPresetPanel();

        bikesListModel = new DefaultListModel<>();
        bikesList = new JList<>(bikesListModel);
        bikesList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bikesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bikesList.setFixedCellHeight(28);
        bikesList.setVisibleRowCount(5);   // JList sam liczy preferowaną wysokość

        // Pre-wybrany rower (jeśli jest)
        for (Bike b : selectedBikes) {
            bikesListModel.addElement(bikeLabel(b));
        }

        addBikeBtn = makePlusButton();
        addBikeBtn.setEnabled(false);
        addBikeBtn.setToolTipText(LanguageManager.getString("error.rent.selectDatesFirst"));
        addBikeBtn.addActionListener(e -> openBikeDialog());

        removeBikeBtn = new JButton("−");
        removeBikeBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        removeBikeBtn.setPreferredSize(new Dimension(40, 35));
        removeBikeBtn.setMargin(new Insets(0, 0, 0, 0));
        removeBikeBtn.setFocusPainted(false);
        removeBikeBtn.setEnabled(false);
        removeBikeBtn.addActionListener(e -> removeSelectedBike());

        bikesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                removeBikeBtn.setEnabled(bikesList.getSelectedIndex() != -1);
            }
        });

        JPanel bikeBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        bikeBtnPanel.add(addBikeBtn);
        bikeBtnPanel.add(removeBikeBtn);

        JScrollPane bikesScroll = new JScrollPane(bikesList);
        bikesScroll.setMinimumSize(new Dimension(280, 28 * 3));

        bikesPanel = new JPanel(new BorderLayout(0, 4));
        bikesPanel.add(bikesScroll,   BorderLayout.CENTER);
        bikesPanel.add(bikeBtnPanel,  BorderLayout.SOUTH);

        notesArea = new JTextArea(3, 20);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
    }

    /** {@inheritDoc} */
    @Override
    protected void buildForm(JPanel formPanel, GridBagConstraints gbc) {
        addFormRow(formPanel, gbc, "client.name",      clientSelectorPanel);
        addFormRow(formPanel, gbc, "date.startDate",  startDatePicker);
        addFormRow(formPanel, gbc, "rent.duration",   presetPanel);
        addFormRow(formPanel, gbc, "date.endDate",    returnDatePicker);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.gridx = 0; gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel(LanguageManager.getString("rent.selectedBikes")), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(bikesPanel, gbc);
        gbc.gridy++;

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        addFormRow(formPanel, gbc, "rent.notes", new JScrollPane(notesArea));
    }

    /** {@inheritDoc} */
    @Override
    protected void onSubmit() {
        try {
            if (selectedClient == null) {
                throw new IllegalArgumentException(
                        LanguageManager.getString("error.clientNotSelected"));
            }
            if (selectedBikes.isEmpty()) {
                throw new IllegalArgumentException(
                        LanguageManager.getString("error.rent.noBikesSelected"));
            }
            LocalDateTime start = startDatePicker.getDateTimeStrict();
            LocalDateTime end   = returnDatePicker.getDateTimeStrict();
            if (start == null || end == null) {
                throw new IllegalArgumentException(
                        LanguageManager.getString("error.dateRequired"));
            }
            if (!end.isAfter(start)) {
                throw new IllegalArgumentException(
                        LanguageManager.getString("error.rent.dateRange"));
            }

            String notes = notesArea.getText().trim();

            List<String> errors = new ArrayList<>();
            int added = 0;
            for (Bike bike : selectedBikes) {
                try {
                    Rent rent = new Rent(bike.getBikeId(), selectedClient.getId(), start, end);
                    rent.setNotes(notes);
                    rentService.addRent(rent);
                    added++;
                } catch (Exception ex) {
                    errors.add("ID " + bike.getBikeId() + ": " + ex.getMessage());
                }
            }

            if (!errors.isEmpty()) {
                showError(String.join("\n", errors));
            }
            if (added > 0) {
                onSuccess.run();
                closeDialog();
            }
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Reaguje na zmianę dat – włącza/wyłącza przycisk dodawania roweru.
     *
     * @author Tomasz Piłat
     */
    private void onDatesChanged() {
        LocalDateTime start = startDatePicker.getDateTimeStrict();
        LocalDateTime end = returnDatePicker.getDateTimeStrict();
        boolean datesValid = start != null && end != null && end.isAfter(start);
        addBikeBtn.setEnabled(datesValid && bikeModelService != null && bikeTypeService != null);
        addBikeBtn.setToolTipText(datesValid
                ? null
                : LanguageManager.getString("error.rent.selectDatesFirst"));
    }

    /**
     * Otwiera okno wyboru klienta.
     *
     * @author Tomasz Piłat
     */
    private void openClientDialog() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        ClientSelectDialog dlg = new ClientSelectDialog(owner, clientService);
        dlg.setVisible(true);
        Client c = dlg.getSelectedClient();
        if (c != null) {
            selectedClient = c;
            clientDisplayField.setText(clientLabel(c));
        }
    }

    /**
     * Otwiera okno wyboru rowerów (tylko jeśli daty są ustawione).
     * Już wybrane rowery są przekazywane jako wykluczone, więc dialog
     * pokazuje aktualnie dostępną pulę. Zwrócona lista (może mieć wiele rowerów)
     * jest dołączana do istniejących wyborów.
     *
     * @author Tomasz Piłat
     */
    private void openBikeDialog() {
        if (bikeModelService == null || bikeTypeService == null) {
            showError(LanguageManager.getString("error.bike.modelRequired"));
            return;
        }
        LocalDateTime start = startDatePicker.getDateTimeStrict();
        LocalDateTime end = returnDatePicker.getDateTimeStrict();
        if (start == null || end == null || !end.isAfter(start)) {
            showError(LanguageManager.getString("error.rent.selectDatesFirst"));
            return;
        }

        Set<Integer> excluded = selectedBikes.stream()
                .map(Bike::getBikeId)
                .collect(java.util.stream.Collectors.toSet());

        Window owner = SwingUtilities.getWindowAncestor(this);
        BikeSelectDialog dlg = new BikeSelectDialog(
                owner, bikeService, bikeModelService, bikeTypeService,
                rentService, start, end, excluded);
        dlg.setVisible(true);

        for (Bike b : dlg.getSelectedBikes()) {
            selectedBikes.add(b);
            bikesListModel.addElement(bikeLabel(b));
        }
    }

    /**
     * Buduje panel z przyciskami presetów czasu trwania (JToggleButton w ButtonGroup).
     * Kliknięcie presetu ustawia datę zwrotu = start + wybrany okres.
     *
     * @return panel z presetami
     * @author Tomasz Piłat
     */
    private JPanel buildPresetPanel() {
        long[]   minutes = {120, 240, 1440, 4320, 10080, 20160};
        String[] keys    = {
                "rent.preset.2h", "rent.preset.4h",
                "rent.preset.1day", "rent.preset.3days",
                "rent.preset.7days", "rent.preset.14days"
        };

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panel.setOpaque(false);
        ButtonGroup group = new ButtonGroup();

        for (int i = 0; i < minutes.length; i++) {
            JToggleButton btn = new JToggleButton(LanguageManager.getString(keys[i]));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btn.setFocusPainted(false);
            btn.setMargin(new Insets(4, 12, 4, 12));
            btn.putClientProperty("JButton.buttonType", "roundRect");

            final long m = minutes[i];
            btn.addActionListener(e -> applyPreset(m));
            group.add(btn);
            panel.add(btn);
        }

        return panel;
    }
    /**
     * Ustawia datę zwrotu na podstawie daty startu + wybrany czas trwania.
     * Jeśli data startu nie jest ustawiona, używa bieżącego czasu.
     *
     * @param minutes liczba minut czasu trwania
     * @author Tomasz Piłat
     */
    private void applyPreset(long minutes) {
        LocalDateTime start = startDatePicker.getDateTimeStrict();
        if (start == null) start = LocalDateTime.now();
        returnDatePicker.setDateTimeStrict(start.plusMinutes(minutes));
        onDatesChanged();
    }

    /**
     * Usuwa zaznaczony rower z listy wybranych.
     *
     * @author Tomasz Piłat
     */
    private void removeSelectedBike() {
        int idx = bikesList.getSelectedIndex();
        if (idx >= 0 && idx < selectedBikes.size()) {
            selectedBikes.remove(idx);
            bikesListModel.remove(idx);
        }
    }

    /**
     * Buduje etykietę tekstową klienta.
     *
     * @param c klient
     * @return opis klienta
     * @author Tomasz Piłat
     */
    private static String clientLabel(Client c) {
        return c.getName() + " " + c.getSurname() + "  [" + c.getEvidence() + "]";
    }

    /**
     * Buduje etykietę tekstową roweru (marka model [ID]).
     *
     * @param b rower
     * @return opis roweru
     * @author Tomasz Piłat
     */
    private String bikeLabel(Bike b) {
        String prefix = "#" + b.getBikeId() + "  ";
        if (bikeModelService != null) {
            BikeModel bm = bikeModelService.getBikeModelById(b.getBikeModelId());
            if (bm != null) {
                return prefix + bm.getBrand() + " " + bm.getModel() + "  " + b.getWheelSize() + "\"";
            }
        }
        return prefix;
    }

    /**
     * Tworzy przycisk "+" do otwierania okna wyboru.
     *
     * @return skonfigurowany przycisk
     * @author Tomasz Piłat
     */
    private JButton makePlusButton() {
        JButton btn = new JButton("+");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setPreferredSize(new Dimension(40, 35));
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setFocusPainted(false);
        return btn;
    }

    /**
     * Buduje panel selektora z polem tekstowym i przyciskiem "+" po prawej.
     *
     * @param field   pole tekstowe wyświetlające wybrany element
     * @param plusBtn przycisk "+"
     * @return panel selektora
     * @author Tomasz Piłat
     */
    private JPanel buildSelectorPanel(JTextField field, JButton plusBtn) {
        JPanel panel = new JPanel(new BorderLayout(4, 0));
        panel.setPreferredSize(new Dimension(280, 35));
        panel.add(field,   BorderLayout.CENTER);
        panel.add(plusBtn, BorderLayout.EAST);
        return panel;
    }
}
