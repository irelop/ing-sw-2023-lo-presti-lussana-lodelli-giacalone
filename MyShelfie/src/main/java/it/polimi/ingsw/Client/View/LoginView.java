package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Client.SocketServerHandler;
import it.polimi.ingsw.Client.View.Exceptions.InvalidNumberOfPlayersException;
import it.polimi.ingsw.Client.View.Exceptions.InvalidReconnectionAnswerException;
import it.polimi.ingsw.Client.View.Exceptions.InvalidRuleAnswerException;
import it.polimi.ingsw.Server.Messages.*;

import java.rmi.RemoteException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 package it.polimi.ingsw.Client.View;

 import it.polimi.ingsw.Server.Messages.*;

 import java.util.Scanner;

 /**
 * LoginView class: this class manages the user interaction during the login phase. It is followed by the WaitingView,
 *                  in which the player will wait until the other players are ready.
 *
 * @author Andrea Giacalone
 */
public class LoginView extends View implements ObserverView {
    private final Object lock; //in order to stop and continue the run() method computation.
    private boolean goOn; //in order to check if the nickname is valid, and so we can go ahead.
    private boolean isFull;
    private String insertedNickname;
    private LoginNicknameAnswer answerToShow; //the answer received by the server which needs to be shown.

    private ReconnectionAnswer reconnectionAnswer;

