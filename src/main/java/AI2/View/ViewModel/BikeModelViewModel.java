package AI2.View.ViewModel;

import AI2.Model.BikeModel;

/**
 * ViewModel dla modelu roweru – dane wyświetlane w tabeli (bez ID).
 *
 * @author Adrian Karpiński
 */
public class BikeModelViewModel {

    /** Identyfikator modelu roweru. */
    private final int id;

    /** Marka roweru. */
    private final String brand;

    /** Nazwa modelu roweru. */
    private final String model;

    /**
     * Tworzy ViewModel na podstawie modelu roweru.
     *
     * @param bikeModel model roweru
     */
    public BikeModelViewModel(BikeModel bikeModel) {
        this.id    = bikeModel.getId();
        this.brand = bikeModel.getBrand();
        this.model = bikeModel.getModel();
    }

    /**
     * Zwraca identyfikator modelu roweru.
     *
     * @return identyfikator
     */
    public int getId() {
        return id;
    }

    /**
     * Zwraca markę roweru.
     *
     * @return marka
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Zwraca nazwę modelu roweru.
     *
     * @return nazwa modelu
     */
    public String getModel() {
        return model;
    }

    /**
     * Zwraca wartości wiersza tabeli (bez ID).
     *
     * @return tablica danych do tabeli
     */
    public Object[] toRow() {
        return new Object[]{ brand, model };
    }
}
