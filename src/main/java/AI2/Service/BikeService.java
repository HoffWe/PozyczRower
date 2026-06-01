package AI2.Service;

import AI2.Enums.BikeStatus;
import AI2.Model.Bike;
import AI2.Repository.BikeRepository;

import java.util.List;

/**
 * Serwis obsługujący rowery.
 */
public class BikeService {

    /**
     * Repozytorium rowerów.
     */
    private final BikeRepository bikeRepository;

    /**
     * Tworzy serwis rowerów.
     *
     * @param bikeRepository repozytorium rowerów
     */
    public BikeService(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }

    /**
     * Dodaje nowy rower.
     *
     * @param brand marka roweru
     * @param model model roweru
     * @param type typ roweru
     * @param wheelSize rozmiar koła
     * @param status status roweru
     * @param description opis roweru
     * @return dodany rower
     */
    public Bike addBike(String brand, String model, String type, int wheelSize,
                        BikeStatus status, String description) {
        validateBikeData(brand, model, type, wheelSize);

        Bike bike = new Bike(
                0,
                brand.trim(),
                model.trim(),
                type.trim(),
                wheelSize,
                status,
                description == null ? "" : description.trim()
        );

        bikeRepository.addBike(bike);
        return bike;
    }

    /**
     * Usuwa rower.
     *
     * @param bikeId identyfikator roweru
     * @return true, jeśli rower został usunięty
     */
    public boolean removeBike(int bikeId) {
        return bikeRepository.removeBike(bikeId);
    }

    /**
     * Aktualizuje rower.
     *
     * @param bike rower z nowymi danymi
     * @return true, jeśli zaktualizowano rower
     */
    public boolean updateBike(Bike bike) {
        if (bike == null) {
            throw new IllegalArgumentException("Rower nie może być pusty.");
        }

        validateBikeData(
                bike.getBrand(),
                bike.getModel(),
                bike.getType(),
                bike.getWheelSize()
        );

        bike.setBrand(bike.getBrand().trim());
        bike.setModel(bike.getModel().trim());
        bike.setType(bike.getType().trim());
        bike.setStatus(bike.getStatus());

        if (bike.getDescription() == null) {
            bike.setDescription("");
        } else {
            bike.setDescription(bike.getDescription().trim());
        }

        return bikeRepository.updateBike(bike);
    }

    /**
     * Zwraca wszystkie rowery.
     *
     * @return lista rowerów
     */
    public List<Bike> getAllBikes() {
        return bikeRepository.getAllBikes();
    }

    /**
     * Zwraca rower po identyfikatorze.
     *
     * @param bikeId identyfikator roweru
     * @return rower albo null
     */
    public Bike getBikeById(int bikeId) {
        return bikeRepository.getBikeById(bikeId);
    }

    /**
     * Zwraca rowery o podanym statusie.
     *
     * @param status status roweru
     * @return lista rowerów
     */
    public List<Bike> getBikesByStatus(BikeStatus status) {
        return bikeRepository.getBikesByStatus(status);
    }

    /**
     * Sprawdza poprawność podstawowych danych roweru.
     *
     * @param brand marka roweru
     * @param model model roweru
     * @param type typ roweru
     * @param wheelSize rozmiar koła
     */
    private void validateBikeData(String brand, String model, String type,
                                  int wheelSize) {
        if (brand == null || brand.isBlank()) {
            throw new IllegalArgumentException("Marka roweru nie może być pusta.");
        }

        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Model roweru nie może być pusty.");
        }

        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Typ roweru nie może być pusty.");
        }

        if (wheelSize <= 0) {
            throw new IllegalArgumentException("Rozmiar koła musi być większy od zera.");
        }
    }
}