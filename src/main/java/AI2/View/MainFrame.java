package AI2.View;

import AI2.Enums.UserRole;
import AI2.Model.User;
import AI2.Repository.BikeModelRepository;
import AI2.Repository.BikeRepository;
import AI2.Repository.BikeTypeRepository;
import AI2.Repository.ClientRepository;
import AI2.Repository.RentRepository;
import AI2.Repository.UserRepository;
import AI2.Service.BikeModelService;
import AI2.Service.BikeService;
import AI2.Service.BikeTypeService;
import AI2.Service.ClientService;
import AI2.Service.RentService;
import AI2.Service.UserService;
import AI2.Util.DataSeeder;
import AI2.Util.LanguageChangeListener;
import AI2.Util.LanguageManager;
import AI2.View.Bike.BikePanel;
import AI2.View.BikeModel.BikeModelPanel;
import AI2.View.BikeType.BikeTypePanel;
import AI2.View.Client.ClientPanel;
import AI2.View.Login.LoginDialog;
import AI2.View.Rent.RentPanel;
import AI2.View.Workers.WorkersPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Główne okno aplikacji.
 * Zawiera pasek menu (Plik), toolbar nawigacyjny i przełącznik języka PL↔EN.
 * Przyciski nawigacyjne są filtrowane według roli zalogowanego użytkownika.
 *
 * @author Tomasz Piłat
 */
public class MainFrame extends JFrame implements LanguageChangeListener {

    // ----------------------------------------------------------------
    // Klucze kart
    // ----------------------------------------------------------------

    /** Klucz karty wypożyczeń. */
    static final String CARD_RENTS       = "RENTS";

    /** Klucz karty klientów. */
    static final String CARD_CLIENTS     = "CLIENTS";

    /** Klucz karty typów rowerów. */
    static final String CARD_BIKE_TYPES  = "BIKE_TYPES";

    /** Klucz karty rowerów. */
    static final String CARD_BIKES       = "BIKES";

    /** Klucz karty modeli rowerów. */
    static final String CARD_BIKE_MODELS = "BIKE_MODELS";

    /** Klucz karty pracowników (tylko ADMIN). */
    static final String CARD_WORKERS = "WORKERS";

    // ----------------------------------------------------------------
    // Pola
    // ----------------------------------------------------------------

    /** Panel kart z widokami. */
    private final JPanel     contentPanel;

    /** Menadżer kart. */
    private final CardLayout layout;

    // Usługi
    private final RentService      rentService;
    private final ClientService    clientService;
    private final BikeTypeService  bikeTypeService;
    private final BikeService      bikeService;
    private final BikeModelService bikeModelService;
    private final UserService      userService;

    /** Aktualnie zalogowany użytkownik. */
    private final User currentUser;

    /** Wywołanie zwrotne po wylogowaniu / ponownym logowaniu. */
    private final Runnable onLogout;

    // Przyciski toolbara
    private JButton rentsButton;
    private JButton bikesButton;
    private JButton bikeTypesButton;
    private JButton clientsButton;
    private JButton bikeModelsButton;
    private JButton workersButton;
    private JButton langButton;

    /** Etykieta zalogowanego użytkownika w toolbarze. */
    private JLabel userLabel;

    // Pozycje menu
    private JMenuItem loginItem;
    private JMenuItem logoutItem;
    private JMenuItem exitItem;
    private JMenu     fileMenu;

    // ----------------------------------------------------------------
    // Konstruktor
    // ----------------------------------------------------------------

