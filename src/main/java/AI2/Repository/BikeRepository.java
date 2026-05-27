package AI2.Repository;

import AI2.Model.Bike.Bike;
import AI2.Model.Bike.BikeBrand;
import AI2.Model.Bike.BikeModel;
import AI2.Model.BikeStatus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repozytorium przechowujące dane modułu rowerów:
 * rowery, marki oraz modele rowerów.
 * Identifikatory są nadawane przez liczniki ostatnio użytych ID i są zapisywane do pliku,
 * dzięki czemu po restarcie aplikacji nie wracają do poprzednich wartości.
 */
public class BikeRepository {

    /**
     * Nazwa pliku, w którym zapisywany jest stan repozytorium.
     */
    private static final String FILE_NAME = "bike_repository.dat";

    /**
     * Lista wszystkich rowerów zapisanych w repozytorium.
     */
    private List<Bike> bikeList;

    /**
     * Lista wszystkich marek rowerów zapisanych w repozytorium.
     */
    private List<BikeBrand> bikeBrandList;

    /**
     * Lista wszystkich modeli rowerów zapisanych w repozytorium.
     */
    private List<BikeModel> bikeModelList;

    /**
     * Ostatnio użyty identyfikator roweru.
     */
    private int currentBikeId;

    /**
     * Ostatnio użyty identyfikator marki roweru.
     */
    private int currentBrandId;

    /**
     * Ostatnio użyty identyfikator modelu roweru.
     */
    private int currentModelId;

    /**
     * Tworzy repozytorium i próbuje odczytać jego stan z pliku.
     */
    public BikeRepository() {
        bikeList = new ArrayList<>();
        bikeBrandList = new ArrayList<>();
        bikeModelList = new ArrayList<>();

        currentBikeId = 0;
        currentBrandId = 0;
        currentModelId = 0;

        loadBikeRepository();
    }

    /**
     * Zwraca następny identyfikator roweru.
     *
     * @return nowy identyfikator roweru
     */
    private int getNextBikeId() {
        currentBikeId++;
        return currentBikeId;
    }

    /**
     * Zwraca następny identyfikator marki.
     *
     * @return nowy identyfikator marki
     */
    private int getNextBrandId() {
        currentBrandId++;
        return currentBrandId;
    }

    /**
     * Zwraca następny identyfikator modelu.
     *
     * @return nowy identyfikator modelu
     */
    private int getNextModelId() {
        currentModelId++;
        return currentModelId;
    }

    /**
     * Dodaje nowy rower do repozytorium.
     * Repozytorium nadaje mu własny identyfikator.
     *
     * @param bike rower do dodania
     */
    public void addBike(Bike bike) {
        bike.setBikeId(getNextBikeId());
        bikeList.add(bike);
        saveBikeRepository();
    }

    /**
     * Usuwa rower o podanym identyfikatorze.
     *
     * @param bikeId identyfikator roweru
     * @return true, jeśli rower został usunięty; false w przeciwnym wypadku
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
     * Aktualizuje dane istniejącego roweru.
     *
     * @param updatedBike nowa wersja roweru
     * @return true, jeśli aktualizacja się powiodła; false w przeciwnym wypadku
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
     * Zwraca wszystkie rowery z repozytorium.
     *
     * @return kopia listy wszystkich rowerów
     */
    public List<Bike> getAllBikes() {
        return new ArrayList<>(bikeList);
    }

    /**
     * Zwraca rower o podanym identyfikatorze.
     *
     * @param bikeId identyfikator roweru
     * @return rower albo null, jeśli nie istnieje
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
     * Zwraca wszystkie rowery o podanym statusie.
     *
     * @param status status roweru
     * @return lista rowerów o wskazanym statusie
     */
    public List<Bike> getBikesByStatus(BikeStatus status) {
        List<Bike> bikesByStatus = new ArrayList<>();

        for (Bike bike : bikeList) {
            if (bike.getStatus() == status) {
                bikesByStatus.add(bike);
            }
        }

        return bikesByStatus;
    }

    /**
     * Dodaje nową markę roweru do repozytorium.
     * Repozytorium nadaje jej własny identyfikator.
     *
     * @param bikeBrand marka roweru do dodania
     */
    public void addBikeBrand(BikeBrand bikeBrand) {
        bikeBrand.setBrandId(getNextBrandId());
        bikeBrandList.add(bikeBrand);
        saveBikeRepository();
    }

    /**
     * Usuwa markę roweru o podanym identyfikatorze.
     *
     * @param brandId identyfikator marki
     * @return true, jeśli marka została usunięta; false w przeciwnym wypadku
     */
    public boolean removeBikeBrand(int brandId) {
        BikeBrand bikeBrand = getBikeBrandById(brandId);

        if (bikeBrand == null) {
            return false;
        }

        bikeBrandList.remove(bikeBrand);
        saveBikeRepository();
        return true;
    }

