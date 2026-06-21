package AI2.View.ViewModel;

import AI2.Model.Bike;
import AI2.Model.BikeModel;
import AI2.Model.Client;
import AI2.Model.Rent;

import java.time.format.DateTimeFormatter;

/**
 * ViewModel dla wypożyczenia – dane wyświetlane w tabeli (bez wewnętrznych ID).
 * Marka i model roweru rozwiązywane przez przekazany obiekt {@link BikeModel}.
 *
 * @author Tomasz Piłat
 */
public class RentViewModel {

    /** Formatter daty i czasu. */
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Identyfikator wypożyczenia. */
    private final int rentId;

    /** Pełne imię i nazwisko klienta. */
    private final String clientFullName;

    /** Opis roweru (marka model). */
    private final String bikeInfo;

    /** Data rozpoczęcia wypożyczenia. */
    private final String startDate;

    /** Data zakończenia wypożyczenia. */
    private final String endDate;

    /** Zlokalizowany status wypożyczenia. */
    private final String status;

    /** Uwagi do wypożyczenia lub zwrotu. */
    private final String notes;

    /**
     * Tworzy ViewModel wypożyczenia z rozwiązanymi obiektami klienta i modelu roweru.
     *
     * @param rent      wypożyczenie
     * @param client    klient (może być {@code null})
     * @param bike      rower (może być {@code null})
     * @param bikeModel model roweru (może być {@code null})
     * @author Tomasz Piłat
     */
    public RentViewModel(Rent rent, Client client, Bike bike, BikeModel bikeModel) {
        this.rentId = rent.getId();
        this.clientFullName = client != null
                ? client.getName() + " " + client.getSurname()
                : "ID:" + rent.getClientId();
        this.bikeInfo = bikeModel != null
                ? bikeModel.getBrand() + " " + bikeModel.getModel()
                : (bike != null ? "ID:" + bike.getBikeId() : "ID:" + rent.getBikeId());
        this.startDate = rent.getRentDate() != null
                ? rent.getRentDate().format(FMT) : "";
        this.endDate = rent.getReturnTime() != null
                ? rent.getReturnTime().format(FMT) : "";
        this.status = rent.getStatus() != null
                ? rent.getStatus().getDisplayName() : "";
        this.notes = rent.getNotes();
    }

    /**
     * Zwraca identyfikator wypożyczenia.
     *
     * @return identyfikator wypożyczenia
     * @author Tomasz Piłat
     */
    public int getRentId() {
        return rentId;
    }

    /**
     * Zwraca pełne imię i nazwisko klienta.
     *
     * @return imię i nazwisko klienta
     * @author Tomasz Piłat
     */
    public String getClientFullName() {
        return clientFullName;
    }

    /**
     * Zwraca opis roweru.
     *
     * @return opis roweru
     * @author Tomasz Piłat
     */
    public String getBikeInfo() {
        return bikeInfo;
    }

    /**
     * Zwraca datę rozpoczęcia wypożyczenia.
     *
     * @return data rozpoczęcia
     * @author Tomasz Piłat
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Zwraca datę zakończenia wypożyczenia.
     *
     * @return data zakończenia
     * @author Tomasz Piłat
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Zwraca zlokalizowany status wypożyczenia.
     *
     * @return status wypożyczenia
     * @author Tomasz Piłat
     */
    public String getStatus() {
        return status;
    }

    /**
     * Zwraca uwagi do wypożyczenia.
     *
     * @return uwagi lub pusty ciąg
     * @author Tomasz Piłat
     */
    public String getNotes() { return notes; }

    /**
     * Zwraca wartości wiersza tabeli (bez rentId).
     *
     * @return tablica danych do tabeli
     * @author Tomasz Piłat
     */
    public Object[] toRow() {
        return new Object[]{ clientFullName, bikeInfo, startDate, endDate, status, notes };
    }
}
