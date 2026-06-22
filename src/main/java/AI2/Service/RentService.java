package AI2.Service;

import AI2.Enums.BikeStatus;
import AI2.Enums.RentStatus;
import AI2.Model.Bike;
import AI2.Model.Rent;
import AI2.Repository.BikeRepository;
import AI2.Repository.ClientRepository;
import AI2.Repository.RentRepository;
import AI2.Util.LanguageManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serwis obsługujący logikę biznesową wypożyczeń.
 *
 * @author Tomasz Piłat
 */
public class RentService {

    /** Repozytorium wypożyczeń. */
    private final RentRepository rentRepository;

    /** Repozytorium rowerów. */
    private final BikeRepository bikeRepository;

    /** Repozytorium klientów. */
    private final ClientRepository clientRepository;

    /**
     * Tworzy serwis wypożyczeń i ustawia statusy przeterminowane.
     *
     * @param rentRepository   repozytorium wypożyczeń
     * @param bikeRepository   repozytorium rowerów
     * @param clientRepository repozytorium klientów
     * @author Tomasz Piłat
     */
    public RentService(RentRepository rentRepository, BikeRepository bikeRepository,
                       ClientRepository clientRepository) {
        this.rentRepository   = rentRepository;
        this.bikeRepository   = bikeRepository;
        this.clientRepository = clientRepository;
        updateStatuses();
    }

    /**
     * Dodaje nowe wypożyczenie.
     * Waliduje rower, klienta, datę i brak konfliktów w okresie, następnie ustawia status SCHEDULED.
     *
     * @param rent wypożyczenie do dodania
     * @throws IllegalArgumentException jeśli dane są niepoprawne
     * @author Tomasz Piłat
     */
    public void addRent(Rent rent) {
        validateRent(rent);
        rent.setStatus(RentStatus.SCHEDULED);
        rentRepository.addRent(rent);
    }

    /**
     * Kończy wypożyczenie – ustawia status FINISHED i zwraca rower (AVAILABLE).
     * Deleguje do {@link #endRent(int, String)} z pustymi uwagami.
     *
     * @param rentId identyfikator wypożyczenia
     * @throws RuntimeException      jeśli wypożyczenie nie istnieje
     * @throws IllegalStateException jeśli wypożyczenie jest już zakończone
     * @author Tomasz Piłat
     */
    public void endRent(int rentId) {
        endRent(rentId, "");
    }

    /**
     * Kończy wypożyczenie – ustawia status FINISHED, zwraca rower (AVAILABLE)
     * i zapisuje uwagi przy zwrocie.
     *
     * @param rentId identyfikator wypożyczenia
     * @param notes  uwagi przy zwrocie (może być pusty)
     * @throws RuntimeException      jeśli wypożyczenie nie istnieje
     * @throws IllegalStateException jeśli wypożyczenie jest już zakończone
     * @author Tomasz Piłat
     */
    public void endRent(int rentId, String notes) {
        Rent rent = rentRepository.getRentByID(rentId);
        if (rent == null) {
            throw new RuntimeException(LanguageManager.getString("error.rent.notFound"));
        }
        if (rent.getStatus() == RentStatus.FINISHED
                || rent.getStatus() == RentStatus.CANCELLED) {
            throw new IllegalStateException(
                    LanguageManager.getString("error.rent.alreadyFinished"));
        }

        Bike bike = bikeRepository.getBikeById(rent.getBikeId());
        if (bike != null && bike.getStatus() != BikeStatus.AVAILABLE) {
            bike.setStatus(BikeStatus.AVAILABLE);
            bikeRepository.updateBike(bike);
        }

        if (notes != null && !notes.isBlank()) {
            rent.setNotes(notes);
        }
        rent.setStatus(RentStatus.FINISHED);
        rentRepository.updateRent(rent);
    }

