
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainFrame extends JFrame implements ActionListener {
    
    JPanel panel;
    JLabel title;
    JButton logIN;
    JButton signUP;
    JButton loadServer;
    JButton changeTheme;
    JButton messageLog;
    
    public static boolean isDarkTheme = false; 
    // Initial theme is light
    
    
    public MainFrame(){
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.LIGHT_GRAY);
   
        title = new JLabel("HOME PAGE");
        title.setBounds(170, 10, 120, 25);
        title.setForeground(Color.BLACK);
        title.setFont(new Font("Arial", Font.PLAIN, 18));
        panel.add(title);
         
        changeTheme = new JButton("Change Theme");
        changeTheme.setBounds(165, 60, 120, 25);        
        changeTheme.addActionListener(this);
        changeTheme.setBackground(Color.WHITE);
        changeTheme.setForeground(Color.BLACK);
        panel.add(changeTheme);
        
        logIN = new JButton("Log In");
        logIN.setBounds(100, 130, 110, 25); 
        logIN.addActionListener(this);
        logIN.setBackground(Color.WHITE);
        logIN.setForeground(Color.BLACK);
        panel.add(logIN);
        
        signUP = new JButton("Sign Up");
        signUP.setBounds(100, 180, 110, 25);
        signUP.addActionListener(this);
        signUP.setBackground(Color.WHITE);
        signUP.setForeground(Color.BLACK);
        panel.add(signUP);
        
        loadServer = new JButton("Load Server");
        loadServer.setBounds(220, 130, 120, 25);
        loadServer.addActionListener(this);
        loadServer.setBackground(Color.WHITE);
        loadServer.setForeground(Color.BLACK);
        panel.add(loadServer);
 
        messageLog = new JButton("Message Log");
        messageLog.setBounds(220, 180, 120, 25);
        messageLog.addActionListener(this);
        messageLog.setBackground(Color.WHITE);
        messageLog.setForeground(Color.BLACK);
        panel.add(messageLog);
                
        setTitle("Home Page");
        setLocation(new Point(700, 300));
        setSize(new Dimension(450, 300));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(panel);
        this.setResizable(false);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource() == logIN){
            LoginFrame logINPage = new LoginFrame();
            logINPage.setVisible(true);
            this.dispose();
        }
        
        else if (ae.getSource() == signUP){
            SignUpFrame signUPPage = new SignUpFrame();
            signUPPage.setVisible(true);
            this.dispose();
        }
        
        else if (ae.getSource() == loadServer){
            LoadServerFrame loadServerPage = new LoadServerFrame();
            loadServerPage.setVisible(true);
            this.dispose();
        }
        
        else if(ae.getSource() == messageLog){
            MessageLogFrame messageLogPage = new MessageLogFrame();
            messageLogPage.setVisible(true);
            this.dispose();
        }
        
        else if (ae.getSource() == changeTheme){
            toggleTheme();
        }
            
    }
    
    private void toggleTheme() {
        isDarkTheme = !isDarkTheme;

        if (isDarkTheme) {
            panel.setBackground(Color.DARK_GRAY);

            title.setForeground(Color.WHITE);
            
            changeTheme.setBackground(Color.BLACK);
            changeTheme.setForeground(Color.WHITE);
            logIN.setBackground(Color.BLACK);
            logIN.setForeground(Color.WHITE);
            signUP.setBackground(Color.BLACK);
            signUP.setForeground(Color.WHITE);    
            loadServer.setBackground(Color.BLACK);
            loadServer.setForeground(Color.WHITE);  
            messageLog.setBackground(Color.BLACK);
            messageLog.setForeground(Color.WHITE);
        } else {
            panel.setBackground(Color.LIGHT_GRAY);
            
            title.setForeground(Color.BLACK);
            
            changeTheme.setBackground(Color.WHITE);
            changeTheme.setForeground(Color.BLACK);
            logIN.setBackground(Color.WHITE);
            logIN.setForeground(Color.BLACK);
            signUP.setBackground(Color.WHITE);
            signUP.setForeground(Color.BLACK);    
            loadServer.setBackground(Color.WHITE);
            loadServer.setForeground(Color.BLACK);
            messageLog.setBackground(Color.WHITE);
            messageLog.setForeground(Color.BLACK);
        }
        repaint();
    }
    
    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setVisible(true);
    }
}
