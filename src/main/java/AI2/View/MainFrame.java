package AI2.View;

import AI2.Repository.BikeRepository;
import AI2.Repository.ClientRepository;
import AI2.Repository.RentRepository;
import AI2.Service.BikeService;
import AI2.Service.RentService;
import AI2.Util.LanguageManager;
import AI2.View.Rent.RentPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private final JPanel contentPanel;
    private final CardLayout layout;
    private final RentService rentService;
    public MainFrame(RentService rentService) {
        this.rentService = rentService;
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

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        JButton rentsButton = new JButton(LanguageManager.getString("rent.name"));
        toolBar.add(rentsButton);
        add(toolBar, BorderLayout.NORTH);
        layout = new CardLayout();
        contentPanel = new JPanel(layout);
        contentPanel.add(new RentPanel(rentService),LanguageManager.getString("rent.name"));
        add(contentPanel, BorderLayout.CENTER);
        rentsButton.addActionListener(e -> {
            layout.show(contentPanel, LanguageManager.getString("rent.name"));
        });
    }

    private void saveData(){
        rentService.saveRents();
    }

    public static void main(String[] args) {
        BikeRepository bikeRepository = new BikeRepository();
        ClientRepository clientRepository = new ClientRepository();
        RentRepository rentRepository = new RentRepository();
        RentService rentService = new RentService(rentRepository,bikeRepository,clientRepository);
        BikeService bikeService = new BikeService(bikeRepository);
        SwingUtilities.invokeLater(() -> {

            MainFrame frame =
                    new MainFrame(rentService);

            frame.setVisible(true);

        });
    }
}

