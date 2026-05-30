package AI2.Service;


import AI2.DTO.BikeTypeDTO;
import AI2.Model.BikeType;
import AI2.Repository.BikeTypeRepository;

import java.util.List;

/**
 @author Adrian Karpiński
 Klasa służąca do obsługi logiki biznesowej z typami rowerów
 */


public class BikeTypeService {

    /**
     * Repozytorium typów rowerów
     */
    private final BikeTypeRepository bikeTypeRepository;

    /**
     * Tworzy serwis typów rowerów
     * @param bikeTypeRepository repozytorium typów rowerów
     */
    public BikeTypeService(BikeTypeRepository bikeTypeRepository) {
        this.bikeTypeRepository = bikeTypeRepository;
    }

    /** Metoda służąca do stworzenia nowego typu roweru
     * @param newType nowy typ i ewentualnie opis
     * @return dodany typ roweru
     * */
    public BikeType addBikeType(BikeTypeDTO newType) {

        validateBikeTypeData(newType);

        String safeName = newType.name().trim();
        String safeDescription= newType.description().trim();

        for(BikeType bikeType : bikeTypeRepository.getAllBikesTypes()){
            if(bikeType.getBikeTypeName().equals(safeName)){
                throw new IllegalArgumentException("Podana nazwa już istnieje!");
            }
        }

        BikeType bikeType = new BikeType(
                0,
                safeName,
                safeDescription
        );

        bikeTypeRepository.addBikeType(bikeType);

        return bikeType;
    }

    /**
     * Usuwa rower.
     *
     * @param bikeTypeId identyfikator typu roweru
     * @return true, jeśli typ roweru został usunięty
     */
    public boolean removeBikeType(int bikeTypeId) {
        return bikeTypeRepository.removeBikeType(bikeTypeId);
    }

    /** Metoda służąca do aktualizowania istniejącego typu roweru
     * @param bikeType typ roweru z nowymi danymi
     * @param bikeTypeDTO nazwa i opis z frontu
     * @return true, jeśli zaktualizowano typ roweru
     * */
    public boolean updateBikeType(BikeType bikeType, BikeTypeDTO bikeTypeDTO) {

        if (bikeType==null){
            throw new IllegalArgumentException("Typ roweru nie może być pusty");
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
     */
    public List<BikeType> getAllBikeTypes() {
        return bikeTypeRepository.getAllBikesTypes();
    }

    /**
     * Zwraca typ roweru po identyfikatorze.
     *
     * @param bikeTypeId identyfikator typu roweru
     * @return typ roweru albo null
     */
    public BikeType getBikeTypeById(int bikeTypeId) {
        return bikeTypeRepository.getBikeTypeById(bikeTypeId);
    }


    private void validateBikeTypeData(BikeTypeDTO newType) {
        if(newType.name() == null || newType.name().isBlank()){
            throw new IllegalArgumentException("Nazwa jest pusta");
        }
        if(newType.description() == null || newType.description().isBlank()){
            throw new IllegalArgumentException("Opis jest pusty");
        }
    }


}
