package AI2.Repository;

import AI2.Model.BikeType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Główna klasa pełniąca rolę bazy danych w pamięci aplikacji.
 *
 * @author Adrian Karpiński
 */
public class BikeTypeRepository {

    /**
     * Nazwa pliku z danymi rowerów.
     */
    private static final String FILE_NAME = "data/bike_type_repository.dat";

    /**
     * Deklaracja prywatnej listy, która służy nam jako tymczasowa baza danych dla typów rowerów.
     */
    private List<BikeType> bikeTypeList;

    /**
     * Ostatnio użyty identyfikator roweru.
     */
    private int currentBikeTypeId;
    /**
     * Konstruktor bezargumentowy
     * Tworzy repozytorium typów rowerów
     */
    public BikeTypeRepository(){
        bikeTypeList = new ArrayList<>();
        currentBikeTypeId = 0;
        loadBikeTypeRepository();
    }

    /**
     * Zwraca następny identyfikator typu roweru.
     *
     * @return następny identyfikator typu roweru
     */
    private int getNextBikeId(){
        currentBikeTypeId++;
        return currentBikeTypeId;
    }

    /**
     * Dodaje nowy rower do naszej "bazy".
     * @param bikeType typ roweru do dodania
     */
    public void addBikeType(BikeType bikeType){
        bikeType.setBikeTypeId(getNextBikeId());
        bikeTypeList.add(bikeType);
        saveBikeTypeRepository();
    }

    /**
     * Usuwa dany obiekt z listy.
     * @param bikeTypeId indentyfikator roweru (nasz)
     * @return true, jesli rower został usunięty
     */
    public boolean removeBikeType(int bikeTypeId){
        BikeType bikeType = getBikeTypeById(bikeTypeId);

        if(bikeType == null){
            return false;
        }

        bikeTypeList.remove(bikeType);
        saveBikeTypeRepository();
        return true;
    }

    /**
     * Aktualizuje rower.
     *
     * @param updatedBikeType typ roweru z nowymi danymi
     * @return true, jeśli zaktualizowano rower
     */
    public boolean updateBikeType(BikeType updatedBikeType) {
        for (int i = 0; i < bikeTypeList.size(); i++) {
              if (bikeTypeList.get(i).getBikeTypeId() == updatedBikeType.getBikeTypeId()){
                  bikeTypeList.set(i, updatedBikeType);
                  saveBikeTypeRepository();
                  return true;
              }
        }

        return false;
    }

    /**
     * Zwraca całą listę wszystkich zapisanych typów rowerów.
     * @return lista typów rowerów
     */
    public List<BikeType> getAllBikesTypes(){
        return new ArrayList<>(bikeTypeList);
    }

    /**
     * Zwraca typ roweru po identyfikatorze.
     *
     * @param bikeTypeId identyfikator typu roweru
     * @return typ roweru albo null
     */
    public BikeType getBikeTypeById(int bikeTypeId){
        for(BikeType bikeType : bikeTypeList){
            if(bikeType.getBikeTypeId() == bikeTypeId){
                return bikeType;
            }
        }
        return null;
    }

    /**
     * Czy jest potrzebny status do typu roweru?
    */


    /**
     * Zwraca ostatnio użyty identyfikator typu roweru.
     *
     * @return ostatnio użyty identyfikator
     * */
    public int getCurrentBikeTypeId(){
        return currentBikeTypeId;
    }

    /**
     * Zapisuje dane typów roweru do pliku
     * */

    public void saveBikeTypeRepository(){
        try(DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(FILE_NAME))){

            outputStream.writeInt(currentBikeTypeId);
            outputStream.writeInt(bikeTypeList.size());

            for(BikeType bikeType : bikeTypeList){
                outputStream.writeInt(bikeType.getBikeTypeId());
                outputStream.writeUTF(bikeType.getBikeTypeName() == null ? "" : bikeType.getBikeTypeName());
                outputStream.writeUTF(bikeType.getBikeTypeDescription() == null ? "" : bikeType.getBikeTypeDescription());
            }

        }catch (IOException e){
            System.out.println("Blad zapisu danych typow rowerow" + e.getMessage());
        }
    }

    /**
     * Odczytuje dane typów roweru do pliku
     * */

    public void loadBikeTypeRepository(){
        try(DataInputStream inputStream = new DataInputStream(new FileInputStream(FILE_NAME))){

            bikeTypeList.clear();

            currentBikeTypeId = inputStream.readInt();
            int bikeTypeCount = inputStream.readInt();

            for(int i = 0; i < bikeTypeCount; i++) {
                int bikeTypeId = inputStream.readInt();
                String bikeTypeName = inputStream.readUTF();
                String bikeTypeDescription = inputStream.readUTF();

                BikeType bikeType = new BikeType(
                        bikeTypeId,
                        bikeTypeName,
                        bikeTypeDescription
                );

                bikeTypeList.add(bikeType);

            }

        }catch(IOException e){
            // Brak pliku przy pierwszym uruchomieniu apki jest normalny
        }
    }


}