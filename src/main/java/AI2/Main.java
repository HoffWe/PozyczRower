package AI2;

import AI2.Repository.*;
import AI2.Service.*;
import AI2.Util.DataSeeder;
import AI2.View.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

/**
 * Punkt wejścia aplikacji PożyczRower.
 * Tworzy repozytoria, serwisy, wykonuje seed danych i uruchamia okno główne.
 *
 */
public class Main {

    public static void main(String[] args) {

        //ustawienia flatlafa
        Color bgNeutral = Color.decode("#F5F5F0");
        Color textNavy = Color.decode("#1A2B3C");
        Color primaryGreen= Color.decode("#2E7D32");

        UIManager.put("Button.arc", 15);
        UIManager.put("Component.arc", 15);
        UIManager.put("TextComponent.arc", 15);

        UIManager.put("Panel.background", bgNeutral);
        UIManager.put("RootPane.background", bgNeutral);
        UIManager.put("Label.foreground", textNavy);

        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.foreground", textNavy);
        UIManager.put("TableHeader.background", textNavy);
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("Table.selectionBackground", Color.decode("#E8F5E9"));
        UIManager.put("Table.selectionForeground", textNavy);

        UIManager.put("ToggleButton.tab.underlineColor", primaryGreen);
        UIManager.put("ToggleButton.selectedForeground", primaryGreen);

        FlatLightLaf.setup();


        BikeRepository bikeRepository = new BikeRepository();
        BikeTypeRepository bikeTypeRepository = new BikeTypeRepository();
        BikeModelRepository bikeModelRepository = new BikeModelRepository();
        ClientRepository clientRepository = new ClientRepository();
        RentRepository rentRepository = new RentRepository();
        UserRepository userRepository = new UserRepository();

        BikeTypeService  bikeTypeService  = new BikeTypeService(bikeTypeRepository);
        BikeModelService bikeModelService = new BikeModelService(bikeModelRepository);
        BikeService  bikeService = new BikeService(bikeRepository);
        ClientService clientService = new ClientService(clientRepository);
        RentService rentService = new RentService(rentRepository, bikeRepository, clientRepository);
        UserService userService = new UserService(userRepository);

        DataSeeder.seedIfEmpty(bikeTypeService, bikeModelService, clientService, bikeService, userService);

        SwingUtilities.invokeLater(() ->
                MainFrame.startLoginFlow(rentService, clientService, bikeTypeService,
                                         bikeService, bikeModelService, userService));
    }
}
