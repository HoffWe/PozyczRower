package AI2.View.Client;

import AI2.Service.ClientService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseFormPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel formularza dodawania nowego klienta.
 */
public class AddClientPanel extends BaseFormPanel {

    private final ClientService clientService;
    private final ClientPanel parentPanel;

    private JTextField nameField;
    private JTextField surnameField;
    private JTextField evidenceField;
    private JTextField opisField;

    public AddClientPanel(ClientService clientService, ClientPanel parentPanel) {
        this.clientService = clientService;
        this.parentPanel   = parentPanel;
        init();
    }

    //
    // BaseFormPanel
    //

    @Override
    protected String getTitleKey() { return "client.nameAdd"; }

    @Override
    protected String getSubmitButtonKey() { return "button.add"; }

    @Override
    protected void initFormComponents() {
        Dimension size = defaultFieldSize();
        nameField = new JTextField(); nameField.setPreferredSize(size);
        surnameField = new JTextField(); surnameField.setPreferredSize(size);
        evidenceField = new JTextField(); evidenceField.setPreferredSize(size);
        opisField = new JTextField(); opisField.setPreferredSize(size);
    }

    @Override
    protected void buildForm(JPanel formPanel, GridBagConstraints gbc) {
        addFormRow(formPanel, gbc, "client.firstName",   nameField);
        addFormRow(formPanel, gbc, "client.lastName",    surnameField);
        addFormRow(formPanel, gbc, "client.evidence",    evidenceField);
        addFormRow(formPanel, gbc, "client.description", opisField);
    }

    @Override
    protected void onSubmit() {
        try {
            clientService.addClient(
                    nameField.getText().trim(),
                    surnameField.getText().trim(),
                    evidenceField.getText().trim(),
                    opisField.getText().trim()
            );
            showSuccess("client.added");
            parentPanel.loadData();
            closeDialog();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }
}
