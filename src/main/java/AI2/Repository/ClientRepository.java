package AI2.Repository;

import AI2.Model.Client;

import AI2.Util.AppConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 *
 *
 * @author Sviatoslav Matsopa
 *
 *
 *
 *
 */
public class ClientRepository {

    private static final String FILE_NAME = "data/clients.dat";
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

    /**
     * Miękkie usunięcie klienta – ustawia flagę {@code deleted} i zapisuje do pliku.
     * Klient pozostaje w pliku, ale jest ukrywany w UI.
     */
    public void removeClient(int id) {
        Client c = getClientById(id);
        if (c != null) {
            c.setDeleted(true);
            saveToFile();
        }
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

    /** Zwraca nastepne dostepne ID na podstawie istniejacych danych */
    public int getNextId() {
        int max = 0;
        for (Client c : clients) {
            if (c.getId() > max) {
                max = c.getId();
            }
        }
        return max + 1;
    }

    /** Zapisuje wszystkich klientów do pliku w oddzielnym wątku (DataOutputStream). */
    private void saveToFile() {
        List<Client> snapshot = new ArrayList<>(clients);
        AppConfig.SAVE_EXECUTOR.submit(() -> {
            new File(AppConfig.DATA_DIR).mkdirs();
            try (DataOutputStream dos = new DataOutputStream(
                    new FileOutputStream(FILE_NAME))) {
                dos.writeInt(snapshot.size());
                for (Client c : snapshot) {
                    dos.writeInt(c.getId());
                    dos.writeUTF(c.getName());
                    dos.writeUTF(c.getSurname());
                    dos.writeUTF(c.getEvidence());
                    dos.writeUTF(c.getOpis());
                    dos.writeBoolean(c.isDeleted());
                }
            } catch (IOException e) {
                System.out.println("Blad zapisu: " + e.getMessage());
            }
        });
    }
    /** Wczytuje klientow z pliku */
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
                boolean isDeleted = false;
                try { isDeleted = dis.readBoolean(); } catch (java.io.EOFException ignored) {}
                Client client = new Client(id, name, surname, evidence, opis);
                client.setDeleted(isDeleted);
                clients.add(client);
            }
            dis.close();
            fis.close();
        } catch (IOException e) {
            System.out.println("Blad odczytu: " + e.getMessage());
        }
    }
}