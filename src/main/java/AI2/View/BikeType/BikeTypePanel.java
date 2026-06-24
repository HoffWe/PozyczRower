package AI2.View.BikeType;

import AI2.Model.BikeType;
import AI2.Service.BikeTypeService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseListPanel;
import AI2.View.ViewModel.BikeTypeViewModel;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel zarządzania typami rowerów.
 * Dane pobierane z serwisu
 * Tabela bez kolumny ID
 *
 * @author Adrian Karpiński
 */
public class BikeTypePanel extends BaseListPanel {

    private final BikeTypeService bikeTypeService;

    public BikeTypePanel(BikeTypeService bikeTypeService) {
        this.bikeTypeService = bikeTypeService;
        loadData();
    }

    // BaseListPanel

    @Override
    protected String getTitleKey() {
        return "bikeType.management";
    }

    @Override
    protected String[] getColumnNames() {
        return new String[]{
                LanguageManager.getString("bikeType.nameField"),
                LanguageManager.getString("bikeType.descriptionField")
        };
    }

    @Override
    public void loadData() {

        String query = searchField != null ? searchField.getText().trim() : "";
        List<BikeType> types = bikeTypeService.getAllBikeTypes();

        if (!query.isEmpty()) {
            String lower = query.toLowerCase();
            types = types.stream()
                    .filter(bt -> bt.getDisplayName().toLowerCase().contains(lower)
                            || bt.getBikeTypeDescription().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }

        clearTable();
        for (BikeType bt : types) {
            BikeTypeViewModel vm = new BikeTypeViewModel(bt);
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
                LanguageManager.getString("bikeType.nameAdd"),
                new AddBikeTypePanel(bikeTypeService, this),
                480, 300
        );
    }

    @Override
    protected void onEdit(int row) {

        int id = getSelectedId();
        if (id == -1) return;

        BikeType bikeType = bikeTypeService.getBikeTypeById(id);

        if (bikeType == null) {
            JOptionPane.showMessageDialog(this,
                    LanguageManager.getString("error.title"));
            return;
        }

        openDialog(
                LanguageManager.getString("bikeType.editTitle"),
                new EditBikeTypePanel(bikeTypeService, bikeType, this),
                480, 300
        );
    }

    @Override
    protected void onDelete(int row) {

        int id = getSelectedId();
        if (id == -1) return;

        int result = JOptionPane.showConfirmDialog(
                this,
                LanguageManager.getString("bikeType.deleteConfirm"),
                LanguageManager.getString("button.delete"),
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            try {
                bikeTypeService.removeBikeType(id);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
