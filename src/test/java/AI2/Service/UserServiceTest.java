package AI2.Service;

import AI2.Enums.UserRole;
import AI2.Model.User;
import AI2.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy jednostkowe dla {@link UserService}.
 * Repozytorium zastąpione stubem in-memory (brak zapisu do pliku).
 *
 * @author Tomasz Piłat
 */
class UserServiceTest {

    private UserService service;

    @BeforeEach
    void setUp() {
        UserRepository stubRepo = new UserRepository() {
            private final List<User> list = new ArrayList<>();
            private int nextId = 1;

            @Override public void addUser(User u)  { u.setId(nextId++); list.add(u); }
            @Override public List<User> getAllUsers() { return new ArrayList<>(list); }
            @Override public User getUserByUsername(String name) {
                return list.stream()
                        .filter(u -> u.getUsername().equalsIgnoreCase(name))
                        .findFirst().orElse(null);
            }
            @Override public void updateUser(User updated) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId() == updated.getId()) { list.set(i, updated); return; }
                }
            }
            @Override public void removeUser(int id) { list.removeIf(u -> u.getId() == id); }
            @Override public boolean isEmpty()       { return list.isEmpty(); }
            @Override public void saveToFile()       {}
        };
        service = new UserService(stubRepo);
    }

    @Test
    void hashPassword_deterministicForSameInput() {
        String h1 = UserService.hashPassword("secret");
        String h2 = UserService.hashPassword("secret");
        assertEquals(h1, h2);
    }

    @Test
    void hashPassword_differentForDifferentInput() {
        assertNotEquals(
                UserService.hashPassword("abc"),
                UserService.hashPassword("ABC"));
    }

    @Test
    void hashPassword_correctLength() {
        // SHA-256 → 64 znaki hex
        assertEquals(64, UserService.hashPassword("test").length());
    }

    @Test
    void addUser_validData_userAdded() {
        service.addUser("jan", "haslo", UserRole.RENTAL_WORKER);
        assertFalse(service.isEmpty());
        assertEquals(1, service.getAllUsers().size());
    }

    @Test
    void addUser_passwordIsHashed() {
        service.addUser("jan", "haslo", UserRole.RENTAL_WORKER);
        User u = service.getAllUsers().get(0);
        assertNotEquals("haslo", u.getPasswordHash());
        assertEquals(UserService.hashPassword("haslo"), u.getPasswordHash());
    }

    @Test
    void addUser_emptyUsername_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addUser("", "haslo", UserRole.ADMIN));
    }

    @Test
    void addUser_blankUsername_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addUser("   ", "haslo", UserRole.ADMIN));
    }

    @Test
    void addUser_emptyPassword_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addUser("jan", "", UserRole.ADMIN));
    }

    @Test
    void addUser_duplicateUsername_throwsException() {
        service.addUser("admin", "haslo1", UserRole.ADMIN);
        assertThrows(IllegalArgumentException.class,
                () -> service.addUser("admin", "haslo2", UserRole.RENTAL_WORKER));
    }

    @Test
    void addUser_duplicateUsernameIgnoresCase_throwsException() {
        service.addUser("Admin", "haslo", UserRole.ADMIN);
        assertThrows(IllegalArgumentException.class,
                () -> service.addUser("ADMIN", "haslo2", UserRole.RENTAL_WORKER));
    }

    @Test
    void login_correctCredentials_returnsUser() {
        service.addUser("jan", "tajne", UserRole.RENTAL_WORKER);
        User logged = service.login("jan", "tajne");
        assertNotNull(logged);
        assertEquals("jan", logged.getUsername());
    }

    @Test
    void login_wrongPassword_throwsException() {
        service.addUser("jan", "tajne", UserRole.RENTAL_WORKER);
        assertThrows(IllegalArgumentException.class,
                () -> service.login("jan", "zle"));
    }

    @Test
    void login_unknownUsername_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.login("niema", "cokolwiek"));
    }

    @Test
    void login_emptyPassword_throwsException() {
        service.addUser("jan", "tajne", UserRole.RENTAL_WORKER);
        // puste hasło hashuje się do czegoś innego niż "tajne"
        assertThrows(IllegalArgumentException.class,
                () -> service.login("jan", ""));
    }


    @Test
    void updateUser_changeRole_roleUpdated() {
        service.addUser("jan", "haslo", UserRole.RENTAL_WORKER);
        User u = service.getAllUsers().get(0);
        u.setRole(UserRole.TECHNICIAN);
        service.updateUser(u, "");
        assertEquals(UserRole.TECHNICIAN, service.getAllUsers().get(0).getRole());
    }

    @Test
    void updateUser_newPassword_hashChanges() {
        service.addUser("jan", "stare", UserRole.ADMIN);
        User u = service.getAllUsers().get(0);
        String oldHash = u.getPasswordHash();
        service.updateUser(u, "nowe");
        assertNotEquals(oldHash, service.getAllUsers().get(0).getPasswordHash());
        assertEquals(UserService.hashPassword("nowe"), service.getAllUsers().get(0).getPasswordHash());
    }

    @Test
    void updateUser_emptyPassword_hashUnchanged() {
        service.addUser("jan", "stare", UserRole.ADMIN);
        User u = service.getAllUsers().get(0);
        String oldHash = u.getPasswordHash();
        service.updateUser(u, "");
        assertEquals(oldHash, service.getAllUsers().get(0).getPasswordHash());
    }

    @Test
    void updateUser_emptyUsername_throwsException() {
        service.addUser("jan", "haslo", UserRole.ADMIN);
        User u = service.getAllUsers().get(0);
        u.setUsername("");
        assertThrows(IllegalArgumentException.class, () -> service.updateUser(u, ""));
    }

    @Test
    void updateUser_duplicateUsernameOtherUser_throwsException() {
        service.addUser("jan", "h1", UserRole.ADMIN);
        service.addUser("anna", "h2", UserRole.RENTAL_WORKER);
        User anna = service.getAllUsers().get(1);
        anna.setUsername("jan");
        assertThrows(IllegalArgumentException.class, () -> service.updateUser(anna, ""));
    }

    @Test
    void updateUser_sameUsernameAsOwn_doesNotThrow() {
        service.addUser("jan", "haslo", UserRole.ADMIN);
        User u = service.getAllUsers().get(0);
        // Aktualizacja bez zmiany nazwy — nie powinno rzucić
        assertDoesNotThrow(() -> service.updateUser(u, ""));
    }



    @Test
    void removeUser_existingUser_userRemoved() {
        service.addUser("jan", "haslo", UserRole.ADMIN);
        int id = service.getAllUsers().get(0).getId();
        service.removeUser(id);
        assertTrue(service.isEmpty());
    }

    @Test
    void getUserById_existingId_returnsUser() {
        service.addUser("jan", "haslo", UserRole.ADMIN);
        int id = service.getAllUsers().get(0).getId();
        User found = service.getUserById(id);
        assertNotNull(found);
        assertEquals("jan", found.getUsername());
    }

    @Test
    void getUserById_nonExistingId_returnsNull() {
        assertNull(service.getUserById(999));
    }

    @Test
    void getAllUsers_multipleUsers_returnsAll() {
        service.addUser("jan",  "h1", UserRole.ADMIN);
        service.addUser("anna", "h2", UserRole.TECHNICIAN);
        assertEquals(2, service.getAllUsers().size());
    }

    @Test
    void isEmpty_initiallyTrue() {
        assertTrue(service.isEmpty());
    }

    @Test
    void isEmpty_afterAdd_false() {
        service.addUser("jan", "haslo", UserRole.ADMIN);
        assertFalse(service.isEmpty());
    }
}
