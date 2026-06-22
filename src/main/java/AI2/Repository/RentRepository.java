package AI2.Repository;

import AI2.Enums.RentStatus;
import AI2.Model.Rent;

import AI2.Util.AppConfig;

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
    private final List<Rent> rentDataBase;
    private int currentId;
    private static final String FILE_PATH =
            "data/RentDataBase.dat";

    public RentRepository() {
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
        rentDataBase.removeIf(r->r.getId()==rentId);
    }

    public Rent getRentByID(int rentId) {
        return rentDataBase.stream().filter(r -> r.getId() == rentId).findFirst().orElse(null);
    }

    public void updateRent(Rent newRent) {
        Rent rent = getRentByID(newRent.getId());
        rent.setBikeId(newRent.getBikeId());
        rent.setRentDate(newRent.getRentDate());
        rent.setReturnTime(newRent.getReturnTime());
        rent.setClientId(newRent.getClientId());
        rent.setStatus(newRent.getStatus());
        rent.setNotes(newRent.getNotes());
    }
    /**
     * Zapisuje bazę danych wypożyczeń do pliku w oddzielnym wątku (DataOutputStream).
     *
     * @author Tomasz Piłat
     */
    public void saveRentDataBase() {
        List<Rent> snapshot = new ArrayList<>(rentDataBase);
        int idSnapshot = currentId;
        AppConfig.SAVE_EXECUTOR.submit(() -> {
            new File(AppConfig.DATA_DIR).mkdirs();
            try (DataOutputStream outputStream = new DataOutputStream(
                    new FileOutputStream(FILE_PATH))) {
                outputStream.writeInt(snapshot.size());
                outputStream.writeInt(idSnapshot);
                for (Rent rent : snapshot) {
                    outputStream.writeInt(rent.getId());
                    outputStream.writeInt(rent.getBikeId());
                    outputStream.writeInt(rent.getClientId());
                    outputStream.writeLong(Timestamp.valueOf(rent.getRentDate()).getTime());
                    outputStream.writeLong(Timestamp.valueOf(rent.getReturnTime()).getTime());
                    outputStream.writeUTF(rent.getStatus().name());
                    outputStream.writeUTF(rent.getNotes() == null ? "" : rent.getNotes());
                }
            } catch (IOException e) {
                System.out.println("Błąd podczas zapisywania bazy danych.");
            }
        });
    }
/**
 *  klasa służąca do wczytania bazy danych wypożyczeń z pliku
 * @return lista wypożyczeń wczytana z pliku
 * @author Tomasz Piłat
 */
    public List<Rent> loadRentDataBase(){
        List<Rent> rentDataBase = new ArrayList<>();

        try (DataInputStream inputStream =
                     new DataInputStream(new FileInputStream(FILE_PATH))) {

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

                RentStatus status = RentStatus.valueOf(inputStream.readUTF());
                String notes = "";
                try { notes = inputStream.readUTF(); } catch (java.io.EOFException ignored) {}

                Rent rent = new Rent(rentId, bikeId, clientId, rentDate, returnTime, status);
                rent.setNotes(notes);
                rentDataBase.add(rent);
            }

        }  catch (IOException e) {

            System.out.println(
                    "Plik bazy danych nie istnieje."
            );
        }

        return rentDataBase;
    }

}
