package securepassmanager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

/**
 * Vault manages all credential storage and related operations such as
 * persistence and undo functionality.
 */
public class Vault implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Credential> credentials;
    private transient Stack<Map<String, Credential>> undoStack;

    /**
     * Creates an empty vault.
     */
    public Vault() {
        credentials = new HashMap<>();
        undoStack = new Stack<>();
    }

    /**
     * Adds or updates a credential in the vault.
     * A snapshot of the current state is pushed onto the undo stack
     * before modification.
     *
     * @param site the website name
     * @param credential credential to add or update
     */
    public void addCredential(String site, Credential credential) {
        pushState();
        credentials.put(site, credential);
    }

    /**
     * Retrieves a credential by website name.
     *
     * @param site the website
     * @return the stored Credential or null if not present
     */
    public Credential getCredential(String site) {
        return credentials.get(site);
    }

    /**
     * Deletes a credential by website name if it exists.
     * A snapshot of the current state is pushed before deletion.
     *
     * @param site the website to delete
     */
    public void deleteCredential(String site) {
        if (credentials.containsKey(site)) {
            pushState();
            credentials.remove(site);
        }
    }

    /**
     * Returns a TreeMap of all credentials sorted by website name.
     *
     * @return sorted map of credentials
     */
    public Map<String, Credential> getAllCredentialsSorted() {
        return new TreeMap<>(credentials);
    }

    /**
     * Saves the entire vault to a file using Java serialization.
     *
     * @param fileName destination file path
     * @throws IOException if an I/O error occurs
     */
    public void saveToFile(String fileName) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(credentials);
        }
    }

    /**
     * Loads credentials from a serialized file. If the file does not exist,
     * an empty vault is kept.
     *
     * @param fileName source file path
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the file does not contain a valid map
     */
    @SuppressWarnings("unchecked")
    public void loadFromFile(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            Object obj = in.readObject();
            if (obj instanceof Map) {
                credentials = (Map<String, Credential>) obj;
            } else {
                throw new IOException("Invalid data in file");
            }
        } catch (IOException e) {
            // If the file doesn't exist or cannot be read, start with an empty vault.
            credentials = new HashMap<>();
            throw e;
        }
        undoStack = new Stack<>();
    }

    /**
     * Restores the vault to the previous state if available.
     *
     * @return true if a state was restored, false otherwise
     */
    public boolean undoLastChange() {
        if (!undoStack.isEmpty()) {
            credentials = undoStack.pop();
            return true;
        }
        return false;
    }

    /** Pushes a deep copy of current credentials onto the undo stack. */
    private void pushState() {
        undoStack.push(new HashMap<>(credentials));
    }
}
