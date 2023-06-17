package it.polimi.ingsw.Server;

import it.polimi.ingsw.Server.Model.GameRecord;
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

        /*Thread socketServer = new Thread(()->manageServerSocket());
        Thread RMIServer = new Thread(()->manageServerRMI());
        socketServer.start();
        RMIServer.start();*/


    }

    /**
     * FA: persistence. This method checks if the server connection has dropped before
     * @return true if the server has crashed, false otherwise
     */
    private static boolean hasCrashed(){
        File file = new File("src/safetxt/game_0.txt");
        return file.exists() && file.length() != 0;
    }

    /**
     * OVERVIEW: this method allows to manage a connection using socket technology.
     */
    public static void manageServerSocket(){
        ServerSocket socket;

        try{
            socket = new ServerSocket(serverPort);
        }catch (IOException ex){
            System.out.println("Failed to open socket server");
            System.exit(1);
            return;
        }

        while(true){
            try{
                //lo lasciamo synchronized?
                synchronized (lock){
                    Socket client = socket.accept();
                    SocketClientHandler clientHandler = new SocketClientHandler(client, gameRecord);
                    Thread clientHandlerThread = new Thread(clientHandler, "server_" + client.getInetAddress());
                    clientHandlerThread.start();
                }
            }catch (IOException ex){
                System.out.println("Connection with server dropped");
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
            System.out.println("Failed to open RMI server");
            System.exit(1);
        }
/*
        while(true){

            //non penso serva più
            //checking if a new client is connected
            int clientsConnected = numRMIClients;
            do{
                try{
                    clientsConnected = serverInterface.getNumClients();
                    Thread.sleep(50); //siamo sicuri?
                }catch(Exception e){
                    e.printStackTrace();
                }
            }while(clientsConnected<=numRMIClients);

            try{
                if (serverInterface.getNumClients() > numRMIClients) {
                    synchronized (lock) {
                        numRMIClients++;
                    }
                }
            }catch(Exception e){
                System.out.println("Problems connecting new RMI client");
                System.exit(1);
            }
        }*/
    }


}
