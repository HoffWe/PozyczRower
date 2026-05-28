package AI2.Service;

import AI2.Model.Client;
import AI2.Repository.ClientRepository;
import java.util.List;

/**
 *
 *
 *
 * @author Sviatoslav Matsopa
 *
 *
 *
 *
 */
public class ClientService {

    private final ClientRepository clientRepository;
    private int nextId = 1;

    /** Konstruktor - tworzy nowe repozytorium */
    public ClientService() {
        this.clientRepository = new ClientRepository();
    }

    /** Dodaje nowego klienta z automatycznym ID */
    public void addClient(String name, String surname, String evidence, String opis) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name nie może byc puste.");
        }
        if (surname == null || surname.isEmpty()) {
            throw new IllegalArgumentException("surname nie może byc puste.");
        }
        if (evidence == null || evidence.isEmpty()) {
            throw new IllegalArgumentException("evidence nie moze byс pusty.");
        }
        Client client = new Client(nextId++, name, surname, evidence, opis);
        clientRepository.addClient(client);
    }

    /** Zwraca wszystkich klientów */
    public List<Client> getAllClients() {
        return clientRepository.getAllClients();
    }

    /** Zwraca klienta po ID */
    public Client getClientById(int id) {
        return clientRepository.getClientById(id);
    }

    /** Usuwa klienta po ID */
    public void removeClient(int id) {
        clientRepository.removeClient(id);
    }

    /** Aktualizuje dane klienta */
    public void updateClient(int id, String name, String surname, String evidence, String opis) {
        Client client = new Client(id, name, surname, evidence, opis);
        clientRepository.updateClient(client);
    }

    /** Wyszukuje klientów po nazwisku */
    public List<Client> searchByName(String keyword) {
        return clientRepository.getAllClients().stream()
                .filter(c -> c.getSurname().toLowerCase()
                        .contains(keyword.toLowerCase()))
                .toList();
    }
}