    /**
     * Aktualizuje dane istniejącej marki roweru.
     *
     * @param updatedBikeBrand nowa wersja marki
     * @return true, jeśli aktualizacja się powiodła; false w przeciwnym wypadku
     */
    public boolean updateBikeBrand(BikeBrand updatedBikeBrand) {
        for (int i = 0; i < bikeBrandList.size(); i++) {
            if (bikeBrandList.get(i).getBrandId() == updatedBikeBrand.getBrandId()) {
                bikeBrandList.set(i, updatedBikeBrand);
                saveBikeRepository();
                return true;
            }
        }

        return false;
    }

    /**
     * Zwraca wszystkie marki rowerów.
     *
     * @return kopia listy wszystkich marek
     */
    public List<BikeBrand> getAllBikeBrands() {
        return new ArrayList<>(bikeBrandList);
    }

    /**
     * Zwraca markę roweru o podanym identyfikatorze.
     *
     * @param brandId identyfikator marki
     * @return marka albo null, jeśli nie istnieje
     */
    public BikeBrand getBikeBrandById(int brandId) {
        for (BikeBrand bikeBrand : bikeBrandList) {
            if (bikeBrand.getBrandId() == brandId) {
                return bikeBrand;
            }
        }

        return null;
    }

    /**
     * Dodaje nowy model roweru do repozytorium.
     * Repozytorium nadaje mu własny identyfikator.
     *
     * @param bikeModel model roweru do dodania
     */
    public void addBikeModel(BikeModel bikeModel) {
        bikeModel.setModelId(getNextModelId());
        bikeModelList.add(bikeModel);
        saveBikeRepository();
    }

    /**
     * Usuwa model roweru o podanym identyfikatorze.
     *
     * @param modelId identyfikator modelu
     * @return true, jeśli model został usunięty; false w przeciwnym wypadku
     */
    public boolean removeBikeModel(int modelId) {
        BikeModel bikeModel = getBikeModelById(modelId);

        if (bikeModel == null) {
            return false;
        }

        bikeModelList.remove(bikeModel);
        saveBikeRepository();
        return true;
    }

    /**
     * Aktualizuje dane istniejącego modelu roweru.
     *
     * @param updatedBikeModel nowa wersja modelu
     * @return true, jeśli aktualizacja się powiodła; false w przeciwnym wypadku
     */
    public boolean updateBikeModel(BikeModel updatedBikeModel) {
        for (int i = 0; i < bikeModelList.size(); i++) {
            if (bikeModelList.get(i).getModelId() == updatedBikeModel.getModelId()) {
                bikeModelList.set(i, updatedBikeModel);
                saveBikeRepository();
                return true;
            }
        }

        return false;
    }

    /**
     * Zwraca wszystkie modele rowerów.
     *
     * @return kopia listy wszystkich modeli
     */
    public List<BikeModel> getAllBikeModels() {
        return new ArrayList<>(bikeModelList);
    }

    /**
     * Zwraca model roweru o podanym identyfikatorze.
     *
     * @param modelId identyfikator modelu
     * @return model albo null, jeśli nie istnieje
     */
    public BikeModel getBikeModelById(int modelId) {
        for (BikeModel bikeModel : bikeModelList) {
            if (bikeModel.getModelId() == modelId) {
                return bikeModel;
            }
        }

        return null;
    }

    /**
     * Zwraca wszystkie modele przypisane do podanej marki.
     *
     * @param brand marka roweru
     * @return lista modeli dla wskazanej marki
     */
    public List<BikeModel> getBikeModelsByBrand(BikeBrand brand) {
        List<BikeModel> modelsByBrand = new ArrayList<>();

        if (brand == null) {
            return modelsByBrand;
        }

        for (BikeModel bikeModel : bikeModelList) {
            if (bikeModel.getBrand() != null
                    && bikeModel.getBrand().getBrandId() == brand.getBrandId()) {
                modelsByBrand.add(bikeModel);
            }
        }

        return modelsByBrand;
    }

    /**
     * Zwraca ostatnio użyty identyfikator roweru.
     *
     * @return ostatni identyfikator roweru
     */
    public int getCurrentBikeId() {
        return currentBikeId;
    }

    /**
     * Zwraca ostatnio użyty identyfikator marki.
     *
     * @return ostatni identyfikator marki
     */
    public int getCurrentBrandId() {
        return currentBrandId;
    }

    /**
     * Zwraca ostatnio użyty identyfikator modelu.
     *
     * @return ostatni identyfikator modelu
     */
    public int getCurrentModelId() {
        return currentModelId;
    }

