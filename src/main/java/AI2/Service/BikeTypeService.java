package AI2.Service;

import AI2.DTO.BikeTypeDTO;
import AI2.Model.BikeType;
import AI2.Repository.BikeTypeRepository;
import AI2.Util.LanguageManager;

import java.util.List;

/**
 * Serwis obsługujący logikę biznesową typów rowerów.
 *
 * @author Adrian Karpiński
 */
public class BikeTypeService {

    /** Repozytorium typów rowerów. */
    private final BikeTypeRepository bikeTypeRepository;

    /**
     * Tworzy serwis typów rowerów.
     *
     * @param bikeTypeRepository repozytorium typów rowerów
     * @author Adrian Karpiński
     */
    public BikeTypeService(BikeTypeRepository bikeTypeRepository) {
        this.bikeTypeRepository = bikeTypeRepository;
    }

    /**
     * Dodaje nowy typ roweru.
     *
     * @param newType dane nowego typu roweru
     * @return dodany typ roweru
     * @throws IllegalArgumentException jeśli nazwa jest pusta lub już istnieje
     * @author Adrian Karpiński
     */
    public BikeType addBikeType(BikeTypeDTO newType) {
        validateBikeTypeData(newType);

        String safeName        = newType.name().trim();
        String safeDescription = newType.description().trim();

        for (BikeType bikeType : bikeTypeRepository.getAllBikesTypes()) {
            if (bikeType.getBikeTypeName().equals(safeName)) {
                throw new IllegalArgumentException(
                        LanguageManager.getString("error.bikeType.nameExists"));
            }
        }

        BikeType bikeType = new BikeType(0, safeName, safeDescription);
        bikeTypeRepository.addBikeType(bikeType);
        return bikeType;
    }

    /**
     * Usuwa typ roweru po identyfikatorze.
     *
     * @param bikeTypeId identyfikator typu roweru
     * @return {@code true} jeśli typ roweru został usunięty
     * @author Adrian Karpiński
     */
    public boolean removeBikeType(int bikeTypeId) {
        return bikeTypeRepository.removeBikeType(bikeTypeId);
    }

    /**
     * Aktualizuje istniejący typ roweru.
     *
     * @param bikeType    typ roweru do zaktualizowania
     * @param bikeTypeDTO nowe dane z widoku
     * @return {@code true} jeśli aktualizacja się powiodła
     * @throws IllegalArgumentException jeśli typ jest null lub dane niepoprawne
     * @author Adrian Karpiński
     */
    public boolean updateBikeType(BikeType bikeType, BikeTypeDTO bikeTypeDTO) {
        if (bikeType == null) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.bikeType.null"));
        }
        validateBikeTypeData(bikeTypeDTO);

        bikeType.setBikeTypeName(bikeTypeDTO.name().trim());
        bikeType.setBikeTypeDescription(bikeTypeDTO.description().trim());
        return bikeTypeRepository.updateBikeType(bikeType);
    }

    /**
     * Zwraca wszystkie typy rowerów.
     *
     * @return lista typów rowerów
     * @author Adrian Karpiński
     */
    public List<BikeType> getAllBikeTypes() {
        return bikeTypeRepository.getAllBikesTypes();
    }

    /**
     * Zwraca typ roweru po identyfikatorze.
     *
     * @param bikeTypeId identyfikator typu roweru
     * @return typ roweru albo {@code null} gdy nie znaleziono
     * @author Adrian Karpiński
     */
    public BikeType getBikeTypeById(int bikeTypeId) {
        return bikeTypeRepository.getBikeTypeById(bikeTypeId);
    }

    /**
     * Zapisuje dane typów rowerów do pliku.
     *
     * @author Adrian Karpiński
     */
    public void saveBikeTypes() {
        bikeTypeRepository.saveBikeTypeRepository();
    }

    /**
     * Sprawdza poprawność danych typu roweru.
     *
     * @param dto dane do walidacji
     * @throws IllegalArgumentException jeśli nazwa lub opis są puste
     * @author Adrian Karpiński
     */
    private void validateBikeTypeData(BikeTypeDTO dto) {
        if (dto.name() == null || dto.name().isBlank()) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.bikeType.nameEmpty"));
        }
        if (dto.description() == null || dto.description().isBlank()) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.bikeType.descriptionEmpty"));
        }
    }
}
