package AI2.Util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
                t.setDaemon(false);
                return t;
            });

    /**
     * Blokuje do 5 sekund, czekając na zakończenie bieżących zadań zapisu.
     * Wywołaj przed zamknięciem aplikacji lub wylogowaniem.
     */
    /**
     * Blokuje (maks. 5 s) aż wszystkie aktualnie zakolejkowane zapisy się zakończą.
     * <p>
     * Wysyła po jednym "sentinel" task na każdy wątek puli. Ponieważ executor jest FIFO,
     * sentinel trafi na koniec kolejki — gdy się wykona, wszystkie wcześniejsze zapisy
     * są już gotowe.
     * </p>
     */
    @SuppressWarnings("unchecked")
    public static void awaitSaveCompletion() {
        CompletableFuture<Void>[] sentinels = new CompletableFuture[THREAD_POOL_SIZE];
        for (int i = 0; i < THREAD_POOL_SIZE; i++) {
            sentinels[i] = CompletableFuture.runAsync(() -> {}, SAVE_EXECUTOR);
        }
        try {
            CompletableFuture.allOf(sentinels).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Klasa narzędziowa – brak instancji. */
    private AppConfig() {}
}
