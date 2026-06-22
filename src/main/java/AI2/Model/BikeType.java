package AI2.Model;

import AI2.Util.LanguageManager;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Model typu roweru.
 *
 * @author Adrian Karpiński
 */
public class BikeType {

    private int id;
    private String name;
    private String nameEn;
    private String description;

    public BikeType(int id, String name, String nameEn, String description) {
        this.id          = id;
        this.name        = name;
        this.nameEn      = nameEn == null ? "" : nameEn;
        this.description = description;
    }

    /** Konstruktor bez nameEn – zachowanie wstecznej kompatybilności. */
    public BikeType(int id, String name, String description) {
        this(id, name, "", description);
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

    public String getNameEn() {
        return nameEn == null ? "" : nameEn;
    }
    public void setNameEn(String nameEn) {
        this.nameEn = nameEn == null ? "" : nameEn;
    }

    /**
     * Zwraca wyświetlaną nazwę typu roweru w aktualnym języku.
     * <ol>
     *   <li>Gdy język angielski i pole {@code nameEn} jest niepuste – zwraca {@code nameEn}.</li>
     *   <li>Fallback EN: szuka klucza {@code bikeType.name.<ascii_name>} w LanguageManager
     *       (obsługa typów z dat-pliku sprzed dodania {@code nameEn}).</li>
     *   <li>Ostateczny fallback: oryginalna nazwa polska.</li>
     * </ol>
     *
     * @return przetłumaczona nazwa lub oryginalna gdy brak tłumaczenia
     */
    public String getDisplayName() {
        if (!LanguageManager.isPolish()) {
            if (nameEn != null && !nameEn.isBlank()) return nameEn;
            String key = "bikeType.name." + Normalizer
                    .normalize(name, Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "")
                    .toLowerCase(Locale.ROOT)
                    .replace(" ", "_");
            String translated = LanguageManager.getString(key);
            return translated.startsWith("!") ? name : translated;
        }
        return name;
    }

    public String getBikeTypeDescription() {
        return description;
    }
    public void setBikeTypeDescription(String description) {
        this.description = description;
    }

    /**
     * Zwraca przetłumaczoną nazwę (używana m.in. przez JComboBox).
     *
     * @return przetłumaczona nazwa
     * @author Adrian Karpiński
     */
    @Override
    public String toString() {
        return getDisplayName();
    }
}
