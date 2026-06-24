package AI2.View.Workers;

import AI2.Enums.UserRole;
import AI2.Model.User;
import AI2.Service.UserService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseFormPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel formularza edycji istniejącego pracownika (użytkownika).
 * Pole hasła — pozostaw puste aby nie zmieniać.
 *
 * @author Tomasz Piłat
 */
public class EditUserPanel extends BaseFormPanel {

    private final UserService userService;
    private final User user;
    private final WorkersPanel parentPanel;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<UserRole> roleCombo;

    public EditUserPanel(UserService userService, User user, WorkersPanel parentPanel) {
        this.userService = userService;
        this.user = user;
        this.parentPanel = parentPanel;
        init();
    }

    @Override
    protected String getTitleKey() {
        return "workers.editTitle";
    }

    @Override
    protected String getSubmitButtonKey() {
        return "button.save";
    }

    @Override
    protected void initFormComponents() {
        Dimension size = defaultFieldSize();

        usernameField = new JTextField(user.getUsername());
        usernameField.setPreferredSize(size);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(size);
        passwordField.setToolTipText(LanguageManager.getString("workers.passwordHint"));

        roleCombo = new JComboBox<>(UserRole.values());
        roleCombo.setSelectedItem(user.getRole());
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
        addFormRow(formPanel, gbc, "user.passwordNew", passwordField);
        addFormRow(formPanel, gbc, "user.role", roleCombo);
    }

    @Override
    protected void onSubmit() {
        try {
            String newPassword = new String(passwordField.getPassword());
            User draft = new User(user.getId(), usernameField.getText().trim(),
                    user.getPasswordHash(), (UserRole) roleCombo.getSelectedItem());
            userService.updateUser(draft, newPassword);
            user.setUsername(draft.getUsername());
            user.setRole(draft.getRole());
            user.setPasswordHash(draft.getPasswordHash());
            showSuccess("workers.updated");
            parentPanel.loadData();
            closeDialog();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }
}
