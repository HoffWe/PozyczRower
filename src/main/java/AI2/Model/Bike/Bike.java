package AI2.Model;

/**
 * Reprezentuje pojedynczy fizyczny egzemplarz roweru w wypożyczalni.
 * Klasa przechowuje dane konkretnego roweru, a nie dane wspólne dla modelu.
 */
public class Bike {

    /**
     * Unikalny identyfikator roweru.
     */
    private int bikeId;

    /**
     * Model roweru przypisany do tego egzemplarza.
     */
    private BikeModel model;

    /**
     * Rozmiar koła roweru podany w calach.
     */
    private int wheelSize;

    /**
     * Aktualny status roweru.
     */
    private BikeStatus status;

    /**
     * Opis konkretnego egzemplarza roweru, np. informacje o stanie technicznym,
     * dodatkowym wyposażeniu lub potrzebie serwisu.
     */
    private String description;

    /**
     * Tworzy nowy obiekt roweru.
     *
     * @param bikeId unikalny identyfikator roweru
     * @param model model roweru
     * @param wheelSize rozmiar koła w calach
     * @param status aktualny status roweru
     * @param description opis konkretnego egzemplarza roweru
     */
    public Bike(int bikeId, BikeModel model, int wheelSize, BikeStatus status, String description) {
        this.bikeId = bikeId;
        this.model = model;
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
     * Zwraca model roweru.
     *
     * @return model roweru
     */
    public BikeModel getModel() {
        return model;
    }

    /**
     * Ustawia model roweru.
     *
     * @param model model roweru
     */
    public void setModel(BikeModel model) {
        this.model = model;
    }

    /**
     * Zwraca rozmiar koła roweru.
     *
     * @return rozmiar koła w calach
     */
    public int getWheelSize() {
        return wheelSize;
    }

    /**
     * Ustawia rozmiar koła roweru.
     *
     * @param wheelSize rozmiar koła w calach
     */
    public void setWheelSize(int wheelSize) {
        this.wheelSize = wheelSize;
    }

    /**
     * Zwraca aktualny status roweru.
     *
     * @return status roweru
     */
    public BikeStatus getStatus() {
        return status;
    }

    /**
     * Ustawia aktualny status roweru.
     *
     * @param status status roweru
     */
    public void setStatus(BikeStatus status) {
        this.status = status;
    }

    /**
     * Zwraca opis konkretnego egzemplarza roweru.
     *
     * @return opis roweru
     */
    public String getDescription() {
        return description;
    }

    /**
     * Ustawia opis konkretnego egzemplarza roweru.
     *
     * @param description opis roweru
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Zwraca tekstową reprezentację roweru.
     *
     * @return dane roweru jako tekst
     */
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