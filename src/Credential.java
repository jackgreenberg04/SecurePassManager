package securepassmanager;

import java.io.Serializable;
import java.util.Base64;

/**
 * Represents a website credential consisting of a username and password.
 * Passwords are stored using Base64 encoding for simple obfuscation.
 */
public class Credential implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String encodedPassword;

    /**
     * Creates a credential with the provided username and plain text password.
     * The password will be encoded using Base64 before storage.
     *
     * @param username the username for the credential
     * @param password the plain text password
     */
    public Credential(String username, String password) {
        this.username = username;
        setPassword(password);
    }

    /** Default constructor required for serialization frameworks. */
    public Credential() {
    }

    /**
     * Returns the username.
     *
     * @return username string
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username new username value
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the decoded password for display.
     *
     * @return plain text password
     */
    public String getPassword() {
        if (encodedPassword == null) {
            return null;
        }
        return new String(Base64.getDecoder().decode(encodedPassword));
    }

    /**
     * Sets the password. The provided plain text password is encoded and stored.
     *
     * @param password plain text password
     */
    public void setPassword(String password) {
        if (password == null) {
            this.encodedPassword = null;
        } else {
            this.encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
        }
    }
}
