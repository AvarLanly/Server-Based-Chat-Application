import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class AddAdminAccount {

    public static void main(String[] args) {
        String username = "admin";
        String plainPassword = "NEAProject2023";
        String hashedPassword = hashPassword(plainPassword); // Hash the password

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/NeaProject", "root", "root");

            // Insert the admin account into the database
            String query = "INSERT INTO Accounts (Username, Password, IsAdmin) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setBoolean(3, true); // Admin account

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Admin account added to the database.");
            } else {
                System.out.println("Failed to add the admin account.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hash the password using SHA-256
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}