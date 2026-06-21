package AI2.View.Bike;

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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel zarządzania rowerami.
 * Wykorzystuje {@link BikeModelService} i {@link BikeTypeService} do rozwiązywania nazw.
 *
 * @author Rafał Wojciechowski
 */
public class BikePanel extends BaseListPanel {

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

    /** Przycisk otwierający formularz wypożyczenia dla zaznaczonego roweru. */
    private JButton rentButton;

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
    }

    /** {@inheritDoc} */
    @Override
    protected void initExtraListeners() {
        rentButton.addActionListener(e -> onRent());
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

        List<Bike> bikes = bikeService.getAllBikes();
        if (!query.isEmpty()) {
            String lower = query.toLowerCase();
            bikes = bikes.stream()
                    .filter(b -> {
                        BikeModel bm = bikeModelService.getBikeModelById(b.getBikeModelId());
                        BikeType  bt = bikeTypeService.getBikeTypeById(b.getBikeTypeId());
                        String brand = bm != null ? bm.getBrand().toLowerCase() : "";
                        String model = bm != null ? bm.getModel().toLowerCase() : "";
                        String type  = bt != null ? bt.getBikeTypeName().toLowerCase() : "";
                        return brand.contains(lower) || model.contains(lower) || type.contains(lower);
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
