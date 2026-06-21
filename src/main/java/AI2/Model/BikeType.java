package AI2.Model;

/**
 @author Adrian Karpiński
 */

public class BikeType {

    private int id;
    private String name;
    private String description;


    public BikeType(int id, String name, String description) {
     this.id = id;
     this.name = name;
     this.description = description;

    }

    public int getBikeTypeId() {
        return id;
    }
    public void setBikeTypeId(int id) {
        this.id = id;
    }

    public String getBikeTypeName() {
        return name;
    }
    public void setBikeTypeName(String name) {
        this.name = name;
    }

    public String getBikeTypeDescription() {
        return description;
    }
    public void setBikeTypeDescription(String description) {
        this.description = description;
    }

    /**
     * Zwraca nazwę typu roweru (używana m.in. przez JComboBox).
     *
     * @return nazwa typu
     * @author Adrian Karpiński
     */
    @Override
    public String toString() {
        return name;
    }
}
