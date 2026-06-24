package AI2.View.Workers;

import AI2.Enums.UserRole;
import AI2.Model.User;
import AI2.Service.UserService;
import AI2.Util.LanguageManager;
import AI2.View.Abstract.BaseListPanel;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel zarządzania pracownikami (użytkownikami systemu).
 * Dostępny wyłącznie dla roli ADMIN.
 *
 * @author Tomasz Piłat
 */
public class WorkersPanel extends BaseListPanel {

    private final UserService userService;

    public WorkersPanel(UserService userService) {
        this.userService = userService;
        loadData();
    }

    @Override
    protected String getTitleKey() { return "workers.management"; }

    @Override
    protected String[] getColumnNames() {
        return new String[]{
                LanguageManager.getString("user.username"),
                LanguageManager.getString("user.role")
        };
    }

    @Override
    public void loadData() {
        String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";

        List<User> users = userService.getAllUsers();
        if (!query.isEmpty()) {
            users = users.stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(query)
                            || u.getRole().getDisplayName().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        clearTable();
        for (User u : users) {
            addRow(u.getId(), new Object[]{ u.getUsername(), u.getRole().getDisplayName() });
        }
    }

    @Override
    protected void filterTable(String query) { loadData(); }

    @Override
    protected void onAdd() {
        openDialog(
                LanguageManager.getString("workers.nameAdd"),
                new AddUserPanel(userService, this),
                420, 320
        );
    }

    @Override
    protected void onEdit(int row) {
        int id = getSelectedId();
        if (id == -1) return;

        User user = userService.getUserById(id);
        if (user == null) {
            JOptionPane.showMessageDialog(this, LanguageManager.getString("error.title"));
            return;
        }

        openDialog(
                LanguageManager.getString("workers.editTitle"),
                new EditUserPanel(userService, user, this),
                420, 320
        );
    }

    @Override
    protected void onDelete(int row) {
        int id = getSelectedId();
        if (id == -1) return;

        User user = userService.getUserById(id);
        if (user != null && user.getRole() == UserRole.ADMIN) {
            long adminCount = userService.getAllUsers().stream()
                    .filter(u -> u.getRole() == UserRole.ADMIN).count();
            if (adminCount <= 1) {
                JOptionPane.showMessageDialog(this,
                        LanguageManager.getString("error.workers.lastAdmin"),
                        LanguageManager.getString("error.title"),
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                LanguageManager.getString("workers.deleteConfirm"),
                LanguageManager.getString("button.delete"),
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            try {
                userService.removeUser(id);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        LanguageManager.getString("error.title"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
