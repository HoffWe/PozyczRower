package AI2.View.Bike;

import AI2.Enums.BikeStatus;
import AI2.Model.Bike;
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
 * Panel formularza edycji istniejącego roweru.
 * Pola wypełnione danymi z przekazanego obiektu {@link Bike}.
 * Model i typ wybierane z rozwijanych list.
 *
 * @author Rafał Wojciechowski
 */
public class EditBikePanel extends BaseFormPanel {

    /** Serwis rowerów. */
    private final BikeService bikeService;

    /** Serwis modeli rowerów. */
    private final BikeModelService bikeModelService;

    /** Serwis typów rowerów. */
    private final BikeTypeService bikeTypeService;

    /** Rower do edycji. */
    private final Bike bike;

    /** Nadrzędny panel listy rowerów. */
    private final BikePanel parentPanel;

    /** Etykieta modelu roweru (tylko do odczytu – modelu nie można zmieniać). */
    private JLabel modelLabel;

    /** Lista rozwijana typów rowerów. */
    private JComboBox<BikeType> typeCombo;

    /** Pole tekstowe rozmiaru koła. */
    private JTextField wheelSizeField;

    /** Lista rozwijana statusów rowerów. */
    private JComboBox<BikeStatus> statusCombo;

    /** Pole tekstowe opisu roweru. */
    private JTextField descriptionField;

    /**
     * Tworzy panel edycji roweru.
     *
     * @param bikeService      serwis rowerów
     * @param bikeModelService serwis modeli rowerów
     * @param bikeTypeService  serwis typów rowerów
     * @param bike             rower do edycji
     * @param parentPanel      nadrzędny panel listy
     * @author Rafał Wojciechowski
     */
    public EditBikePanel(BikeService bikeService, BikeModelService bikeModelService,
                         BikeTypeService bikeTypeService, Bike bike, BikePanel parentPanel) {
        this.bikeService      = bikeService;
        this.bikeModelService = bikeModelService;
        this.bikeTypeService  = bikeTypeService;
        this.bike             = bike;
        this.parentPanel      = parentPanel;
        init();
    }

    /** {@inheritDoc} */
    @Override
    protected String getTitleKey() { return "bike.editTitle"; }

    /** {@inheritDoc} */
    @Override
    protected String getSubmitButtonKey() { return "button.save"; }

    /** {@inheritDoc} */
    @Override
    protected void initFormComponents() {
        Dimension size = defaultFieldSize();

        BikeModel currentModel = bikeModelService.getAllBikeModels().stream()
                .filter(bm -> bm.getId() == bike.getBikeModelId())
                .findFirst().orElse(null);
        String modelName = currentModel != null
                ? currentModel.getBrand() + " " + currentModel.getModel()
                : "—";
        modelLabel = new JLabel(modelName);
        modelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        modelLabel.setPreferredSize(size);

        typeCombo = new JComboBox<>();
        typeCombo.setPreferredSize(size);
        for (BikeType bt : bikeTypeService.getAllBikeTypes()) {
            typeCombo.addItem(bt);
            if (bt.getBikeTypeId() == bike.getBikeTypeId()) typeCombo.setSelectedItem(bt);
        }

        wheelSizeField = new JTextField(String.valueOf(bike.getWheelSize()));
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
        if (bike.getStatus() != null) statusCombo.setSelectedItem(bike.getStatus());
        descriptionField = new JTextField(bike.getDescription());
        descriptionField.setPreferredSize(size);
    }

    /** {@inheritDoc} */
    @Override
    protected void buildForm(JPanel formPanel, GridBagConstraints gbc) {
        addFormRow(formPanel, gbc, "bike.model",       modelLabel);
        addFormRow(formPanel, gbc, "bike.type",        typeCombo);
        addFormRow(formPanel, gbc, "bike.wheelSize",   wheelSizeField);
        addFormRow(formPanel, gbc, "bike.status",      statusCombo);
        addFormRow(formPanel, gbc, "bike.description", descriptionField);
    }

    /** {@inheritDoc} */
    @Override
    protected void onSubmit() {
        try {
            BikeType selectedType = (BikeType) typeCombo.getSelectedItem();

            if (selectedType == null) {
                throw new IllegalArgumentException(
                        LanguageManager.getString("error.bike.typeRequired"));
            }

            int wheelSize = Integer.parseInt(wheelSizeField.getText().trim());
            if (wheelSize <= 0) throw new NumberFormatException();

            // bikeModelId pozostaje bez zmian
            bike.setBikeTypeId(selectedType.getBikeTypeId());
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
