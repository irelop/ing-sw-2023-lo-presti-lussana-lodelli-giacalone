package it.polimi.ingsw.Client;

import it.polimi.ingsw.Client.View.Exceptions.InvalidNetworkChoiceException;
import it.polimi.ingsw.Client.View.LoginView;
import it.polimi.ingsw.Client.View.View;
import it.polimi.ingsw.Client.View.WaitingView;
import it.polimi.ingsw.Server.RMIAdapter;
import it.polimi.ingsw.Server.RemoteInterface;

import java.io.IOException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client implements Runnable{
    private ServerHandler serverHandler;
    private boolean terminate = false;

    private View currentView;
    private View nextView;
    private boolean isRMI;
    private RemoteInterface remoteServer;
    private RemoteInterface client;

    public static void main(String[] args){
        Client client = new Client();
        client.run();
    }

    @Override
    public void run(){
        askNetworkChoice();

        nextView = new LoginView();
        runViewStateMachine();
        if(!isRMI)
            serverHandler.stop();
        else{
            stopRMIConnection();
        }
    }

    private char getNetworkChoice() throws InvalidNetworkChoiceException {
        Scanner input = new Scanner(System.in);
        System.out.println("Insert 'R' for RMI connection or 'S' for Socket connection");
        String answer = input.next().toUpperCase();

        if(answer.length()>1) throw new InvalidNetworkChoiceException();

        char networkChoice = answer.charAt(0);
        if(networkChoice != 'R' && networkChoice != 'S') throw new InvalidNetworkChoiceException();
        else return networkChoice;
    }

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

    public void manageRMIConnection(){

        try{
            client = new RMIAdapter();
            Registry registry = LocateRegistry.getRegistry();
            remoteServer = (RemoteInterface) registry.lookup("server");
            remoteServer.printLoginStatus("["+this.getClass().getName()+"] now connected through RMI");
            remoteServer.addRemoteClient(client);
            client.setClient(this);
            //Thread.sleep(50);


        }catch(RemoteException e){
            System.out.println("[ERROR]: Unable to invoke remote method");
        } catch (NotBoundException e) {
            System.out.println("[ERROR]: Server unreachable");
        }
    }

    public void manageSocketConnection(){
        System.out.println("Please insert the IP address of the server:\n");
        Scanner input = new Scanner(System.in);
        String ip = input.nextLine();

        Socket server;
        try{
            server = new Socket(ip, 9999);
        }catch(IOException e){
            System.out.println("server unreachable");
            return;
        }

        serverHandler = new SocketServerHandler(server, this);

        Thread serverHandlerThread = new Thread((SocketServerHandler)serverHandler,"server_"+server.getInetAddress().getHostAddress());
        serverHandlerThread.start();
    }
    private void runViewStateMachine(){
        boolean stop;

        synchronized (this){
            stop = terminate;
            currentView = nextView;
            nextView = null;
        }
        while(!stop){
            if(currentView==null){
                currentView = new WaitingView();
            }
            currentView.setOwner(this);
            currentView.run();

            synchronized (this){
                stop = terminate;
                currentView = nextView;

                nextView = null;
            }
        }
    }


    public synchronized void transitionToView(View nextView){
        this.nextView = nextView;

    }

    public synchronized void setTrueTerminate(){
        if(!terminate){
            terminate = true;
            if(currentView!=null)
                currentView.setStopInteraction();
        }
    }


    public View getCurrentView() {
        return currentView;
    }

    public ServerHandler getServerHandler() {
        return serverHandler;
    }


    public boolean isRMI() {
        return isRMI;
    }

    public RemoteInterface getRemoteServer() {
        return remoteServer;
    }

    public RemoteInterface getClient() {
        return client;
    }

    public void stopRMIConnection(){
        try {
            this.remoteServer.disconnectClient(this.client);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        this.remoteServer = null;

        System.out.println("Connection with RMI server closed");
        System.exit(0);
    }
}
