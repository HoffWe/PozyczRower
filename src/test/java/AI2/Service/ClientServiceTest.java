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

    /** Prawidłowy numer dowodu spełniający regex ^[A-Z]{3}[0-9]{6}$. */
    private static final String VALID_EVIDENCE = "ABC123456";
    private static final String VALID_EVIDENCE_2 = "XYZ000001";
    private static final String VALID_EVIDENCE_3 = "DEF999999";

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
            @Override public void removeClient(int id) {
                Client c = getClientById(id);
                if (c != null) c.setDeleted(true);
            }
            @Override public void updateClient(Client updated) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId() == updated.getId()) { list.set(i, updated); return; }
                }
            }
            @Override public int getNextId() { return nextId; }
        };
        service = new ClientService(stubRepo);
    }

    @Test
    void addClient_validData_clientIsAdded() {
        service.addClient("Jan", "Kowalski", VALID_EVIDENCE, "");
        List<Client> clients = service.getAllClients();
        assertEquals(1, clients.size());
        assertEquals("Jan", clients.get(0).getName());
    }

    @Test
    void addClient_emptyName_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addClient("", "Kowalski", VALID_EVIDENCE, ""));
    }

    @Test
    void addClient_emptySurname_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addClient("Jan", "", VALID_EVIDENCE, ""));
    }

    @Test
    void addClient_emptyEvidence_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addClient("Jan", "Kowalski", "", ""));
    }

    /** Format ^[A-Z]{3}[0-9]{6}$ – małe litery są nieprawidłowe. */
    @Test
    void addClient_invalidEvidenceFormat_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addClient("Jan", "Kowalski", "abc123456", ""));
    }

    /** Za mało cyfr (4 zamiast 6). */
    @Test
    void addClient_evidenceTooShort_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addClient("Jan", "Kowalski", "ABC1234", ""));
    }

    @Test
    void removeClient_existingId_clientIsMarkedDeleted() {
        service.addClient("Anna", "Nowak", VALID_EVIDENCE, "");
        int id = service.getAllClients().get(0).getId();
        service.removeClient(id);
        Client c = service.getClientById(id);
        assertNotNull(c);
        assertTrue(c.isDeleted());
    }

    @Test
    void removeClient_deletedClient_notInGetAllClients() {
        service.addClient("Anna", "Nowak", VALID_EVIDENCE, "");
        int id = service.getAllClients().get(0).getId();
        service.removeClient(id);
        assertTrue(service.getAllClients().isEmpty());
    }

    @Test
    void getClientById_existingId_returnsClient() {
        service.addClient("Jan", "Kowalski", VALID_EVIDENCE, "");
        int id = service.getAllClients().get(0).getId();
        Client found = service.getClientById(id);
        assertNotNull(found);
        assertEquals("Kowalski", found.getSurname());
    }

    @Test
    void getClientById_nonExistingId_returnsNull() {
        assertNull(service.getClientById(999));
    }

    @Test
    void getAllClients_emptyRepo_returnsEmptyList() {
        assertTrue(service.getAllClients().isEmpty());
    }

    @Test
    void getAllClients_deletedClient_isNotReturned() {
        service.addClient("Jan", "Kowalski", VALID_EVIDENCE, "");
        Client c = service.getAllClients().get(0);
        c.setDeleted(true);
        assertTrue(service.getAllClients().isEmpty());
    }

    @Test
    void getAllClients_mixedDeletedAndActive_returnsOnlyActive() {
        service.addClient("Jan",  "Kowalski", VALID_EVIDENCE,   "");
        service.addClient("Anna", "Nowak",    VALID_EVIDENCE_2, "");
        service.getAllClients().get(0).setDeleted(true);
        List<Client> result = service.getAllClients();
        assertEquals(1, result.size());
        assertEquals("Anna", result.get(0).getName());
    }

    @Test
    void updateClient_validData_updatesFields() {
        service.addClient("Jan", "Kowalski", VALID_EVIDENCE, "stary opis");
        int id = service.getAllClients().get(0).getId();

        service.updateClient(id, "Marek", "Wiśniewski", VALID_EVIDENCE_2, "nowy opis");

        Client updated = service.getClientById(id);
        assertEquals("Marek",       updated.getName());
        assertEquals("Wiśniewski",  updated.getSurname());
        assertEquals(VALID_EVIDENCE_2, updated.getEvidence());
        assertEquals("nowy opis",   updated.getOpis());
    }

    @Test
    void updateClient_emptyEvidence_throwsException() {
        service.addClient("Jan", "Kowalski", VALID_EVIDENCE, "");
        int id = service.getAllClients().get(0).getId();
        assertThrows(IllegalArgumentException.class,
                () -> service.updateClient(id, "Jan", "Kowalski", "", ""));
    }

    @Test
    void updateClient_invalidEvidenceFormat_throwsException() {
        service.addClient("Jan", "Kowalski", VALID_EVIDENCE, "");
        int id = service.getAllClients().get(0).getId();
        assertThrows(IllegalArgumentException.class,
                () -> service.updateClient(id, "Jan", "Kowalski", "abc999999", ""));
    }

    @Test
    void searchByName_matchingSurname_returnsClient() {
        service.addClient("Jan",  "Kowalski", VALID_EVIDENCE,   "");
        service.addClient("Anna", "Nowak",    VALID_EVIDENCE_2, "");
        List<Client> result = service.searchByName("Kowalski");
        assertEquals(1, result.size());
        assertEquals("Jan", result.get(0).getName());
    }

    @Test
    void searchByName_nullKeyword_returnsAllClients() {
        service.addClient("Jan",  "Kowalski", VALID_EVIDENCE,   "");
        service.addClient("Anna", "Nowak",    VALID_EVIDENCE_2, "");
        assertEquals(2, service.searchByName(null).size());
    }

    @Test
    void searchByName_noMatch_returnsEmptyList() {
        service.addClient("Jan", "Kowalski", VALID_EVIDENCE, "");
        assertTrue(service.searchByName("Nowak").isEmpty());
    }

    @Test
    void searchByName_partialMatch_returnsClient() {
        service.addClient("Jan", "Kowalski", VALID_EVIDENCE, "");
        List<Client> result = service.searchByName("kowal");
        assertEquals(1, result.size());
    }

    @Test
    void searchByName_deletedClient_notReturned() {
        service.addClient("Jan", "Kowalski", VALID_EVIDENCE, "");
        service.getAllClients().get(0).setDeleted(true);
        assertTrue(service.searchByName("Kowalski").isEmpty());
    }
}
