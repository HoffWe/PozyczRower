package AI2.Enums;

import AI2.Util.LanguageManager;

import java.util.Set;

/**
 * Role użytkowników systemu wypożyczalni rowerów.
 *
 * @author Tomasz Piłat
 */
public enum UserRole {

    /** Pracownik zajmujący się wypożyczeniami: dostęp do Wypożyczeń, Rowerów, Klientów. */
    RENTAL_WORKER,

    /** Serwisant: dostęp do Rowerów, Typów rowerów, Modeli rowerów. */
    TECHNICIAN,

    /** Administrator: pełny dostęp do wszystkich paneli. */
    ADMIN;

    /**
     * Zwraca zlokalizowaną nazwę roli.
     *
     * @return wyświetlana nazwa roli
     */
    public String getDisplayName() {
        return LanguageManager.getString("role." + this.name());
    }

    /**
     * Sprawdza, czy rola ma dostęp do panelu identyfikowanego podanym kluczem karty.
     *
     * @param card klucz karty (np. "RENTS", "BIKES")
     * @return {@code true} jeśli dostęp jest dozwolony
     */
    public boolean canAccess(String card) {
        return switch (this) {
            case RENTAL_WORKER -> Set.of("RENTS", "BIKES", "CLIENTS").contains(card);
            case TECHNICIAN    -> Set.of("BIKES", "BIKE_TYPES", "BIKE_MODELS").contains(card);
            case ADMIN         -> true;
        };
    }
}
