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
 * @author Andrea Giacalone
 */
public class Server {
    public static int serverPort = 9999;
    //private static MyShelfie game;
    private static int numRMIClients;
    private static Object lock;
    private static RemoteInterface serverInterface;
    private static Registry registry;

    private static GameRecord gameRecord;

    /**
     * OVERVIEW: the main method of the server. It allows to establish connection with multiple connections adopting two
     *           possible technologies: the Socket and the RMI one.
     * @param args
     */
    public static void main(String[] args) {
        lock = new Object();
        numRMIClients = 0;
        gameRecord = new GameRecord();

        //FA: persistence
        if(hasCrashed()){
            gameRecord.reset();
        }

        System.out.println("Server is open: listening for new clients...");
        Thread socketServer = new Thread(()->manageServerSocket());
        Thread RMIServer = new Thread(()->manageServerRMI());
        socketServer.start();
        RMIServer.start();

    }

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
                Socket client =socket.accept();
                // gestione partite multiple
                synchronized (lock){
                    /*
                    if (currentGame == -1 || games.get(currentGame).getAllPlayersReady()) {
                        MyShelfie game = new MyShelfie();
                        games.add(game);
                        currentGame++;
                    }
                     */
                    //SocketClientHandler clientHandler = new SocketClientHandler(client, games.get(currentGame));
                    /*MyShelfie game = gameRecord.getGame();
                    System.out.println(game);
                    SocketClientHandler clientHandler = new SocketClientHandler(client, game);*/

                    SocketClientHandler clientHandler = new SocketClientHandler(client, gameRecord);
                    Thread clientHandlerThread = new Thread(clientHandler, "server_" + client.getInetAddress());
                    clientHandlerThread.start();
                }
            }catch (IOException ex){
                System.out.println("Connection with server dropped");
            }
        }
    }



    public static void manageServerRMI(){
        try{
            serverInterface = new RMIAdapter();
            registry = LocateRegistry.createRegistry(1099);
            registry.rebind("server", serverInterface);

            serverInterface.setGameRecord(gameRecord);
        }catch(Exception e){
            System.out.println("Failed to open RMI server");
            System.exit(1);
            return;
        }

        gameRecord.setRemoteServer(serverInterface);


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
                        /*
                        if (currentGame == -1 || games.get(currentGame).getAllPlayersReady()) {
                            MyShelfie game = new MyShelfie();
                            games.add(game);
                            currentGame++;
                            serverInterface.addController(game);
                        } else
                            serverInterface.addController(games.get(currentGame));
                         */
                        numRMIClients++;
                        //serverInterface.setMapClientsToController(games.get(currentGame), numRMIClients-1);

                        //serverInterface.setMapClientsToController(gameRecord.getGame(), numRMIClients-1);
                    }
                }
            }catch(Exception e){
                System.out.println("Problems connecting new RMI client");
                System.exit(1);
            }
        }
    }


}