    /**
     * Tworzy główne okno aplikacji dla zalogowanego użytkownika.
     *
     * @param currentUser      zalogowany użytkownik
     * @param userService      serwis użytkowników
     * @param rentService      serwis wypożyczeń
     * @param clientService    serwis klientów
     * @param bikeTypeService  serwis typów rowerów
     * @param bikeService      serwis rowerów
     * @param bikeModelService serwis modeli rowerów
     * @param onLogout         callback wywoływany przy wylogowaniu (dispose + ponowne logowanie)
     * @author Tomasz Piłat
     */
    public MainFrame(User currentUser, UserService userService,
                     RentService rentService, ClientService clientService,
                     BikeTypeService bikeTypeService, BikeService bikeService,
                     BikeModelService bikeModelService, Runnable onLogout) {
        this.currentUser      = currentUser;
        this.userService      = userService;
        this.rentService      = rentService;
        this.clientService    = clientService;
        this.bikeTypeService  = bikeTypeService;
        this.bikeService      = bikeService;
        this.bikeModelService = bikeModelService;
        this.onLogout         = onLogout;

        LanguageManager.addListener(this);

        setTitle(LanguageManager.getString("app.title"));
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(900, 600));
        setResizable(true);
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
                dispose();
                System.exit(0);
            }
        });

        setJMenuBar(buildMenuBar());
        add(buildToolBar(), BorderLayout.NORTH);

        layout       = new CardLayout();
        contentPanel = new JPanel(layout);

        UserRole role = currentUser.getRole();

        // Dodaj tylko panele dostępne dla danej roli
        if (role.canAccess(CARD_RENTS)) {
            contentPanel.add(new RentPanel(rentService, clientService, bikeService,
                    bikeModelService, bikeTypeService), CARD_RENTS);
        }
        if (role.canAccess(CARD_CLIENTS)) {
            contentPanel.add(new ClientPanel(clientService, rentService, bikeService,
                    bikeModelService, bikeTypeService), CARD_CLIENTS);
        }
        if (role.canAccess(CARD_BIKE_TYPES)) {
            contentPanel.add(new BikeTypePanel(bikeTypeService), CARD_BIKE_TYPES);
        }
        if (role.canAccess(CARD_BIKES)) {
            contentPanel.add(new BikePanel(bikeService, bikeModelService, bikeTypeService,
                    rentService, clientService), CARD_BIKES);
        }
        if (role.canAccess(CARD_BIKE_MODELS)) {
            contentPanel.add(new BikeModelPanel(bikeModelService), CARD_BIKE_MODELS);
        }
        if (role.canAccess(CARD_WORKERS)) {
            contentPanel.add(new WorkersPanel(userService), CARD_WORKERS);
        }

        add(contentPanel, BorderLayout.CENTER);

        // Pokaż pierwszy dostępny panel
        showFirstAccessibleCard(role);
    }

    // ----------------------------------------------------------------
    // Menu Plik
    // ----------------------------------------------------------------

    /**
     * Buduje pasek menu z pozycjami Plik → Zaloguj / Wyloguj / Zamknij.
     *
     * @return skonfigurowany pasek menu
     * @author Tomasz Piłat
     */
    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        fileMenu = new JMenu(LanguageManager.getString("menu.file"));
        fileMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        loginItem  = new JMenuItem(LanguageManager.getString("menu.login"));
        logoutItem = new JMenuItem(LanguageManager.getString("menu.logout"));
        exitItem   = new JMenuItem(LanguageManager.getString("menu.exit"));

        loginItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        logoutItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        exitItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        loginItem.addActionListener(e  -> doRelogin());
        logoutItem.addActionListener(e -> doRelogin());
        exitItem.addActionListener(e   -> {
            saveData();
            dispose();
            System.exit(0);
        });

        fileMenu.add(loginItem);
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        return menuBar;
    }

    /**
     * Wylogowuje bieżącego użytkownika: zapisuje dane, zamyka okno i uruchamia callback logowania.
     *
     * @author Tomasz Piłat
     */
    private void doRelogin() {
        saveData();
        LanguageManager.removeListener(this);
        dispose();
        onLogout.run();
    }

    // ----------------------------------------------------------------
    // Toolbar
    // ----------------------------------------------------------------

    /**
     * Buduje toolbar nawigacyjny. Pokazuje tylko przyciski dostępne dla roli użytkownika.
     *
     * @return skonfigurowany pasek narzędzi
     * @author Tomasz Piłat
     */
    private JToolBar buildToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        UserRole role = currentUser.getRole();

        rentsButton      = new JButton(LanguageManager.getString("rent.name"));
        bikesButton      = new JButton(LanguageManager.getString("bike.name"));
        bikeTypesButton  = new JButton(LanguageManager.getString("bikeType.name"));
        clientsButton    = new JButton(LanguageManager.getString("client.name"));
        bikeModelsButton = new JButton(LanguageManager.getString("bikeModel.name"));
        workersButton    = new JButton(LanguageManager.getString("workers.name"));

        styleToolbarButton(rentsButton);
        styleToolbarButton(bikesButton);
        styleToolbarButton(bikeTypesButton);
        styleToolbarButton(clientsButton);
        styleToolbarButton(bikeModelsButton);
        styleToolbarButton(workersButton);

        rentsButton.addActionListener(e      -> layout.show(contentPanel, CARD_RENTS));
        bikesButton.addActionListener(e      -> layout.show(contentPanel, CARD_BIKES));
        bikeTypesButton.addActionListener(e  -> layout.show(contentPanel, CARD_BIKE_TYPES));
        clientsButton.addActionListener(e    -> layout.show(contentPanel, CARD_CLIENTS));
        bikeModelsButton.addActionListener(e -> layout.show(contentPanel, CARD_BIKE_MODELS));
        workersButton.addActionListener(e    -> layout.show(contentPanel, CARD_WORKERS));

        if (role.canAccess(CARD_RENTS))       toolBar.add(rentsButton);
        if (role.canAccess(CARD_BIKES))       toolBar.add(bikesButton);
        if (role.canAccess(CARD_BIKE_MODELS)) toolBar.add(bikeModelsButton);
        if (role.canAccess(CARD_BIKE_TYPES))  toolBar.add(bikeTypesButton);
        if (role.canAccess(CARD_CLIENTS))     toolBar.add(clientsButton);
        if (role.canAccess(CARD_WORKERS))     toolBar.add(workersButton);

        toolBar.add(Box.createHorizontalGlue());

        // Etykieta zalogowanego użytkownika
        userLabel = new JLabel(LanguageManager.getString("user.loggedAs")
                + " " + currentUser.getUsername()
                + " (" + currentUser.getRole().getDisplayName() + ")");
        userLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));
        toolBar.add(userLabel);

        langButton = new JButton(LanguageManager.getString("lang.switch"));
        langButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        langButton.setFocusPainted(false);
        langButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        langButton.setToolTipText(LanguageManager.getString("lang.current"));
        langButton.addActionListener(e -> LanguageManager.toggleLocale());
        toolBar.add(langButton);

        return toolBar;
    }

    /**
     * Stylizuje przycisk paska narzędzi.
     *
     * @param btn przycisk do ostylowania
     * @author Tomasz Piłat
     */
    private void styleToolbarButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Wyświetla pierwszą kartę dostępną dla danej roli.
     *
     * @param role rola użytkownika
     */
    private void showFirstAccessibleCard(UserRole role) {
        String[] order = { CARD_RENTS, CARD_BIKES, CARD_CLIENTS,
                           CARD_BIKE_TYPES, CARD_BIKE_MODELS, CARD_WORKERS };
        for (String card : order) {
            if (role.canAccess(card)) {
                layout.show(contentPanel, card);
                return;
            }
        }
    }

    // ----------------------------------------------------------------
    // LanguageChangeListener
    // ----------------------------------------------------------------

    /** {@inheritDoc} */
    @Override
    public void onLanguageChanged() {
        setTitle(LanguageManager.getString("app.title"));

        UserRole role = currentUser.getRole();
        rentsButton.setText(LanguageManager.getString("rent.name"));
        bikesButton.setText(LanguageManager.getString("bike.name"));
        bikeTypesButton.setText(LanguageManager.getString("bikeType.name"));
        clientsButton.setText(LanguageManager.getString("client.name"));
        bikeModelsButton.setText(LanguageManager.getString("bikeModel.name"));
        workersButton.setText(LanguageManager.getString("workers.name"));

        langButton.setText(LanguageManager.getString("lang.switch"));
        langButton.setToolTipText(LanguageManager.getString("lang.current"));

        userLabel.setText(LanguageManager.getString("user.loggedAs")
                + " " + currentUser.getUsername()
                + " (" + currentUser.getRole().getDisplayName() + ")");

        fileMenu.setText(LanguageManager.getString("menu.file"));
        loginItem.setText(LanguageManager.getString("menu.login"));
        logoutItem.setText(LanguageManager.getString("menu.logout"));
        exitItem.setText(LanguageManager.getString("menu.exit"));
    }

    // ----------------------------------------------------------------
    // Zapis danych
    // ----------------------------------------------------------------

    /**
     * Zapisuje dane do pliku przed zamknięciem aplikacji.
     *
     * @author Tomasz Piłat
     */
    private void saveData() {
        try {
            rentService.saveRents();
            bikeTypeService.saveBikeTypes();
            bikeModelService.saveBikeModels();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Błąd zapisu danych: " + ex.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ----------------------------------------------------------------
    // startLoginFlow  (wywoływane z Main i przy wylogowaniu)
    // ----------------------------------------------------------------

    /**
     * Uruchamia przepływ logowania: okno login → okno główne.
     * Przy wylogowaniu wywoływane jest ponownie.
     *
     * @author Tomasz Piłat
     */
    public static void startLoginFlow(RentService rentService, ClientService clientService,
                                BikeTypeService bikeTypeService, BikeService bikeService,
                                BikeModelService bikeModelService, UserService userService) {
        LoginDialog login = new LoginDialog(null, userService);
        login.setVisible(true);         // blokuje – modal dialog

        User user = login.getLoggedUser();
        if (user == null) {
            System.exit(0);
            return;
        }

        MainFrame frame = new MainFrame(
                user, userService,
                rentService, clientService,
                bikeTypeService, bikeService, bikeModelService,
                () -> SwingUtilities.invokeLater(
                        () -> startLoginFlow(rentService, clientService,
                                             bikeTypeService, bikeService,
                                             bikeModelService, userService)));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
