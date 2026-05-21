package AI2.Repository;

import AI2.Model.BikeType;

import java.util.ArrayList;
import java.util.List;

/**
    @author Adrian Karpiński
*/

public class BikeTypeRepository {

    private List<BikeType> bikeTypeList;


    public BikeTypeRepository(){
        bikeTypeList = new ArrayList<>();
    }

    //metody
    public void addBikeType(BikeType bikeType){
        int nextId = findLastId()+1;
        bikeType.setId(nextId);
        bikeTypeList.add(bikeType);
    }
    public void removeBikeType(BikeType bikeType){
        bikeTypeList.remove(bikeType);
        //zamiast usuwania może dodanie pustego rowera? flagi
    }
    public void editBikeType(BikeType bikeType){
        bikeTypeList.set(bikeTypeList.indexOf(bikeType),bikeType);
    }
    public List<BikeType> getAllBikesTypes(){
        return bikeTypeList;
    }
    public BikeType getBikeType(int id){
        return bikeTypeList.get(id);
    }

    public int findLastId(){
        int id = 0;
        for(BikeType bikeType : bikeTypeList){
            if(bikeType.getId() > id){
                id = bikeType.getId();
            }
        }
        return id;
    }

}
