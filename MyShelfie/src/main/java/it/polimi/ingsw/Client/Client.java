package it.polimi.ingsw.Client;

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
    }


    private void askNetworkChoice() {
        Scanner input = new Scanner(System.in);

        System.out.println("Do you want to switch to RMI?");
        String networkChoice = input.next().toUpperCase();

        if (networkChoice.equals("Y")) {
            isRMI = true;
            manageRMIConnection();
        }
        else{
            isRMI = false;
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
}
