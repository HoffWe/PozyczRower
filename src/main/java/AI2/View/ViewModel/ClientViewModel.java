package AI2.View.ViewModel;

import AI2.Model.Client;

/**
 * ViewModel dla klienta – zawiera tylko dane wyświetlane w tabeli.
 * ID jest przechowywane wewnętrznie, ale nie pojawia się jako kolumna.
 */
public class ClientViewModel {

    private final int id;
    private final String firstName;
    private final String lastName;
    private final String evidence;
    private final String description;

    public ClientViewModel(Client client) {
        this.id = client.getId();
        this.firstName = client.getName();
        this.lastName = client.getSurname();
        this.evidence = client.getEvidence();
        this.description = client.getOpis();
    }

    public int getId(){
        return id;
    }
    public String getFirstName(){
        return firstName;
    }
    public String getLastName(){
        return lastName;
    }
    public String getEvidence(){
        return evidence;
    }
    public String getDescription(){
        return description;
    }

    /** Wartości do wierszy tabeli (bez ID). */
    public Object[] toRow() {
        return new Object[]{ firstName, lastName, evidence, description };
    }
}
