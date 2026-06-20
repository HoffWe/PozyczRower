package AI2.View.Bike;

import AI2.Enums.BikeStatus;
import AI2.Service.BikeService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseFormPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel formularza dodawania nowego roweru.
 */
public class AddBikePanel extends BaseFormPanel {

    private final BikeService bikeService;
    private final BikePanel   parentPanel;

    private JTextField brandField;
    private JTextField modelField;
    private JTextField typeField;
    private JTextField wheelSizeField;
    private JComboBox<BikeStatus> statusCombo;
    private JTextField descriptionField;

    public AddBikePanel(BikeService bikeService, BikePanel parentPanel) {
        this.bikeService  = bikeService;
        this.parentPanel  = parentPanel;
        init();
    }

    // ----------------------------------------------------------------
    // BaseFormPanel
    // ----------------------------------------------------------------

    @Override
    protected String getTitleKey() { return "bike.nameAdd"; }

    @Override
    protected String getSubmitButtonKey() { return "button.add"; }

    @Override
    protected void initFormComponents() {
        Dimension size = defaultFieldSize();
        brandField       = new JTextField(); brandField.setPreferredSize(size);
        modelField       = new JTextField(); modelField.setPreferredSize(size);
        typeField        = new JTextField(); typeField.setPreferredSize(size);
        wheelSizeField   = new JTextField(); wheelSizeField.setPreferredSize(size);
        statusCombo      = new JComboBox<>(BikeStatus.values());
        descriptionField = new JTextField(); descriptionField.setPreferredSize(size);
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

            bikeService.addBike(
                    brandField.getText().trim(),
                    modelField.getText().trim(),
                    typeField.getText().trim(),
                    wheelSize,
                    (BikeStatus) statusCombo.getSelectedItem(),
                    descriptionField.getText().trim()
            );
            showSuccess("bike.added");
            parentPanel.loadData();
            closeDialog();
        } catch (NumberFormatException ex) {
            showError(LanguageManager.getString("bike.invalidWheelSize"));
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }
}
