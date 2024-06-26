package it.polimi.ingsw.Client;

import it.polimi.ingsw.Client.view.GUI.JavaGUI;
import it.polimi.ingsw.Client.view.GUI.StageManager;
import it.polimi.ingsw.utils.Exceptions.InvalidGUIChoiceException;
import it.polimi.ingsw.utils.Exceptions.InvalidNetworkChoiceException;
import it.polimi.ingsw.Client.view.CLI.LoginView;
import it.polimi.ingsw.Client.view.CLI.View;
import it.polimi.ingsw.Client.view.CLI.WaitingView;
import it.polimi.ingsw.utils.rmi.RMIAdapter;
import it.polimi.ingsw.utils.rmi.RemoteInterface;
import javafx.application.Application;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * Client class: this class represents the client in the network protocol. It wraps all the references useful in order
 *               to communicate with a server in a Socket or RMI communication protocol.
 */
public class Client implements Runnable{
    private String nickname;        //nickname chosen by the user once connected
    private ServerHandler serverHandler;        //network handler client side
    private boolean terminate = false;      //in order to stop the view state machine

    private View currentView;       //view currently visualized in the client side
    private View nextView;      //the following view after the transition
    private boolean isRMI;      //checks if a client chooses the RMI mode
    private RemoteInterface client;

    public boolean gui;

    private StageManager stageManager;

    public JavaGUI mainApp;

    public static void main(String[] args){
        Client client = new Client();
        client.run();
    }

    @Override
    public void run(){
        askNetworkChoice();

        System.out.println("do you want the GUI? Type (Y) if yes, otherwise type (N)");

        Scanner input = new Scanner(System.in);
        String inputString;
        char GUIchoice;
        while (true) {
            try {
                inputString = input.next().toUpperCase();
                if (inputString.length() > 1)
                    throw new InvalidGUIChoiceException();
                GUIchoice = inputString.charAt(0);
                if (GUIchoice == 'Y' || GUIchoice == 'N')
                    break;
                else
                    throw new InvalidGUIChoiceException();
            } catch (InvalidGUIChoiceException e) {
                System.out.println(e);
            }
        }
        // checks input
        this.gui = (GUIchoice == 'Y');
        if(!gui) {
            nextView = new LoginView();
            runViewStateMachine();
        }
        else{
            stageManager = StageManager.getInstance();
            stageManager.setOwner(this);
            mainApp = new JavaGUI();
            Application.launch(JavaGUI.class);
        }      //allows to run the state machine

        serverHandler.stop();
        System.exit(0);
    }

    /**
     * OVERVIEW: this method allows to get the correct user choice between RMI or Socket and check for possible typos
     *           from the user.
     * @return the digit choice of the user.
     * @throws InvalidNetworkChoiceException: exception thrown when the digit choice isn't valid.
     * @author Andrea Giacalone, Irene Lo Presti
     */
    private char getNetworkChoice() throws InvalidNetworkChoiceException {
        Scanner input = new Scanner(System.in);
        System.out.println("Insert 'R' for RMI connection or 'S' for Socket connection");
        String answer = input.next().toUpperCase();

        if(answer.length()>1) throw new InvalidNetworkChoiceException();

        char networkChoice = answer.charAt(0);
        if(networkChoice != 'R' && networkChoice != 'S') throw new InvalidNetworkChoiceException();
        else return networkChoice;
    }

    /**
     * OVERVIEW: this method allows to manage the request for the connection mode and to trigger the proper methods in
     *           order to handle the connection following the chosen protocol.
     * @author Andrea Giacalone, Irene Lo Presti
     */
    private void askNetworkChoice(){
        char networkChoice;
        do{
            try {
                networkChoice = getNetworkChoice();
                break;
            } catch (InvalidNetworkChoiceException e) {
                System.out.println(e);
            }
        }while(true);
        if(networkChoice=='R') {
            this.isRMI = true;
            manageRMIConnection();
        }
        else {
            this.isRMI = false;
            manageSocketConnection();
        }
    }


    //- - - - - - - - - - - - - - - - - V I E W   M A N A G E M E N T   M E T H O D S  - - - - - - - - - - - - - - - - - - -

