package AI2.View.BikeModel;

import AI2.DTO.BikeModelDTO;
import AI2.Model.BikeModel;
import AI2.Service.BikeModelService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseFormPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel formularza edycji istniejącego modelu roweru.
 * Pola wypełnione danymi z przekazanego obiektu {@link BikeModel}.
 *
 * @author Rafał Wojciechowski
 */
public class EditBikeModelPanel extends BaseFormPanel {

    /** Serwis modeli rowerów. */
    private final BikeModelService bikeModelService;

    /** Model roweru do edycji. */
    private final BikeModel bikeModel;

    /** Nadrzędny panel listy modeli rowerów. */
    private final BikeModelPanel parentPanel;

    /** Pole tekstowe marki roweru. */
    private JTextField brandField;

    /** Pole tekstowe nazwy modelu roweru. */
    private JTextField modelField;

    /**
     * Tworzy panel edycji modelu roweru.
     *
     * @param bikeModelService serwis modeli rowerów
     * @param bikeModel        model roweru do edycji
     * @param parentPanel      nadrzędny panel listy
     * @author Rafał Wojciechowski
     */
    public EditBikeModelPanel(BikeModelService bikeModelService, BikeModel bikeModel,
                              BikeModelPanel parentPanel) {
        this.bikeModelService= bikeModelService;
        this.bikeModel= bikeModel;
        this.parentPanel= parentPanel;
        init();
    }

    /** {@inheritDoc} */
    @Override
    protected String getTitleKey() {
        return "bikeModel.editTitle";
    }

    /** {@inheritDoc} */
    @Override
    protected String getSubmitButtonKey() {
        return "button.save";
    }

    /** {@inheritDoc} */
    @Override
    protected void initFormComponents() {
        Dimension size = defaultFieldSize();
        brandField = new JTextField(bikeModel.getBrand()); brandField.setPreferredSize(size);
        modelField = new JTextField(bikeModel.getModel()); modelField.setPreferredSize(size);
    }

    /** {@inheritDoc} */
    @Override
    protected void buildForm(JPanel formPanel, GridBagConstraints gbc) {
        addFormRow(formPanel, gbc, "bikeModel.brand", brandField);
        addFormRow(formPanel, gbc, "bikeModel.model", modelField);
    }

    /** {@inheritDoc} */
    @Override
    protected void onSubmit() {
        try {
            bikeModelService.updateBikeModel(bikeModel, new BikeModelDTO(
                    brandField.getText().trim(),
                    modelField.getText().trim()
            ));
            showSuccess("bikeModel.updated");
            parentPanel.loadData();
            closeDialog();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }
}
