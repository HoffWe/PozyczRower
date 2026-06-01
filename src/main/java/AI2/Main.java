package AI2;

import AI2.Repository.ClientRepository;
import AI2.Service.ClientService;

/**
 *
 * @author Sviatoslav Matsopa
 *
 */
public class Main {
    public static void main(String[] args) {
        ClientRepository clientRepository = new ClientRepository();
        ClientService clientService = new ClientService(clientRepository);
    }
}