package AI2.View.BikeType;

import AI2.DTO.BikeTypeDTO;
import AI2.Model.BikeType;
import AI2.Service.BikeTypeService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseFormPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel formularza edycji istniejącego typu roweru.
 * Pola wypełnione danymi z przekazanego obiektu {@link BikeType}.
 */
public class EditBikeTypePanel extends BaseFormPanel {

    private final BikeTypeService bikeTypeService;
    private final BikeType bikeType;
    private final BikeTypePanel parentPanel;

    private JTextField nameField;
    private JTextField descriptionField;

    public EditBikeTypePanel(BikeTypeService bikeTypeService, BikeType bikeType,
                             BikeTypePanel parentPanel) {
        this.bikeTypeService = bikeTypeService;
        this.bikeType        = bikeType;
        this.parentPanel     = parentPanel;
        init();
    }

    // ----------------------------------------------------------------
    // BaseFormPanel
    // ----------------------------------------------------------------

    @Override
    protected String getTitleKey() { return "bikeType.editTitle"; }

    @Override
    protected String getSubmitButtonKey() { return "button.save"; }

    @Override
    protected void initFormComponents() {
        Dimension size = defaultFieldSize();
        nameField        = new JTextField(bikeType.getBikeTypeName());        nameField.setPreferredSize(size);
        descriptionField = new JTextField(bikeType.getBikeTypeDescription()); descriptionField.setPreferredSize(size);
    }

    @Override
    protected void buildForm(JPanel formPanel, GridBagConstraints gbc) {
        addFormRow(formPanel, gbc, "bikeType.nameField",        nameField);
        addFormRow(formPanel, gbc, "bikeType.descriptionField", descriptionField);
    }

    @Override
    protected void onSubmit() {
        try {
            bikeTypeService.updateBikeType(bikeType, new BikeTypeDTO(
                    nameField.getText().trim(),
                    descriptionField.getText().trim()
            ));
            showSuccess("bikeType.updated");
            parentPanel.loadData();
            closeDialog();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }
}
