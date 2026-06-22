package AI2.Service;

import AI2.Enums.UserRole;
import AI2.Model.User;
import AI2.Repository.UserRepository;
import AI2.Util.LanguageManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Serwis zarządzania użytkownikami i uwierzytelnianiem.
 *
 * @author Tomasz Piłat
 */
public class UserService {

    private final UserRepository userRepository;

    /**
     * Tworzy serwis użytkowników.
     *
     * @param userRepository repozytorium użytkowników
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loguje użytkownika. Weryfikuje nazwę i hasło (SHA-256).
     *
     * @param username nazwa użytkownika
     * @param password hasło w postaci jawnej
     * @return zalogowany użytkownik
     * @throws IllegalArgumentException jeśli dane logowania są błędne
     */
    public User login(String username, String password) {
        User user = userRepository.getUserByUsername(username);
        if (user == null || !user.getPasswordHash().equals(hashPassword(password))) {
            throw new IllegalArgumentException(
                    LanguageManager.getString("error.login.invalid"));
        }
        return user;
    }

    /**
     * Dodaje nowego użytkownika.
     *
     * @param username nazwa użytkownika
     * @param password hasło w postaci jawnej (zostanie zahaszowane)
     * @param role     rola
     * @throws IllegalArgumentException jeśli dane są niepoprawne lub użytkownik już istnieje
     */
    public void addUser(String username, String password, UserRole role) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Nazwa użytkownika nie może być pusta.");
        if (password == null || password.isBlank())
            throw new IllegalArgumentException("Hasło nie może być puste.");
        if (userRepository.getUserByUsername(username) != null)
            throw new IllegalArgumentException("Użytkownik o tej nazwie już istnieje.");

        userRepository.addUser(new User(0, username, hashPassword(password), role));
    }

    /**
     * Aktualizuje dane użytkownika. Jeśli newPassword jest puste, hasło nie zmienia się.
     *
     * @param user        użytkownik z nowymi danymi (id musi być poprawne)
     * @param newPassword nowe hasło (lub pusty string — wtedy hasło bez zmiany)
     * @throws IllegalArgumentException jeśli dane są niepoprawne
     */
    public void updateUser(User user, String newPassword) {
        if (user.getUsername() == null || user.getUsername().isBlank())
            throw new IllegalArgumentException("Nazwa użytkownika nie może być pusta.");

        User existing = userRepository.getUserByUsername(user.getUsername());
        if (existing != null && existing.getId() != user.getId())
            throw new IllegalArgumentException("Użytkownik o tej nazwie już istnieje.");

        if (!newPassword.isBlank()) {
            user.setPasswordHash(hashPassword(newPassword));
        }
        userRepository.updateUser(user);
    }

    /**
     * Usuwa użytkownika po ID.
     *
     * @param id identyfikator użytkownika
     */
    public void removeUser(int id) {
        userRepository.removeUser(id);
    }

    /**
     * Zwraca użytkownika po ID lub {@code null}.
     *
     * @param id identyfikator
     * @return użytkownik lub null
     */
    public User getUserById(int id) {
        return userRepository.getAllUsers().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Zwraca wszystkich użytkowników.
     *
     * @return lista użytkowników
     */
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    /**
     * Sprawdza czy brak zarejestrowanych użytkowników.
     *
     * @return {@code true} jeśli brak użytkowników
     */
    public boolean isEmpty() {
        return userRepository.isEmpty();
    }

    /**
     * Hashuje hasło algorytmem SHA-256.
     *
     * @param password hasło w postaci jawnej
     * @return heksadecymalny skrót SHA-256
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 niedostępny", e);
        }
    }
}
