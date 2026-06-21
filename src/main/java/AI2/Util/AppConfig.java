package AI2.Util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Klasa przechowująca konfigurowalne parametry aplikacji.
 * Umożliwia centralne zarządzanie ustawieniami bez modyfikacji kodu biznesowego.
 *
 * <p>Wszystkie pola są publiczne, statyczne i finalne – dostęp bezpośredni
 * bez tworzenia instancji klasy.</p>
 *
 * @author Tomasz Piłat
 */
public final class AppConfig {

    // ----------------------------------------------------------------
    // Parametry konfiguracyjne
    // ----------------------------------------------------------------

    /**
     * Liczba wątków w puli używanej do asynchronicznego zapisu danych.
     * Wartość 2 zapewnia przeplatanie zapisów bez przeciążania dysku.
     */
    public static final int THREAD_POOL_SIZE = 2;

    /**
     * Katalog przechowywania plików danych aplikacji.
     * Wszystkie repozytoria tworzą pliki w tym katalogu.
     */
    public static final String DATA_DIR = "data";

    // ----------------------------------------------------------------
    // Współdzielona pula wątków
    // ----------------------------------------------------------------

    /**
     * Pula wątków używana przez wszystkie repozytoria do zapisu danych w tle.
     * Singleton – jedne wątki obsługują całą aplikację.
     *
     * <p>Wątki mają ustawioną flagę {@code daemon = true}, dzięki czemu nie blokują
     * zamknięcia JVM po wyjściu z okna głównego.</p>
     */
    public static final ExecutorService SAVE_EXECUTOR =
            Executors.newFixedThreadPool(THREAD_POOL_SIZE, r -> {
                Thread t = new Thread(r, "file-saver");
                t.setDaemon(true);
                return t;
            });

    /** Klasa narzędziowa – brak instancji. */
    private AppConfig() {}
}
