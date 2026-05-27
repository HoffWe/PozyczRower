package AI2.Model;

/**
 * Reprezentuje pojedynczy rower w systemie.
 */
public class Bike {

    /**
     * Unikalny identyfikator roweru.
     */
    private int bikeId;

    /**
     * Marka roweru.
     */
    private String brand;

    /**
     * Model roweru.
     */
    private String model;

    /**
     * Typ roweru.
     */
    private String type;

    /**
     * Rozmiar koła roweru.
     */
    private int wheelSize;

    /**
     * Status roweru.
     */
    private String status;

    /**
     * Opis roweru.
     */
    private String description;

    /**
     * Tworzy nowy rower.
     *
     * @param bikeId identyfikator roweru
     * @param brand marka roweru
     * @param model model roweru
     * @param type typ roweru
     * @param wheelSize rozmiar koła
     * @param status status roweru
     * @param description opis roweru
     */
    public Bike(int bikeId, String brand, String model, String type, int wheelSize,
                String status, String description) {
        this.bikeId = bikeId;
        this.brand = brand;
        this.model = model;
        this.type = type;
        this.wheelSize = wheelSize;
        this.status = status;
        this.description = description;
    }

    /**
     * Zwraca identyfikator roweru.
     *
     * @return identyfikator roweru
     */
    public int getBikeId() {
        return bikeId;
    }

    /**
     * Ustawia identyfikator roweru.
     *
     * @param bikeId identyfikator roweru
     */
    public void setBikeId(int bikeId) {
        this.bikeId = bikeId;
    }

    /**
     * Zwraca markę roweru.
     *
     * @return marka roweru
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Ustawia markę roweru.
     *
     * @param brand marka roweru
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Zwraca model roweru.
     *
     * @return model roweru
     */
    public String getModel() {
        return model;
    }

    /**
     * Ustawia model roweru.
     *
     * @param model model roweru
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Zwraca typ roweru.
     *
     * @return typ roweru
     */
    public String getType() {
        return type;
    }

    /**
     * Ustawia typ roweru.
     *
     * @param type typ roweru
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Zwraca rozmiar koła.
     *
     * @return rozmiar koła
     */
    public int getWheelSize() {
        return wheelSize;
    }

    /**
     * Ustawia rozmiar koła.
     *
     * @param wheelSize rozmiar koła
     */
    public void setWheelSize(int wheelSize) {
        this.wheelSize = wheelSize;
    }

    /**
     * Zwraca status roweru.
     *
     * @return status roweru
     */
    public String getStatus() {
        return status;
    }

    /**
     * Ustawia status roweru.
     *
     * @param status status roweru
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Zwraca opis roweru.
     *
     * @return opis roweru
     */
    public String getDescription() {
        return description;
    }

    /**
     * Ustawia opis roweru.
     *
     * @param description opis roweru
     */
    public void setDescription(String description) {
        this.description = description;
    }
}