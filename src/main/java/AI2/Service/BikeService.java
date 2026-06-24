package AI2.Service;

import AI2.Enums.BikeStatus;
import AI2.Model.Bike;
import AI2.Repository.BikeRepository;
import AI2.Util.LanguageManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serwis obsługujący logikę biznesową rowerów.
 *
 * @author Rafał Wojciechowski
 */
public class BikeService {

    /** Repozytorium rowerów. */
    private final BikeRepository bikeRepository;

    /**
     * Tworzy serwis rowerów.
     *
     * @param bikeRepository repozytorium rowerów
     * @author Rafał Wojciechowski
     */
    public BikeService(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }

    /**
     * Dodaje nowy rower do systemu.
     *
     * @param bikeModelId identyfikator modelu roweru
     * @param bikeTypeId identyfikator typu roweru
     * @param wheelSize rozmiar koła
     * @param status status roweru
     * @param description opis roweru
     * @return dodany rower
     * @throws IllegalArgumentException jeśli dane są niepoprawne
     * @author Rafał Wojciechowski
     */
    public Bike addBike(int bikeModelId, int bikeTypeId, int wheelSize,
                        BikeStatus status, String description) {
        validateBikeData(bikeModelId, bikeTypeId, wheelSize);

        Bike bike = new Bike(
                0,
                bikeModelId,
                bikeTypeId,
                wheelSize,
                status,
                description == null ? "" : description.trim()
        );
        bikeRepository.addBike(bike);
        return bike;
    }

    /**
     * Usuwa rower z systemu.
     *
     * @param bikeId identyfikator roweru
     * @return {@code true} jeśli rower został usunięty
     * @author Rafał Wojciechowski
     */
    public boolean removeBike(int bikeId) {
        return bikeRepository.removeBike(bikeId);
    }

    /**
     * Aktualizuje dane roweru.
     *
     * @param bike rower z nowymi danymi
     * @return {@code true} jeśli aktualizacja się powiodła
     * @throws IllegalArgumentException jeśli rower jest null lub dane niepoprawne
     * @author Rafał Wojciechowski
     */
    public boolean updateBike(Bike bike) {
        if (bike == null) {
            throw new IllegalArgumentException(LanguageManager.getString("error.bike.null"));
        }
        validateBikeData(bike.getBikeModelId(), bike.getBikeTypeId(), bike.getWheelSize());

        if (bike.getDescription() == null) bike.setDescription("");

        return bikeRepository.updateBike(bike);
    }

    /**
     * Zwraca wszystkie aktywne (nie usunięte) rowery.
     *
     * @return lista rowerów
     * @author Rafał Wojciechowski
     */
    public List<Bike> getAllBikes() {
        return bikeRepository.getAllBikes().stream()
                .filter(b -> !b.isDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Zwraca rower po identyfikatorze.
     *
     * @param bikeId identyfikator roweru
     * @return rower albo {@code null} gdy nie znaleziono
     * @author Rafał Wojciechowski
     */
    public Bike getBikeById(int bikeId) {
        return bikeRepository.getBikeById(bikeId);
    }

    /**
     * Zwraca rowery o podanym statusie.
     *
     * @param status status roweru
     * @return lista rowerów o podanym statusie
     * @author Rafał Wojciechowski
     */
    public List<Bike> getBikesByStatus(BikeStatus status) {
        return bikeRepository.getBikesByStatus(status);
    }

    /**
     * Sprawdza poprawność danych roweru.
     *
     * @param bikeModelId identyfikator modelu roweru (musi być > 0)
     * @param bikeTypeId identyfikator typu roweru (musi być > 0)
     * @param wheelSize rozmiar koła (musi być > 0)
     * @throws IllegalArgumentException jeśli któraś wartość jest niepoprawna
     * @author Rafał Wojciechowski
     */
    private void validateBikeData(int bikeModelId, int bikeTypeId, int wheelSize) {
        if (bikeModelId <= 0) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.bike.modelRequired"));
        }
        if (bikeTypeId <= 0) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.bike.typeRequired"));
        }
        if (wheelSize <= 0) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.bike.wheelSizePositive"));
        }
    }
}
