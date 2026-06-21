package AI2.Model;

import AI2.Enums.UserRole;

/**
 * Model użytkownika systemu.
 *
 * @author Tomasz Piłat
 */
public class User {

    private int      id;
    private String   username;
    private String   passwordHash;
    private UserRole role;

    /** Domyślny konstruktor. */
    public User() {}

    /**
     * Tworzy użytkownika z podanymi danymi.
     *
     * @param id           identyfikator
     * @param username     nazwa użytkownika
     * @param passwordHash skrót SHA-256 hasła
     * @param role         rola
     */
    public User(int id, String username, String passwordHash, UserRole role) {
        this.id           = id;
        this.username     = username;
        this.passwordHash = passwordHash;
        this.role         = role;
    }

    // ----------------------------------------------------------------
    // Gettery i settery
    // ----------------------------------------------------------------

    public int getId()                     { return id; }
    public void setId(int id)              { this.id = id; }

    public String getUsername()            { return username; }
    public void setUsername(String u)      { this.username = u; }

    public String getPasswordHash()        { return passwordHash; }
    public void setPasswordHash(String h)  { this.passwordHash = h; }

    public UserRole getRole()              { return role; }
    public void setRole(UserRole role)     { this.role = role; }

    @Override
    public String toString() {
        return username + " (" + (role != null ? role.getDisplayName() : "?") + ")";
    }
}
