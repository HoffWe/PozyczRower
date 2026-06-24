package AI2.Service;

import AI2.DTO.BikeModelDTO;
import AI2.Model.BikeModel;
import AI2.Repository.BikeModelRepository;
import AI2.Util.LanguageManager;

import java.util.List;

/**
 * Serwis obsługujący logikę biznesową modeli rowerów.
 *
 * @author Rafał Wojciechowski
 */
public class BikeModelService {

    /** Repozytorium modeli rowerów. */
    private final BikeModelRepository bikeModelRepository;

    /**
     * Tworzy serwis modeli rowerów.
     *
     * @param bikeModelRepository repozytorium modeli rowerów
     * @author Rafał Wojciechowski
     */
    public BikeModelService(BikeModelRepository bikeModelRepository) {
        this.bikeModelRepository = bikeModelRepository;
    }

    /**
     * Dodaje nowy model roweru.
     *
     * @param dto dane modelu roweru z widoku
     * @return dodany model roweru
     * @throws IllegalArgumentException jeśli dane są niepoprawne lub model już istnieje
     * @author Rafał Wojciechowski
     */
    public BikeModel addBikeModel(BikeModelDTO dto) {
        validateBikeModelData(dto);

        String safeBrand = dto.brand().trim();
        String safeModel = dto.model().trim();

        for (BikeModel bm : bikeModelRepository.getAllBikeModels()) {
            if (bm.getBrand().equalsIgnoreCase(safeBrand)
                    && bm.getModel().equalsIgnoreCase(safeModel)) {
                throw new IllegalArgumentException(
                        LanguageManager.getString("error.bikeModel.nameExists"));
            }
        }

        BikeModel bikeModel = new BikeModel(0, safeBrand, safeModel);
        bikeModelRepository.addBikeModel(bikeModel);
        return bikeModel;
    }

    /**
     * Usuwa model roweru po identyfikatorze.
     *
     * @param bikeModelId identyfikator modelu roweru
     * @return {@code true} jeśli model został usunięty
     * @author Rafał Wojciechowski
     */
    public boolean removeBikeModel(int bikeModelId) {
        return bikeModelRepository.removeBikeModel(bikeModelId);
    }

    /**
     * Aktualizuje istniejący model roweru.
     *
     * @param bikeModel model roweru do zaktualizowania
     * @param dto nowe dane z widoku
     * @return {@code true} jeśli aktualizacja się powiodła
     * @throws IllegalArgumentException jeśli model jest null lub dane niepoprawne
     * @author Rafał Wojciechowski
     */
    public boolean updateBikeModel(BikeModel bikeModel, BikeModelDTO dto) {
        if (bikeModel == null) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.bikeModel.null"));
        }

        validateBikeModelData(dto);

        String safeBrand = dto.brand().trim();
        String safeModel = dto.model().trim();

        if (isBikeModelTaken(safeBrand, safeModel, bikeModel.getId())) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.bikeModel.nameExists"));
        }

        bikeModel.setBrand(safeBrand);
        bikeModel.setModel(safeModel);
        return bikeModelRepository.updateBikeModel(bikeModel);
    }

    /**
     * Zwraca wszystkie modele rowerów.
     *
     * @return lista modeli rowerów
     * @author Rafał Wojciechowski
     */
    public List<BikeModel> getAllBikeModels() {
        return bikeModelRepository.getAllBikeModels();
    }

    /**
     * Zwraca model roweru po identyfikatorze.
     *
     * @param bikeModelId identyfikator modelu roweru
     * @return model roweru albo {@code null} gdy nie znaleziono
     * @author Rafał Wojciechowski
     */
    public BikeModel getBikeModelById(int bikeModelId) {
        return bikeModelRepository.getBikeModelById(bikeModelId);
    }

    /**
     * Zapisuje dane modeli rowerów do pliku.
     *
     * @author Rafał Wojciechowski
     */
    public void saveBikeModels() {
        bikeModelRepository.saveBikeModelRepository();
    }

    /**
     * Sprawdza poprawność danych modelu roweru.
     *
     * @param dto dane do walidacji
     * @throws IllegalArgumentException jeśli marka lub model są puste
     * @author Rafał Wojciechowski
     */
    private void validateBikeModelData(BikeModelDTO dto) {
        if (dto.brand() == null || dto.brand().isBlank()) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.bikeModel.brandEmpty"));
        }
        if (dto.model() == null || dto.model().isBlank()) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.bikeModel.modelEmpty"));
        }
    }
    /**
     * Sprawdza, czy para marka + model jest już zajęta przez inny rekord.
     *
     * @param brand marka do sprawdzenia
     * @param model model do sprawdzenia
     * @param excludedId identyfikator rekordu, który ma być pominięty
     * @return {@code true} jeśli taka para istnieje w innym rekordzie
     */
    private boolean isBikeModelTaken(String brand, String model, Integer excludedId) {
        for (BikeModel bikeModel : bikeModelRepository.getAllBikeModels()) {
            if (excludedId != null && bikeModel.getId() == excludedId) {
                continue;
            }
            if (bikeModel.getBrand().equalsIgnoreCase(brand)
                    && bikeModel.getModel().equalsIgnoreCase(model)) {
                return true;
            }
        }
        return false;
    }
}
