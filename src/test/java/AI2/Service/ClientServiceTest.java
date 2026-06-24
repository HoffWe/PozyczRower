package AI2.Service;

import AI2.Model.Client;
import AI2.Repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy jednostkowe dla {@link ClientService}.
 *
 * @author Światosław Matsopa
 */
class ClientServiceTest {

    private ClientRepository stubRepo;
    private ClientService    service;

    /**
     * Inicjalizacja przed każdym testem.
     *
     * @author Światosław Matsopa
     */
    @BeforeEach
    void setUp() {
        stubRepo = new ClientRepository() {
            private final List<Client> list = new ArrayList<>();
            private int nextId = 1;

            @Override public void addClient(Client c) {
                c.setId(nextId++); list.add(c);
            }
            @Override public List<Client> getAllClients() { return new ArrayList<>(list); }
            @Override public Client getClientById(int id) {
                return list.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
            }
            @Override public void removeClient(int id) { list.removeIf(c -> c.getId() == id); }
            @Override public void updateClient(Client updated) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId() == updated.getId()) { list.set(i, updated); return; }
                }
            }
            @Override public int getNextId() { return nextId; }
        };
        service = new ClientService(stubRepo);
    }

    /**
     * Sprawdza poprawne dodanie klienta.
     *
     * @author Światosław Matsopa
     */
    @Test
    void addClient_validData_clientIsAdded() {
        service.addClient("Jan", "Kowalski", "ABC123234", "");
        List<Client> clients = service.getAllClients();
        assertEquals(1, clients.size());
        assertEquals("Jan", clients.get(0).getName());
    }

    /**
     * Sprawdza że puste imię powoduje wyjątek.
     *
     * @author Światosław Matsopa
     */
    @Test
    void addClient_emptyName_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addClient("", "Kowalski", "ABC123", ""));
    }

    /**
     * Sprawdza że puste nazwisko powoduje wyjątek.
     *
     * @author Światosław Matsopa
     */
    @Test
    void addClient_emptySurname_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addClient("Jan", "", "ABC123", ""));
    }

    /**
     * Sprawdza że pusty numer dowodu powoduje wyjątek.
     *
     * @author Światosław Matsopa
     */
    @Test
    void addClient_emptyEvidence_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addClient("Jan", "Kowalski", "", ""));
    }

    /**
     * Sprawdza usunięcie klienta.
     *
     * @author Światosław Matsopa
     */
    @Test
    void removeClient_existingId_clientIsRemoved() {
        service.addClient("Anna", "Nowak", "XY1234", "");
        int id = service.getAllClients().get(0).getId();
        service.removeClient(id);
        assertNull(service.getClientById(id));
    }

    /**
     * Sprawdza wyszukiwanie po nazwisku.
     *
     * @author Światosław Matsopa
     */
    @Test
    void searchByName_matchingSurname_returnsClient() {
        service.addClient("Jan", "Kowalski", "A1", "");
        service.addClient("Anna", "Nowak", "B2", "");
        List<Client> result = service.searchByName("Kowalski");
        assertEquals(1, result.size());
        assertEquals("Jan", result.get(0).getName());
    }

    /**
     * Sprawdza że null keyword zwraca wszystkich klientów.
     *
     * @author Światosław Matsopa
     */
    @Test
    void searchByName_nullKeyword_returnsAllClients() {
        service.addClient("Jan", "Kowalski", "A1", "");
        service.addClient("Anna", "Nowak", "B2", "");
        assertEquals(2, service.searchByName(null).size());
    }
}
