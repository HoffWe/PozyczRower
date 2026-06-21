package AI2.Service;

import AI2.Enums.BikeStatus;
import AI2.Model.Bike;
import AI2.Repository.BikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy jednostkowe dla {@link BikeService}.
 *
 * @author Rafał Wojciechowski
 */
class BikeServiceTest {

    private BikeRepository stubRepo;
    private BikeService    service;

    /**
     * Inicjalizacja przed każdym testem.
     *
     * @author Rafał Wojciechowski
     */
    @BeforeEach
    void setUp() {
        stubRepo = new BikeRepository() {
            private final List<Bike> list = new ArrayList<>();
            private int nextId = 1;

            @Override public void addBike(Bike b) {
                b.setBikeId(nextId++); list.add(b);
            }
            @Override public boolean removeBike(int id) {
                return list.removeIf(b -> b.getBikeId() == id);
            }
            @Override public boolean updateBike(Bike updated) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getBikeId() == updated.getBikeId()) {
                        list.set(i, updated); return true;
                    }
                }
                return false;
            }
            @Override public List<Bike> getAllBikes() { return new ArrayList<>(list); }
            @Override public Bike getBikeById(int id) {
                return list.stream().filter(b -> b.getBikeId() == id).findFirst().orElse(null);
            }
            @Override public List<Bike> getBikesByStatus(BikeStatus status) {
                return list.stream().filter(b -> b.getStatus() == status)
                        .collect(java.util.stream.Collectors.toList());
            }
            @Override public void saveBikeRepository() {}
            @Override public void loadBikeRepository() {}
        };
        service = new BikeService(stubRepo);
    }

    /**
     * Sprawdza poprawne dodanie roweru.
     *
     * @author Rafał Wojciechowski
     */
    @Test
    void addBike_validData_returnsAddedBike() {
        Bike bike = service.addBike(1, 1, 26, BikeStatus.AVAILABLE, "Testowy rower");
        assertNotNull(bike);
        assertEquals(26, bike.getWheelSize());
        assertEquals(BikeStatus.AVAILABLE, bike.getStatus());
    }

    /**
     * Sprawdza że bikeModelId = 0 powoduje wyjątek.
     *
     * @author Rafał Wojciechowski
     */
    @Test
    void addBike_invalidModelId_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addBike(0, 1, 26, BikeStatus.AVAILABLE, ""));
    }

    /**
     * Sprawdza że bikeTypeId = 0 powoduje wyjątek.
     *
     * @author Rafał Wojciechowski
     */
    @Test
    void addBike_invalidTypeId_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addBike(1, 0, 26, BikeStatus.AVAILABLE, ""));
    }

    /**
     * Sprawdza że rozmiar koła <= 0 powoduje wyjątek.
     *
     * @author Rafał Wojciechowski
     */
    @Test
    void addBike_invalidWheelSize_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addBike(1, 1, 0, BikeStatus.AVAILABLE, ""));
    }

    /**
     * Sprawdza poprawne usunięcie roweru.
     *
     * @author Rafał Wojciechowski
     */
    @Test
    void removeBike_existingId_returnsTrue() {
        Bike bike = service.addBike(1, 1, 28, BikeStatus.AVAILABLE, "");
        assertTrue(service.removeBike(bike.getBikeId()));
        assertNull(service.getBikeById(bike.getBikeId()));
    }

    /**
     * Sprawdza że usunięcie nieistniejącego ID zwraca false.
     *
     * @author Rafał Wojciechowski
     */
    @Test
    void removeBike_nonExistingId_returnsFalse() {
        assertFalse(service.removeBike(999));
    }

    /**
     * Sprawdza filtrowanie rowerów po statusie.
     *
     * @author Rafał Wojciechowski
     */
    @Test
    void getBikesByStatus_onlyAvailable_returnsCorrectBikes() {
        service.addBike(1, 1, 26, BikeStatus.AVAILABLE, "");
        service.addBike(1, 1, 28, BikeStatus.RENTED,    "");
        List<Bike> available = service.getBikesByStatus(BikeStatus.AVAILABLE);
        assertEquals(1, available.size());
        assertEquals(BikeStatus.AVAILABLE, available.get(0).getStatus());
    }

    /**
     * Sprawdza że updateBike z null powoduje wyjątek.
     *
     * @author Rafał Wojciechowski
     */
    @Test
    void updateBike_nullBike_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> service.updateBike(null));
    }
}
