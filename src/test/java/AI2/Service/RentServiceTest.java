package AI2.Service;

import AI2.Enums.BikeStatus;
import AI2.Enums.RentStatus;
import AI2.Model.Bike;
import AI2.Model.Client;
import AI2.Model.Rent;
import AI2.Repository.BikeRepository;
import AI2.Repository.ClientRepository;
import AI2.Repository.RentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy jednostkowe dla {@link RentService}.
 *
 * @author Tomasz Piłat
 */
class RentServiceTest {

    private BikeRepository   bikeRepo;
    private ClientRepository clientRepo;
    private RentRepository   rentRepo;
    private RentService      service;

    private static final LocalDateTime FUTURE     = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime FAR_FUTURE = LocalDateTime.now().plusDays(7);

    @BeforeEach
    void setUp() {
        bikeRepo = new BikeRepository() {
            private final List<Bike> list = new ArrayList<>();
            { list.add(new Bike(1, 1, 1, 26, BikeStatus.AVAILABLE, "")); }

            @Override public Bike getBikeById(int id) {
                return list.stream().filter(b -> b.getBikeId() == id).findFirst().orElse(null);
            }
            @Override public List<Bike> getAllBikes() { return new ArrayList<>(list); }
            @Override public boolean updateBike(Bike u) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getBikeId() == u.getBikeId()) { list.set(i, u); return true; }
                }
                return false;
            }
            @Override public void addBike(Bike b)               {}
            @Override public boolean removeBike(int id)          { return false; }
            @Override public List<Bike> getBikesByStatus(BikeStatus s) { return new ArrayList<>(); }
            @Override public void saveBikeRepository()           {}
            @Override public void loadBikeRepository()           {}
        };

        clientRepo = new ClientRepository() {
            private final Client c = new Client(1, "Jan", "Kowalski", "ABC", "");
            @Override public Client getClientById(int id) { return id == 1 ? c : null; }
            @Override public List<Client> getAllClients()  { return List.of(c); }
            @Override public void addClient(Client client)     {}
            @Override public void removeClient(int id)         {}
            @Override public void updateClient(Client client)  {}
            @Override public int getNextId()                   { return 2; }
        };

        rentRepo = new RentRepository() {
            private final List<Rent> list = new ArrayList<>();
            private int nextId = 0;
            @Override public void addRent(Rent r)   { r.setId(nextId++); list.add(r); }
            @Override public void removeRent(int id) { list.removeIf(r -> r.getId() == id); }
            @Override public Rent getRentByID(int id) {
                return list.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
            }
            @Override public void updateRent(Rent nr) {
                for (Rent r : list) {
                    if (r.getId() == nr.getId()) {
                        r.setBikeId(nr.getBikeId());
                        r.setClientId(nr.getClientId());
                        r.setRentDate(nr.getRentDate());
                        r.setReturnTime(nr.getReturnTime());
                        r.setStatus(nr.getStatus());
                        return;
                    }
                }
            }
            @Override public List<Rent> getRentDataBase()  { return new ArrayList<>(list); }
            @Override public void saveRentDataBase()        {}
            @Override public List<Rent> loadRentDataBase() { return new ArrayList<>(); }
        };

        service = new RentService(rentRepo, bikeRepo, clientRepo);
    }

    // ----------------------------------------------------------------
    // addRent
    // ----------------------------------------------------------------

    @Test
    void addRent_validData_rentIsScheduled() {
        Rent rent = new Rent(1, 1, FUTURE, FAR_FUTURE);
        service.addRent(rent);
        assertEquals(RentStatus.SCHEDULED, rent.getStatus());
    }

    @Test
    void addRent_pastDate_throwsException() {
        Rent rent = new Rent(1, 1, LocalDateTime.now().minusDays(1), FAR_FUTURE);
        assertThrows(IllegalArgumentException.class, () -> service.addRent(rent));
    }

    @Test
    void addRent_nonExistingBike_throwsException() {
        Rent rent = new Rent(999, 1, FUTURE, FAR_FUTURE);
        assertThrows(IllegalArgumentException.class, () -> service.addRent(rent));
    }

    @Test
    void addRent_nonExistingClient_throwsException() {
        Rent rent = new Rent(1, 999, FUTURE, FAR_FUTURE);
        assertThrows(IllegalArgumentException.class, () -> service.addRent(rent));
    }

    @Test
    void addRent_conflictingPeriod_throwsException() {
        service.addRent(new Rent(1, 1, FUTURE, FAR_FUTURE));
        // Ten sam rower w nakładającym się terminie
        Rent conflict = new Rent(1, 1,
                FUTURE.plusHours(1), FAR_FUTURE.plusDays(1));
        assertThrows(IllegalArgumentException.class, () -> service.addRent(conflict));
    }

    // ----------------------------------------------------------------
    // endRent
    // ----------------------------------------------------------------

    @Test
    void endRent_scheduledRent_finishesAndBikeStaysAvailable() {
        Rent rent = new Rent(1, 1, FUTURE, FAR_FUTURE);
        service.addRent(rent);
        // Rower SCHEDULED → rower wciąż AVAILABLE, endRent nie powinien go zmieniać
        service.endRent(rent.getId());
        assertEquals(RentStatus.FINISHED, service.getRentByID(rent.getId()).getStatus());
        assertEquals(BikeStatus.AVAILABLE, bikeRepo.getBikeById(1).getStatus());
    }

    @Test
    void endRent_alreadyFinished_throwsException() {
        Rent rent = new Rent(1, 1, FUTURE, FAR_FUTURE);
        service.addRent(rent);
        service.endRent(rent.getId());
        assertThrows(IllegalStateException.class, () -> service.endRent(rent.getId()));
    }

    @Test
    void endRent_nonExisting_throwsException() {
        assertThrows(RuntimeException.class, () -> service.endRent(999));
    }

    // ----------------------------------------------------------------
    // removeRent
    // ----------------------------------------------------------------

    @Test
    void removeRent_scheduled_rentIsRemoved() {
        Rent rent = new Rent(1, 1, FUTURE, FAR_FUTURE);
        service.addRent(rent);
        int id = rent.getId();
        service.removeRent(id);
        assertThrows(RuntimeException.class, () -> service.getRentByID(id));
    }

    @Test
    void removeRent_nonExisting_throwsException() {
        assertThrows(RuntimeException.class, () -> service.removeRent(999));
    }

    // ----------------------------------------------------------------
    // getRentByID / getAllRents
    // ----------------------------------------------------------------

    @Test
    void getRentByID_nonExisting_throwsException() {
        assertThrows(RuntimeException.class, () -> service.getRentByID(999));
    }

    @Test
    void getAllRents_empty_returnsEmptyList() {
        assertTrue(service.getAllRents().isEmpty());
    }

    @Test
    void getAllRents_afterAdd_containsRent() {
        service.addRent(new Rent(1, 1, FUTURE, FAR_FUTURE));
        assertEquals(1, service.getAllRents().size());
    }

    // ----------------------------------------------------------------
    // findClientRents / findBikeRents
    // ----------------------------------------------------------------

    @Test
    void findClientRents_correctClientId_returnsRent() {
        service.addRent(new Rent(1, 1, FUTURE, FAR_FUTURE));
        List<Rent> found = service.findClientRents(1);
        assertEquals(1, found.size());
    }

    @Test
    void findClientRents_wrongClientId_returnsEmpty() {
        service.addRent(new Rent(1, 1, FUTURE, FAR_FUTURE));
        assertTrue(service.findClientRents(99).isEmpty());
    }

    @Test
    void findBikeRents_correctBikeId_returnsRent() {
        service.addRent(new Rent(1, 1, FUTURE, FAR_FUTURE));
        assertEquals(1, service.findBikeRents(1).size());
    }

    // ----------------------------------------------------------------
    // clientHasActiveRentals / bikeHasActiveRentals
    // ----------------------------------------------------------------

    @Test
    void clientHasActiveRentals_scheduledRent_returnsTrue() {
        service.addRent(new Rent(1, 1, FUTURE, FAR_FUTURE));
        assertTrue(service.clientHasActiveRentals(1));
    }

    @Test
    void clientHasActiveRentals_noRents_returnsFalse() {
        assertFalse(service.clientHasActiveRentals(1));
    }

    @Test
    void clientHasActiveRentals_finishedRent_returnsFalse() {
        Rent rent = new Rent(1, 1, FUTURE, FAR_FUTURE);
        service.addRent(rent);
        service.endRent(rent.getId());
        assertFalse(service.clientHasActiveRentals(1));
    }

    @Test
    void bikeHasActiveRentals_scheduledRent_returnsTrue() {
        service.addRent(new Rent(1, 1, FUTURE, FAR_FUTURE));
        assertTrue(service.bikeHasActiveRentals(1));
    }

    @Test
    void bikeHasActiveRentals_noRents_returnsFalse() {
        assertFalse(service.bikeHasActiveRentals(1));
    }

    // ----------------------------------------------------------------
    // isBikeAvailableInPeriod
    // ----------------------------------------------------------------

    @Test
    void isBikeAvailableInPeriod_noRents_returnsTrue() {
        assertTrue(service.isBikeAvailableInPeriod(1, FUTURE, FAR_FUTURE));
    }

    @Test
    void isBikeAvailableInPeriod_overlappingScheduled_returnsFalse() {
        service.addRent(new Rent(1, 1, FUTURE, FAR_FUTURE));
        assertFalse(service.isBikeAvailableInPeriod(1,
                FUTURE.plusHours(1), FAR_FUTURE.plusDays(1)));
    }

    @Test
    void isBikeAvailableInPeriod_noOverlap_returnsTrue() {
        service.addRent(new Rent(1, 1, FUTURE, FUTURE.plusHours(4)));
        assertTrue(service.isBikeAvailableInPeriod(1,
                FUTURE.plusDays(2), FUTURE.plusDays(3)));
    }

    @Test
    void isBikeAvailableInPeriod_differentBike_returnsTrue() {
        service.addRent(new Rent(1, 1, FUTURE, FAR_FUTURE));
        // Bike ID 2 nie ma żadnych wypożyczeń
        assertTrue(service.isBikeAvailableInPeriod(2, FUTURE, FAR_FUTURE));
    }
}
