package AI2.View.Bike;

import AI2.Model.Bike;
import AI2.Service.BikeService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseListPanel;
import AI2.View.ViewModel.BikeViewModel;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel zarządzania rowerami.
 */
public class BikePanel extends BaseListPanel {

    private final BikeService bikeService;

    public BikePanel(BikeService bikeService) {
        this.bikeService = bikeService;
        loadData();
    }

    // ----------------------------------------------------------------
    // BaseListPanel
    // ----------------------------------------------------------------

    @Override
    protected String getTitleKey() {
        return "bike.management";
    }

    @Override
    protected String[] getColumnNames() {
        return new String[]{
                LanguageManager.getString("bike.brand"),
                LanguageManager.getString("bike.model"),
                LanguageManager.getString("bike.type"),
                LanguageManager.getString("bike.wheelSize"),
                LanguageManager.getString("bike.status")
        };
    }

    @Override
    public void loadData() {
        String query = searchField != null ? searchField.getText().trim() : "";

        List<Bike> bikes = bikeService.getAllBikes();
        if (!query.isEmpty()) {
            String lower = query.toLowerCase();
            bikes = bikes.stream()
                    .filter(b -> b.getBrand().toLowerCase().contains(lower)
                            || b.getModel().toLowerCase().contains(lower)
                            || b.getType().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }

        clearTable();
        for (Bike bike : bikes) {
            BikeViewModel vm = new BikeViewModel(bike);
            addRow(vm.getId(), vm.toRow());
        }
    }

    @Override
    protected void filterTable(String query) {
        loadData();
    }

    @Override
    protected void onAdd() {
        openDialog(
                LanguageManager.getString("bike.nameAdd"),
                new AddBikePanel(bikeService, this),
                500, 400
        );
    }

    @Override
    protected void onEdit(int row) {
        int id = getSelectedId();
        if (id == -1) return;

        Bike bike = bikeService.getBikeById(id);
        if (bike == null) {
            JOptionPane.showMessageDialog(this,
                    LanguageManager.getString("error.title"));
            return;
        }

        openDialog(
                LanguageManager.getString("bike.editTitle"),
                new EditBikePanel(bikeService, bike, this),
                500, 400
        );
    }

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
            bikeService.removeBike(id);
            loadData();
        }
    }
}
