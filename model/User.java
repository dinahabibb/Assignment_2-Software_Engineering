package model;

/**
 * Represents a system user.
 * Stores basic authentication and profile information.
 */
public class User {

    /** User's first name */
    private String firstName;

    /** User's last name */
    private String lastName;

    /** User's email (used for login) */
    private String email;

    /** User's password (stored in plain text for simplicity) */
    private String password;

    /**
     * Creates a new User.
     *
     * @param firstName user's first name
     * @param lastName user's last name
     * @param email user's email address
     * @param password user's password
     */
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    /** @return first name */
    public String getFirstName() {
        return firstName;
    }

    /** @return last name */
    public String getLastName() {
        return lastName;
    }

    /** @return email address */
    public String getEmail() {
        return email;
    }

    /** @return password */
    public String getPassword() {
        return password;
    }
}
