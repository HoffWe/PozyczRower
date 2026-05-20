package AI2.Service;


import AI2.DTO.BikeTypeDTO;
import AI2.Model.BikeType;
import AI2.Repository.BikeTypeRepository;

/**
 @author Adrian Karpiński
 Klasa służąca do obsługi logiki biznesowej z typami rowerów
 */

public class BikeTypeService {

    BikeTypeRepository bikeTypeRepository;

    public BikeTypeService(BikeTypeRepository bikeTypeRepository) {
        this.bikeTypeRepository = bikeTypeRepository;
    }

    /** Metoda służąca do stworzenia nowego typu roweru*/
    public void addType(BikeTypeDTO newType) {

        if(newType.name() == null
                || newType.description() == null
                || newType.name().isBlank()
                || newType.description().isBlank()) {
            throw new IllegalArgumentException("Nazwa albo opis są puste");
        }
        for(BikeType bikeType : bikeTypeRepository.getAllBikesTypes()){
            if(bikeType.getName().equals(newType.name())){
                throw new IllegalArgumentException("Podana nazwa już istnieje!");
            }
        }


        BikeType bikeType = new BikeType();

        bikeType.setName(newType.name());
        bikeType.setDescription(newType.description());

        bikeTypeRepository.addBikeType(bikeType);
    }



}
