package AI2.View.ViewModel;

import AI2.Model.Bike;
import AI2.Model.BikeModel;
import AI2.Model.BikeType;

/**
 * ViewModel dla roweru – dane wyświetlane w tabeli (bez ID).
 * Marka, model i typ są rozwiązywane przez przekazane obiekty.
 *
 * @author Rafał Wojciechowski
 */
public class BikeViewModel {

    /** Identyfikator roweru. */
    private final int id;

    /** Wyświetlana marka roweru. */
    private final String brand;

    /** Wyświetlana nazwa modelu roweru. */
    private final String model;

    /** Wyświetlana nazwa typu roweru. */
    private final String type;

    /** Rozmiar koła roweru. */
    private final int wheelSize;

    /** Zlokalizowana nazwa statusu roweru. */
    private final String statusDisplay;

    /** Opis roweru. */
    private final String description;

    /**
     * Tworzy ViewModel roweru z rozwiązanymi obiektami modelu i typu.
     *
     * @param bike      rower
     * @param bikeModel model roweru (może być {@code null})
     * @param bikeType  typ roweru (może być {@code null})
     * @author Rafał Wojciechowski
     */
    public BikeViewModel(Bike bike, BikeModel bikeModel, BikeType bikeType) {
        this.id = bike.getBikeId();
        this.brand = bikeModel != null ? bikeModel.getBrand() : "?";
        this.model = bikeModel != null ? bikeModel.getModel() : "?";
        this.type = bikeType  != null ? bikeType.getDisplayName() : "?";
        this.wheelSize = bike.getWheelSize();
        this.statusDisplay = bike.getStatus() != null ? bike.getStatus().getDisplayName() : "";
        this.description = bike.getDescription() != null ? bike.getDescription() : "";
    }

    /**
     * Zwraca identyfikator roweru.
     *
     * @return identyfikator
     * @author Rafał Wojciechowski
     */
    public int getId() {
        return id;
    }

    /**
     * Zwraca markę roweru.
     *
     * @return marka
     * @author Rafał Wojciechowski
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Zwraca nazwę modelu roweru.
     *
     * @return nazwa modelu
     * @author Rafał Wojciechowski
     */
    public String getModel() {
        return model;
    }

    /**
     * Zwraca wartości wiersza tabeli.
     *
     * @return tablica danych do tabeli: nr rej., marka, model, typ, rozmiar koła, status, opis
     * @author Rafał Wojciechowski
     */
    public Object[] toRow() {
        return new Object[]{ id, brand, model, type, wheelSize, statusDisplay, description };
    }
}
