package AI2.Service;

import AI2.DTO.BikeTypeDTO;
import AI2.Model.BikeType;
import AI2.Repository.BikeTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy jednostkowe dla {@link BikeTypeService}.
 *
 * @author Adrian Karpiński
 */
class BikeTypeServiceTest {

    private BikeTypeRepository stubRepo;
    private BikeTypeService service;

    /**
     * Inicjalizacja przed każdym testem.
     *
     * @author Adrian Karpiński
     */
    @BeforeEach
    void setUp() {
        stubRepo = new BikeTypeRepository() {
            private final List<BikeType> list = new ArrayList<>();
            private int nextId = 1;

            @Override public void addBikeType(BikeType bt) {
                bt.setBikeTypeId(nextId++); list.add(bt);
            }
            @Override public boolean removeBikeType(int id) {
                return list.removeIf(b -> b.getBikeTypeId() == id);
            }
            @Override public boolean updateBikeType(BikeType updated) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getBikeTypeId() == updated.getBikeTypeId()) {
                        list.set(i, updated); return true;
                    }
                }
                return false;
            }
            @Override public List<BikeType> getAllBikeTypes() { return new ArrayList<>(list); }
            @Override public BikeType getBikeTypeById(int id) {
                return list.stream().filter(b -> b.getBikeTypeId() == id).findFirst().orElse(null);
            }
            @Override public void saveBikeTypeRepository() {}
            @Override public void loadBikeTypeRepository() {}
        };
        service = new BikeTypeService(stubRepo);
    }

    /**
     * Sprawdza że poprawny typ roweru zostaje dodany.
     *
     * @author Adrian Karpiński
     */
    @Test
    void addBikeType_validData_returnsBikeType() {
        BikeType result = service.addBikeType(new BikeTypeDTO("Górski", "Mountain", "Rower górski"));
        assertNotNull(result);
        assertEquals("Górski", result.getBikeTypeName());
    }

    /**
     * Sprawdza że pusty opis powoduje wyjątek.
     *
     * @author Adrian Karpiński
     */
    @Test
    void addBikeType_emptyDescription_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addBikeType(new BikeTypeDTO("Górski", "", "")));
    }

    /**
     * Sprawdza że zduplikowana nazwa powoduje wyjątek.
     *
     * @author Adrian Karpiński
     */
    @Test
    void addBikeType_duplicateName_throwsException() {
        service.addBikeType(new BikeTypeDTO("Górski", "", "Opis"));
        assertThrows(IllegalArgumentException.class,
                () -> service.addBikeType(new BikeTypeDTO("Górski", "", "Inny opis")));
    }

    /**
     * Sprawdza poprawne usunięcie typu roweru.
     *
     * @author Adrian Karpiński
     */
    @Test
    void removeBikeType_existingId_returnsTrue() {
        BikeType bt = service.addBikeType(new BikeTypeDTO("Miejski", "City", "Opis"));
        assertTrue(service.removeBikeType(bt.getBikeTypeId()));
    }

    /**
     * Sprawdza że usunięcie nieistniejącego ID zwraca false.
     *
     * @author Adrian Karpiński
     */
    @Test
    void removeBikeType_nonExistingId_returnsFalse() {
        assertFalse(service.removeBikeType(999));
    }

    /**
     * Sprawdza poprawną aktualizację typu roweru.
     *
     * @author Adrian Karpiński
     */
    @Test
    void updateBikeType_validData_updatesType() {
        BikeType bt = service.addBikeType(new BikeTypeDTO("Miejski", "City", "Opis"));
        service.updateBikeType(bt, new BikeTypeDTO("Szosowy", "Road", "Nowy opis"));
        assertEquals("Szosowy", service.getBikeTypeById(bt.getBikeTypeId()).getBikeTypeName());
    }

    /**
     * Sprawdza że aktualizacja null powoduje wyjątek.
     *
     * @author Adrian Karpiński
     */
    @Test
    void updateBikeType_nullType_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.updateBikeType(null, new BikeTypeDTO("X", "", "Y")));
    }
}
