package AI2.View.ViewModel;

import AI2.Model.BikeType;

/**
 * ViewModel dla typu roweru – dane wyświetlane w tabeli (bez ID).
 */
public class BikeTypeViewModel {

    private final int id;
    private final String name;
    private final String description;

    public BikeTypeViewModel(BikeType bikeType) {
        this.id = bikeType.getBikeTypeId();
        this.name = bikeType.getBikeTypeName();
        this.description = bikeType.getBikeTypeDescription();
    }

    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getDescription() {
        return description;
    }

    /** Wartości do wierszy tabeli (bez ID). */
    public Object[] toRow() {
        return new Object[]{ name, description };
    }
}
