package AI2.View.ViewModel;

import AI2.Model.Client;
import AI2.Model.Bike;
import AI2.Model.Rent;

import java.time.format.DateTimeFormatter;

/**
 * ViewModel dla wypożyczenia – dane wyświetlane w tabeli (bez wewnętrznych ID).
 */
public class RentViewModel {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final int rentId;
    private final String clientFullName;
    private final String bikeInfo;
    private final String startDate;
    private final String endDate;
    private final String status;

    public RentViewModel(Rent rent, Client client, Bike bike) {
        this.rentId = rent.getId();
        this.clientFullName = client != null
                ? client.getName() + " " + client.getSurname()
                : "ID:" + rent.getClientId();
        this.bikeInfo = bike != null
                ? bike.getBrand() + " " + bike.getModel()
                : "ID:" + rent.getBikeId();
        this.startDate = rent.getRentDate() != null
                ? rent.getRentDate().format(FMT) : "";
        this.endDate = rent.getReturnTime() != null
                ? rent.getReturnTime().format(FMT) : "";
        this.status = rent.getStatus() != null
                ? rent.getStatus().name() : "";
    }

    public int getRentId(){
        return rentId;
    }

    public String getClientFullName(){
        return clientFullName;
    }

    public String getBikeInfo(){
        return bikeInfo;
    }

    public String getStartDate(){
        return startDate;
    }
    public String getEndDate() {
        return endDate;
    }

    public String getStatus(){
        return status;
    }

    /** Wartości do wierszy tabeli (bez rentId). */
    public Object[] toRow() {
        return new Object[]{ clientFullName, bikeInfo, startDate, endDate, status };
    }
}
