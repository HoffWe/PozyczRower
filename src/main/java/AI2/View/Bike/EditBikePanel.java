package AI2.View.Bike;

import AI2.Enums.BikeStatus;
import AI2.Model.Bike;
import AI2.Service.BikeService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseFormPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel formularza edycji istniejącego roweru.
 * Pola wypełnione danymi z przekazanego obiektu {@link Bike}.
 */
public class EditBikePanel extends BaseFormPanel {

    private final BikeService bikeService;
    private final Bike        bike;
    private final BikePanel   parentPanel;

    private JTextField brandField;
    private JTextField modelField;
    private JTextField typeField;
    private JTextField wheelSizeField;
    private JComboBox<BikeStatus> statusCombo;
    private JTextField descriptionField;

    public EditBikePanel(BikeService bikeService, Bike bike, BikePanel parentPanel) {
        this.bikeService = bikeService;
        this.bike        = bike;
        this.parentPanel = parentPanel;
        init();
    }

    // ----------------------------------------------------------------
    // BaseFormPanel
    // ----------------------------------------------------------------

    @Override
    protected String getTitleKey() { return "bike.editTitle"; }

    @Override
    protected String getSubmitButtonKey() { return "button.save"; }

    @Override
    protected void initFormComponents() {
        Dimension size = defaultFieldSize();
        brandField       = new JTextField(bike.getBrand());       brandField.setPreferredSize(size);
        modelField       = new JTextField(bike.getModel());       modelField.setPreferredSize(size);
        typeField        = new JTextField(bike.getType());        typeField.setPreferredSize(size);
        wheelSizeField   = new JTextField(String.valueOf(bike.getWheelSize())); wheelSizeField.setPreferredSize(size);
        statusCombo      = new JComboBox<>(BikeStatus.values());
        if (bike.getStatus() != null) statusCombo.setSelectedItem(bike.getStatus());
        descriptionField = new JTextField(bike.getDescription()); descriptionField.setPreferredSize(size);
    }

    @Override
    protected void buildForm(JPanel formPanel, GridBagConstraints gbc) {
        addFormRow(formPanel, gbc, "bike.brand",       brandField);
        addFormRow(formPanel, gbc, "bike.model",       modelField);
        addFormRow(formPanel, gbc, "bike.type",        typeField);
        addFormRow(formPanel, gbc, "bike.wheelSize",   wheelSizeField);
        addFormRow(formPanel, gbc, "bike.status",      statusCombo);
        addFormRow(formPanel, gbc, "bike.description", descriptionField);
    }

    @Override
    protected void onSubmit() {
        try {
            int wheelSize = Integer.parseInt(wheelSizeField.getText().trim());
            if (wheelSize <= 0) throw new NumberFormatException();

            bike.setBrand(brandField.getText().trim());
            bike.setModel(modelField.getText().trim());
            bike.setType(typeField.getText().trim());
            bike.setWheelSize(wheelSize);
            bike.setStatus((BikeStatus) statusCombo.getSelectedItem());
            bike.setDescription(descriptionField.getText().trim());

            bikeService.updateBike(bike);
            showSuccess("bike.updated");
            parentPanel.loadData();
            closeDialog();
        } catch (NumberFormatException ex) {
            showError(LanguageManager.getString("bike.invalidWheelSize"));
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }
}
