package AI2.Util;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {

    private static Locale locale = new Locale("pl");

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);

    private static void  setLocale(Locale newLocale) {
        locale = newLocale;

        resourceBundle = ResourceBundle.getBundle("messages", locale);
    }

    public static String getString(String key) {
        return resourceBundle.getString(key);
    }
}
