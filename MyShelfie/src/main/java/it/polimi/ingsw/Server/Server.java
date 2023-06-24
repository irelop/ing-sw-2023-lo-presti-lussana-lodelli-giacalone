package it.polimi.ingsw.Server;

import it.polimi.ingsw.Server.controller.GameRecord;
import it.polimi.ingsw.utils.rmi.RMIAdapter;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/**
 * Server class: this class represents the server of the game.
 *
 * @author Andrea Giacalone, Irene Lo Presti
 */
public class Server {
    public static int serverPort = 9999;
    private static int numRMIClients;
    private final static Object lock = new Object();
    private static RemoteInterface serverInterface;
    private static Registry registry;

    private static GameRecord gameRecord;

    /**
     * OVERVIEW: the main method of the server. It allows to establish connection with multiple connections adopting two
     *           possible technologies: the Socket and the RMI one.
     */
    public static void main(String[] args) {
        numRMIClients = 0;
        gameRecord = new GameRecord();

        //FA: persistence
        if(hasCrashed()){
            gameRecord.reset();
        }
        manageServerRMI();
        System.out.println("Server is open: listening for new clients...");
        manageServerSocket();

    }

    /**
     * FA: persistence. This method checks if the server connection has dropped before
     * @return true if the server has crashed, false otherwise
     */
    private static boolean hasCrashed(){
        String[] files = new File("src/safetxt/games").list();
        if(files == null)
            return false;
        else return files.length > 0;
    }

    /**
     * OVERVIEW: this method allows to manage a connection using socket technology.
     */
    public static void manageServerSocket(){
        ServerSocket socket;

        try{
            socket = new ServerSocket(serverPort);
        }catch (IOException ex){
            System.out.println("[SKT] Error: failed to open socket server");
            System.exit(1);
            return;
        }

        while(true){
            try{
                synchronized (lock){
                    Socket client = socket.accept();
                    SocketClientHandler clientHandler = new SocketClientHandler(client, gameRecord);
                    Thread clientHandlerThread = new Thread(clientHandler, "server_" + client.getInetAddress());
                    clientHandlerThread.start();
                }
            }catch (IOException ex){
                System.out.println("[SKT] Error: connection with server dropped");
            }
        }
    }


    /**
     * OVERVIEW: this method allows to manage a connection using RMI technology.
     */
    public static void manageServerRMI(){
        try{
            serverInterface = new RMIAdapter();
            registry = LocateRegistry.createRegistry(1099);
            registry.rebind("server", serverInterface);

            serverInterface.setGameRecord(gameRecord);
            gameRecord.setRemoteServer(serverInterface);
        }catch(Exception e){
            System.out.println("[RMI] Error: failed to open RMI server");
            System.exit(1);
        }

    }


}
