import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ClientHandler implements Runnable {
    //allows for each instantation of ClientHandler to run in a separate thread,
    //allowing for multiple users to simultaneously use the application.
    
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    //array list to keep track of every client that is online simultaneously.
    
    private Socket socket;
    //socket object

    private BufferedReader bufferedReader;
    //buffered reader object that reads messages sent from clients    
    private BufferedWriter bufferedWriter;
    //buffered writer object that sends messages to other clients
    
    public ClientHandler(Socket socket) {
        //client handler constructor with arguments
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            synchronized (clientHandlers) {
                clientHandlers.add(this);
            }
        } catch (IOException e) {
            closeEverything();
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        //string variable that is the message sent from a client

        while (socket.isConnected()) {
        //while loop to ensure clientHandler only runs when connected
            try {
                messageFromClient = bufferedReader.readLine();
                //listening for messages by reading from buffered reader
                
                    broadcastMessage(messageFromClient);
  
            } catch (IOException e) {
                closeEverything();
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        
        synchronized (clientHandlers) {
            Iterator<ClientHandler> iterator = clientHandlers.iterator();
            while (iterator.hasNext()) {
                ClientHandler clientHandler = iterator.next();
                try {
                    
                        clientHandler.bufferedWriter.write(messageToSend);
                        //writes the message to everyone else
                        clientHandler.bufferedWriter.newLine();
                        //used to separate the next line as a new line, essentially acting as an enter key
                        clientHandler.bufferedWriter.flush();
                        //messages wont likely be big enough to fill buffer so a force flush is needed 

                    
                } catch (IOException e) {
                    iterator.remove();
                    // Safely remove the client
                    clientHandler.closeEverything();
                }
            }
        }
    }

    public void closeEverything() {
        //remove a client from the clienthandler array list
        synchronized (clientHandlers) {
            clientHandlers.remove(this);
        }
        try {
            //if statements are used to ensure there is no NullPointerException
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}

