package it.polimi.ingsw.Server;

import it.polimi.ingsw.Server.Model.MyShelfie;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static it.polimi.ingsw.Server.Model.MyShelfie.*;


/**
 * Server class: this class represents the server of the game.
 *
 * @author Andrea Giacalone
 */
public class Server {
    public static int serverPort;
    private static MyShelfie game;

    /**
     * OVERVIEW: the constructor of the class which initializes the server with the its port open to receive
     *           new possible connections with the clients.
     * @param serverPort: the port chosen for the connection.
     */
    public Server(int serverPort){
        this.serverPort = 9999;
    }

    /**
     * OVERVIEW: the main method of the server. It allows to enstablish connection with multiple connections adopting two
     *           possible tecnologies: the Socket and the RMI one.
     * @param args
     */
    public static void main(String[] args) {
        Server server = new Server(serverPort);
        //qui metteremo due scelte a seconda che venga selezionata la tecnologia di comunicazione RMI o socket
        manageServerSocket();
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
                // gestione partite multiple

                game = getMyShelfie();
                ClientHandler clientHandler = new SocketClientHandler(client, game);

                Thread clientHandlerThread = new Thread(clientHandler,"server_"+client.getInetAddress());
                clientHandlerThread.start();
            }catch (IOException ex){
                System.out.println("Connection with server dropped");
            }
        }
    }


}
