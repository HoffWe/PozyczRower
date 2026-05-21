package AI2.Model;

/**
 * Reprezentuje model roweru należący do konkretnej marki.
 * Model przechowuje także identyfikator typu roweru z osobnego modułu.
 */
public class BikeModel {

    /**
     * Unikalny identyfikator modelu roweru.
     */
    private int modelId;

    /**
     * Marka, do której należy dany model roweru.
     */
    private BikeBrand brand;

    /**
     * Identyfikator typu roweru zarządzanego przez osobny moduł.
     */
    private int typeId;

    /**
     * Nazwa modelu roweru.
     */
    private String name;

    /**
     * Opis modelu roweru.
     */
    private String description;

    /**
     * Tworzy nowy model roweru.
     *
     * @param modelId unikalny identyfikator modelu
     * @param brand marka roweru
     * @param typeId identyfikator typu roweru z osobnego modułu
     * @param name nazwa modelu roweru
     * @param description opis modelu roweru
     */
    public BikeModel(int modelId, BikeBrand brand, int typeId, String name, String description) {
        this.modelId = modelId;
        this.brand = brand;
        this.typeId = typeId;
        this.name = name;
        this.description = description;
    }

    /**
     * Zwraca identyfikator modelu.
     *
     * @return identyfikator modelu
     */
    public int getModelId() {
        return modelId;
    }

    /**
     * Ustawia identyfikator modelu.
     *
     * @param modelId identyfikator modelu
     */
    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    /**
     * Zwraca markę modelu.
     *
     * @return marka modelu
     */
    public BikeBrand getBrand() {
        return brand;
    }

    /**
     * Ustawia markę modelu.
     *
     * @param brand marka modelu
     */
    public void setBrand(BikeBrand brand) {
        this.brand = brand;
    }

    /**
     * Zwraca identyfikator typu roweru.
     *
     * @return identyfikator typu roweru
     */
    public int getTypeId() {
        return typeId;
    }

    /**
     * Ustawia identyfikator typu roweru.
     *
     * @param typeId identyfikator typu roweru
     */
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    /**
     * Zwraca nazwę modelu.
     *
     * @return nazwa modelu
     */
    public String getName() {
        return name;
    }

    /**
     * Ustawia nazwę modelu.
     *
     * @param name nazwa modelu
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Zwraca opis modelu.
     *
     * @return opis modelu
     */
    public String getDescription() {
        return description;
    }

    /**
     * Ustawia opis modelu.
     *
     * @param description opis modelu
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Zwraca tekstową reprezentację modelu.
     * Jeśli marka jest ustawiona, zwracana jest pełna nazwa marki i modelu.
     *
     * @return nazwa marki i modelu albo sama nazwa modelu
     */
    @Override
    public String toString() {
        if (brand != null) {
            //jeśli w GUI będzie się powtarzało brand, przez osobny wybór brand, zmienić na samo return name;
            return brand.getName() + " " + name;
        }
        return name;
    }
}