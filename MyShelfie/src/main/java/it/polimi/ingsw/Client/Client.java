package it.polimi.ingsw.Client;

import it.polimi.ingsw.Client.View.LoginView;
import it.polimi.ingsw.Client.View.View;
import it.polimi.ingsw.Client.View.WaitingView;
import it.polimi.ingsw.Server.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{
    private ServerHandler serverHandler;
    private boolean terminate;

    private View currentView;
    private View nextView;

    public static void main(String[] args){
        Client client = new Client();
        client.run();
    }

    @Override
    public void run(){
        Scanner input = new Scanner(System.in);
        System.out.println("Please insert the IP address of the client:\n");
        String ip = input.nextLine();

        Socket server;
        try{
            server = new Socket(ip, Server.serverPort); //nel server bisogna specificare la porta per comunicare
        }catch(IOException e){
            System.out.println("server unreachable");
            return;
        }
        //dopo aver stabilito la connessione con il server, il client delega la connessione all'Handler
        serverHandler = new SocketServerHandler(server, this);
        Thread serverHandlerThread = new Thread(serverHandler,"server_"+server.getInetAddress().getHostAddress());
        serverHandlerThread.start();

        nextView = new LoginView();
        runViewStateMachine();

        serverHandler.stop();
    }


    private void runViewStateMachine(){
        boolean stop;

        synchronized (this){
            stop = terminate;
            currentView = nextView;
            nextView = null;
        }
        while(stop!=false){
            if(currentView==null){
                currentView = new WaitingView();  //questa view viene mostrata quando nessun altra view è disponibile
                                                  //ovvero quando stanno giocando il turno gli altri
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
        //currentView.setStopInteraction(); //ho commentato questo commentato questa istruzione perchè dava problemi
                                            //con l'owner della view alla fine della lobbyView
                                            //bisogna capire come fixare
    }

    public synchronized void setTrueTerminate(){
        if(!terminate){
            terminate = true;
            currentView.setStopInteraction();
        }
    }


    public View getCurrentView() {
        return currentView;
    }

    public ServerHandler getServerHandler() {
        return serverHandler;
    }

    public boolean isTerminate() {
        return terminate;
    }

    public View getNextView() {
        return nextView;
    }
}
