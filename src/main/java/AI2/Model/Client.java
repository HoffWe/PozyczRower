package AI2.Model;

/**
 *
 *
 *
 *
 * @author Sviatoslav Matsopa
 *
 *
 *
 */
public class Client {
    private int id;
    private String name;
    private String surname;
    private String evidence;
    private String opis;

    /** Konstruktor domyslny */
    public Client() {}

    /**
     * Konstruktor z parametrami
     * */
    public Client(int id, String name, String surname, String evidence, String opis) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.evidence = evidence;
        this.opis = opis;
    }

    /** Zwraca ID klienta */
    public int getId() {

        return id;
    }
    /** Ustawia ID klienta */
    public void setId(int id) {

        this.id = id;
    }
    /** Zwraca imie klienta */
    public String getName() {

        return name;
    }
    /** Ustawia imie klienta */
    public void setName(String name) {

        this.name = name;
    }
    /** Zwraca nazwisko klienta */
    public String getSurname() {

        return surname;
    }
    /** Ustawia nazwisko klienta */
    public void setSurname(String surname) {

        this.surname = surname;
    }
    /** Zwraca numer dowodu */
    public String getEvidence() {

        return evidence;
    }
    /** Ustawia numer dowodu */
    public void setEvidence(String evidence) {

        this.evidence = evidence;
    }
    /** Zwraca opis */
    public String getOpis() {

        return opis;
    }
    /** Ustawia opis */
    public void setOpis(String opis) {

        this.opis = opis;
    }

    @Override
    public String toString() {

        return name + " " + surname + " (ID: " + id + ")";

    }
}