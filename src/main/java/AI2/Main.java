package AI2;

import AI2.Repository.ClientRepository;
import AI2.Service.ClientService;
import AI2.View.ClientPanel;

import javax.swing.*;

/**
 * Główna klasa uruchamiająca aplikację
 * @author Sviatoslav Matsopa
 */
public class Main {
    public static void main(String[] args) {
        ClientRepository clientRepository = new ClientRepository();
        ClientService clientService = new ClientService();
        ClientPanel clientPanel = new ClientPanel(clientService, clientRepository);

        JFrame frame = new JFrame("PożyczRower");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(clientPanel);
        frame.setVisible(true);
    }
}