package AI2.Model;

/**
 * Reprezentuje model roweru łączący markę i nazwę modelu.
 *
 * @author Rafał Wojciechowski
 */
public class BikeModel {

    /** Unikalny identyfikator modelu roweru. */
    private int id;

    /** Marka roweru (np. Trek, Giant). */
    private String brand;

    /** Nazwa modelu roweru (np. X5, Escape). */
    private String model;

    /**
     * Tworzy nowy model roweru.
     *
     * @param id    unikalny identyfikator
     * @param brand marka roweru
     * @param model nazwa modelu roweru
     * @author Rafał Wojciechowski
     */
    public BikeModel(int id, String brand, String model) {
        this.id    = id;
        this.brand = brand;
        this.model = model;
    }

    /**
     * Zwraca identyfikator modelu roweru.
     *
     * @return identyfikator modelu
     * @author Rafał Wojciechowski
     */
    public int getId() { return id; }

    /**
     * Ustawia identyfikator modelu roweru.
     *
     * @param id identyfikator modelu
     * @author Rafał Wojciechowski
     */
    public void setId(int id) { this.id = id; }

    /**
     * Zwraca markę roweru.
     *
     * @return marka roweru
     * @author Rafał Wojciechowski
     */
    public String getBrand() { return brand; }

    /**
     * Ustawia markę roweru.
     *
     * @param brand marka roweru
     * @author Rafał Wojciechowski
     */
    public void setBrand(String brand) { this.brand = brand; }

    /**
     * Zwraca nazwę modelu roweru.
     *
     * @return nazwa modelu
     * @author Rafał Wojciechowski
     */
    public String getModel() { return model; }

    /**
     * Ustawia nazwę modelu roweru.
     *
     * @param model nazwa modelu
     * @author Rafał Wojciechowski
     */
    public void setModel(String model) { this.model = model; }

    /**
     * Zwraca tekstową reprezentację modelu roweru (Marka Model).
     *
     * @return reprezentacja tekstowa
     * @author Rafał Wojciechowski
     */
    @Override
    public String toString() {
        return brand + " " + model;
    }
}
