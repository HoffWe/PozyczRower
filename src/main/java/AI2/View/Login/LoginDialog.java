package AI2.View.Login;

import AI2.Model.User;
import AI2.Service.UserService;
import AI2.Util.LanguageManager;
import AI2.View.Components.AppButton;

import javax.swing.*;
import java.awt.*;

/**
 * Okno dialogowe logowania do systemu.
 * Blokuje dostęp do aplikacji do czasu poprawnego uwierzytelnienia.
 *
 * @author Tomasz Piłat
 */
public class LoginDialog extends JDialog {

    private final UserService userService;

    /** Zalogowany użytkownik – {@code null} jeśli dialog zamknięto bez logowania. */
    private User loggedUser = null;

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         errorLabel;

    /**
     * Tworzy okno logowania.
     *
     * @param owner       okno nadrzędne (może być {@code null})
     * @param userService serwis użytkowników
     */
    public LoginDialog(Frame owner, UserService userService) {
        super(owner, LanguageManager.getString("login.title"), true);
        this.userService = userService;
        buildUI();
    }

    // ----------------------------------------------------------------
    // Budowanie interfejsu
    // ----------------------------------------------------------------

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(24, 36, 20, 36));
        getContentPane().setBackground(Color.WHITE);

        // --- Tytuł ---
        JLabel title = new JLabel(LanguageManager.getString("login.title"), SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        add(title, BorderLayout.NORTH);

        // --- Formularz ---
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.insets  = new Insets(6, 4, 6, 4);
        gbc.anchor  = GridBagConstraints.WEST;

        Dimension fieldSize = new Dimension(220, 32);

        // Nazwa użytkownika
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(new JLabel(LanguageManager.getString("user.username") + ":"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        usernameField = new JTextField();
        usernameField.setPreferredSize(fieldSize);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(usernameField, gbc);

        // Hasło
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(new JLabel(LanguageManager.getString("user.password") + ":"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(fieldSize);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(passwordField, gbc);

        // Komunikat błędu
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        form.add(errorLabel, gbc);

        add(form, BorderLayout.CENTER);

        // --- Przycisk ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnPanel.setBackground(Color.WHITE);
        AppButton loginBtn = new AppButton(LanguageManager.getString("login.button"));
        loginBtn.addActionListener(e -> tryLogin());
        btnPanel.add(loginBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Enter uruchamia logowanie
        getRootPane().setDefaultButton(loginBtn);

        setSize(380, 270);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    // ----------------------------------------------------------------
    // Logowanie
    // ----------------------------------------------------------------

    private void tryLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        try {
            loggedUser = userService.login(username, password);
            dispose();
        } catch (Exception ex) {
            errorLabel.setText(ex.getMessage());
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

    // ----------------------------------------------------------------
    // Publiczne API
    // ----------------------------------------------------------------

    /**
     * Zwraca zalogowanego użytkownika po zamknięciu dialogu.
     *
     * @return zalogowany użytkownik lub {@code null} jeśli dialog zamknięto bez logowania
     */
    public User getLoggedUser() {
        return loggedUser;
    }
}
