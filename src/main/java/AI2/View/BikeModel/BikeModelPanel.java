package AI2.View.BikeModel;

import AI2.Model.BikeModel;
import AI2.Service.BikeModelService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseListPanel;
import AI2.View.ViewModel.BikeModelViewModel;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel zarządzania modelami rowerów.
 * ID modeli NIE jest wyświetlane w tabeli.
 *
 * @author Rafał Wojciechowski
 */
public class BikeModelPanel extends BaseListPanel {

    /** Serwis modeli rowerów. */
    private final BikeModelService bikeModelService;

    /**
     * Tworzy panel zarządzania modelami rowerów.
     *
     * @param bikeModelService serwis modeli rowerów
     * @author Rafał Wojciechowski
     */
    public BikeModelPanel(BikeModelService bikeModelService) {
        this.bikeModelService = bikeModelService;
        loadData();
    }

    /** {@inheritDoc} */
    @Override
    protected String getTitleKey() { return "bikeModel.management"; }

    /** {@inheritDoc} */
    @Override
    protected String[] getColumnNames() {
        return new String[]{
                LanguageManager.getString("bikeModel.brand"),
                LanguageManager.getString("bikeModel.model")
        };
    }

    /** {@inheritDoc} */
    @Override
    public void loadData() {
        String query = searchField != null ? searchField.getText().trim() : "";

        List<BikeModel> models = bikeModelService.getAllBikeModels();
        if (!query.isEmpty()) {
            String lower = query.toLowerCase();
            models = models.stream()
                    .filter(bm -> bm.getBrand().toLowerCase().contains(lower)
                            || bm.getModel().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }

        clearTable();
        for (BikeModel bm : models) {
            addRow(new BikeModelViewModel(bm).getId(),
                   new BikeModelViewModel(bm).toRow());
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void filterTable(String query) { loadData(); }

    /** {@inheritDoc} */
    @Override
    protected void onAdd() {
        openDialog(
                LanguageManager.getString("bikeModel.nameAdd"),
                new AddBikeModelPanel(bikeModelService, this),
                480, 260
        );
    }

    /** {@inheritDoc} */
    @Override
    protected void onEdit(int row) {
        int id = getSelectedId();
        if (id == -1) return;

        BikeModel bm = bikeModelService.getBikeModelById(id);
        if (bm == null) {
            JOptionPane.showMessageDialog(this, LanguageManager.getString("error.title"));
            return;
        }

        openDialog(
                LanguageManager.getString("bikeModel.editTitle"),
                new EditBikeModelPanel(bikeModelService, bm, this),
                480, 260
        );
    }

    /** {@inheritDoc} */
    @Override
    protected void onDelete(int row) {
        int id = getSelectedId();
        if (id == -1) return;

        int result = JOptionPane.showConfirmDialog(
                this,
                LanguageManager.getString("bikeModel.deleteConfirm"),
                LanguageManager.getString("button.delete"),
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            try {
                bikeModelService.removeBikeModel(id);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
