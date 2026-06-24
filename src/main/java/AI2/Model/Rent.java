package AI2.Model;

import AI2.Enums.RentStatus;

import java.time.LocalDateTime;

/**
 * Model wypożyczenia roweru.
 * Przechowuje powiązanie rower–klient, okno czasowe, status oraz uwagi.
 *
 * @author Tomasz Piłat
 */
public class Rent {

    /** Identyfikator wypożyczenia. */
    private int id;

    /** Identyfikator roweru. */
    private int bikeId;

    /** Identyfikator klienta. */
    private int clientId;

    /** Data i czas rozpoczęcia wypożyczenia. */
    private LocalDateTime rentDate;

    /** Planowana data i czas zwrotu. */
    private LocalDateTime returnTime;

    /** Status wypożyczenia. */
    private RentStatus status;

    /**
     * Uwagi do wypożyczenia lub zwrotu.
     * Pole opcjonalne – uzupełniane przy tworzeniu lub zakończeniu wypożyczenia.
     */
    private String notes;

    /** Konstruktor bezargumentowy. */
    public Rent() {}

    /**
     * Konstruktor pełny (używany przy wczytywaniu z pliku).
     *
     * @param id identyfikator
     * @param bikeId identyfikator roweru
     * @param clientId identyfikator klienta
     * @param rentDate data rozpoczęcia
     * @param returnTime data zwrotu
     * @param status status wypożyczenia
     */
    public Rent(int id, int bikeId, int clientId, LocalDateTime rentDate,
                LocalDateTime returnTime, RentStatus status) {
        this.id = id;
        this.bikeId = bikeId;
        this.clientId = clientId;
        this.rentDate = rentDate;
        this.returnTime = returnTime;
        this.status = status;
        this.notes = "";
    }

    /**
     * Konstruktor skrócony – nowe wypożyczenie bez statusu i uwag.
     *
     * @param bikeId     identyfikator roweru
     * @param clientId   identyfikator klienta
     * @param startDate  data rozpoczęcia
     * @param returnTime data zwrotu
     */
    public Rent(int bikeId, int clientId, LocalDateTime startDate, LocalDateTime returnTime) {
        this.bikeId     = bikeId;
        this.clientId   = clientId;
        this.rentDate   = startDate;
        this.returnTime = returnTime;
        this.notes      = "";
    }

    /** @return identyfikator wypożyczenia */
    public int getId() {
        return id;
    }

    /** @param id identyfikator wypożyczenia */
    public void setId(int id) {
        this.id = id;
    }

    /** @return identyfikator roweru */
    public int getBikeId() {
        return bikeId;
    }

    /** @param bikeId identyfikator roweru */
    public void setBikeId(int bikeId) {
        this.bikeId = bikeId;
    }

    /** @return identyfikator klienta */
    public int getClientId() {
        return clientId;
    }

    /** @param clientId identyfikator klienta */
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    /** @return data i czas rozpoczęcia */
    public LocalDateTime getRentDate() {
        return rentDate;
    }

    /** @param rentDate data i czas rozpoczęcia */
    public void setRentDate(LocalDateTime rentDate) {
        this.rentDate = rentDate;
    }

    /** @return planowana data i czas zwrotu */
    public LocalDateTime getReturnTime() {
        return returnTime;
    }

    /** @param returnTime planowana data i czas zwrotu */
    public void setReturnTime(LocalDateTime returnTime) {
        this.returnTime = returnTime;
    }

    /** @return status wypożyczenia */
    public RentStatus getStatus() {
        return status;
    }

    /** @param status status wypożyczenia */
    public void setStatus(RentStatus status) {
        this.status = status;
    }

    /**
     * Zwraca uwagi do wypożyczenia.
     *
     * @return uwagi lub pusty ciąg (nigdy {@code null})
     */
    public String getNotes() {
        return notes == null ? "" : notes;
    }

    /**
     * Ustawia uwagi do wypożyczenia.
     *
     * @param notes uwagi (może być {@code null} – traktowane jako pusty ciąg)
     */
    public void setNotes(String notes) {
        this.notes = notes == null ? "" : notes;
    }
}
