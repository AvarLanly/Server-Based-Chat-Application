
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import javax.swing.table.TableCellRenderer;


public class Client {
    
    // VARIABLES
    
    private Socket socket;
    //socket object 
    private BufferedReader bufferedReader;
    //buffered reader object that reads messages sent from clients
    private BufferedWriter bufferedWriter;
    //buffered writer object that sends messages to other clients
    static String username;
    //variable to represent username.
    private Connection databaseConnection;
    //connection to database object
    private String serverIP;
    //variable for ip address
    private int serverPort;
    //variable for server port
    
    private JFrame frame;
    //JFrame for the UI
    private JPanel panel;
    //JPanel for the UI
    private  JButton sendMessage;
    //JButton to send the message
    private  JTextArea messageArea;
    //JTextArea which holds all of the texts sent by the users
    private  JTextField messageInput;
    //JTextField where user inputs their message
    private JScrollPane scrollPane;
    //JScrollPane to allow to scroll thru messages
    
   
    // CONSTRUCTOR
    
    public Client(String username, String serverIP, int serverPort) {
        
        this.username = LoginFrame.userValue;
        this.serverIP = serverIP;
        this.serverPort = serverPort;

        try {
            // Initialize the socket and database connection here
            socket = new Socket(serverIP, serverPort);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            databaseConnection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/NeaProject", "root", "root");
        }   catch (IOException | SQLException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
    }
    
    
    
    // START CLIENT METHOD
    
    
    public void startClient() {
        try {
            socket = new Socket(serverIP, serverPort); // Use the provided server IP and port
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            mainPageUI();
            
            serverMessage("SERVER: " + username + " has joined the Chat!");
            
        } catch (IOException e) {
            e.printStackTrace();
            serverMessage("SERVER: " + username + " has left the Chat!");
            
        }
    }
    
    
    
    
    // MAIN PAGE UI
    
    
    private void mainPageUI() {
        frame = new JFrame("Chat Client - Welcome, " + username);
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a panel to hold the components
        panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);
        
        messageArea = new JTextArea();
        messageArea.setBounds(45, 50, 400, 300);
        panel.add(messageArea);
        messageArea.setEditable(false);
        // Set the JTextArea to wrap lines
        messageArea.setLineWrap(true);
        // Set the JTextArea to wrap at word boundaries
        messageArea.setWrapStyleWord(true);
        
        scrollPane = new JScrollPane(messageArea);
        scrollPane.setBounds(45, 50, 400, 300);
        panel.add(scrollPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        sendMessage = new JButton("Send");
        sendMessage.setBounds(375, 450, 70, 40);
        panel.add(sendMessage);
       
        messageInput = new JTextField();
        messageInput.setBounds(45, 450, 340, 40);
        panel.add(messageInput);
        
        applyTheme();
        
        frame.setResizable(false);
        frame.setVisible(true);
        
        ActionListener sendListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = messageInput.getText();
                if (message != null && !message.trim().isEmpty()) {
                    sendMessage(message);
                }
            }
        };
        
