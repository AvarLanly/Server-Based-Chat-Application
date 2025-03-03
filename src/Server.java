import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    
    private ServerSocket serverSocket;
    //Serversocket object resposnible for incoming clients
    
    private String ipAddress;
    //IP Address variable for the server
    
    private int port;
    //Port Number variable for the server
   
    

    public Server(String ipAddress, int port) {
        
        this.ipAddress = ipAddress;
        this.port = port;
        
        try {
            this.serverSocket = new ServerSocket();
            this.serverSocket.bind(new InetSocketAddress(ipAddress, port));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public void startServer() {
        //method to keep server running
        try{
            while (!serverSocket.isClosed()){
                //while loop to ensure server runs whilst socket is open
                
                
                Socket socket = serverSocket.accept();
                //object that listens for a connection to be made to socket and accepts it
                System.out.println("A new client has connected");
                //output whenever a client connects
                ClientHandler clientHandler = new ClientHandler(socket);
                //create client handler object to deal with each new client
                
                Thread newThread = new Thread(clientHandler);
                newThread.start();
                //create a new thread for each client that connects.
                
            }
        } catch(IOException e){
          closeServerSocket();
            //call upon method to close server socket;
        }        
    }
    
  
    public void closeServerSocket(){
        // method to simply close server socket
        try{
            
            if (serverSocket != null) {
                // if statement used to ensure there is no NullPointerException
                serverSocket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }   
}


