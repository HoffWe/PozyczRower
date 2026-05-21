package AI2.Model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Rent {
    private int id;
    private int bikeId;
    private int clientId;
    private LocalDateTime rentDate;
    private LocalDateTime returnTime;

    public Rent(){}

    public Rent(int id,int bikeId, int clientId, LocalDateTime rentDate, LocalDateTime returnTime) {
        this.id = id;
        this.bikeId = bikeId;
        this.clientId = clientId;
        this.rentDate = rentDate;
        this.returnTime = returnTime;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getBikeId() {
        return bikeId;
    }
    public void setBikeId(int bikeId) {
        this.bikeId = bikeId;
    }
    public int getClientId() {
        return clientId;
    }
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
    public LocalDateTime getRentDate() {
        return rentDate;
    }
    public void setRentDate(LocalDateTime rentDate) {
        this.rentDate = rentDate;
    }
    public LocalDateTime getReturnTime() {
        return returnTime;
    }
    public void setReturnTime(LocalDateTime returnTime) {
        this.returnTime = returnTime;
    }

}
