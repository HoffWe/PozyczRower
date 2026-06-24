package AI2.Service;

import AI2.Model.Client;
import AI2.Repository.ClientRepository;
import AI2.Util.LanguageManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serwis obsługujący logikę biznesową klientów.
 *
 * @author Światosław Matsopa
 */
public class ClientService {

    /** Repozytorium klientów. */
    private final ClientRepository clientRepository;

    /** Następne dostępne ID klienta. */
    private int nextId;

    /**
     * Tworzy serwis klientów i inicjalizuje następne ID.
     *
     * @param clientRepository repozytorium klientów
     * @author Światosław Matsopa
     */
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
        this.nextId = clientRepository.getNextId();
    }

    /**
     * Dodaje nowego klienta z automatycznym ID.
     *
     * @param name imię klienta
     * @param surname nazwisko klienta
     * @param evidence numer dowodu klienta
     * @param opis opis klienta
     * @throws IllegalArgumentException jeśli imię, nazwisko lub numer dowodu są puste
     * @author Światosław Matsopa
     */
    public void addClient(String name, String surname, String evidence, String opis) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.client.nameEmpty"));
        }
        if (surname == null || surname.isEmpty()) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.client.surnameEmpty"));
        }
        if (evidence == null || evidence.isEmpty()) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.client.evidenceEmpty"));
        }
        if (!evidence.matches("^[A-Z]{3}[0-9]{6}$")) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.client.evidenceInvalid"));
        }
        Client client = new Client(nextId++, name, surname, evidence, opis);
        clientRepository.addClient(client);
    }

    /**
     * Zwraca wszystkich aktywnych (nie usuniętych) klientów.
     *
     * @return lista klientów
     * @author Światosław Matsopa
     *
     */
    public List<Client> getAllClients() {
        return clientRepository.getAllClients().stream()
                .filter(c -> !c.isDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Zwraca klienta po identyfikatorze.
     *
     * @param id identyfikator klienta
     * @return klient albo {@code null} gdy nie znaleziono
     * @author Światosław Matsopa
     */
    public Client getClientById(int id) {
        return clientRepository.getClientById(id);
    }

    /**
     * Usuwa klienta po identyfikatorze.
     *
     * @param id identyfikator klienta
     * @author Światosław Matsopa
     */
    public void removeClient(int id) {
        clientRepository.removeClient(id);
    }

    /**
     * Aktualizuje dane klienta.
     *
     * @param id identyfikator klienta
     * @param name nowe imię
     * @param surname nowe nazwisko
     * @param evidence nowy numer dowodu
     * @param opis nowy opis
     * @author Światosław Matsopa
     */
    public void updateClient(int id, String name, String surname, String evidence, String opis) {
        if (evidence == null || evidence.isEmpty()) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.client.evidenceEmpty"));
        }
        if (!evidence.matches("^[A-Z]{3}[0-9]{6}$")) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.client.evidenceInvalid"));
        }
        Client client = new Client(id, name, surname, evidence, opis);
        clientRepository.updateClient(client);
    }

    /**
     * Wyszukuje klientów po nazwisku.
     * Zwraca wszystkich klientów jeśli słowo kluczowe jest {@code null}.
     *
     * @param keyword słowo kluczowe wyszukiwania
     * @return lista pasujących klientów
     * @author Światosław Matsopa
     */
    public List<Client> searchByName(String keyword) {
        if (keyword == null) {
            return getAllClients();
        }
        return getAllClients().stream()
                .filter(c -> c.getSurname().toLowerCase()
                        .contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
}
