package AI2;

import AI2.Repository.*;
import AI2.Service.*;
import AI2.Util.DataSeeder;
import AI2.View.MainFrame;

import javax.swing.*;

/**
 * Punkt wejścia aplikacji PożyczRower.
 * Tworzy repozytoria, serwisy, wykonuje seed danych i uruchamia okno główne.
 *
 * @author Tomasz Piłat
 */
public class Main {

    /**
     * Uruchamia aplikację.
     *
     * @param args argumenty wiersza poleceń (nieużywane)
     */
    public static void main(String[] args) {
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
