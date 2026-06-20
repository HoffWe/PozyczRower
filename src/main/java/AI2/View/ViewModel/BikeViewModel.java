package AI2.View.ViewModel;

import AI2.Model.Bike;

/**
 * ViewModel dla roweru – dane wyświetlane w tabeli (bez ID).
 */
public class BikeViewModel {

    private final int id;
    private final String brand;
    private final String model;
    private final String type;
    private final int wheelSize;
    private final String statusDisplay;

    public BikeViewModel(Bike bike) {
        this.id            = bike.getBikeId();
        this.brand         = bike.getBrand();
        this.model         = bike.getModel();
        this.type          = bike.getType();
        this.wheelSize     = bike.getWheelSize();
        this.statusDisplay = bike.getStatus() != null ? bike.getStatus().getDisplayName() : "";
    }

    public int getId() { return id; }

    /** Wartości do wierszy tabeli (bez ID). */
    public Object[] toRow() {
        return new Object[]{ brand, model, type, wheelSize, statusDisplay };
    }
}
