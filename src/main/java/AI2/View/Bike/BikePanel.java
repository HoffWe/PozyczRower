package AI2.View.Bike;

import AI2.Enums.BikeStatus;
import AI2.Model.Bike;
import AI2.Model.BikeModel;
import AI2.Model.BikeType;
import AI2.Service.BikeModelService;
import AI2.Service.BikeService;
import AI2.Service.BikeTypeService;
import AI2.Service.ClientService;
import AI2.Service.RentService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseListPanel;
import AI2.View.Components.AppButton;
import AI2.View.Rent.AddRentPanel;
import AI2.View.ViewModel.BikeViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel zarządzania rowerami.
 * Zawiera filtr statusu (Wszystkie / Dostępny / Wypożyczony / …) oraz
 * przycisk szybkiego wypożyczenia zaznaczonego roweru.
 *
 * @author Rafał Wojciechowski
 */
public class BikePanel extends BaseListPanel {

    // ----------------------------------------------------------------
    // Sentinel – "brak filtra statusu"
    // ----------------------------------------------------------------

    /** Obiekt sentinel reprezentujący opcję "Wszystkie" w combo filtra statusu. */
    private static final Object STATUS_ALL = "ALL";

    // ----------------------------------------------------------------
    // Serwisy
    // ----------------------------------------------------------------

    /** Serwis rowerów. */
    private final BikeService bikeService;

    /** Serwis modeli rowerów. */
    private final BikeModelService bikeModelService;

    /** Serwis typów rowerów. */
    private final BikeTypeService bikeTypeService;

    /** Serwis wypożyczeń (dla przycisku Wypożycz). */
    private final RentService rentService;

    /** Serwis klientów (dla formularza wypożyczenia). */
    private final ClientService clientService;

    // ----------------------------------------------------------------
    // Komponenty
    // ----------------------------------------------------------------

    /** Przycisk otwierający formularz wypożyczenia dla zaznaczonego roweru. */
    private JButton rentButton;

    /**
     * Combo box do filtrowania tabeli wg statusu roweru.
     * Zawiera sentinel {@link #STATUS_ALL} jako pierwszą pozycję ("Wszystkie"),
     * a następnie wszystkie wartości {@link BikeStatus}.
     */
    private JComboBox<Object> statusFilterCombo;

    // ----------------------------------------------------------------
    // Konstruktor
    // ----------------------------------------------------------------

    /**
     * Tworzy panel zarządzania rowerami.
     *
     * @param bikeService      serwis rowerów
     * @param bikeModelService serwis modeli rowerów
     * @param bikeTypeService  serwis typów rowerów
     * @param rentService      serwis wypożyczeń
     * @param clientService    serwis klientów
     * @author Rafał Wojciechowski
     */
    public BikePanel(BikeService bikeService, BikeModelService bikeModelService,
                     BikeTypeService bikeTypeService, RentService rentService,
                     ClientService clientService) {
        this.bikeService      = bikeService;
        this.bikeModelService = bikeModelService;
        this.bikeTypeService  = bikeTypeService;
        this.rentService      = rentService;
        this.clientService    = clientService;
        loadData();
    }

    // ----------------------------------------------------------------
    // BaseListPanel – dodatkowe komponenty
    // ----------------------------------------------------------------

    /** {@inheritDoc} */
    @Override
    protected void initExtraComponents() {
        rentButton = new AppButton(LanguageManager.getString("button.rent"));
        rentButton.setEnabled(false);

        // --- Filtr statusu ---
        statusFilterCombo = new JComboBox<>();
        statusFilterCombo.addItem(STATUS_ALL);
        for (BikeStatus s : BikeStatus.values()) {
            statusFilterCombo.addItem(s);
        }
        statusFilterCombo.setPreferredSize(new Dimension(160, 28));
        statusFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusFilterCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (STATUS_ALL.equals(value)) {
                    setText(LanguageManager.getString("bike.filter.all"));
                } else if (value instanceof BikeStatus) {
                    setText(((BikeStatus) value).getDisplayName());
                }
                return this;
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    protected JPanel buildFilterBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(LanguageManager.getString("bike.filter.status"));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(label);
        panel.add(statusFilterCombo);
        return panel;
    }

    /** {@inheritDoc} */
    @Override
    protected void buildExtraButtons(JPanel buttonPanel) {
        buttonPanel.add(rentButton);
    }

    /** {@inheritDoc} */
    @Override
    protected void onSelectionChanged(boolean selected) {
        rentButton.setEnabled(selected);
    }

    /** {@inheritDoc} */
    @Override
    protected void refreshLanguageTexts() {
        rentButton.setText(LanguageManager.getString("button.rent"));
        // Combo używa renderera dynamicznego – repaint wystarczy do odświeżenia etykiet
        if (statusFilterCombo != null) statusFilterCombo.repaint();
    }

