package AI2.Util;

/**
 * Interfejs nasłuchujący zmian języka w aplikacji.
 * Implementowany przez każdy panel/komponent, który wyświetla tekst
 * i musi odświeżyć etykiety po zmianie języka.
 *
 * @author Adrian Karpiński
 */
public interface LanguageChangeListener {

    /** Wywoływane po każdej zmianie języka. */
    void onLanguageChanged();
}
