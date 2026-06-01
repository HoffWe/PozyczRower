package AI2.Enums;

public enum RentStatus {
    ACTIVE("key.active"), CLOSED("key.closed"), OVERDUE("key.overdue"), FINISHED("key.finished"),SCHEDULED("key.scheduled") ;

    private String status;
    public String getStatus(){
        return status;
    }

    private RentStatus(String status){
        this.status = status;
    }
}