        messageInput.addActionListener(sendListener);
        sendMessage.addActionListener(sendListener);
    }
    
    
    // APPLY THEME METHOD
    
    
    private void applyTheme() {
        if (MainFrame.isDarkTheme) {
            frame.setBackground(Color.DARK_GRAY);
            panel.setBackground(Color.DARK_GRAY);
            messageArea.setBackground(Color.BLACK);
            messageArea.setForeground(Color.WHITE);
            sendMessage.setBackground(Color.BLACK);
            sendMessage.setForeground(Color.WHITE);
            messageInput.setBackground(Color.BLACK);
            messageInput.setForeground(Color.WHITE);
        } 
        else {
            frame.setBackground(Color.LIGHT_GRAY);
            panel.setBackground(Color.LIGHT_GRAY);
            messageArea.setBackground(Color.WHITE);
            messageArea.setForeground(Color.BLACK);
            sendMessage.setBackground(Color.WHITE);
            sendMessage.setForeground(Color.BLACK);
            messageInput.setBackground(Color.WHITE);
            messageInput.setForeground(Color.BLACK);
        }
    }


    
    
    // SEND MESSAGE METHOD
    
    
    public void sendMessage(String message) {
        try {
            
            backupMessageToDatabase(username, message);
            
            String profanityWord = containsProfanity(message);
            if (profanityWord != null) {
                recordSaidProfanity(username, profanityWord);
                message = censorProfanity(message);
            }


            bufferedWriter.write(username + ": " + message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            

            // Clear the input field after sending the message
            messageInput.setText("");
        }   
        catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    
    public void serverMessage(String message) {
        try {
            
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            
        }   catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
    }
    
    
    public void displayMessage(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                messageArea.append(message + "\n");
            }
        });
    }   

    
    
    // LISTEN FOR MESSAGE METHOD
    
    
    public void listenForMessage() {
        new Thread(new Runnable() {
        
            @Override
            public void run() {
            
                String messageFromChat;
                while (socket.isConnected()) {
                    try {
                    
                        messageFromChat = bufferedReader.readLine();
                        if (messageFromChat != null) {
                            // Output the received message from other users to the JTextArea 
                            displayMessage(messageFromChat);
                        }
                    }   catch (IOException e) {
                            closeEverything(socket, bufferedReader, bufferedWriter);
                        }
                }
            }
        }).start();
    }
    
    
    
    
    
    // CHECK FOR PROFANITY METHOD
    
    
    public String containsProfanity(String message) {
        try (Connection databaseConnection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/NeaProject", "root", "root")) {
            if (message != null && !message.isEmpty()) {
                String query = "SELECT profanities FROM Profanity WHERE ? LIKE CONCAT('%', profanities, '%')";
            
                try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(query)) {
                    // Set the value of the placeholder in the SQL query to 'message'
                    preparedStatement.setString(1, message);
                
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            String profanity = resultSet.getString("profanities");
                            return profanity; // Return the first profanity found
                        }
                    }
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no profanity is found
    }
    
    

    
     
     
    // CENSOR ANY PROFANITY METHOD
     
     
    public String censorProfanity(String message) {
        
        try (Connection databaseConnection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/NeaProject", "root", "root")) {
            if (message != null && !message.isEmpty()) {
                
                String query = "SELECT profanities FROM Profanity WHERE ? LIKE CONCAT('%', profanities, '%')";
                
                try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(query)) {
                    // Set the value of the placeholder in the SQL query to 'message'
                    preparedStatement.setString(1, message);
                    
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            String profanity = resultSet.getString("profanities");
                            message = message.replaceAll("(?i)" + profanity, "*".repeat(profanity.length()));
                            // (?i) makes the regex case-insensitive
                        }
                    }
                }
            }
        }   catch (SQLException e) {
                e.printStackTrace();
            }
        return message;
    }
    
    
    private void recordSaidProfanity(String username, String profanityWord) {
        
        try (Connection databaseConnection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/NeaProject", "root", "root")) {
            
            int employeeID = getEmployeeIDByUsername(username);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            
            if (employeeID != -1) {
                String query = "INSERT INTO SaidProfanity (EmployeeID, DateTime, Profanities) VALUES (?, ?, ?)";

                try (PreparedStatement st = databaseConnection.prepareStatement(query)) {
                    
                    st.setInt(1, employeeID);
                    st.setTimestamp(2, timestamp);
                    st.setString(3, profanityWord);

                    st.executeUpdate();
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    // BACKUP MESSAGE METHOD
    
    
    private void backupMessageToDatabase(String username, String message) {
        try (Connection databaseConnection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/NeaProject", "root", "root")) {
            String query = "INSERT INTO ChatBackup (DateMessageTime, EmployeeID, Message) VALUES (?, ?, ?)";
        
            try (PreparedStatement st = databaseConnection.prepareStatement(query)) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                int employeeID = getEmployeeIDByUsername(username);

                st.setTimestamp(1, timestamp);
                st.setInt(2, employeeID);
                st.setString(3, message);

                st.executeUpdate();
            }
        }   catch (SQLException e) {
                e.printStackTrace();
            }
    }
    

    // GET EMPLOYEE ID METHOD FOR CHAT BACKUP
    
    
    private int getEmployeeIDByUsername(String username) {
        // For simplicity, I'm assuming a direct query here. Replace with actual SQL.
        int employeeID = -1;
    
        try (Connection databaseConnection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/NeaProject", "root", "root")) {
            String query = "SELECT EmployeeID FROM Accounts WHERE Username = ?";
        
            try (PreparedStatement st = databaseConnection.prepareStatement(query)) {
                st.setString(1, username);
            
                try (ResultSet resultSet = st.executeQuery()) {
                    if (resultSet.next()) {
                        employeeID = resultSet.getInt("EmployeeID");
                    }
                }
            }
        }   catch (SQLException e) {
                e.printStackTrace();
            }
    
        return employeeID;
    }
    
    // CLOSE EVERYTHING METHOD
    
    
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }           
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}


