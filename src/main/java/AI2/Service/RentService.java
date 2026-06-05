package AI2.Service;

import AI2.Enums.BikeStatus;
import AI2.Enums.RentStatus;
import AI2.Model.Bike;
import AI2.Model.Rent;
import AI2.Repository.BikeRepository;
import AI2.Repository.ClientRepository;
import AI2.Repository.RentRepository;
import AI2.Util.LanguageManager;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RentService {
    private final RentRepository rentRepository;
    private final BikeRepository bikeRepository;
    private final ClientRepository clientRepository;

    public RentService(RentRepository rentRepository, BikeRepository bikeRepository,
                       ClientRepository clientRepository) {
        this.rentRepository = rentRepository;
        this.bikeRepository = bikeRepository;
        this.clientRepository = clientRepository;
        setOverdueStatus();
    }

    public void addRent(Rent rent){
        validateRent(rent);
        if(Objects.equals(rent.getRentDate(), LocalDateTime.now())){}
        rent.setStatus(RentStatus.ACTIVE);
        bikeRepository.getBikeById(rent.getBikeId()).setStatus(BikeStatus.RENTED);
        rentRepository.addRent(rent);
    }

    public void endRent(int rentId){
        if (rentRepository.getRentByID(rentId) == null){
            throw new RuntimeException("Rent not found");
        }
        Rent rent = rentRepository.getRentByID(rentId);
        rentRepository.getRentByID(rentId).setStatus(RentStatus.FINISHED);

        Bike bike = bikeRepository.getBikeById(rent.getBikeId());
        if(bike.getStatus() == BikeStatus.AVAILABLE){
            throw new RuntimeException("Bike is already returned");
        }
        bike.setStatus(BikeStatus.AVAILABLE);
        bikeRepository.updateBike(bike);
    }

    public void removeRent(int rentId){
        if  (rentRepository.getRentByID(rentId) == null){
            throw new RuntimeException("Rent not found");
        }
        Rent rent = rentRepository.getRentByID(rentId);
        if (rent.getRentDate().isBefore(LocalDateTime.now()) || rent.getStatus()!=RentStatus.SCHEDULED){
            throw new RuntimeException("Rent can't be deleted");
        }
        rentRepository.removeRent(rentId);
    }

    public void updateRent(Rent newRent){
        validateRent(newRent);
        rentRepository.updateRent(newRent);
    }

    public Rent getRentByID(int rentId){
        if (rentRepository.getRentByID(rentId) == null){
            throw new RuntimeException("Rent not found");
        }
        return rentRepository.getRentByID(rentId);
    }

    public List<Rent> getAllRents() {

        try {

            return rentRepository
                    .getRentDataBase();

        } catch (Exception e) {

            throw new RuntimeException(
                    LanguageManager.getString(
                            "error.databaseLoad"
                    ),
                    e
            );
        }
    }

    public void setOverdueStatus(){
        for  (Rent rent : getAllRents()){
            if (rent.getReturnTime().isAfter(LocalDateTime.now())){
                rent.setStatus(RentStatus.OVERDUE);
            }
        }
    }

    public List<Rent> findClientRents(int clientId){
         return rentRepository.getRentDataBase().stream().filter(r-> r.getClientId() == clientId).collect(Collectors.toList());
    }
    public List<Rent> findActiveRents(){
        return rentRepository.getRentDataBase().stream().filter(r-> r.getStatus() == RentStatus.ACTIVE).collect(Collectors.toList());
    }

    public void saveRents(){
        rentRepository.saveRentDataBase();
    }






    public void validateRent(Rent rent){
        if(bikeRepository.getBikeById(rent.getBikeId())==null){
            throw  new IllegalArgumentException("Bike does not exist");
        }
        if (bikeRepository.getBikeById(rent.getBikeId()).getStatus()!= BikeStatus.AVAILABLE){
            throw  new IllegalArgumentException("Bike is not available");
        }
        if(clientRepository.getClientById(rent.getClientId())==null){
            throw  new IllegalArgumentException("Client does not exist");
        }
        if(rent.getRentDate().isBefore(LocalDateTime.now())){
            throw  new IllegalArgumentException("Rent date is before current date");
        }
    }
}