    /**
     * Usuwa wypożyczenie. Można usunąć tylko wypożyczenia ze statusem SCHEDULED.
     *
     * @param rentId identyfikator wypożyczenia
     * @throws RuntimeException         jeśli wypożyczenie nie istnieje
     * @throws IllegalArgumentException jeśli wypożyczenie nie jest SCHEDULED
     * @author Tomasz Piłat
     */
    public void removeRent(int rentId) {
        Rent rent = rentRepository.getRentByID(rentId);
        if (rent == null) {
            throw new RuntimeException(LanguageManager.getString("error.rent.notFound"));
        }
        if (rent.getStatus() != RentStatus.SCHEDULED) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.rent.cannotDelete"));
        }
        rentRepository.removeRent(rentId);
    }

    /**
     * Aktualizuje dane wypożyczenia (daty, rower). Wywołuje pełną walidację.
     * Używać tylko dla statusów SCHEDULED i ACTIVE.
     *
     * @param newRent wypożyczenie z nowymi danymi
     * @throws IllegalArgumentException jeśli dane są niepoprawne
     * @author Tomasz Piłat
     */
    public void updateRent(Rent newRent) {
        validateRent(newRent);
        rentRepository.updateRent(newRent);
    }

    /**
     * Aktualizuje wyłącznie pole uwag wypożyczenia, bez żadnej walidacji.
     * Działa dla każdego statusu (SCHEDULED, ACTIVE, OVERDUE, FINISHED, CLOSED).
     *
     * @param rentId identyfikator wypożyczenia
     * @param notes  nowe uwagi
     * @throws RuntimeException jeśli wypożyczenie nie istnieje
     * @author Tomasz Piłat
     */
    public void updateNotes(int rentId, String notes) {
        Rent rent = rentRepository.getRentByID(rentId);
        if (rent == null) {
            throw new RuntimeException(LanguageManager.getString("error.rent.notFound"));
        }
        rent.setNotes(notes == null ? "" : notes);
        rentRepository.updateRent(rent);
    }

    /**
     * Zwraca wypożyczenie po identyfikatorze.
     *
     * @param rentId identyfikator wypożyczenia
     * @return wypożyczenie
     * @throws RuntimeException jeśli wypożyczenie nie istnieje
     * @author Tomasz Piłat
     */
    public Rent getRentByID(int rentId) {
        Rent rent = rentRepository.getRentByID(rentId);
        if (rent == null) {
            throw new RuntimeException(LanguageManager.getString("error.rent.notFound"));
        }
        return rent;
    }

    /**
     * Zwraca wszystkie wypożyczenia.
     *
     * @return lista wypożyczeń
     * @throws RuntimeException jeśli wystąpił błąd wczytywania danych
     * @author Tomasz Piłat
     */
    public List<Rent> getAllRents() {
        try {
            return rentRepository.getRentDataBase();
        } catch (Exception e) {
            throw new RuntimeException(
                    LanguageManager.getString("error.databaseLoad"), e);
        }
    }

    /**
     * Aktualizuje statusy wszystkich wypożyczeń na podstawie aktualnego czasu:
     * <ul>
     *   <li>SCHEDULED → ACTIVE gdy data rozpoczęcia minęła</li>
     *   <li>SCHEDULED/ACTIVE → OVERDUE gdy data zwrotu minęła</li>
     * </ul>
     * Przy przejściu SCHEDULED → ACTIVE rower jest oznaczany jako RENT.
     * Jeśli cokolwiek się zmieniło, zapisuje dane do pliku.
     *
     * @return {@code true} jeśli przynajmniej jeden status uległ zmianie
     * @author Tomasz Piłat
     */
    public boolean updateStatuses() {
        LocalDateTime now = LocalDateTime.now();
        boolean changed = false;

        for (Rent rent : getAllRents()) {
            RentStatus status = rent.getStatus();
            if (status != RentStatus.SCHEDULED
                    && status != RentStatus.ACTIVE
                    && status != RentStatus.PENDING) continue;

            if (rent.getReturnTime().isBefore(now)) {
                // SCHEDULED/PENDING → OVERDUE: trzeba teraz oznaczyć rower jako wypożyczony
                if (status == RentStatus.SCHEDULED || status == RentStatus.PENDING) {
                    markBikeRented(rent.getBikeId());
                }
                rent.setStatus(RentStatus.OVERDUE);
                changed = true;

            } else if (status == RentStatus.SCHEDULED && !rent.getRentDate().isAfter(now)) {
                // Czas startu minął – czekamy na potwierdzenie pracownika
                rent.setStatus(RentStatus.PENDING);
                changed = true;
            }
        }

        if (changed) {
            rentRepository.saveRentDataBase();
        }
        return changed;
    }

    /**
     * Potwierdza rozpoczęcie wypożyczenia – zmienia status PENDING → ACTIVE
     * i oznacza rower jako wypożyczony.
     *
     * @param rentId identyfikator wypożyczenia
     * @throws RuntimeException      jeśli wypożyczenie nie istnieje
     * @throws IllegalStateException jeśli wypożyczenie nie jest w statusie PENDING
     * @author Tomasz Piłat
     */
    public void confirmRent(int rentId) {
        Rent rent = rentRepository.getRentByID(rentId);
        if (rent == null) {
            throw new RuntimeException(LanguageManager.getString("error.rent.notFound"));
        }
        if (rent.getStatus() != RentStatus.PENDING) {
            throw new IllegalStateException(
                    LanguageManager.getString("error.rent.cannotConfirm"));
        }
        rent.setStatus(RentStatus.ACTIVE);
        markBikeRented(rent.getBikeId());
        rentRepository.updateRent(rent);
    }

    /**
     * Anuluje wypożyczenie – zmienia status SCHEDULED/PENDING → CANCELLED.
     *
     * @param rentId identyfikator wypożyczenia
     * @throws RuntimeException      jeśli wypożyczenie nie istnieje
     * @throws IllegalStateException jeśli wypożyczenie nie może być anulowane
     * @author Tomasz Piłat
     */
    public void cancelRent(int rentId) {
        Rent rent = rentRepository.getRentByID(rentId);
        if (rent == null) {
            throw new RuntimeException(LanguageManager.getString("error.rent.notFound"));
        }
        if (rent.getStatus() != RentStatus.PENDING
                && rent.getStatus() != RentStatus.SCHEDULED) {
            throw new IllegalStateException(
                    LanguageManager.getString("error.rent.cannotCancel"));
        }
        rent.setStatus(RentStatus.CANCELLED);
        rentRepository.updateRent(rent);
    }

    /**
     * Ustawia status roweru na RENT jeśli jest AVAILABLE.
     * Pomocnicza metoda wywoływana przy aktywacji wypożyczenia.
     *
     * @param bikeId identyfikator roweru
     * @author Tomasz Piłat
     */
    private void markBikeRented(int bikeId) {
        Bike bike = bikeRepository.getBikeById(bikeId);
        if (bike != null && bike.getStatus() == BikeStatus.AVAILABLE) {
            bike.setStatus(BikeStatus.RENTED);
            bikeRepository.updateBike(bike);
        }
    }

    /**
     * Zwraca wypożyczenia danego klienta.
     *
     * @param clientId identyfikator klienta
     * @return lista wypożyczeń klienta
     * @author Tomasz Piłat
     */
    public List<Rent> findClientRents(int clientId) {
        return rentRepository.getRentDataBase().stream()
                .filter(r -> r.getClientId() == clientId)
                .collect(Collectors.toList());
    }

    /**
     * Zwraca wypożyczenia powiązane z danym rowerem.
     *
     * @param bikeId identyfikator roweru
     * @return lista wypożyczeń roweru
     * @author Tomasz Piłat
     */
    public List<Rent> findBikeRents(int bikeId) {
        return rentRepository.getRentDataBase().stream()
                .filter(r -> r.getBikeId() == bikeId)
                .collect(Collectors.toList());
    }

    /**
     * Zwraca aktywne wypożyczenia.
     *
     * @return lista aktywnych wypożyczeń
     * @author Tomasz Piłat
     */
    public List<Rent> findActiveRents() {
        return rentRepository.getRentDataBase().stream()
                .filter(r -> r.getStatus() == RentStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    /**
     * Zapisuje dane wypożyczeń do pliku.
     *
     * @author Tomasz Piłat
     */
    public void saveRents() {
        rentRepository.saveRentDataBase();
    }

    /**
     * Sprawdza, czy rower jest dostępny w całym podanym przedziale czasowym.
     * Konflikty z wypożyczeniami SCHEDULED i ACTIVE są brane pod uwagę.
     *
     * @param bikeId identyfikator roweru
     * @param start  data rozpoczęcia
     * @param end    data zakończenia
     * @return {@code true} jeśli rower jest dostępny w całym przedziale
     * @author Tomasz Piłat
     */
    public boolean isBikeAvailableInPeriod(int bikeId, LocalDateTime start, LocalDateTime end) {
        return rentRepository.getRentDataBase().stream()
                .filter(r -> r.getBikeId() == bikeId)
                .filter(r -> r.getStatus() == RentStatus.SCHEDULED
                          || r.getStatus() == RentStatus.ACTIVE
                          || r.getStatus() == RentStatus.PENDING)
                .noneMatch(r -> r.getRentDate().isBefore(end)
                             && r.getReturnTime().isAfter(start));
    }

    /**
     * Sprawdza czy klient ma aktywne wypożyczenia (SCHEDULED, ACTIVE lub OVERDUE).
     *
     * @param clientId identyfikator klienta
     * @return {@code true} jeśli klient ma co najmniej jedno aktywne wypożyczenie
     * @author Tomasz Piłat
     */
    public boolean clientHasActiveRentals(int clientId) {
        return rentRepository.getRentDataBase().stream()
                .filter(r -> r.getClientId() == clientId)
                .anyMatch(r -> r.getStatus() == RentStatus.SCHEDULED
                            || r.getStatus() == RentStatus.ACTIVE
                            || r.getStatus() == RentStatus.PENDING
                            || r.getStatus() == RentStatus.OVERDUE);
    }

    /**
     * Sprawdza czy rower ma aktywne wypożyczenia (SCHEDULED, ACTIVE lub OVERDUE).
     *
     * @param bikeId identyfikator roweru
     * @return {@code true} jeśli rower ma co najmniej jedno aktywne wypożyczenie
     * @author Tomasz Piłat
     */
    public boolean bikeHasActiveRentals(int bikeId) {
        return rentRepository.getRentDataBase().stream()
                .filter(r -> r.getBikeId() == bikeId)
                .anyMatch(r -> r.getStatus() == RentStatus.SCHEDULED
                            || r.getStatus() == RentStatus.ACTIVE
                            || r.getStatus() == RentStatus.PENDING
                            || r.getStatus() == RentStatus.OVERDUE);
    }

    /**
     * Waliduje wypożyczenie – sprawdza istnienie roweru, klienta, dostępność roweru
     * oraz brak konfliktów rezerwacji w żądanym przedziale czasowym.
     *
     * @param rent wypożyczenie do walidacji
     * @throws IllegalArgumentException jeśli dane są niepoprawne
     * @author Tomasz Piłat
     */
    public void validateRent(Rent rent) {
        Bike bike = bikeRepository.getBikeById(rent.getBikeId());
        if (bike == null) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.rent.bikeNotFound"));
        }
        if (bike.getStatus() != BikeStatus.AVAILABLE) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.rent.bikeNotAvailable"));
        }
        if (clientRepository.getClientById(rent.getClientId()) == null) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.rent.clientNotFound"));
        }
        if (rent.getRentDate().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.rent.dateInPast"));
        }
        if (!isBikeAvailableInPeriod(rent.getBikeId(), rent.getRentDate(), rent.getReturnTime())) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.rent.bikeConflict"));
        }
    }
}
