import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.TableCellRenderer;


public class MessageLogFrame extends JFrame {
    
    JPanel panel;
    private JTable messageTable;
    private JComboBox<String> userFilterComboBox;

    private Connection connection;
    private DefaultTableModel tableModel;

    public MessageLogFrame() {
        
        // Connect to the database
        connectToDatabase();
        
        // Initialize the frame
        setTitle("Chat Backup Log");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a panel to hold components
        panel = new JPanel(new BorderLayout());

        // Create a ComboBox for user filtering
        userFilterComboBox = new JComboBox<>();
        userFilterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUser = userFilterComboBox.getSelectedItem().toString();
                updateTableWithFilteredMessages(selectedUser);
            }
        });

        // Add the ComboBox to the North area of the panel
        panel.add(userFilterComboBox, BorderLayout.NORTH);

        // Create a table to display messages
        tableModel = new DefaultTableModel(new Object[]{"Date/Time", "Username", "Message"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        messageTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(messageTable);
        messageTable.getColumnModel().getColumn(2).setCellRenderer(new MultiLineTableCellRenderer());


        // Add the table (with scroll pane) to the Center area of the panel
        panel.add(scrollPane, BorderLayout.CENTER);
        applyTheme();

        // Add the panel to the frame
        add(panel);

        // Load users into the filter ComboBox
        loadUsers();
    }


    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://127.0.0.1:3306/NeaProject";
            String username = "root";
            String password = "root";
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    

    private void loadUsers() {
        userFilterComboBox.addItem("All Users"); 
   
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT Username FROM Accounts");
            while (resultSet.next()) {
                userFilterComboBox.addItem(resultSet.getString("Username"));
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    private void updateTableWithAllMessages() {
        try {
            tableModel.setRowCount(0);
        
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT C.DateMessageTime, A.Username, C.Message " +
                    "FROM ChatBackup C " +
                    "JOIN Accounts A ON C.EmployeeID = A.EmployeeID " +
                    "ORDER BY C.DateMessageTime ASC");
            while (resultSet.next()) {
                String dateTime = resultSet.getTimestamp("DateMessageTime").toString();
                String username = resultSet.getString("Username");
                String message = resultSet.getString("Message");
                tableModel.addRow(new Object[]{dateTime, username, message});
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void updateTableWithFilteredMessages(String selectedUser) {
        if (selectedUser.equals("All Users")) {
            updateTableWithAllMessages();
        } 
        else {
        // Query messages for the selected user and update the table
            tableModel.setRowCount(0); // Clear existing data
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT C.DateMessageTime, A.Username, C.Message " +
                        "FROM ChatBackup C " +
                        "JOIN Accounts A ON C.EmployeeID = A.EmployeeID " +
                        "WHERE A.Username = '" + selectedUser + "'");
                while (resultSet.next()) {
                    String dateTime = resultSet.getTimestamp("DateMessageTime").toString();
                    String username = resultSet.getString("Username");
                    String message = resultSet.getString("Message");
                    tableModel.addRow(new Object[]{dateTime, username, message});
                }
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }   
    }

    
    private void applyTheme() {
        if (MainFrame.isDarkTheme) {
     
            panel.setBackground(Color.DARK_GRAY);
            userFilterComboBox.setForeground(Color.WHITE);
            userFilterComboBox.setBackground(Color.BLACK);
            messageTable.setBackground(Color.BLACK);
            messageTable.setForeground(Color.WHITE);
            
        } 
        else {
            
            panel.setBackground(Color.LIGHT_GRAY);      
            userFilterComboBox.setBackground(Color.WHITE);
            userFilterComboBox.setForeground(Color.BLACK);
            messageTable.setBackground(Color.WHITE);
            messageTable.setForeground(Color.BLACK);
            
        }
    }
}



// Custom cell renderer allowing text wrapping in a JTable cell
class MultiLineTableCellRenderer extends JTextArea implements TableCellRenderer {
    MultiLineTableCellRenderer() {
        setLineWrap(true); // Enable text wrapping
        setWrapStyleWord(true); // Wrap at word boundaries
        setOpaque(true); // Make sure the background is painted
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // Add padding if needed
        setFont(UIManager.getFont("Table.font")); // Use the table's default font
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        // Set text color and background based on selection
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        // Set the text content in the cell
        setText((value == null) ? "" : value.toString());

        // Adjust row height to accommodate the wrapped text
        adjustRowHeight(table, row, column);

        return this;
    }

    
    // Adjust row height to fit the wrapped text
    private void adjustRowHeight(JTable table, int row, int column) {
        // Get the width of the column
        int columnWidth = table.getTableHeader().getColumnModel().getColumn(column).getWidth();

        // Set a temporary large height (arbitrary value) for text area to get preferred height
        setSize(new Dimension(columnWidth, 1000));
        
        // Get preferred height after setting the temporary size
        int preferredHeight = getPreferredSize().height;
        
        // If the row height is different from the preferred height, set the row height
        if (table.getRowHeight(row) != preferredHeight) {
            table.setRowHeight(row, preferredHeight);
        }
    }
}


