package AI2.Model;

/**
 * Reprezentuje markę roweru, np. Kross, Romet lub Trek.
 * Obiekt tej klasy jest wykorzystywany przez model roweru.
 */
public class BikeBrand {

    /**
     * Unikalny identyfikator marki roweru.
     */
    private int brandId;

    /**
     * Nazwa marki roweru.
     */
    private String name;

    /**
     * Tworzy nową markę roweru.
     *
     * @param brandId unikalny identyfikator marki
     * @param name nazwa marki roweru
     */
    public BikeBrand(int brandId, String name) {
        this.brandId = brandId;
        this.name = name;
    }

    /**
     * Zwraca identyfikator marki.
     *
     * @return identyfikator marki
     */
    public int getBrandId() {
        return brandId;
    }

    /**
     * Ustawia identyfikator marki.
     *
     * @param brandId identyfikator marki
     */
    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    /**
     * Zwraca nazwę marki.
     *
     * @return nazwa marki
     */
    public String getName() {
        return name;
    }

    /**
     * Ustawia nazwę marki.
     *
     * @param name nazwa marki
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Zwraca nazwę marki.
     * Dzięki temu obiekt jest czytelny np. w JComboBox.
     *
     * @return nazwa marki
     */
    @Override
    public String toString() {
        return name;
    }
}