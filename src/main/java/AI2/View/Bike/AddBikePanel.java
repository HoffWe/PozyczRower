package AI2.View.Bike;

import AI2.Enums.BikeStatus;
import AI2.Model.BikeModel;
import AI2.Model.BikeType;
import AI2.Service.BikeModelService;
import AI2.Service.BikeService;
import AI2.Service.BikeTypeService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseFormPanel;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

/**
 * Panel formularza dodawania nowego roweru.
 * Model roweru i typ wybierane z rozwijanych list.
 *
 * @author Rafał Wojciechowski
 */
public class AddBikePanel extends BaseFormPanel {

    /** Serwis rowerów. */
    private final BikeService bikeService;

    /** Serwis modeli rowerów. */
    private final BikeModelService bikeModelService;

    /** Serwis typów rowerów. */
    private final BikeTypeService bikeTypeService;

    /** Nadrzędny panel listy rowerów. */
    private final BikePanel parentPanel;

    /** Lista rozwijana modeli rowerów. */
    private JComboBox<BikeModel> modelCombo;

    /** Lista rozwijana typów rowerów. */
    private JComboBox<BikeType> typeCombo;

    /** Pole tekstowe rozmiaru koła. */
    private JTextField wheelSizeField;

    /** Lista rozwijana statusów rowerów. */
    private JComboBox<BikeStatus> statusCombo;

    /** Pole tekstowe opisu roweru. */
    private JTextField descriptionField;

    /**
     * Tworzy panel dodawania roweru.
     *
     * @param bikeService      serwis rowerów
     * @param bikeModelService serwis modeli rowerów
     * @param bikeTypeService  serwis typów rowerów
     * @param parentPanel      nadrzędny panel listy
     * @author Rafał Wojciechowski
     */
    public AddBikePanel(BikeService bikeService, BikeModelService bikeModelService,
                        BikeTypeService bikeTypeService, BikePanel parentPanel) {
        this.bikeService      = bikeService;
        this.bikeModelService = bikeModelService;
        this.bikeTypeService  = bikeTypeService;
        this.parentPanel      = parentPanel;
        init();
    }

    /** {@inheritDoc} */
    @Override
    protected String getTitleKey() { return "bike.nameAdd"; }

    /** {@inheritDoc} */
    @Override
    protected String getSubmitButtonKey() { return "button.add"; }

    /** {@inheritDoc} */
    @Override
    protected void initFormComponents() {
        Dimension size = defaultFieldSize();

        modelCombo = new JComboBox<>();
        modelCombo.setPreferredSize(size);
        for (BikeModel bm : bikeModelService.getAllBikeModels()) {
            modelCombo.addItem(bm);
        }

        typeCombo = new JComboBox<>();
        typeCombo.setPreferredSize(size);
        for (BikeType bt : bikeTypeService.getAllBikeTypes()) {
            typeCombo.addItem(bt);
        }

        wheelSizeField = new JTextField();
        wheelSizeField.setPreferredSize(size);
        ((AbstractDocument) wheelSizeField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int off, String str, AttributeSet a)
                    throws BadLocationException {
                if (str != null && str.chars().allMatch(Character::isDigit))
                    super.insertString(fb, off, str, a);
            }
            @Override
            public void replace(FilterBypass fb, int off, int len, String str, AttributeSet a)
                    throws BadLocationException {
                if (str != null && str.chars().allMatch(Character::isDigit))
                    super.replace(fb, off, len, str, a);
            }
        });
        statusCombo      = new JComboBox<>(BikeStatus.values());
        descriptionField = new JTextField(); descriptionField.setPreferredSize(size);
    }

    /** {@inheritDoc} */
    @Override
    protected void buildForm(JPanel formPanel, GridBagConstraints gbc) {
        addFormRow(formPanel, gbc, "bike.model",       modelCombo);
        addFormRow(formPanel, gbc, "bike.type",        typeCombo);
        addFormRow(formPanel, gbc, "bike.wheelSize",   wheelSizeField);
        addFormRow(formPanel, gbc, "bike.status",      statusCombo);
        addFormRow(formPanel, gbc, "bike.description", descriptionField);
    }

    /** {@inheritDoc} */
    @Override
    protected void onSubmit() {
        try {
            BikeModel selectedModel = (BikeModel) modelCombo.getSelectedItem();
            BikeType  selectedType  = (BikeType)  typeCombo.getSelectedItem();

            if (selectedModel == null) {
                throw new IllegalArgumentException(
                        LanguageManager.getString("error.bike.modelRequired"));
            }
            if (selectedType == null) {
                throw new IllegalArgumentException(
                        LanguageManager.getString("error.bike.typeRequired"));
            }

            int wheelSize = Integer.parseInt(wheelSizeField.getText().trim());
            if (wheelSize <= 0) throw new NumberFormatException();

            bikeService.addBike(
                    selectedModel.getId(),
                    selectedType.getBikeTypeId(),
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
