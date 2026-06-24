package AI2.View.Workers;

import AI2.Enums.UserRole;
import AI2.Service.UserService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseFormPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel formularza dodawania nowego pracownika (użytkownika).
 *
 * @author Tomasz Piłat
 */
public class AddUserPanel extends BaseFormPanel {

    private final UserService userService;
    private final WorkersPanel parentPanel;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<UserRole> roleCombo;

    public AddUserPanel(UserService userService, WorkersPanel parentPanel) {
        this.userService = userService;
        this.parentPanel = parentPanel;
        init();
    }
    @Override
    protected String getTitleKey() {
        return "workers.nameAdd";
    }

    @Override
    protected String getSubmitButtonKey() {
        return "button.add";
    }

    @Override
    protected void initFormComponents() {
        Dimension size = defaultFieldSize();

        usernameField = new JTextField();
        usernameField.setPreferredSize(size);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(size);

        roleCombo = new JComboBox<>(UserRole.values());
        roleCombo.setPreferredSize(size);
        roleCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof UserRole r) setText(r.getDisplayName());
                return this;
            }
        });
    }

    @Override
    protected void buildForm(JPanel formPanel, GridBagConstraints gbc) {
        addFormRow(formPanel, gbc, "user.username", usernameField);
        addFormRow(formPanel, gbc, "user.password", passwordField);
        addFormRow(formPanel, gbc, "user.role",     roleCombo);
    }

    @Override
    protected void onSubmit() {
        try {
            String password = new String(passwordField.getPassword());
            userService.addUser(
                    usernameField.getText().trim(),
                    password,
                    (UserRole) roleCombo.getSelectedItem()
            );
            showSuccess("workers.added");
            parentPanel.loadData();
            closeDialog();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }
}
