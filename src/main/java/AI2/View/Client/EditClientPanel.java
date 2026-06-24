package AI2.View.Client;

import AI2.Model.Client;
import AI2.Service.ClientService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseFormPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel formularza edycji istniejącego klienta.
 * Pola są wstępnie wypełnione danymi przekazanego obiektu {@link Client}.
 *
 *  @author Światosław Matsopa
 */
public class EditClientPanel extends BaseFormPanel {

    private final ClientService clientService;
    private final Client client;
    private final ClientPanel parentPanel;

    private JTextField nameField;
    private JTextField surnameField;
    private JTextField evidenceField;
    private JTextField opisField;

    public EditClientPanel(ClientService clientService, Client client,
                           ClientPanel parentPanel) {
        this.clientService = clientService;
        this.client = client;
        this.parentPanel = parentPanel;
        init();
    }

    @Override
    protected String getTitleKey() { return "client.editTitle"; }

    @Override
    protected String getSubmitButtonKey() { return "button.save"; }

    @Override
    protected void initFormComponents() {
        Dimension size = defaultFieldSize();
        nameField = new JTextField(client.getName());     nameField.setPreferredSize(size);
        surnameField = new JTextField(client.getSurname());  surnameField.setPreferredSize(size);
        evidenceField = new JTextField(client.getEvidence()); evidenceField.setPreferredSize(size);
        opisField = new JTextField(client.getOpis());     opisField.setPreferredSize(size);
    }

    @Override
    protected void buildForm(JPanel formPanel, GridBagConstraints gbc) {
        addFormRow(formPanel, gbc, "client.firstName",  nameField);
        addFormRow(formPanel, gbc, "client.lastName", surnameField);
        addFormRow(formPanel, gbc, "client.evidence",  evidenceField);
        addFormRow(formPanel, gbc, "client.description", opisField);
    }

    @Override
    protected void onSubmit() {
        try {
            clientService.updateClient(
                    client.getId(),
                    nameField.getText().trim(),
                    surnameField.getText().trim(),
                    evidenceField.getText().trim(),
                    opisField.getText().trim()
            );
            showSuccess("client.updated");
            parentPanel.loadData();
            closeDialog();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }
}