    /**
     * Zapisuje stan repozytorium do pliku.
     * W pliku zapisywane są liczniki ID oraz wszystkie przechowywane listy.
     */
    public void saveBikeRepository() {
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(FILE_NAME))) {
            outputStream.writeInt(currentBikeId);
            outputStream.writeInt(currentBrandId);
            outputStream.writeInt(currentModelId);

            outputStream.writeInt(bikeBrandList.size());
            for (BikeBrand bikeBrand : bikeBrandList) {
                outputStream.writeInt(bikeBrand.getBrandId());
                writeString(outputStream, bikeBrand.getName());
            }

            outputStream.writeInt(bikeModelList.size());
            for (BikeModel bikeModel : bikeModelList) {
                outputStream.writeInt(bikeModel.getModelId());

                if (bikeModel.getBrand() != null) {
                    outputStream.writeInt(bikeModel.getBrand().getBrandId());
                } else {
                    outputStream.writeInt(-1);
                }

                outputStream.writeInt(bikeModel.getTypeId());
                writeString(outputStream, bikeModel.getName());
                writeString(outputStream, bikeModel.getDescription());
            }

            outputStream.writeInt(bikeList.size());
            for (Bike bike : bikeList) {
                outputStream.writeInt(bike.getBikeId());

                if (bike.getModel() != null) {
                    outputStream.writeInt(bike.getModel().getModelId());
                } else {
                    outputStream.writeInt(-1);
                }

                outputStream.writeInt(bike.getWheelSize());

                if (bike.getStatus() != null) {
                    writeString(outputStream, bike.getStatus().name());
                } else {
                    writeString(outputStream, null);
                }

                writeString(outputStream, bike.getDescription());
            }
        } catch (IOException e) {
            System.out.println("Błąd zapisu danych rowerów: " + e.getMessage());
        }
    }

    /**
     * Odczytuje stan repozytorium z pliku.
     * Jeśli plik nie istnieje przy pierwszym uruchomieniu aplikacji, metoda kończy się bez błędu.
     */
    public void loadBikeRepository() {
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(FILE_NAME))) {
            bikeList = new ArrayList<>();
            bikeBrandList = new ArrayList<>();
            bikeModelList = new ArrayList<>();

            currentBikeId = inputStream.readInt();
            currentBrandId = inputStream.readInt();
            currentModelId = inputStream.readInt();

            int brandCount = inputStream.readInt();
            for (int i = 0; i < brandCount; i++) {
                int brandId = inputStream.readInt();
                String name = readString(inputStream);

                BikeBrand bikeBrand = new BikeBrand(brandId, name);
                bikeBrandList.add(bikeBrand);
            }

            int modelCount = inputStream.readInt();
            for (int i = 0; i < modelCount; i++) {
                int modelId = inputStream.readInt();
                int brandId = inputStream.readInt();
                int typeId = inputStream.readInt();
                String name = readString(inputStream);
                String description = readString(inputStream);

                BikeBrand brand = getBikeBrandById(brandId);
                BikeModel bikeModel = new BikeModel(modelId, brand, typeId, name, description);
                bikeModelList.add(bikeModel);
            }

            int bikeCount = inputStream.readInt();
            for (int i = 0; i < bikeCount; i++) {
                int bikeId = inputStream.readInt();
                int modelId = inputStream.readInt();
                int wheelSize = inputStream.readInt();
                String statusName = readString(inputStream);
                String description = readString(inputStream);

                BikeModel model = getBikeModelById(modelId);
                Bike bike = new Bike(
                        bikeId,
                        model,
                        wheelSize,
                        statusName != null ? BikeStatus.valueOf(statusName) : null,
                        description
                );
                bikeList.add(bike);
            }
        } catch (EOFException e) {
            System.out.println("Plik danych rowerów jest uszkodzony albo niepełny.");
        } catch (IOException e) {
            // Brak pliku przy pierwszym uruchomieniu aplikacji jest normalny.
        }
    }

    /**
     * Zapisuje pojedynczy napis do strumienia.
     * Jeśli wartość jest nullem, zapisywana jest informacja o braku wartości.
     *
     * @param outputStream strumień wyjściowy
     * @param value napis do zapisania
     * @throws IOException gdy wystąpi błąd zapisu
     */
    private void writeString(DataOutputStream outputStream, String value) throws IOException {
        if (value == null) {
            outputStream.writeBoolean(false);
        } else {
            outputStream.writeBoolean(true);
            outputStream.writeUTF(value);
        }
    }

    /**
     * Odczytuje pojedynczy napis ze strumienia.
     * Najpierw odczytywana jest informacja, czy wartość istnieje.
     *
     * @param inputStream strumień wejściowy
     * @return odczytany napis albo null
     * @throws IOException gdy wystąpi błąd odczytu
     */
    private String readString(DataInputStream inputStream) throws IOException {
        boolean hasValue = inputStream.readBoolean();

        if (!hasValue) {
            return null;
        }

        return inputStream.readUTF();
    }
}