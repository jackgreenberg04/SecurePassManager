package securepassmanager;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Main class containing the command line interface for the password manager.
 */
public class SecurePassManager {
    private static final String DATA_FILE = "vault.dat";

    /**
     * Entry point for the application. Displays a menu and processes user input.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        Vault vault = new Vault();
        // Attempt to load existing data
        try {
            vault.loadFromFile(DATA_FILE);
            System.out.println("Loaded credentials from " + DATA_FILE);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Starting with an empty vault.");
        }

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                System.out.print("Select an option: ");
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1":
                        addCredential(scanner, vault);
                        break;
                    case "2":
                        retrieveCredential(scanner, vault);
                        break;
                    case "3":
                        deleteCredential(scanner, vault);
                        break;
                    case "4":
                        displayAllCredentials(vault);
                        break;
                    case "5":
                        saveVault(vault);
                        break;
                    case "6":
                        loadVault(vault);
                        break;
                    case "7":
                        undoChange(vault);
                        break;
                    case "8":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        }

        // Save automatically on exit
        try {
            vault.saveToFile(DATA_FILE);
            System.out.println("Vault saved to " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Failed to save vault: " + e.getMessage());
        }
    }

    /** Prints the main menu options. */
    private static void printMenu() {
        System.out.println();
        System.out.println("SecurePassManager");
        System.out.println("1. Add new credential");
        System.out.println("2. Retrieve credential");
        System.out.println("3. Delete credential");
        System.out.println("4. Display all credentials");
        System.out.println("5. Save to file");
        System.out.println("6. Load from file");
        System.out.println("7. Undo last change");
        System.out.println("8. Exit");
    }

    /**
     * Prompts user for credential details and adds them to the vault.
     */
    private static void addCredential(Scanner scanner, Vault vault) {
        System.out.print("Enter website: ");
        String site = scanner.nextLine().trim();
        System.out.print("Enter username: ");
        String user = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String pass = scanner.nextLine().trim();
        vault.addCredential(site, new Credential(user, pass));
        System.out.println("Credential added for " + site);
    }

    /**
     * Retrieves and displays a credential from the vault.
     */
    private static void retrieveCredential(Scanner scanner, Vault vault) {
        System.out.print("Enter website to retrieve: ");
        String site = scanner.nextLine().trim();
        Credential cred = vault.getCredential(site);
        if (cred != null) {
            System.out.println("Username: " + cred.getUsername());
            System.out.println("Password: " + cred.getPassword());
        } else {
            System.out.println("No credential found for " + site);
        }
    }

    /**
     * Deletes a credential from the vault if present.
     */
    private static void deleteCredential(Scanner scanner, Vault vault) {
        System.out.print("Enter website to delete: ");
        String site = scanner.nextLine().trim();
        Credential cred = vault.getCredential(site);
        if (cred != null) {
            vault.deleteCredential(site);
            System.out.println("Deleted credential for " + site);
        } else {
            System.out.println("No credential stored for " + site);
        }
    }

    /**
     * Displays all credentials sorted by website name.
     */
    private static void displayAllCredentials(Vault vault) {
        Map<String, Credential> sorted = vault.getAllCredentialsSorted();
        if (sorted.isEmpty()) {
            System.out.println("Vault is empty.");
            return;
        }
        System.out.println("Saved credentials:");
        for (Map.Entry<String, Credential> entry : sorted.entrySet()) {
            String site = entry.getKey();
            Credential cred = entry.getValue();
            System.out.println(site + " => " + cred.getUsername() + " / " + cred.getPassword());
        }
    }

    /** Saves the vault to the default file. */
    private static void saveVault(Vault vault) {
        try {
            vault.saveToFile(DATA_FILE);
            System.out.println("Vault saved to " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Error saving vault: " + e.getMessage());
        }
    }

    /** Loads the vault from the default file. */
    private static void loadVault(Vault vault) {
        try {
            vault.loadFromFile(DATA_FILE);
            System.out.println("Vault loaded from " + DATA_FILE);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading vault: " + e.getMessage());
        }
    }

    /** Undoes the last modification to the vault. */
    private static void undoChange(Vault vault) {
        if (vault.undoLastChange()) {
            System.out.println("Last change undone.");
        } else {
            System.out.println("Nothing to undo.");
        }
    }
}