    public LoginView() {
        this.lock = new Object();
        this.goOn = false;
        this.isFull = false;
    }


    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * OVERVIEW: the main method of the view.
     */
    @Override
    public void run() {
            showTitleScreen();
            askNickname();
            manageReconnectionChoice();
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public void showTitleScreen(){
        System.out.println("\n,   .     .                     .         .   ,        ,-.  .       .          \n" +
                "| . |     |                     |         |\\ /|       (   ` |       |  ,- o    \n" +
                "| ) ) ,-. | ,-. ,-. ;-.-. ,-.   |-  ,-.   | V | . .    `-.  |-. ,-. |  |  . ,-.\n" +
                "|/|/  |-' | |   | | | | | |-'   |   | |   |   | | |   .   ) | | |-' |  |- | |-'\n" +
                "' '   `-' ' `-' `-' ' ' ' `-'   `-' `-'   '   ' `-|    `-'  ' ' `-' '  |  ' `-'\n" +
                "                                                `-'                   -'       \n" +
                "\n");
    }



    /**
     * OVERVIEW: this method allows the user to insert its nickname and checks its validity through a request forwarded
     *           to the server.
     */
    public void askNickname(){
        Scanner input = new Scanner(System.in);
        System.out.println("\nPlease select your nickname:\n");
        insertedNickname = input.nextLine().replace(" ", "").toUpperCase();
    }
    private void sendNicknameRequest(){
        C2SMessage nicknameRequest = new LoginNicknameRequest(insertedNickname);
        if(!getOwner().isRMI())
            getOwner().getServerHandler().sendMessageToServer(nicknameRequest);
        else{
            try {
                getOwner().getRemoteServer().sendMessageToServer(nicknameRequest, getOwner().getClient());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * OVERVIEW: this method allows to show the answer received by the server and, for the first player,
     *           calls the method responsible for the request of the number of players of the game.
     * @param nicknameAnswer
     */
    private void showNicknameAnswer(LoginNicknameAnswer nicknameAnswer){
        switch (nicknameAnswer.getNicknameStatus()){
            case ACCEPTED ->{
                System.out.println("\nGreat! So you are: " +nicknameAnswer.getParent().getInsertedNickname()+ ", nice to meet you!\n");
                goOn = true;
            }
            case INVALID -> {
                System.out.println("\nSorry, your nickname has already been chosen - please select another one\n");
                askNickname();
            }
            case FIRST_ACCEPTED -> {
                System.out.println("\nGreat! So you are: " + nicknameAnswer.getParent().getInsertedNickname()+ ", nice to meet you!\n");
                goOn = true;
                int insertedNumPlayers;
                do{
                    try{
                        insertedNumPlayers = askNumPlayersRequest();
                        break;
                    }catch (InvalidNumberOfPlayersException e){
                        System.out.println(e);
                    }
                }while(true);
                C2SMessage numPlayersRequest = new LoginNumPlayersRequest(insertedNumPlayers);
                if(!getOwner().isRMI())
                    getOwner().getServerHandler().sendMessageToServer(numPlayersRequest);
                else{
                    try {
                        getOwner().getRemoteServer().sendMessageToServer(numPlayersRequest, getOwner().getClient());
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            case FULL_LOBBY -> {
                System.out.println("\nDeeply sorry! We cannot let you join because the game lobby is already full\n");
                System.out.println("Probably there is a problem with you connection. Try to restart the client\n");
                isFull = true;
                goOn = true;
            }
        }
    }

    /**
     * OVERVIEW: this method allows to ask the number of players of the game and to forward the request to the server.
     */
    public int askNumPlayersRequest() throws InvalidNumberOfPlayersException {
        Scanner input = new Scanner(System.in);
        int insertedNumPlayers;

        System.out.println("\nPlease select the number of the players for the game\n");

        do{
            try{
                insertedNumPlayers = input.nextInt();
                break;
            }catch(InputMismatchException e){
                System.out.println("You have to insert a number. Try again!");
                input.next();
            }
        }while(true);

        if(insertedNumPlayers <=1 || insertedNumPlayers > 4)
            throw new InvalidNumberOfPlayersException();
        else
            return insertedNumPlayers;
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public void notifyView(){
        synchronized (lock) {
            this.lock.notifyAll();
        }
    }

    public void setLoginNicknameAnswer(LoginNicknameAnswer answerToShow) {
        this.answerToShow = answerToShow;
    }

    //gestione FA Disconnection Resilience

    private char getReconnectionChoice() throws InvalidReconnectionAnswerException{
        System.out.println("Do you want to join a new lobby or an already existing game?");
        System.out.println("Insert L for a new lobby or G for the existing game");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next().toUpperCase();
        if(input.length() > 1) throw new InvalidReconnectionAnswerException();

        char answer = input.charAt(0);
        if(answer!='L' && answer!='G') throw new InvalidReconnectionAnswerException();
        else return answer;
    }

    private void manageReconnectionChoice(){
        char answer;
        do{
            try{
                answer = getReconnectionChoice();
                break;
            }catch(InvalidReconnectionAnswerException e){
                System.out.println(e);
            }
        }while(true);

        if(answer == 'L') manageNewLobbyConnection();
        else manageExistingGameConnection();

    }

    private void manageNewLobbyConnection(){
        synchronized (lock) {
            while (!goOn && !isFull) {
                sendNicknameRequest();
                if (!getOwner().isRMI()) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                showNicknameAnswer(answerToShow);
            }
            if (answerToShow.getNicknameStatus() == LoginNicknameAnswer.Status.ACCEPTED || answerToShow.getNicknameStatus() == LoginNicknameAnswer.Status.FIRST_ACCEPTED) {
                if (!getOwner().isRMI()) {
                    getOwner().getServerHandler().sendMessageToServer(new LobbyUpdateRequest());
                } else {
                    try {
                        getOwner().getRemoteServer().sendMessageToServer(new LobbyUpdateRequest(), getOwner().getClient());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void manageExistingGameConnection(){
        ReconnectionRequest reconnectionRequest = new ReconnectionRequest(insertedNickname);
        synchronized (lock){
            if (!getOwner().isRMI()) {
                getOwner().getServerHandler().sendMessageToServer(reconnectionRequest);
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                try {
                    getOwner().getRemoteServer().sendMessageToServer(reconnectionRequest, getOwner().getClient());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if(!reconnectionAnswer.canConnect){
            System.out.println("\nThere isn't any disconnected player matching with your nickname.\n" +
                    "Redirecting to a new lobby.\n");
            manageNewLobbyConnection();
        }


    }

    public void setReconnectionAnswer(ReconnectionAnswer reconnectionAnswer) {
        this.reconnectionAnswer = reconnectionAnswer;
    }



}