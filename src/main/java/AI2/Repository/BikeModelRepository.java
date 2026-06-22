package AI2.Repository;

import AI2.Model.BikeModel;

import AI2.Util.AppConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repozytorium modeli rowerów – pełni rolę bazy danych w pamięci aplikacji.
 * Dane są utrwalane w pliku binarnym.
 *
 * @author Rafał Wojciechowski
 */
public class BikeModelRepository {

    /** Ścieżka do pliku z danymi modeli rowerów. */
    private static final String FILE_NAME = "data/bike_model_repository.dat";

    /** Lista modeli rowerów przechowywywa w pamięci. */
    private List<BikeModel> bikeModelList;

    /** Ostatnio użyty identyfikator modelu roweru. */
    private int currentBikeModelId;

    /**
     * Tworzy repozytorium i wczytuje dane z pliku.
     *
     * @author Rafał Wojciechowski
     */
    public BikeModelRepository() {
        bikeModelList      = new ArrayList<>();
        currentBikeModelId = 0;
        loadBikeModelRepository();
    }

    /**
     * Zwraca i inkrementuje następny identyfikator modelu roweru.
     *
     * @return następny identyfikator
     * @author Rafał Wojciechowski
     */
    private int getNextId() {
        currentBikeModelId++;
        return currentBikeModelId;
    }

    /**
     * Dodaje model roweru do repozytorium i zapisuje do pliku.
     *
     * @param bikeModel model roweru do dodania
     * @author Rafał Wojciechowski
     */
    public void addBikeModel(BikeModel bikeModel) {
        bikeModel.setId(getNextId());
        bikeModelList.add(bikeModel);
        saveBikeModelRepository();
    }

    /**
     * Usuwa model roweru z repozytorium po identyfikatorze.
     *
     * @param bikeModelId identyfikator modelu roweru
     * @return {@code true} jeśli model został usunięty, {@code false} gdy nie znaleziono
     * @author Rafał Wojciechowski
     */
    public boolean removeBikeModel(int bikeModelId) {
        BikeModel found = getBikeModelById(bikeModelId);
        if (found == null) return false;
        bikeModelList.remove(found);
        saveBikeModelRepository();
        return true;
    }

    /**
     * Aktualizuje model roweru w repozytorium.
     *
     * @param updated model roweru z nowymi danymi
     * @return {@code true} jeśli aktualizacja się powiodła
     * @author Rafał Wojciechowski
     */
    public boolean updateBikeModel(BikeModel updated) {
        for (int i = 0; i < bikeModelList.size(); i++) {
            if (bikeModelList.get(i).getId() == updated.getId()) {
                bikeModelList.set(i, updated);
                saveBikeModelRepository();
                return true;
            }
        }
        return false;
    }

    /**
     * Zwraca wszystkie modele rowerów.
     *
     * @return lista modeli rowerów
     * @author Rafał Wojciechowski
     */
    public List<BikeModel> getAllBikeModels() {
        return new ArrayList<>(bikeModelList);
    }

    /**
     * Zwraca model roweru po identyfikatorze.
     *
     * @param bikeModelId identyfikator modelu roweru
     * @return model roweru albo {@code null} gdy nie znaleziono
     * @author Rafał Wojciechowski
     */
    public BikeModel getBikeModelById(int bikeModelId) {
        for (BikeModel bm : bikeModelList) {
            if (bm.getId() == bikeModelId) return bm;
        }
        return null;
    }

    /**
     * Zwraca ostatnio użyty identyfikator modelu roweru.
     *
     * @return ostatni użyty identyfikator
     * @author Rafał Wojciechowski
     */
    public int getCurrentBikeModelId() {
        return currentBikeModelId;
    }

    /**
     * Zapisuje dane modeli rowerów do pliku binarnego.
     *
     * @author Rafał Wojciechowski
     */
    public void saveBikeModelRepository() {
        List<BikeModel> snapshot = new ArrayList<>(bikeModelList);
        int idSnapshot = currentBikeModelId;
        AppConfig.SAVE_EXECUTOR.submit(() -> {
            new File(AppConfig.DATA_DIR).mkdirs();
            try (DataOutputStream out = new DataOutputStream(new FileOutputStream(FILE_NAME))) {
                out.writeInt(idSnapshot);
                out.writeInt(snapshot.size());
                for (BikeModel bm : snapshot) {
                    out.writeInt(bm.getId());
                    out.writeUTF(bm.getBrand() == null ? "" : bm.getBrand());
                    out.writeUTF(bm.getModel() == null ? "" : bm.getModel());
                }
            } catch (IOException e) {
                System.err.println("Błąd zapisu danych modeli rowerów: " + e.getMessage());
            }
        });
    }

    /**
     * Wczytuje dane modeli rowerów z pliku binarnego.
     * Brak pliku przy pierwszym uruchomieniu jest normalny.
     *
     * @author Rafał Wojciechowski
     */
    public void loadBikeModelRepository() {
        try (DataInputStream in = new DataInputStream(new FileInputStream(FILE_NAME))) {
            bikeModelList      = new ArrayList<>();
            currentBikeModelId = in.readInt();
            int count = in.readInt();
            for (int i = 0; i < count; i++) {
                int id     = in.readInt();
                String brand = in.readUTF();
                String model = in.readUTF();
                bikeModelList.add(new BikeModel(id, brand, model));
            }
        } catch (IOException ignored) {}
    }
}
