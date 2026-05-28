package AI2.Repository;

import AI2.Model.Client;
import java.util.ArrayList;
import java.util.List;

/**
 * Repozytorium przechowujące listę klientów.
 * @author Sviatoslav Matsopa
 */
public class ClientRepository {

    private List<Client> clients = new ArrayList<>();

    /** Dodaje nowego klienta do listy */
    public void addClient(Client client) {
        clients.add(client);
    }

    /** Zwraca listę wszystkich klientów */
    public List<Client> getAllClients() {
        return clients;
    }

    /** Zwraca klienta po jego ID, lub null jeśli nie istnieje */
    public Client getClientById(int id) {
        return clients.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /** Usuwa klienta po ID */
    public void removeClient(int id) {
        clients.removeIf(c -> c.getId() == id);
    }

    /** Aktualizuje dane klienta */
    public void updateClient(Client updated) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getId() == updated.getId()) {
                clients.set(i, updated);
                return;
            }
        }
    }
}