package AI2.View;

import AI2.Repository.BikeRepository;
import AI2.Repository.BikeTypeRepository;
import AI2.Repository.ClientRepository;
import AI2.Repository.RentRepository;
import AI2.Service.BikeService;
import AI2.Service.BikeTypeService;
import AI2.Service.ClientService;
import AI2.Service.RentService;
import AI2.Util.LanguageChangeListener;
import AI2.Util.LanguageManager;
import AI2.View.Bike.BikePanel;
import AI2.View.BikeType.BikeTypePanel;
import AI2.View.Client.ClientPanel;
import AI2.View.Rent.RentPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Główne okno aplikacji.
 * Zawiera toolbar z nawigacją i przełącznikiem języka PL↔EN.
 */
public class MainFrame extends JFrame implements LanguageChangeListener {

    // --- Klucze kart – stałe, niezależne od języka ---
    private static final String CARD_RENTS       = "RENTS";
    private static final String CARD_CLIENTS     = "CLIENTS";
    private static final String CARD_BIKE_TYPES  = "BIKE_TYPES";
    private static final String CARD_BIKES       = "BIKES";

    private final JPanel     contentPanel;
    private final CardLayout layout;

    // Usługi
    private final RentService     rentService;
    private final ClientService   clientService;
    private final BikeTypeService bikeTypeService;
    private final BikeService     bikeService;

    // Przyciski toolbara (muszą być polami, aby obsłużyć zmianę języka)
    private JButton rentsButton;
    private JButton bikesButton;
    private JButton bikeTypesButton;
    private JButton clientsButton;
    private JButton langButton;

    // ----------------------------------------------------------------
    // Konstruktor
    // ----------------------------------------------------------------

    public MainFrame(RentService rentService, ClientService clientService,
                     BikeTypeService bikeTypeService, BikeService bikeService) {
        this.rentService     = rentService;
        this.clientService   = clientService;
        this.bikeTypeService = bikeTypeService;
        this.bikeService     = bikeService;

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

        // Toolbar
        add(buildToolBar(), BorderLayout.NORTH);

        // Karty z panelami
        layout = new CardLayout();
        contentPanel = new JPanel(layout);

        contentPanel.add(new RentPanel(rentService, clientService, bikeService), CARD_RENTS);
        contentPanel.add(new ClientPanel(clientService), CARD_CLIENTS);
        contentPanel.add(new BikeTypePanel(bikeTypeService), CARD_BIKE_TYPES);
        contentPanel.add(new BikePanel(bikeService), CARD_BIKES);

        add(contentPanel, BorderLayout.CENTER);

        // Domyślna karta
        layout.show(contentPanel, CARD_RENTS);
    }

    // ----------------------------------------------------------------
    // Budowanie toolbara
    // ----------------------------------------------------------------

    private JToolBar buildToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        rentsButton     = new JButton(LanguageManager.getString("rent.name"));
        bikesButton     = new JButton(LanguageManager.getString("bike.name"));
        bikeTypesButton = new JButton(LanguageManager.getString("bikeType.name"));
        clientsButton   = new JButton(LanguageManager.getString("client.name"));

        styleToolbarButton(rentsButton);
        styleToolbarButton(bikesButton);
        styleToolbarButton(bikeTypesButton);
        styleToolbarButton(clientsButton);

        rentsButton.addActionListener(e -> layout.show(contentPanel, CARD_RENTS));
        bikesButton.addActionListener(e -> layout.show(contentPanel, CARD_BIKES));
        bikeTypesButton.addActionListener(e -> layout.show(contentPanel, CARD_BIKE_TYPES));
        clientsButton.addActionListener(e -> layout.show(contentPanel, CARD_CLIENTS));

        toolBar.add(rentsButton);
        toolBar.add(bikesButton);
        toolBar.add(bikeTypesButton);
        toolBar.add(clientsButton);

        // --- Przełącznik języka (po prawej) ---
        toolBar.add(Box.createHorizontalGlue());

        langButton = new JButton(LanguageManager.getString("lang.switch"));
        langButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        langButton.setFocusPainted(false);
        langButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        langButton.setToolTipText(LanguageManager.getString("lang.current"));
        langButton.addActionListener(e -> LanguageManager.toggleLocale());
        toolBar.add(langButton);

        return toolBar;
    }

    private void styleToolbarButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // ----------------------------------------------------------------
    // LanguageChangeListener
    // ----------------------------------------------------------------

    @Override
    public void onLanguageChanged() {
        setTitle(LanguageManager.getString("app.title"));
        rentsButton.setText(LanguageManager.getString("rent.name"));
        bikesButton.setText(LanguageManager.getString("bike.name"));
        bikeTypesButton.setText(LanguageManager.getString("bikeType.name"));
        clientsButton.setText(LanguageManager.getString("client.name"));
        langButton.setText(LanguageManager.getString("lang.switch"));
        langButton.setToolTipText(LanguageManager.getString("lang.current"));
    }

    // ----------------------------------------------------------------
    // Zapis danych
    // ----------------------------------------------------------------

    private void saveData() {
        rentService.saveRents();
        bikeTypeService.saveBikeTypes();
    }

    // ----------------------------------------------------------------
    // main()
    // ----------------------------------------------------------------

    public static void main(String[] args) {
        BikeRepository     bikeRepository     = new BikeRepository();
        BikeTypeRepository bikeTypeRepository = new BikeTypeRepository();
        ClientRepository   clientRepository   = new ClientRepository();
        RentRepository     rentRepository     = new RentRepository();

        BikeTypeService bikeTypeService = new BikeTypeService(bikeTypeRepository);
        BikeService     bikeService     = new BikeService(bikeRepository);
        ClientService   clientService   = new ClientService(clientRepository);
        RentService     rentService     = new RentService(rentRepository, bikeRepository, clientRepository);

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(rentService, clientService, bikeTypeService, bikeService);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
