package AI2.Repository;

import AI2.Model.BikeType;

import AI2.Util.AppConfig;

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
    public List<BikeType> getAllBikeTypes(){
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
     * Zwraca ostatnio użyty identyfikator typu roweru.
     *
     * @return ostatnio użyty identyfikator
     * */
    public int getCurrentBikeTypeId(){
        return currentBikeTypeId;
    }

    /**
     * Znacznik wersji 2 pliku (pierwszy int w pliku)
     * Stare pliki zaczynały się od currentBikeTypeId (wartość > 0),
     * Integer.MIN_VALUE jest bezpiecznym znacznikiem wersji.
     */
    private static final int VERSION_MARKER = Integer.MIN_VALUE;
    private static final int FILE_VERSION = 2; // wersja obsługująca nameEn

    /**
     * Zapisuje dane typów roweru do pliku (format z nameEn)
     */
    public void saveBikeTypeRepository(){

        List<BikeType> snapshot = new ArrayList<>(bikeTypeList);
        int idSnapshot = currentBikeTypeId;

        AppConfig.SAVE_EXECUTOR.submit(() -> {

            new File(AppConfig.DATA_DIR).mkdirs();

            try (DataOutputStream out = new DataOutputStream(new FileOutputStream(FILE_NAME))) {
                out.writeInt(VERSION_MARKER);   // znacznik wersji
                out.writeInt(FILE_VERSION);      // numer wersji formatu
                out.writeInt(idSnapshot);
                out.writeInt(snapshot.size());

                for (BikeType bt : snapshot) {
                    out.writeInt(bt.getBikeTypeId());
                    out.writeUTF(bt.getBikeTypeName() == null ? "" : bt.getBikeTypeName());
                    out.writeUTF(bt.getBikeTypeDescription() == null ? "" : bt.getBikeTypeDescription());
                    out.writeUTF(bt.getBikeTypeNameEn());
                }
            } catch (IOException e) {
                System.out.println("Blad zapisu danych typow rowerow: " + e.getMessage());
            }
        });
    }

    /**
     * Wczytuje dane typów roweru z pliku.
     * Obsługuje stary format (bez nameEn) i nowy (z nameEn).
     */
    public void loadBikeTypeRepository(){
        try (DataInputStream in = new DataInputStream(new FileInputStream(FILE_NAME))) {
            bikeTypeList.clear();

            int first = in.readInt();
            boolean hasNameEn;

            if (first == VERSION_MARKER) {

                int version = in.readInt();
                currentBikeTypeId = in.readInt();
                hasNameEn = (version >= 2);

            } else {
                currentBikeTypeId = first;
                hasNameEn = false;
            }

            int count = in.readInt();

            for (int i = 0; i < count; i++) {
                int id = in.readInt();
                String name = in.readUTF();
                String desc = in.readUTF();
                String en = hasNameEn ? in.readUTF() : "";

                bikeTypeList.add(new BikeType(id, name, en, desc));
            }
        } catch (IOException ignored) {}
    }
}