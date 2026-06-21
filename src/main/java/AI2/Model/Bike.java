package AI2.Model;

import AI2.Enums.BikeStatus;

/**
 * Reprezentuje pojedynczy rower w systemie.
 * Marka i model przechowywane są przez referencję do {@link BikeModel},
 * typ roweru – przez referencję do {@link BikeType}.
 *
 * @author Rafał Wojciechowski
 */
public class Bike {

    /** Unikalny identyfikator roweru. */
    private int bikeId;

    /** Identyfikator modelu roweru ({@link BikeModel}). */
    private int bikeModelId;

    /** Identyfikator typu roweru ({@link BikeType}). */
    private int bikeTypeId;

    /** Rozmiar koła roweru (w calach). */
    private int wheelSize;

    /** Aktualny status roweru. */
    private BikeStatus status;

    /** Opis roweru. */
    private String description;

    /**
     * Flaga miękkiego usunięcia.
     * {@code true} oznacza że rower jest usunięty i nie powinien być wyświetlany.
     */
    private boolean deleted;

    /**
     * Tworzy nowy rower.
     *
     * @param bikeId      identyfikator roweru
     * @param bikeModelId identyfikator modelu roweru
     * @param bikeTypeId  identyfikator typu roweru
     * @param wheelSize   rozmiar koła
     * @param status      status roweru
     * @param description opis roweru
     * @author Rafał Wojciechowski
     */
    public Bike(int bikeId, int bikeModelId, int bikeTypeId,
                int wheelSize, BikeStatus status, String description) {
        this.bikeId      = bikeId;
        this.bikeModelId = bikeModelId;
        this.bikeTypeId  = bikeTypeId;
        this.wheelSize   = wheelSize;
        this.status      = status;
        this.description = description;
        this.deleted     = false;
    }

    /**
     * Zwraca identyfikator roweru.
     *
     * @return identyfikator roweru
     * @author Rafał Wojciechowski
     */
    public int getBikeId() { return bikeId; }

    /**
     * Ustawia identyfikator roweru.
     *
     * @param bikeId identyfikator roweru
     * @author Rafał Wojciechowski
     */
    public void setBikeId(int bikeId) { this.bikeId = bikeId; }

    /**
     * Zwraca identyfikator modelu roweru.
     *
     * @return identyfikator modelu
     * @author Rafał Wojciechowski
     */
    public int getBikeModelId() { return bikeModelId; }

    /**
     * Ustawia identyfikator modelu roweru.
     *
     * @param bikeModelId identyfikator modelu
     * @author Rafał Wojciechowski
     */
    public void setBikeModelId(int bikeModelId) { this.bikeModelId = bikeModelId; }

    /**
     * Zwraca identyfikator typu roweru.
     *
     * @return identyfikator typu
     * @author Rafał Wojciechowski
     */
    public int getBikeTypeId() { return bikeTypeId; }

    /**
     * Ustawia identyfikator typu roweru.
     *
     * @param bikeTypeId identyfikator typu
     * @author Rafał Wojciechowski
     */
    public void setBikeTypeId(int bikeTypeId) { this.bikeTypeId = bikeTypeId; }

    /**
     * Zwraca rozmiar koła roweru.
     *
     * @return rozmiar koła
     * @author Rafał Wojciechowski
     */
    public int getWheelSize() { return wheelSize; }

    /**
     * Ustawia rozmiar koła roweru.
     *
     * @param wheelSize rozmiar koła
     * @author Rafał Wojciechowski
     */
    public void setWheelSize(int wheelSize) { this.wheelSize = wheelSize; }

    /**
     * Zwraca status roweru.
     *
     * @return status roweru
     * @author Rafał Wojciechowski
     */
    public BikeStatus getStatus() { return status; }

    /**
     * Ustawia status roweru.
     *
     * @param status status roweru
     * @author Rafał Wojciechowski
     */
    public void setStatus(BikeStatus status) { this.status = status; }

    /**
     * Zwraca opis roweru.
     *
     * @return opis roweru
     * @author Rafał Wojciechowski
     */
    public String getDescription() { return description; }

    /**
     * Ustawia opis roweru.
     *
     * @param description opis roweru
     * @author Rafał Wojciechowski
     */
    public void setDescription(String description) { this.description = description; }

    /** Zwraca czy rower jest miękko usunięty. */
    public boolean isDeleted() { return deleted; }

    /** Ustawia flagę miękkiego usunięcia. */
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
