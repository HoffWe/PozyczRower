package AI2.Repository;

import AI2.Enums.UserRole;
import AI2.Model.User;

import AI2.Util.AppConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repozytorium użytkowników – zapis i odczyt z pliku binarnego.
 *
 * <p>Format rekordu: id(int) | username(UTF) | passwordHash(UTF) | role(UTF)</p>
 *
 * @author Tomasz Piłat
 */
public class UserRepository {

    private static final String FILE_NAME = "data/users.dat";

    private final List<User> users = new ArrayList<>();
    private int nextId = 1;

    /** Tworzy repozytorium i wczytuje dane z pliku. */
    public UserRepository() {
        loadFromFile();
        nextId = users.stream().mapToInt(User::getId).max().orElse(0) + 1;
    }

    /**
     * Dodaje nowego użytkownika (przypisuje ID) i zapisuje.
     *
     * @param user użytkownik do dodania
     */
    public void addUser(User user) {
        user.setId(nextId++);
        users.add(user);
        saveToFile();
    }

    /**
     * Zwraca wszystkich użytkowników.
     *
     * @return lista użytkowników
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Wyszukuje użytkownika po nazwie (bez rozróżniania wielkości liter).
     *
     * @param username nazwa użytkownika
     * @return znaleziony użytkownik lub {@code null}
     */
    public User getUserByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * Aktualizuje dane istniejącego użytkownika i zapisuje.
     *
     * @param updated użytkownik z nowymi danymi (musi mieć poprawne id)
     */
    public void updateUser(User updated) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == updated.getId()) {
                users.set(i, updated);
                saveToFile();
                return;
            }
        }
    }

    /**
     * Usuwa użytkownika po ID i zapisuje.
     *
     * @param id identyfikator użytkownika do usunięcia
     */
    public void removeUser(int id) {
        users.removeIf(u -> u.getId() == id);
        saveToFile();
    }

    /**
     * Sprawdza czy lista użytkowników jest pusta.
     *
     * @return {@code true} jeśli brak użytkowników
     */
    public boolean isEmpty() {
        return users.isEmpty();
    }

    /** Zapisuje wszystkich użytkowników do pliku w oddzielnym wątku (DataOutputStream). */
    public void saveToFile() {
        List<User> snapshot = new ArrayList<>(users);
        AppConfig.SAVE_EXECUTOR.submit(() -> {
            new File(AppConfig.DATA_DIR).mkdirs();
            try (DataOutputStream dos = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(FILE_NAME)))) {
                dos.writeInt(snapshot.size());
                for (User u : snapshot) {
                    dos.writeInt(u.getId());
                    dos.writeUTF(u.getUsername());
                    dos.writeUTF(u.getPasswordHash());
                    dos.writeUTF(u.getRole().name());
                }
            } catch (IOException e) {
                System.err.println("Błąd zapisu użytkowników: " + e.getMessage());
            }
        });
    }

    /** Wczytuje użytkowników z pliku. */
    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) return;
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(FILE_NAME)))) {
            int count = dis.readInt();
            for (int i = 0; i < count; i++) {
                int      id           = dis.readInt();
                String   username     = dis.readUTF();
                String   passwordHash = dis.readUTF();
                UserRole role         = UserRole.valueOf(dis.readUTF());
                users.add(new User(id, username, passwordHash, role));
            }
        } catch (IOException e) {
            System.err.println("Błąd odczytu użytkowników: " + e.getMessage());
        }
    }
}
