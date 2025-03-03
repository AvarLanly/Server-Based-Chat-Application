import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginFrame extends JFrame implements ActionListener {
    JPanel panel;
    JLabel userLabel;
    JLabel passLabel;
    JLabel title;
    JTextField userTextField;
    JPasswordField passTextField;
    JButton submit;
    
    public static String userValue;
    //allows for username to be used in other classes
    

    public LoginFrame() {
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.LIGHT_GRAY);
        
        title = new JLabel("LOGIN PAGE");
        title.setBounds(140, 20, 120, 25);
        title.setFont(new Font("Arial", Font.PLAIN, 18));
        panel.add(title);
        
        userLabel = new JLabel("Username:");
        userLabel.setBounds(40, 70, 80, 25);
        panel.add(userLabel);

        userTextField = new JTextField(20);
        userTextField.setBounds(110, 70, 200, 25);
        userTextField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        panel.add(userTextField);

        passLabel = new JLabel("Password:");
        passLabel.setBounds(40, 117, 80, 25);
        panel.add(passLabel);

        passTextField = new JPasswordField(20);
        passTextField.setBounds(110, 117, 200, 25);
        passTextField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        panel.add(passTextField);
        
        submit = new JButton("Log In");
        submit.setBounds(143, 172, 107, 25);
        submit.addActionListener(this);
        panel.add(submit);
        
        setTitle("Login Page");
        setLocation(new Point(500, 300));
        setSize(new Dimension(400, 330));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(panel);
        this.setResizable(false);
        
        applyTheme();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae){  
 
        
        if(ae.getSource() == submit){
            userValue = userTextField.getText();        
            //get user entered username from the userTextField  
            String passValue = passTextField.getText(); 
            //get user entered pasword from the passtextField   
            String hashedPassValue = hashPassword(passValue);
            //get hashed version of password for database verification
          
            //check whether the credentials are authentic or not by checking against database 
            try {
                    java.sql.Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/NeaProject", "root", "root");
                    // Use the 'connection' object for database operations
                    
                    PreparedStatement st = (PreparedStatement) connection.prepareStatement("Select Username, "
                            + "Password from Accounts where Username=? and Password =?");
                    
                    st.setString(1, userValue);    
                    st.setString(2, hashedPassValue);  
                    
                    ResultSet rs = st.executeQuery();

                    if (rs.next()) {

                    String serverIP = "127.0.0.1"; 
                    // server IP address
                    int serverPort = 1234; 
                    // server port
                    
                    Client client = new Client(userValue, serverIP, serverPort);
                    client.startClient();
                    client.listenForMessage();

                    // Dispose the login form
                    this.dispose();
                                   
                    } 
                    
                    else {
                        JOptionPane.showMessageDialog(null, "Please enter a valid username and password");
                    }
            } 
            
            catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Server is offline.");
                e.printStackTrace();
                
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
        } 
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    
    
    private void applyTheme() {
        if (MainFrame.isDarkTheme) {
            
            panel.setBackground(Color.DARK_GRAY);
            
            userLabel.setForeground(Color.WHITE);
            passLabel.setForeground(Color.WHITE);
            title.setForeground(Color.WHITE);
            
            userTextField.setBackground(Color.BLACK);
            userTextField.setForeground(Color.WHITE);
            
            passTextField.setBackground(Color.BLACK);
            passTextField.setForeground(Color.WHITE);
            
            submit.setBackground(Color.BLACK);
            submit.setForeground(Color.WHITE);
        } 
        else {
            panel.setBackground(Color.LIGHT_GRAY);
            
            userLabel.setForeground(Color.BLACK);
            passLabel.setForeground(Color.BLACK);
            title.setForeground(Color.BLACK);
            
            userTextField.setBackground(Color.WHITE);
            userTextField.setForeground(Color.BLACK);
            
            passTextField.setBackground(Color.WHITE);
            passTextField.setForeground(Color.BLACK);
            
            submit.setBackground(Color.WHITE);
            submit.setForeground(Color.BLACK);
        }
    }
}
