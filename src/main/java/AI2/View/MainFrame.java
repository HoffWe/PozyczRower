package AI2.View;

import AI2.Repository.BikeRepository;
import AI2.Repository.BikeTypeRepository;
import AI2.Repository.ClientRepository;
import AI2.Repository.RentRepository;
import AI2.Service.BikeService;
import AI2.Service.BikeTypeService;
import AI2.Service.RentService;
import AI2.Util.LanguageManager;
import AI2.View.BikeType.BikeTypePanel;
import AI2.Service.ClientService;
import AI2.Service.RentService;
import AI2.Util.LanguageManager;
import AI2.View.Client.ClientPanel;
import AI2.View.Rent.RentPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private final JPanel contentPanel;
    private final CardLayout layout;

    private final RentService rentService;
    private final BikeTypeService bikeTypeService;

    public MainFrame(RentService rentService,  BikeTypeService bikeTypeService) {

    public MainFrame(RentService rentService, ClientService clientService) {
        this.rentService = rentService;
        this.bikeTypeService = bikeTypeService;
        setTitle(LanguageManager.getString("app.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setResizable(true);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(
                WindowConstants.DO_NOTHING_ON_CLOSE
        );

        addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosing(
                            WindowEvent e) {

                        saveData();

                        dispose();

                        System.exit(0);
                    }
                }
        );


        // --- GÓRNY PASEK NARZĘDZI (TOOLBAR) ---
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton rentsButton = new JButton(LanguageManager.getString("rent.name"));
        JButton bikeTypesButton = new JButton(LanguageManager.getString("bikeType.name"));

        toolBar.add(rentsButton);
        toolBar.add(bikeTypesButton);

        JButton clientsButton = new JButton(LanguageManager.getString("client.name"));
        toolBar.add(rentsButton);
        toolBar.add(clientsButton);
        add(toolBar, BorderLayout.NORTH);


        // --- CARD LAYOUT ---
        layout = new CardLayout();
        contentPanel = new JPanel(layout);
        contentPanel.add(new RentPanel(rentService), LanguageManager.getString("rent.name"));
        contentPanel.add(new ClientPanel(clientService), LanguageManager.getString("client.name"));
        add(contentPanel, BorderLayout.CENTER);
        rentsButton.addActionListener(e -> layout.show(contentPanel, LanguageManager.getString("rent.name")));
        clientsButton.addActionListener(e -> layout.show(contentPanel, LanguageManager.getString("client.name")));
    }

    private void saveData() {
        rentService.saveRents();
        bikeTypeService.saveBikeTypes();
    }

    public static void main(String[] args) {
        BikeRepository bikeRepository = new BikeRepository();
        ClientRepository clientRepository = new ClientRepository();
        RentRepository rentRepository = new RentRepository();
        RentService rentService = new RentService(rentRepository, bikeRepository, clientRepository);
        BikeService bikeService = new BikeService(bikeRepository);
        ClientService clientService = new ClientService(clientRepository);

        SwingUtilities.invokeLater(() -> {

            MainFrame frame = new MainFrame(rentService, clientService);

            frame.setVisible(true);

        });
    }
}