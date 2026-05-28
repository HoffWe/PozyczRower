package AI2.Service;

import AI2.Model.Client;
import AI2.Repository.ClientRepository;

import java.io.*;
import java.util.List;

/**
 * .
 *
 *
 * @author Sviatoslav Matsopa
 */
public class ClientFileService {

    private static final String FILE_NAME = "clients.dat";

    /**
     * Zapisuje wszystkich klientów do pliku
     * @param clientRepository repozytorium klientów
     */
    public void saveToFile(ClientRepository clientRepository) {
        List<Client> lista = clientRepository.getAllClients();

        try {
            FileOutputStream fos = new FileOutputStream(FILE_NAME);
            DataOutputStream dos = new DataOutputStream(fos);

            dos.writeInt(lista.size());

            for (int i = 0; i < lista.size(); i++) {
                Client c = lista.get(i);
                dos.writeInt(c.getId());
                dos.writeUTF(c.getName());
                dos.writeUTF(c.getSurname());
                dos.writeUTF(c.getEvidence());
                dos.writeUTF(c.getOpis());
            }

            dos.close();
            fos.close();

        } catch (IOException e) {
            System.out.println("Błąd zapisu: " + e.getMessage());
        }
    }

    /**
     * Odczytuje klientów z pliku
     * @param clientRepository repozytorium klientów
     */
    public void loadFromFile(ClientRepository clientRepository) {
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

                Client c = new Client(id, name, surname, evidence, opis);
                clientRepository.addClient(c);
            }

            dis.close();
            fis.close();

        } catch (IOException e) {
            System.out.println("Błąd odczytu: " + e.getMessage());
        }
    }
}