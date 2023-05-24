package it.polimi.ingsw.Server;

import it.polimi.ingsw.Server.Model.MyShelfie;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;


/**
 * Server class: this class represents the server of the game.
 *
 * @author Andrea Giacalone
 */
public class Server {
    public static int serverPort = 9999;
    //private static MyShelfie game;
    private static ArrayList<MyShelfie> games;
    private static int currentGame;
    private static int numRMIClients;
    private static Object lock;
    private static RemoteInterface serverInterface;
    private static Registry registry;


    /**
     * OVERVIEW: the constructor of the class which initializes the server with the its port open to receive
     *           new possible connections with the clients.
     */
    public Server(int serverPort){
        serverPort = 9999;
        currentGame = -1;
        games = new ArrayList<>();
        lock = new Object();
        numRMIClients = 0;
    }

    /**
     * OVERVIEW: the main method of the server. It allows to enstablish connection with multiple connections adopting two
     *           possible tecnologies: the Socket and the RMI one.
     * @param args
     */
    public static void main(String[] args) {
        //Server server = new Server(serverPort);
        currentGame = -1;
        games = new ArrayList<>();
        lock = new Object();
        numRMIClients = 0;
        System.out.println("Server is open: listening for new clients...");
        Thread socketServer = new Thread(()->manageServerSocket());
        Thread RMIServer = new Thread(()->manageServerRMI());
        socketServer.start();
        RMIServer.start();
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
                Socket client =socket.accept();
                // gestione partite multiple
                synchronized (lock){
                    if (currentGame == -1 || games.get(currentGame).getAllPlayersReady()) {
                        MyShelfie game = new MyShelfie();
                        games.add(game);
                        currentGame++;
                    }
                    SocketClientHandler clientHandler = new SocketClientHandler(client, games.get(currentGame));
                    Thread clientHandlerThread = new Thread(clientHandler, "server_" + client.getInetAddress());
                    clientHandlerThread.start();
                }
            }catch (IOException ex){
                System.out.println("Connection with server dropped");
            }
        }
    }

    //perché è tutto static?
    public static void manageServerRMI(){
        try{
            serverInterface = new RMIAdapter();
            registry = LocateRegistry.createRegistry(1099);
            registry.rebind("server", serverInterface);
        }catch(Exception e){
            System.out.println("Failed to open RMI server");
            System.exit(1);
            return;
        }

        while(true){

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

            //serve questo if? c'è il do while
            //o proviamo a togliere il do while? tanto ci sono sleep e while(true)
            try{
                if (serverInterface.getNumClients() > numRMIClients) {
                    synchronized (lock) {
                        if (currentGame == -1 || games.get(currentGame).getAllPlayersReady()) {
                            MyShelfie game = new MyShelfie();
                            games.add(game);
                            currentGame++;
                            serverInterface.setController(game);
                        } else
                            serverInterface.setController(games.get(currentGame));
                        numRMIClients++;
                        //server.setMapClientsToController(games.get(currentGame));

                    }
                }
            }catch(Exception e){
                System.out.println("Problems connecting new RMI client");
            }
        }
    }


}
