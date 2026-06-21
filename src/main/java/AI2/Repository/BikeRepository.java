package AI2.Repository;

import AI2.Enums.BikeStatus;
import AI2.Model.Bike;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repozytorium rowerów – przechowuje dane w pamięci i utrwala je w pliku binarnym.
 *
 * @author Rafał Wojciechowski
 */
public class BikeRepository {

    /** Ścieżka do pliku z danymi rowerów. */
    private static final String FILE_NAME = "data/bike_repository.dat";

    /** Lista rowerów przechowywana w pamięci. */
    private List<Bike> bikeList;

    /** Ostatnio użyty identyfikator roweru. */
    private int currentBikeId;

    /**
     * Tworzy repozytorium i wczytuje dane z pliku.
     *
     * @author Rafał Wojciechowski
     */
    public BikeRepository() {
        bikeList      = new ArrayList<>();
        currentBikeId = 0;
        loadBikeRepository();
    }

    /**
     * Zwraca i inkrementuje następny identyfikator roweru.
     *
     * @return następny identyfikator roweru
     * @author Rafał Wojciechowski
     */
    private int getNextBikeId() {
        currentBikeId++;
        return currentBikeId;
    }

    /**
     * Dodaje rower do repozytorium i zapisuje do pliku.
     *
     * @param bike rower do dodania
     * @author Rafał Wojciechowski
     */
    public void addBike(Bike bike) {
        bike.setBikeId(getNextBikeId());
        bikeList.add(bike);
        saveBikeRepository();
    }

    /**
     * Miękkie usunięcie roweru – ustawia flagę {@code deleted} i zapisuje do pliku.
     * Rower pozostaje w pliku, ale jest ukrywany w UI.
     *
     * @param bikeId identyfikator roweru
     * @return {@code true} jeśli rower został znaleziony i oznaczony jako usunięty
     * @author Rafał Wojciechowski
     */
    public boolean removeBike(int bikeId) {
        Bike bike = getBikeById(bikeId);
        if (bike == null) return false;
        bike.setDeleted(true);
        saveBikeRepository();
        return true;
    }

    /**
     * Aktualizuje dane roweru.
     *
     * @param updatedBike rower z nowymi danymi
     * @return {@code true} jeśli zaktualizowano rower
     * @author Rafał Wojciechowski
     */
    public boolean updateBike(Bike updatedBike) {
        for (int i = 0; i < bikeList.size(); i++) {
            if (bikeList.get(i).getBikeId() == updatedBike.getBikeId()) {
                bikeList.set(i, updatedBike);
                saveBikeRepository();
                return true;
            }
        }
        return false;
    }

    /**
     * Zwraca wszystkie rowery.
     *
     * @return lista rowerów
     * @author Rafał Wojciechowski
     */
    public List<Bike> getAllBikes() {
        return new ArrayList<>(bikeList);
    }

    /**
     * Zwraca rower po identyfikatorze.
     *
     * @param bikeId identyfikator roweru
     * @return rower albo {@code null} gdy nie znaleziono
     * @author Rafał Wojciechowski
     */
    public Bike getBikeById(int bikeId) {
        for (Bike bike : bikeList) {
            if (bike.getBikeId() == bikeId) return bike;
        }
        return null;
    }

    /**
     * Zwraca rowery o podanym statusie.
     *
     * @param status status roweru
     * @return lista rowerów o podanym statusie
     * @author Rafał Wojciechowski
     */
    public List<Bike> getBikesByStatus(BikeStatus status) {
        List<Bike> result = new ArrayList<>();
        for (Bike bike : bikeList) {
            if (bike.getStatus() != null && bike.getStatus() == status) {
                result.add(bike);
            }
        }
        return result;
    }

    /**
     * Zwraca ostatnio użyty identyfikator roweru.
     *
     * @return ostatni identyfikator
     * @author Rafał Wojciechowski
     */
    public int getCurrentBikeId() { return currentBikeId; }

    /**
     * Zapisuje dane rowerów do pliku binarnego.
     * Format: currentBikeId, count, [bikeId, bikeModelId, bikeTypeId, wheelSize, status, description]*
     *
     * @author Rafał Wojciechowski
     */
    public void saveBikeRepository() {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeInt(currentBikeId);
            out.writeInt(bikeList.size());
            for (Bike bike : bikeList) {
                out.writeInt(bike.getBikeId());
                out.writeInt(bike.getBikeModelId());
                out.writeInt(bike.getBikeTypeId());
                out.writeInt(bike.getWheelSize());
                out.writeUTF(bike.getStatus() != null ? bike.getStatus().name() : "");
                out.writeUTF(bike.getDescription() == null ? "" : bike.getDescription());
                out.writeBoolean(bike.isDeleted());
            }
        } catch (IOException e) {
            System.err.println("Błąd zapisu danych rowerów: " + e.getMessage());
        }
    }

    /**
     * Wczytuje dane rowerów z pliku binarnego.
     * Brak pliku przy pierwszym uruchomieniu jest normalny.
     *
     * @author Rafał Wojciechowski
     */
    public void loadBikeRepository() {
        try (DataInputStream in = new DataInputStream(new FileInputStream(FILE_NAME))) {
            bikeList      = new ArrayList<>();
            currentBikeId = in.readInt();
            int count     = in.readInt();
            for (int i = 0; i < count; i++) {
                int bikeId      = in.readInt();
                int bikeModelId = in.readInt();
                int bikeTypeId  = in.readInt();
                int wheelSize   = in.readInt();
                String statusStr = in.readUTF();
                String description = in.readUTF();
                // Backward-compat: starszy format nie miał flagi deleted
                boolean isDeleted = false;
                try { isDeleted = in.readBoolean(); } catch (java.io.EOFException ignored) {}

                BikeStatus status = statusStr.isEmpty() ? null : BikeStatus.valueOf(statusStr);
                Bike bike = new Bike(bikeId, bikeModelId, bikeTypeId, wheelSize, status, description);
                bike.setDeleted(isDeleted);
                bikeList.add(bike);
            }
        } catch (IOException e) {
            // Brak pliku przy pierwszym uruchomieniu jest normalny.
        }
    }
}
