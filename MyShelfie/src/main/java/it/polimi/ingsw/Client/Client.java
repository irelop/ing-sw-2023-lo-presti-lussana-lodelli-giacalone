package it.polimi.ingsw.Client;

import it.polimi.ingsw.Client.View.ChooseInitialPositionView;
import it.polimi.ingsw.Client.View.View;
import it.polimi.ingsw.Client.View.WaitingView;

import java.io.IOException;
import java.net.Socket;

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
        //Prendo l'ip dall'utente o lo inserisco io
        String ip = "xxxxxx";

        Socket server;
        try{
            server = new Socket(ip, Server.SOCKET_PORT); //nel server bisogna specificare la porta per comunicare
        }catch(IOException e){
            System.out.println("server unreachable");
            return;
        }
        //dopo aver stabilito la connessione con il server, il client delega la connessione all'Handler
        serverHandler = new ServerHandler(server, this);
        serverHandlerThread.start();

        nextView = new LoginView();
        runViewStateMachine();

        serverHandler.stop();
    }

    public ServerHandler getServerHandler(){return serverHandler;}

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
        currentView.setStopInteraction();
    }

    public synchronized void setTrueTerminate(){
        if(!terminate){
            terminate = true;
            currentView.setStopInteraction();
        }
    }


}
