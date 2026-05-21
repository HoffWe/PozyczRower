package AI2.Repository;

import AI2.Model.Rent;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
Klasa służąca jako baza danych dla wypożyczeń

 @author Tomasz Piłat
 */
public class RentRepository {
    private List<Rent> rentDataBase;
    private int currentId;

    public RentRepository() {
        rentDataBase = new ArrayList<>();
        rentDataBase = loadRentDataBase();
    }

    public List<Rent> getRentDataBase() {
        return rentDataBase;
    }

    public void addRent(Rent rent) {
        rent.setId(currentId);
        currentId++;
        rentDataBase.add(rent);
    }
    public void removeRent(int rentId) {
        rentDataBase.stream().filter(r -> r.getId() == rentId).findFirst()
                .ifPresent(r -> {rentDataBase.remove(r);});
    }
    public Rent getRentByID(int rentId) {
        return rentDataBase.stream().filter(r -> r.getId() == rentId).findFirst().orElse(null);
    }
    public void updateRent(int rentId, Rent newRent) {
        rentDataBase.stream().filter(r -> r.getId() == rentId).findFirst().ifPresent(r -> {r.setRentDate(newRent.getRentDate());
                                                                                            r.setReturnTime(newRent.getReturnTime());
                                                                                            r.setClientId(newRent.getClientId());
                                                                                            r.setBikeId(newRent.getBikeId());});

    }
    /**
     *  klasa służąca do zapisania bazy danych wypożyczeń w pliku
     * @author Tomasz Piłat
     */
    public void saveRentDataBase(List<Rent> rentDataBase) {

        try (DataOutputStream outputStream = new DataOutputStream(
                new FileOutputStream("RentDataBase.dat"))) {

            outputStream.writeInt(rentDataBase.size());
            outputStream.writeInt(currentId);

            for (Rent rent : rentDataBase) {
                outputStream.writeInt(rent.getId());
                outputStream.writeInt(rent.getBikeId());
                outputStream.writeInt(rent.getClientId());
                outputStream.writeLong(Timestamp.valueOf(rent.getRentDate()).getTime());
                outputStream.writeLong(Timestamp.valueOf(rent.getReturnTime()).getTime());
            }

        } catch (IOException e) {
            System.out.println("Błąd podczas zapisywania bazy danych.");
        }
    }
/**
 *  klasa służąca do wczytania bazy danych wypożyczeń z pliku
 * @author Tomasz Piłat
 */
    public List<Rent> loadRentDataBase(){
        List<Rent> rentDataBase = new ArrayList<>();

        try (DataInputStream inputStream =
                     new DataInputStream(new FileInputStream("RentDataBase.dat"))) {

            int size = inputStream.readInt();
            currentId = inputStream.readInt();

            for (int i = 0; i < size; i++) {

                int rentId = inputStream.readInt();
                int bikeId = inputStream.readInt();
                int clientId = inputStream.readInt();

                LocalDateTime rentDate =
                        new Timestamp(inputStream.readLong()).toLocalDateTime();

                LocalDateTime returnTime =
                        new Timestamp(inputStream.readLong()).toLocalDateTime();

                rentDataBase.add(
                        new Rent(rentId, bikeId, clientId, rentDate, returnTime)
                );
            }

        } catch (FileNotFoundException e) {

            System.out.println("Plik bazy danych nie istnieje.");

        } catch (IOException e) {

            System.out.println("Błąd podczas wczytywania bazy danych.");
        }

        return rentDataBase;
    }

    public int getCurrentId() {
        return currentId;
    }
    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }
}