    /**
     * OVERVIEW: this method allows to manage the view state machine for the correct visualization and transition of the views.
     * @author Matteo Lussana
     */
    private void runViewStateMachine(){
        boolean stop;

        synchronized (this){
            stop = terminate;
            currentView = nextView;     //setting the first view
            nextView = null;
        }
        while(!stop){
            if(currentView==null){
                currentView = new WaitingView();    //if there is no runnable view currently
            }
            currentView.setOwner(this);
            currentView.run();      //allows to start the view

            synchronized (this){
                stop = terminate;       //checks if the state machine should be stopped
                currentView = nextView;
                nextView = null;
            }
        }
    }

    /**
     * OVERVIEW: this method allows to make a transition in the state machine of the views setting the next view to
     *           run after the current view.
     * @param nextView: the following view.
     * @author Matteo Lussana
     */
    public synchronized void transitionToView(View nextView){
        this.nextView = nextView;

    }

    //- - - - - - - - - - - - - - -C O N N E C T I O N   M A N A G E M E N T   M E T H O D S - - - - - - - - - - - - - - - - - - -

    /**
     * OVERVIEW: this method allows to manage the RMI connection client-server.
     * @author Andrea Giacalone, Irene Lo Presti
     */
    public void manageRMIConnection(){
        RemoteInterface remoteServer = null;
        System.out.println("Please insert the IP address of the server:");
        Scanner input = new Scanner(System.in);
        String ip;
        do{
            try {
                ip = input.nextLine();

                if(ip.equals("exit"))
                    System.exit(0);

                client = new RMIAdapter();      //instantiating the remote interface of the client
                Registry registry = LocateRegistry.getRegistry(ip, 1099);       //getting the reference of the RMI registry
                remoteServer = (RemoteInterface) registry.lookup("server");     //getting the remote interface of the server


                //printing logging message in the server side
                try {
                    remoteServer.printLoginStatus("[" + InetAddress.getByName(ip) + "] now connected through RMI");
                } catch (UnknownHostException e) {
                    System.out.println("[RMI] Error in retrieving host address");
                }
                remoteServer.addRemoteClient(client);       //adding the remote interface of the client to the remote server
                client.setOwner(this);     //adding the reference of this client to the remote client interface
                break;
            } catch (RemoteException e) {
                System.out.println("[RMI] Error: unable to invoke remote method, try again to insert the ip address,or insert 'exit' if you don't want to connect to the server anymore");
            } catch (NotBoundException e) {
                System.out.println("[RMI] Error: Server unreachable, try again to insert the ip address, or insert 'exit' if you don't want to connect to the server anymore");
            }
        }while(true);

        //creating the handler for the RMI connection
        serverHandler = new RMIServerHandler(this,remoteServer);
        Thread serverHandlerThread = new Thread(serverHandler);
        serverHandlerThread.start();

    }

    /**
     * OVERVIEW: this method allows to manage the Socket connection client-server.
     * @author Andrea Giacalone, Irene Lo Presti
     */
    public void manageSocketConnection(){
        System.out.println("Please insert the IP address of the server:");
        Scanner input = new Scanner(System.in);
        String ip;

        Socket server;
        do{
            try{
                ip = input.nextLine();
                if(ip.equals("exit"))
                    System.exit(0);
                else{
                    server = new Socket(ip, 9999);
                    break;
                }
            }catch(IOException e){
                System.out.println("[SKT] Server unreachable: try another address, or insert 'exit' if you don't want to connect to the server anymore");
            }
        }while(true);


        serverHandler = new SocketServerHandler(server, this);

        Thread serverHandlerThread = new Thread(serverHandler,"server_"+server.getInetAddress().getHostAddress());
        serverHandlerThread.start();
    }




    //- - - - - - - - - S E T T E R S - - - - - - - - - - - - -
    /**
     * OVERVIEW: this method allows to notify the state machine in order to stop it and eventually also the interaction
     *           with the current view.
     * @author Matteo Lussana
     */
    public synchronized void setTrueTerminate(){
        if(!terminate){
            terminate = true;
            if(currentView!=null)
                currentView.setStopInteraction();
        }
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    //- - - - - - - G E T T E R S - - - - - - - - - - - - - - -
    public View getCurrentView() {
        return currentView;
    }
    public ServerHandler getServerHandler() {
        return serverHandler;
    }
    public boolean isRMI() {
        return isRMI;
    }
    public RemoteInterface getRemoteClient() {
        return client;
    }
    public String getNickname() {
        return nickname;
    }
    public StageManager getStageManager() {
        return stageManager;
    }
}
