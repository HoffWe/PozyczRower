package AI2.Model;

public class Bike {

    private int bikeId;

    private BikeModel model;

    private int wheelSize;

    private BikeStatus status;

    private String description;

    public Bike(int bikeId, BikeModel model, int wheelSize,
                BikeStatus status, String description) {
        this.bikeId = bikeId;
        this.model = model;
        this.wheelSize = wheelSize;
        this.status = status;
        this.description = description;
    }

    public int getBikeId() {
        return bikeId;
    }

    public void setBikeId(int bikeId) {
        this.bikeId = bikeId;
    }

    public BikeModel getModel() {
        return model;
    }

    public void setModel(BikeModel model) {
        this.model = model;
    }

    public int getWheelSize() {
        return wheelSize;
    }

    public void setWheelSize(int wheelSize) {
        this.wheelSize = wheelSize;
    }

    public BikeStatus getStatus() {
        return status;
    }

    public void setStatus(BikeStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Bike{" +
                "bikeId=" + bikeId +
                ", model=" + model +
                ", wheelSize=" + wheelSize +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }
}