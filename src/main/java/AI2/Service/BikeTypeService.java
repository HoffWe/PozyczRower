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
     */
    public BikeType addBikeType(BikeTypeDTO newType) {
        validateBikeTypeData(newType);

        String safeName = newType.name().trim();
        String safeDescription = newType.description().trim();

        for (BikeType bikeType : bikeTypeRepository.getAllBikeTypes()) {

            if (bikeType.getBikeTypeName().equals(safeName)) {
                throw new IllegalArgumentException(
                        LanguageManager.getString("error.bikeType.nameExists"));
            }

        }

        String safeNameEn = newType.nameEn() == null ? "" : newType.nameEn().trim();
        BikeType bikeType = new BikeType(0, safeName, safeNameEn, safeDescription);

        bikeTypeRepository.addBikeType(bikeType);

        return bikeType;
    }

    /**
     * Usuwa typ roweru po identyfikatorze.
     *
     * @param bikeTypeId identyfikator typu roweru
     * @return {@code true} jeśli typ roweru został usunięty
     */
    public boolean removeBikeType(int bikeTypeId) {
        return bikeTypeRepository.removeBikeType(bikeTypeId);
    }

    /**
     * Aktualizuje istniejący typ roweru.
     *
     * @param bikeType typ roweru do zaktualizowania
     * @param bikeTypeDTO nowe dane z widoku
     * @return {@code true} jeśli aktualizacja się powiodłsa
     * @throws IllegalArgumentException jeśli typ jest null lub dane niepoprawne
     */
    public boolean updateBikeType(BikeType bikeType, BikeTypeDTO bikeTypeDTO) {
        if (bikeType == null) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.bikeType.null"));
        }

        validateBikeTypeData(bikeTypeDTO);

        String safeName = bikeTypeDTO.name().trim();
        String safeDescription = bikeTypeDTO.description().trim();
        String safeNameEn = bikeTypeDTO.nameEn() == null ? "" : bikeTypeDTO.nameEn().trim();

        if (isBikeTypeNameTaken(safeName, bikeType.getBikeTypeId())) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.bikeType.nameExists"));
        }

        bikeType.setBikeTypeName(safeName);
        bikeType.setBikeTypeNameEn(safeNameEn);
        bikeType.setBikeTypeDescription(safeDescription);

        return bikeTypeRepository.updateBikeType(bikeType);
    }

    /**
     * Zwraca wszystkie typy rowerów.
     *
     * @return lista typów rowerów
     */
    public List<BikeType> getAllBikeTypes() {
        return bikeTypeRepository.getAllBikeTypes();
    }

    /**
     * Zwraca typ roweru po identyfikatorze.
     *
     * @param bikeTypeId identyfikator typu roweru
     * @return typ roweru albo {@code null} gdy nie znaleziono
     */
    public BikeType getBikeTypeById(int bikeTypeId) {
        return bikeTypeRepository.getBikeTypeById(bikeTypeId);
    }

    /**
     * Zapisuje dane typów rowerów do pliku.
     *
     */
    public void saveBikeTypes() {
        bikeTypeRepository.saveBikeTypeRepository();
    }

    /**
     * Sprawdza poprawność danych typu roweru.
     *
     * @param dto dane do walidacji
     * @throws IllegalArgumentException jeśli nazwa lub opis są puste
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

    /**
     * Sprawdza, czy nazwa typu roweru jest już zajęta przez inny rekord.
     *
     * @param name nazwa do sprawdzenia
     * @param excludedId identyfikator rekordu, który ma być pominięty
     * @return {@code true} jeśli nazwa istnieje w innym rekordzie
     */
    private boolean isBikeTypeNameTaken(String name, Integer excludedId) {
        for (BikeType bikeType : bikeTypeRepository.getAllBikeTypes()) {
            if (excludedId != null && bikeType.getBikeTypeId() == excludedId) {
                continue;
            }
            if (bikeType.getBikeTypeName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
