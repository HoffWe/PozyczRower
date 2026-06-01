package AI2.Repository;

import AI2.Model.Client;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Sviatoslav Matsopa
 *
 *
 */
public class ClientRepository {

    private static final String FILE_NAME = "clients.dat";
    private List<Client> clients = new ArrayList<>();

    /** Konstruktor - automatycznie wczytuje dane z pliku przy starcie */
    public ClientRepository() {

        loadFromFile();

    }

    /** Dodaje nowego klienta i zapisuje do pliku */
    public void addClient(Client client) {

        clients.add(client);
        saveToFile();

    }

    /** Zwraca liste wszystkich klientow */
    public List<Client> getAllClients() {

        return clients;

    }

    /** Zwraca klienta po ID, lub null jesli nie istnieje */
    public Client getClientById(int id) {

        for (Client c : clients) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    /** Usuwa klienta po ID i zapisuje do pliku */
    public void removeClient(int id) {

        clients.removeIf(c -> c.getId() == id);
        saveToFile();
    }

    /** Aktualizuje dane klienta i zapisuje do pliku */
    public void updateClient(Client updated) {

        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getId() == updated.getId()) {
                clients.set(i, updated);
                saveToFile();
                return;
            }
        }
    }

    /** Zapisuje wszystkich klientow do pliku za pomoca DataOutputStream
     * */
    private void saveToFile() {
        try {
            FileOutputStream fos = new FileOutputStream(FILE_NAME);
            DataOutputStream dos = new DataOutputStream(fos);

            dos.writeInt(clients.size());
            for (int i = 0; i < clients.size(); i++) {
                Client c = clients.get(i);
                dos.writeInt(c.getId());
                dos.writeUTF(c.getName());
                dos.writeUTF(c.getSurname());
                dos.writeUTF(c.getEvidence());
                dos.writeUTF(c.getOpis());
            }

            dos.close();
            fos.close();
        } catch (IOException e) {
            System.out.println("Blad zapisu: " + e.getMessage());
        }
    }

    /** Wczytuje klientow z pliku
     *
     * */
    private void loadFromFile() {

        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }
        try {
            FileInputStream fis = new FileInputStream(FILE_NAME);
            DataInputStream dis = new DataInputStream(fis);

            int size = dis.readInt();
            for (int i = 0; i < size; i++) {
                int id = dis.readInt();
                String name = dis.readUTF();
                String surname = dis.readUTF();
                String evidence = dis.readUTF();
                String opis = dis.readUTF();
                clients.add(new Client(id, name, surname, evidence, opis));
            }

            dis.close();
            fis.close();
        } catch (IOException e) {
            System.out.println("Blad odczytu: " + e.getMessage());
        }
    }
}