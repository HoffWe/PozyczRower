package AI2.View.BikeType;

import AI2.DTO.BikeTypeDTO;
import AI2.Service.BikeTypeService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseFormPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel formularza dodawania nowego typu roweru.
 */
public class AddBikeTypePanel extends BaseFormPanel {

    private final BikeTypeService bikeTypeService;
    private final BikeTypePanel parentPanel;

    private JTextField nameField;
    private JTextField nameEnField;
    private JTextField descriptionField;

    public AddBikeTypePanel(BikeTypeService bikeTypeService, BikeTypePanel parentPanel) {
        this.bikeTypeService = bikeTypeService;
        this.parentPanel     = parentPanel;
        init();
    }

    // ----------------------------------------------------------------
    // BaseFormPanel
    // ----------------------------------------------------------------

    @Override
    protected String getTitleKey() { return "bikeType.nameAdd"; }

    @Override
    protected String getSubmitButtonKey() { return "button.add"; }

    @Override
    protected void initFormComponents() {
        Dimension size = defaultFieldSize();
        nameField        = new JTextField(); nameField.setPreferredSize(size);
        nameEnField      = new JTextField(); nameEnField.setPreferredSize(size);
        descriptionField = new JTextField(); descriptionField.setPreferredSize(size);
    }

    @Override
    protected void buildForm(JPanel formPanel, GridBagConstraints gbc) {
        addFormRow(formPanel, gbc, "bikeType.nameFieldPl", nameField);
        addFormRow(formPanel, gbc, "bikeType.nameFieldEn", nameEnField);
        addFormRow(formPanel, gbc, "bikeType.descriptionField", descriptionField);
    }

    @Override
    protected void onSubmit() {
        try {
            bikeTypeService.addBikeType(new BikeTypeDTO(
                    nameField.getText().trim(),
                    nameEnField.getText().trim(),
                    descriptionField.getText().trim()
            ));
            showSuccess("bikeType.added");
            parentPanel.loadData();
            closeDialog();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }
}
