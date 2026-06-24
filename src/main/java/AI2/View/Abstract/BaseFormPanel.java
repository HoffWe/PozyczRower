package AI2.View.Abstract;

import AI2.Util.LanguageChangeListener;
import AI2.Util.LanguageManager;
import AI2.View.Components.AppButton;

import javax.swing.*;
import java.awt.*;

/**
 * Abstrakcyjna klasa bazowa dla paneli formularzy (Dodaj / Edytuj).
 * <p>
 * Wzorzec Template Method: podklasy definiują pola formularza ({@link #buildForm}),
 * tytuł ({@link #getTitleKey}), etykietę przycisku ({@link #getSubmitButtonKey})
 * oraz logikę zapisu ({@link #onSubmit}).
 */
public abstract class BaseFormPanel extends JPanel implements LanguageChangeListener {

    protected JButton submitButton;
    private JLabel titleLabel;

    /**
     * Konstruktor nie buduje UI – podklasa musi wywołać {@link #init()}
     * jako ostatnią instrukcję swojego konstruktora, po ustawieniu własnych pól.
     */
    protected BaseFormPanel() {}

    /**
     * Inicjalizuje i buduje UI panelu formularza.
     * Musi być wywołana jako ostatnia instrukcja konstruktora podklasy.
     */
    protected final void init() {
        LanguageManager.addListener(this);
//        setBackground(Color.WHITE);
        initComponents();
        buildLayout();
        registerListeners();
    }

    private void initComponents() {
        submitButton = new AppButton(LanguageManager.getString(getSubmitButtonKey()));
        initFormComponents();
    }

    /** Podklasy tworzą tutaj własne pola (JTextField, JComboBox itp.). */
    protected abstract void initFormComponents();

    private void buildLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        titleLabel = new JLabel(LanguageManager.getString(getTitleKey()), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
//        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = createDefaultGbc();
        buildForm(formPanel, gbc);
        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(submitButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void registerListeners() {
        submitButton.addActionListener(e -> onSubmit());
    }

    /** Domyślne GridBagConstraints dla formularzy. */
    protected GridBagConstraints createDefaultGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(10, 10, 10, 10);
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.gridx   = 0;
        gbc.gridy   = 0;
        return gbc;
    }

    /**
     * Wygodna metoda dodająca parę etykieta + pole do formularza.
     * Po wywołaniu gbc.gridy jest automatycznie zwiększane.
     */
    protected void addFormRow(JPanel panel, GridBagConstraints gbc,
                              String labelKey, JComponent field) {
        gbc.gridx    = 0;
        gbc.weightx  = 0.0;
        panel.add(new JLabel(LanguageManager.getString(labelKey)), gbc);

        gbc.gridx    = 1;
        gbc.weightx  = 1.0;
        panel.add(field, gbc);

        gbc.gridy++;
    }

    /** Standardowy rozmiar pól tekstowych w formularzach. */
    protected static Dimension defaultFieldSize() {
        return new Dimension(280, 35);
    }

    /** Zamyka okno dialogowe zawierające ten panel. */
    protected void closeDialog() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) w.dispose();
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message,
                LanguageManager.getString("error.title"),
                JOptionPane.ERROR_MESSAGE
        );
    }

    protected void showSuccess(String messageKey) {
        JOptionPane.showMessageDialog(this, LanguageManager.getString(messageKey));
    }

    @Override
    public void onLanguageChanged() {
        titleLabel.setText(LanguageManager.getString(getTitleKey()));
        submitButton.setText(LanguageManager.getString(getSubmitButtonKey()));
        refreshLanguageTexts();
    }

    /** Podklasy nadpisują, aby odświeżyć etykiety pól formularza. */
    protected void refreshLanguageTexts() {}

    protected abstract String getTitleKey();
    protected abstract String getSubmitButtonKey();

    /** Buduje wiersze formularza. Użyj {@link #addFormRow}. */
    protected abstract void buildForm(JPanel formPanel, GridBagConstraints gbc);

    /** Logika zapisu po kliknięciu przycisku. */
    protected abstract void onSubmit();
}
