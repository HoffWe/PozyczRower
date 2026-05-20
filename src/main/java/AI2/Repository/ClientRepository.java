package AI2.Repository;

import AI2.Model.Client;
import AI2.Model.Rent;


import java.util.ArrayList;
import java.util.List;

/**

    @author: Sviatoslav Matsopa

*/

public class ClientRepository {

    private List<Client> clients;
    /** Metoda dodawania nowego klienta */
    public void addClient(Client client) {

        clients.add(client);
    }

    /** Metoda Pobierania listę wszystkich klientów */
    public List<Client> getAllClients() {

        return clients;
    }

    /** Metoda (znajdź klienta po jego id)  */
    public Client getClientById(int id) {

        return clients.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null); /**  Metoda zwraca jeżeli nie ma takiego clienta   */
    }

    /** Metoda Usuwania clienta za id */
    public void removeClient(int id) {

        clients.removeIf(c -> c.getId() == id);

    }

}

