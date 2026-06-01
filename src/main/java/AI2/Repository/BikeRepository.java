package AI2.Repository;

import AI2.Enums.BikeStatus;
import AI2.Model.Bike;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repozytorium przechowujące dane rowerów.
 */
public class BikeRepository {

    /**
     * Nazwa pliku z danymi rowerów.
     */
    private static final String FILE_NAME = "bike_repository.dat";

    /**
     * Lista rowerów.
     */
    private List<Bike> bikeList;

    /**
     * Ostatnio użyty identyfikator roweru.
     */
    private int currentBikeId;

    /**
     * Tworzy repozytorium rowerów.
     */
    public BikeRepository() {
        bikeList = new ArrayList<>();
        currentBikeId = 0;
        loadBikeRepository();
    }

    /**
     * Zwraca następny identyfikator roweru.
     *
     * @return następny identyfikator roweru
     */
    private int getNextBikeId() {
        currentBikeId++;
        return currentBikeId;
    }

    /**
     * Dodaje rower do repozytorium.
     *
     * @param bike rower do dodania
     */
    public void addBike(Bike bike) {
        bike.setBikeId(getNextBikeId());
        bikeList.add(bike);
        saveBikeRepository();
    }

    /**
     * Usuwa rower po identyfikatorze.
     *
     * @param bikeId identyfikator roweru
     * @return true, jeśli rower został usunięty
     */
    public boolean removeBike(int bikeId) {
        Bike bike = getBikeById(bikeId);

        if (bike == null) {
            return false;
        }

        bikeList.remove(bike);
        saveBikeRepository();
        return true;
    }

    /**
     * Aktualizuje rower.
     *
     * @param updatedBike rower z nowymi danymi
     * @return true, jeśli zaktualizowano rower
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
     */
    public List<Bike> getAllBikes() {
        return new ArrayList<>(bikeList);
    }

    /**
     * Zwraca rower po identyfikatorze.
     *
     * @param bikeId identyfikator roweru
     * @return rower albo null
     */
    public Bike getBikeById(int bikeId) {
        for (Bike bike : bikeList) {
            if (bike.getBikeId() == bikeId) {
                return bike;
            }
        }

        return null;
    }

    /**
     * Zwraca rowery o podanym statusie.
     *
     * @param status status roweru
     * @return lista rowerów
     */
    public List<Bike> getBikesByStatus(BikeStatus status) {
        List<Bike> bikesByStatus = new ArrayList<>();

        for (Bike bike : bikeList) {
            if (bike.getStatus() != null && bike.getStatus()==status) {
                bikesByStatus.add(bike);
            }
        }

        return bikesByStatus;
    }

    /**
     * Zwraca ostatnio użyty identyfikator roweru.
     *
     * @return ostatnio użyty identyfikator
     */
    public int getCurrentBikeId() {
        return currentBikeId;
    }

    /**
     * Zapisuje dane rowerów do pliku.
     */
    public void saveBikeRepository() {
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(FILE_NAME))) {
            outputStream.writeInt(currentBikeId);
            outputStream.writeInt(bikeList.size());

            for (Bike bike : bikeList) {
                outputStream.writeInt(bike.getBikeId());
                outputStream.writeUTF(bike.getBrand() == null ? "" : bike.getBrand());
                outputStream.writeUTF(bike.getModel() == null ? "" : bike.getModel());
                outputStream.writeUTF(bike.getType() == null ? "" : bike.getType());
                outputStream.writeInt(bike.getWheelSize());
                outputStream.writeUTF(bike.getStatus().name());
                outputStream.writeUTF(bike.getDescription() == null ? "" : bike.getDescription());
            }
        } catch (IOException e) {
            System.out.println("Błąd zapisu danych rowerów: " + e.getMessage());
        }
    }

    /**
     * Odczytuje dane rowerów z pliku.
     */
    public void loadBikeRepository() {
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(FILE_NAME))) {
            bikeList = new ArrayList<>();
            currentBikeId = inputStream.readInt();

            int bikeCount = inputStream.readInt();

            for (int i = 0; i < bikeCount; i++) {
                int bikeId = inputStream.readInt();
                String brand = inputStream.readUTF();
                String model = inputStream.readUTF();
                String type = inputStream.readUTF();
                int wheelSize = inputStream.readInt();
                BikeStatus status = BikeStatus.valueOf(inputStream.readUTF());
                String description = inputStream.readUTF();

                Bike bike = new Bike(
                        bikeId,
                        brand,
                        model,
                        type,
                        wheelSize,
                        status,
                        description
                );

                bikeList.add(bike);
            }
        } catch (IOException e) {
            // Brak pliku przy pierwszym uruchomieniu aplikacji jest normalny.
        }
    }
}