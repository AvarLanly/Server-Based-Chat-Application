
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LoadServerFrame extends JFrame implements ActionListener {
    
    JPanel panel;
    JLabel adminUserLabel;
    JLabel adminPassLabel;
    JLabel title;
    JTextField adminUserTextField;
    JPasswordField adminPassTextField;
    JButton submit;
    
    String adminUserValue;
    String adminPassValue;
    String hashedAdminPassValue;

    public LoadServerFrame(){
        
        panel = new JPanel();
        panel.setLayout(null);
        
        title = new JLabel("LOAD SERVER");
        title.setBounds(150, 10, 150, 25);
        title.setFont(new Font("Arial", Font.PLAIN, 18));
        panel.add(title);
        
        adminUserLabel = new JLabel("Admin Username:");
        adminUserLabel.setBounds(40, 60, 120, 25);
        panel.add(adminUserLabel);

        adminUserTextField = new JTextField(20);
        adminUserTextField.setBounds(150, 60, 200, 25);
        adminUserTextField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        panel.add(adminUserTextField);

        adminPassLabel = new JLabel("Admin Password:");
        adminPassLabel.setBounds(40, 107, 120, 25);
        panel.add(adminPassLabel);

        adminPassTextField = new JPasswordField(20);
        adminPassTextField.setBounds(150, 107, 200, 25);
        adminPassTextField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        panel.add(adminPassTextField);

        submit = new JButton("Submit");
        submit.setBounds(270, 155, 80, 25);
        submit.addActionListener(this);
        panel.add(submit);
        
        setTitle("Load Server");
        setLocation(new Point(700, 300));
        setSize(new Dimension(450, 300));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(panel);
        this.setResizable(false);
        
        applyTheme();
    }
    
    
    private void applyTheme() {
        if (MainFrame.isDarkTheme) {
            
            panel.setBackground(Color.DARK_GRAY);
            
            adminUserLabel.setForeground(Color.WHITE);
            adminPassLabel.setForeground(Color.WHITE);
            title.setForeground(Color.WHITE);
            
            adminUserTextField.setBackground(Color.BLACK);
            adminUserTextField.setForeground(Color.WHITE);
            
            adminPassTextField.setBackground(Color.BLACK);
            adminPassTextField.setForeground(Color.WHITE);
            
            submit.setBackground(Color.BLACK);
            submit.setForeground(Color.WHITE);
        } 
        else {
            panel.setBackground(Color.LIGHT_GRAY);
            
            adminUserLabel.setForeground(Color.BLACK);
            adminPassLabel.setForeground(Color.BLACK);
            title.setForeground(Color.BLACK);
            
            adminUserTextField.setBackground(Color.WHITE);
            adminUserTextField.setForeground(Color.BLACK);
            
            adminPassTextField.setBackground(Color.WHITE);
            adminPassTextField.setForeground(Color.BLACK);
            
            submit.setBackground(Color.WHITE);
            submit.setForeground(Color.BLACK);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        
        if(ae.getSource() == submit){
            
            adminUserValue = adminUserTextField.getText();
            adminPassValue = adminPassTextField.getText();
            hashedAdminPassValue = hashPassword(adminPassValue);
            
            
            try{
                java.sql.Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/NeaProject", "root", "root");
                // Use the 'connection' object for database operations

                PreparedStatement st = (PreparedStatement) connection.prepareStatement("SELECT Username, Password FROM Accounts WHERE Username=? "
                    + "AND Password=? AND IsAdmin=true");

                st.setString(1, adminUserValue);
                st.setString(2, hashedAdminPassValue);

                ResultSet rs = st.executeQuery();
                
                if(rs.next()){
                    
                    this.dispose();
                    
    
                    String serverIP = "127.0.0.1"; 
                    // server IP address
                    int serverPort = 1234; 
                    // server port
                    
                    Server server = new Server(serverIP, serverPort);
                    
                    server.startServer();
                                      
                }
                else{
                    JOptionPane.showMessageDialog(this, "Incorrect Details Entered");
                }
            }
            catch(SQLException e){
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
    
    
}
