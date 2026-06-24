package AI2.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Zarządza językiem aplikacji i powiadamia zarejestrowanych słuchaczy
 * o każdej zmianie.
 * @author Adrian Karpiński
 */
public class LanguageManager {

    private static Locale locale = new Locale("pl");
    private static ResourceBundle resourceBundle =
            ResourceBundle.getBundle("messages", locale);

    private static final List<LanguageChangeListener> listeners = new ArrayList<>();

    public static Locale getLocale() {
        return locale;
    }

    /**
     * Ustawia nowy język i powiadamia wszystkich zarejestrowanych słuchaczy.
     */
    public static void setLocale(Locale newLocale) {
        locale = newLocale;
        resourceBundle = ResourceBundle.getBundle("messages", locale);
        notifyListeners();
    }

    /** Przełącza między PL a EN. */
    public static void toggleLocale() {
        if ("pl".equals(locale.getLanguage())) {
            setLocale(new Locale("en"));
        } else {
            setLocale(new Locale("pl"));
        }
    }

    public static boolean isPolish() {
        return "pl".equals(locale.getLanguage());
    }

    public static String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }

    public static void addListener(LanguageChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static void removeListener(LanguageChangeListener listener) {
        listeners.remove(listener);
    }

    private static void notifyListeners() {
        for (LanguageChangeListener l : new ArrayList<>(listeners)) {
            l.onLanguageChanged();
        }
    }
}
