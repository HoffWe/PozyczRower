package AI2.View.BikeModel;

import AI2.DTO.BikeModelDTO;
import AI2.Service.BikeModelService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseFormPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel formularza dodawania nowego modelu roweru.
 *
 * @author Rafał Wojciechowski
 */
public class AddBikeModelPanel extends BaseFormPanel {

    /** Serwis modeli rowerów. */
    private final BikeModelService bikeModelService;

    /** Nadrzędny panel listy modeli rowerów. */
    private final BikeModelPanel parentPanel;

    /** Pole tekstowe marki roweru. */
    private JTextField brandField;

    /** Pole tekstowe nazwy modelu roweru. */
    private JTextField modelField;

    /**
     * Tworzy panel dodawania modelu roweru.
     *
     * @param bikeModelService serwis modeli rowerów
     * @param parentPanel      nadrzędny panel listy
     * @author Rafał Wojciechowski
     */
    public AddBikeModelPanel(BikeModelService bikeModelService, BikeModelPanel parentPanel) {
        this.bikeModelService = bikeModelService;
        this.parentPanel      = parentPanel;
        init();
    }

    /** {@inheritDoc} */
    @Override
    protected String getTitleKey() { return "bikeModel.nameAdd"; }

    /** {@inheritDoc} */
    @Override
    protected String getSubmitButtonKey() { return "button.add"; }

    /** {@inheritDoc} */
    @Override
    protected void initFormComponents() {
        Dimension size = defaultFieldSize();
        brandField = new JTextField(); brandField.setPreferredSize(size);
        modelField = new JTextField(); modelField.setPreferredSize(size);
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
            bikeModelService.addBikeModel(new BikeModelDTO(
                    brandField.getText().trim(),
                    modelField.getText().trim()
            ));
            showSuccess("bikeModel.added");
            parentPanel.loadData();
            closeDialog();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }
}
