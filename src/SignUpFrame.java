

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class SignUpFrame extends JFrame implements ActionListener {
    
    JFrame frame;
    JPanel panel;
    JLabel userLabel;
    JLabel passLabel;
    JLabel title;
    JTextField signUserTextField;
    JPasswordField signPassTextField;
    JButton submit;
    JCheckBox adminCheckBox;
    
    
    JPanel panel2;
    JLabel adminUserLabel;
    JLabel adminPassLabel;
    JLabel title2;
    JTextField adminUserTextField;
    JPasswordField adminPassTextField;
    JButton submit2;
    
    String adminUserValue;
    String adminPassValue;
    String hashedAdminPassValue;
    
    static String signUserValue;
    static String signPassValue;
    static String hashedSignPassValue;
    
    static boolean verified = false;
    
    public SignUpFrame(){
        
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.LIGHT_GRAY);
        
        title = new JLabel("SIGN UP PAGE");
        title.setBounds(130, 20, 150, 25);
        title.setFont(new Font("Arial", Font.PLAIN, 18));
        panel.add(title);
        
        userLabel = new JLabel("Username:");
        userLabel.setBounds(40, 70, 80, 25);
        panel.add(userLabel);

        signUserTextField = new JTextField(20);
        signUserTextField.setBounds(110, 70, 200, 25);
        signUserTextField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        panel.add(signUserTextField);

        passLabel = new JLabel("Password:");
        passLabel.setBounds(40, 117, 80, 25);
        panel.add(passLabel);

        signPassTextField = new JPasswordField(20);
        signPassTextField.setBounds(110, 117, 200, 25);
        signPassTextField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        panel.add(signPassTextField);
        
        adminCheckBox = new JCheckBox("Admin Account");
        adminCheckBox.setBounds(134, 170, 117, 25);
        panel.add(adminCheckBox);
        
        submit = new JButton("Sign Up");
        submit.setBounds(134, 210, 117, 25);
        submit.addActionListener(this);
        panel.add(submit);
        
        setTitle("Sign Up Page");
        setLocation(new Point(500, 300));
        setSize(new Dimension(400, 330));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(panel);
        this.setResizable(false);
        
        // Initialize the admin verification frame
        frame = new JFrame("Admin Verification");
        frame.setSize(400, 330);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel2 = new JPanel();
        panel2.setLayout(null);

        title2 = new JLabel("ADMIN VERIFICATION");
        title2.setBounds(100, 10, 200, 25);
        title2.setFont(new Font("Arial", Font.PLAIN, 18));
        panel2.add(title2);

        adminUserLabel = new JLabel("Admin Username:");
        adminUserLabel.setBounds(40, 60, 130, 25);
        panel2.add(adminUserLabel);

        adminUserTextField = new JTextField(20);
        adminUserTextField.setBounds(180, 60, 150, 25);
        adminUserTextField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        panel2.add(adminUserTextField);

        adminPassLabel = new JLabel("Admin Password:");
        adminPassLabel.setBounds(40, 107, 130, 25);
        panel2.add(adminPassLabel);

        adminPassTextField = new JPasswordField(20);
        adminPassTextField.setBounds(180, 107, 150, 25);
        adminPassTextField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        panel2.add(adminPassTextField);

        submit2 = new JButton("Submit");
        submit2.setBounds(180, 155, 80, 25);
        submit2.addActionListener(this);
        panel2.add(submit2);
    
        applyTheme();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        
        if(ae.getSource() == submit){
            signUserValue = signUserTextField.getText();        
            //get user entered username from the userTextField  
            signPassValue = signPassTextField.getText(); 
            //get user entered pasword from the passTextField
            hashedSignPassValue = hashPassword(signPassValue);
            //get hashed version of user entered password
            boolean isAdmin = adminCheckBox.isSelected();
            
            if (signUserValue.isEmpty() || signUserValue.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid username.");
            }
            else{
                if (isAdmin) {
                    // Display a confirmation dialog
                    int choice = JOptionPane.showConfirmDialog(this, "Admin Login required for Admin Registration. "
                            + "Continue?", "Admin Confirmation", JOptionPane.YES_NO_OPTION);

                    if (choice == JOptionPane.YES_OPTION) {
                        // User chose to continue with admin registration
                        // run admin verification method
                        frame.add(panel2);
                        frame.setVisible(true);
                    }
                    else {
                        boolean isUsernameUnique = checkUsernameUniqueness(signUserValue);
                        if (isUsernameUnique) {
                            registerUser(signUserValue, hashedSignPassValue, !isAdmin);
                            JOptionPane.showMessageDialog(this, "Successful Sign Up (Non-Admin Account).");
                            this.dispose();
                        } 
                        else {
                        JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different username.");
                        }
                    }
                }
                else {
                    // Check username uniqueness before registering as a user
                    boolean isUsernameUnique = checkUsernameUniqueness(signUserValue);
                    if (isUsernameUnique) {
                        registerUser(signUserValue, hashedSignPassValue, false);
                        JOptionPane.showMessageDialog(this, "Successful Sign Up (Non-Admin Account).");
                        // Close the frame or show a success message here
                        this.dispose();
                    } 
                    else {
                        JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different username.");
                    }
               }
            }
        }
        
        else if (ae.getSource() == submit2) {
            // This block runs when the submit2 button is clicked
            adminUserValue = adminUserTextField.getText();
            adminPassValue = adminPassTextField.getText();
            hashedAdminPassValue = hashPassword(adminPassValue);

            try {
                java.sql.Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/NeaProject", "root", "root");

                PreparedStatement st = (PreparedStatement) connection.prepareStatement("SELECT Username, Password FROM Accounts WHERE Username=? "
                    + "AND Password=? AND IsAdmin=true");

                st.setString(1, adminUserValue);
                st.setString(2, hashedAdminPassValue);

                ResultSet rs = st.executeQuery();

                if (rs.next()) {
                    verified = true;
                    // If admin credentials are verified, register user as an admin
                    registerUser(signUserValue, hashedSignPassValue, true);
                    
                    frame.dispose();
                    this.dispose();
                    JOptionPane.showMessageDialog(this, "Successful Sign Up (Admin Account) .");
                    
                }   
                else {
                    JOptionPane.showMessageDialog(null, "Please enter a valid admin username and password");
                }
            } 
            catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());

            // Convert the byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void registerUser(String username, String hashedPassword, boolean isAdmin) {
        try {
            java.sql.Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/NeaProject", "root", "root");
            PreparedStatement st = connection.prepareStatement("INSERT INTO Accounts (Username, Password, IsAdmin) VALUES (?, ?, ?)");
            st.setString(1, username);
            st.setString(2, hashedPassword);
            st.setBoolean(3, isAdmin);
            st.executeUpdate();
            // Close the frame or show a success message here
        } 
        catch (SQLException e) {
            e.printStackTrace();
            // Handle the registration error, e.g., show an error message
        }
    }
    
    
    private void applyTheme() {
        if (MainFrame.isDarkTheme) {
            
            panel.setBackground(Color.DARK_GRAY); 
            userLabel.setForeground(Color.WHITE);
            passLabel.setForeground(Color.WHITE);
            title.setForeground(Color.WHITE);
            
            signUserTextField.setBackground(Color.BLACK);
            signUserTextField.setForeground(Color.WHITE);
            signPassTextField.setBackground(Color.BLACK);
            signPassTextField.setForeground(Color.WHITE);
            submit.setBackground(Color.BLACK);
            submit.setForeground(Color.WHITE);
            
            panel2.setBackground(Color.DARK_GRAY); 
            adminUserLabel.setForeground(Color.WHITE);
            adminPassLabel.setForeground(Color.WHITE);
            title2.setForeground(Color.WHITE);
            
            adminUserTextField.setBackground(Color.BLACK);
            adminUserTextField.setForeground(Color.WHITE);
            adminPassTextField.setBackground(Color.BLACK);
            adminPassTextField.setForeground(Color.WHITE);
            submit2.setBackground(Color.BLACK);
            submit2.setForeground(Color.WHITE);
            
        } 
        else {
            panel.setBackground(Color.LIGHT_GRAY);
            userLabel.setForeground(Color.BLACK);
            passLabel.setForeground(Color.BLACK);
            title.setForeground(Color.BLACK);
            
            signPassTextField.setBackground(Color.WHITE);
            signPassTextField.setForeground(Color.BLACK);
            signPassTextField.setBackground(Color.WHITE);
            signPassTextField.setForeground(Color.BLACK);
            submit.setBackground(Color.WHITE);
            submit.setForeground(Color.BLACK);
            
            panel2.setBackground(Color.LIGHT_GRAY);
            adminUserLabel.setForeground(Color.BLACK);
            adminPassLabel.setForeground(Color.BLACK);
            title2.setForeground(Color.BLACK);
            
            adminPassTextField.setBackground(Color.WHITE);
            adminPassTextField.setForeground(Color.BLACK);
            adminPassTextField.setBackground(Color.WHITE);
            adminPassTextField.setForeground(Color.BLACK);
            submit2.setBackground(Color.WHITE);
            submit2.setForeground(Color.BLACK);
        }
    }
    
    private boolean checkUsernameUniqueness(String username) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/NeaProject", "root", "root");
            PreparedStatement st = connection.prepareStatement("SELECT COUNT(*) FROM Accounts WHERE Username=?");
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count == 0; // Username is unique if count is 0
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false; 
        // An error occurred, so assume the username is not unique
    }
}    