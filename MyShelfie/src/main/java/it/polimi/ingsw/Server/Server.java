package it.polimi.ingsw.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server class: this class represents the server of the game.
 *
 * @author Andrea Giacalone
 */
public class Server {
    private final static int serverPort;

    /**
     * OVERVIEW: the constructor of the class which initializes the server with the its port open to receive
     *           new possible connections with the clients.
     * @param serverPort: the port chosen for the connection.
     */
    public Server(int serverPort){
        this.serverPort = serverPort;
    }

    /**
     * OVERVIEW: the main method of the server. It allows to enstablish connection with multiple connections adopting two
     *           possible tecnologies: the Socket and the RMI one.
     * @param args
     */
    public static void main(String[] args) {
        Server server = new Server(serverPort);
        //qui metteremo due scelte a seconda che venga selezionata la tecnologia di comunicazione RMI o socket
        this.manageServerSocket();
        //else manageServerRMI();

    }

    /**
     * OVERVIEW: this method allows to manage a connection using socket technology.
     */
    public static void manageServerSocket(){
        ServerSocket socket;

        try{
            socket = new ServerSocket(serverPort);
        }catch (IOException ex){
            System.out.println("Failed to open server socket");
            System.exit(1);
            return;
        }

        while(true){
            try{
                Socket client =socket.accept();
                ClientHandler clientHandler = new ClientHandler(client);

                Thread clientHandlerThread = new Thread(clientHandler,"server_"+clientHandler.getInetAddress());
                clientHandlerThread.start();
            }catch (IOException ex){
                System.out.println("Connection with server dropped");
            }
        }
    }


}
