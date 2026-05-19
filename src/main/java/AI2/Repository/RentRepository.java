package AI2.Repository;

import AI2.Model.Rent;

import java.util.ArrayList;
import java.util.List;

public class RentRepository {
    private List<Rent> rentDataBase = new ArrayList<Rent>();

    public void addRent(Rent rent) {
        rentDataBase.add(rent);
    }
}
