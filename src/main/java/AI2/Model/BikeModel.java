package AI2.Model;

public class BikeModel {

    private int modelId;

    private BikeBrand brand;

    private BikeType type;

    private String name;

    private String description;

    public BikeModel(int modelId, BikeBrand brand, BikeType type, String name, String description) {
        this.modelId = modelId;
        this.brand = brand;
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public BikeBrand getBrand() {
        return brand;
    }

    public void setBrand(BikeBrand brand) {
        this.brand = brand;
    }

    public BikeType getType() {
        return type;
    }

    public void setType(BikeType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        // Jeśli w GUI będzie powtarzalne (marka wybierana osobno), to zmienić na: return name;
        return brand.getName() + " " + name;
    }
}