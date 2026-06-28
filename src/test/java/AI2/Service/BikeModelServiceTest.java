package AI2.Service;

import AI2.DTO.BikeModelDTO;
import AI2.Model.BikeModel;
import AI2.Repository.BikeModelRepository;
import AI2.Repository.BikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy jednostkowe dla {@link BikeModelService}.
 *
 * @author Rafał Wojciechowski
 */
class BikeModelServiceTest {

    /** Stub repozytorium in-memory (bez zapisu do pliku). */
    private BikeModelRepository stubRepo;

    /** Testowany serwis. */
    private BikeModelService service;

    /**
     * Inicjalizacja przed każdym testem.
     *
     * @author Rafał Wojciechowski
     */
    @BeforeEach
    void setUp() {
        stubRepo = new BikeModelRepository() {
            private final List<BikeModel> list = new ArrayList<>();
            private int nextId = 1;

            @Override public void addBikeModel(BikeModel bm) {
                bm.setId(nextId++); list.add(bm);
            }
            @Override public boolean removeBikeModel(int id) {
                return list.removeIf(b -> b.getId() == id);
            }
            @Override public boolean updateBikeModel(BikeModel updated) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId() == updated.getId()) {
                        list.set(i, updated); return true;
                    }
                }
                return false;
            }
            @Override public List<BikeModel> getAllBikeModels() {
                return new ArrayList<>(list);
            }
            @Override public BikeModel getBikeModelById(int id) {
                return list.stream().filter(b -> b.getId() == id).findFirst().orElse(null);
            }
            @Override public void saveBikeModelRepository() { /* brak zapisu w testach */ }
            @Override public void loadBikeModelRepository() { /* brak odczytu w testach */ }
        };
        service = new BikeModelService(stubRepo, new BikeRepository());
    }

    /**
     * Sprawdza że poprawny model roweru zostaje dodany.
     *
     * @author Rafał Wojciechowski
     */
    @Test
    void addBikeModel_validData_returnsAddedModel() {
        BikeModel result = service.addBikeModel(new BikeModelDTO("Trek", "X5"));
        assertNotNull(result);
        assertEquals("Trek", result.getBrand());
        assertEquals("X5",   result.getModel());
    }

    /**
     * Sprawdza że pusta marka powoduje wyjątek.
     *
     * @author Rafał Wojciechowski
     */
    @Test
    void addBikeModel_emptyBrand_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addBikeModel(new BikeModelDTO("", "X5")));
    }

    /**
     * Sprawdza że pusty model powoduje wyjątek.
     *
     * @author Rafał Wojciechowski
     */
    @Test
    void addBikeModel_emptyModel_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addBikeModel(new BikeModelDTO("Trek", "")));
    }

    /**
     * Sprawdza że zduplikowany model powoduje wyjątek.
     *
     * @author Rafał Wojciechowski
     */
    @Test
    void addBikeModel_duplicate_throwsException() {
        service.addBikeModel(new BikeModelDTO("Trek", "X5"));
        assertThrows(IllegalArgumentException.class,
                () -> service.addBikeModel(new BikeModelDTO("Trek", "X5")));
    }

    /**
     * Sprawdza poprawne usuwanie modelu roweru.
     *
     * @author Rafał Wojciechowski
     */
    @Test
    void removeBikeModel_existingId_returnsTrue() {
        BikeModel added = service.addBikeModel(new BikeModelDTO("Giant", "Escape"));
        assertTrue(service.removeBikeModel(added.getId()));
        assertTrue(service.getAllBikeModels().isEmpty());
    }

    /**
     * Sprawdza że usunięcie nieistniejącego modelu zwraca false.
     *
     * @author Rafał Wojciechowski
     */
    @Test
    void removeBikeModel_nonExistingId_returnsFalse() {
        assertFalse(service.removeBikeModel(999));
    }

    /**
     * Sprawdza poprawną aktualizację modelu roweru.
     *
     * @author Rafał Wojciechowski
     */
    @Test
    void updateBikeModel_validData_updatesModel() {
        BikeModel added = service.addBikeModel(new BikeModelDTO("Giant", "Escape"));
        service.updateBikeModel(added, new BikeModelDTO("Trek", "Marlin"));
        BikeModel updated = service.getBikeModelById(added.getId());
        assertEquals("Trek",   updated.getBrand());
        assertEquals("Marlin", updated.getModel());
    }

    /**
     * Sprawdza że aktualizacja null modelu powoduje wyjątek.
     *
     * @author Rafał Wojciechowski
     */
    @Test
    void updateBikeModel_nullModel_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.updateBikeModel(null, new BikeModelDTO("Trek", "X5")));
    }

    /**
     * Sprawdza że lista modeli jest pusta po inicjalizacji.
     *
     * @author Rafał Wojciechowski
     */
    @Test
    void getAllBikeModels_emptyRepo_returnsEmptyList() {
        assertTrue(service.getAllBikeModels().isEmpty());
    }
}