    /** {@inheritDoc} */
    @Override
    protected void initExtraListeners() {
        rentButton.addActionListener(e -> onRent());
        statusFilterCombo.addActionListener(e -> loadData());
    }

    // ----------------------------------------------------------------
    // BaseListPanel – metody abstrakcyjne
    // ----------------------------------------------------------------

    /** {@inheritDoc} */
    @Override
    protected String getTitleKey() { return "bike.management"; }

    /** {@inheritDoc} */
    @Override
    protected String[] getColumnNames() {
        return new String[]{
                LanguageManager.getString("bike.id"),
                LanguageManager.getString("bike.brand"),
                LanguageManager.getString("bike.model"),
                LanguageManager.getString("bike.type"),
                LanguageManager.getString("bike.wheelSize"),
                LanguageManager.getString("bike.status"),
                LanguageManager.getString("bike.description")
        };
    }

    /** {@inheritDoc} */
    @Override
    public void loadData() {
        String query = searchField != null ? searchField.getText().trim() : "";

        // Ustal wybrany filtr statusu
        Object sel = statusFilterCombo != null ? statusFilterCombo.getSelectedItem() : STATUS_ALL;
        BikeStatus filterStatus = (sel instanceof BikeStatus) ? (BikeStatus) sel : null;

        List<Bike> bikes = bikeService.getAllBikes();

        // Filtruj wg statusu
        if (filterStatus != null) {
            final BikeStatus fs = filterStatus;
            bikes = bikes.stream()
                    .filter(b -> b.getStatus() == fs)
                    .collect(Collectors.toList());
        }

        // Filtruj wg tekstu wyszukiwania
        if (!query.isEmpty()) {
            String lower = query.toLowerCase();
            bikes = bikes.stream()
                    .filter(b -> {
                        BikeModel bm = bikeModelService.getBikeModelById(b.getBikeModelId());
                        BikeType  bt = bikeTypeService.getBikeTypeById(b.getBikeTypeId());
                        String brand  = bm != null ? bm.getBrand().toLowerCase() : "";
                        String model  = bm != null ? bm.getModel().toLowerCase() : "";
                        String type   = bt != null ? bt.getDisplayName().toLowerCase() : "";
                        String wheels = String.valueOf(b.getWheelSize());
                        return brand.contains(lower) || model.contains(lower)
                                || type.contains(lower) || wheels.contains(lower);
                    })
                    .collect(Collectors.toList());
        }

        clearTable();
        for (Bike bike : bikes) {
            BikeModel bm = bikeModelService.getBikeModelById(bike.getBikeModelId());
            BikeType  bt = bikeTypeService.getBikeTypeById(bike.getBikeTypeId());
            BikeViewModel vm = new BikeViewModel(bike, bm, bt);
            addRow(vm.getId(), vm.toRow());
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void filterTable(String query) { loadData(); }

    /** {@inheritDoc} */
    @Override
    protected void onAdd() {
        openDialog(
                LanguageManager.getString("bike.nameAdd"),
                new AddBikePanel(bikeService, bikeModelService, bikeTypeService, this),
                520, 380
        );
    }

    /** {@inheritDoc} */
    @Override
    protected void onEdit(int row) {
        int id = getSelectedId();
        if (id == -1) return;

        Bike bike = bikeService.getBikeById(id);
        if (bike == null) {
            JOptionPane.showMessageDialog(this, LanguageManager.getString("error.title"));
            return;
        }

        openDialog(
                LanguageManager.getString("bike.editTitle"),
                new EditBikePanel(bikeService, bikeModelService, bikeTypeService, bike, this),
                520, 380
        );
    }

    /** {@inheritDoc} */
    @Override
    protected void onDelete(int row) {
        int id = getSelectedId();
        if (id == -1) return;

        int result = JOptionPane.showConfirmDialog(
                this,
                LanguageManager.getString("bike.deleteConfirm"),
                LanguageManager.getString("button.delete"),
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            try {
                if (rentService.bikeHasActiveRentals(id)) {
                    throw new IllegalStateException(
                            LanguageManager.getString("error.bike.hasRents"));
                }
                bikeService.removeBike(id);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ----------------------------------------------------------------
    // Wypożycz
    // ----------------------------------------------------------------

    /**
     * Otwiera formularz dodawania wypożyczenia z pre-wybranym rowerem.
     *
     * @author Rafał Wojciechowski
     */
    private void onRent() {
        int id = getSelectedId();
        if (id == -1) return;

        Bike bike;
        try {
            bike = bikeService.getBikeById(id);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (bike == null) return;

        openDialog(
                LanguageManager.getString("rent.nameAdd"),
                new AddRentPanel(rentService, clientService, bikeService,
                        bikeModelService, bikeTypeService, bike),
                580, 600
        );
    }
}
