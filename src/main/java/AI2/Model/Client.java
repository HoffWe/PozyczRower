package AI2.Model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**

    author:Sviatoslav

*/

public class Client {
    private int id;
    private String name;
    private String surname;
    private String evidence; /** numer dowodu*/
    private String opis;

    /** Konstruktor domyślny */
    public Client() {}

    /** Konstruktor z parametrami */
    public Client(int id, String name, String surname, String evidence, String opis) {

        this.id = id;
        this.name = name;
        this.surname = surname;
        this.evidence = evidence;
        this.opis = opis;
    }

    public int getId() {

        return id;
    }
    public void setId(int id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }
    public void setName(String name) {

        this.name = name;
    }

    public String getSurname() {

        return surname;
    }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEvidence() {

        return evidence;
    }
    public void setEvidence(String evidence) {

        this.evidence = evidence;
    }

    public String getOpis() {

        return opis;
    }
    public void setOpis(String opis) {

        this.opis = opis;
    }

    @Override
    public String toString() {

        return name + " " + surname + " (ID: " + id + ")";
    }
}

