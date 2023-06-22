package it.polimi.ingsw.Client.view.CLI;

import it.polimi.ingsw.utils.Exceptions.InvalidNumberOfPlayersException;
import it.polimi.ingsw.utils.Exceptions.InvalidReconnectionAnswerException;
import it.polimi.ingsw.Server.Messages.*;
import java.util.InputMismatchException;
import java.util.Scanner;

 /**
 * LoginView class: this class manages the user interaction during the login phase. It is followed by the WaitingView,
 *                  in which the player will wait until the other players are ready.
 *
 * @author Andrea Giacalone
 */
public class LoginView extends View implements ObserverView {
    private final Object lock;
    private boolean goOn;
    private boolean isFull; //if the game lobby is already full;
    private String insertedNickname;    //the nickname inserted by the player
    private LoginNicknameAnswer answerToShow;

    private ReconnectionAnswer reconnectionAnswer;

    public LoginView() {
        this.lock = new Object();
        this.goOn = false;
        this.isFull = false;
        this.insertedNickname = null;
    }

    public LoginView(NumberOfPlayerManagementMsg msg){
        this.lock = new Object();
        this.goOn = false;
        this.isFull = false;
        this.insertedNickname = msg.nickname;
    }


    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * OVERVIEW: the main method of the view.
     */
    @Override
    public void run() {
        if(insertedNickname == null) {
            showTitleScreen();
            askNickname();
            manageReconnectionChoice();
        }

        else{
            System.out.println("Sorry, there are some problems with the lobby you were inserted in.\n" +
                    "Insertion in a new lobby");
            manageNewLobbyConnection();
        }
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

     /**
      * OVERVIEW: this method allows to show the game title screen.
      */
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

     /**
      * OVERVIEW: this method allows to send the request for the nickname inserted by the user.
      */
    private void sendNicknameRequest(){
        C2SMessage nicknameRequest = new LoginNicknameRequest(insertedNickname, false);

        getOwner().getServerHandler().sendMessageToServer(nicknameRequest);
    }

    /**
     * OVERVIEW: this method allows to show the answer received by the server regarding the validity status of the
     * inserted nickname.
     * @param nicknameAnswer: the answer from the server regarding the user nickname.
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
                    getOwner().getServerHandler().sendMessageToServer(numPlayersRequest);
                }
            }
            case FULL_LOBBY -> {
                System.out.println("Probably there is a problem with your connection. Try to reboot the application\n");
                isFull = true;
                goOn = true;
                getOwner().setTrueTerminate();
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

     /**
      * OVERVIEW: this setter method allows to set the mailbox wrapping the answer from the server regarding
      * the validity status of the inserted nickname.
      * @param answerToShow: the nickname answer from the server.
      */
    public void setLoginNicknameAnswer(LoginNicknameAnswer answerToShow) {
        this.answerToShow = answerToShow;
    }

    // - - - - - - - - - - - - - - - - - - -D I S C O N N E C T I O N   R E S I L I E N C E - - - - - - - - - - - - - - - - - - -

     /**
      * OVERVIEW: this method allows to properly get the user reconnection choice.
      * @return the reconnection choice of the user.
      * @throws InvalidReconnectionAnswerException: an exception thrown if the user choice isn't valid.
      * @authors Irene Lo Presti, Andrea Giacalone
      */
    private char getReconnectionChoice() throws InvalidReconnectionAnswerException{
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next().toUpperCase();
        if(input.length() > 1) throw new InvalidReconnectionAnswerException();

        char answer = input.charAt(0);
        if(answer!='N' && answer!='C') throw new InvalidReconnectionAnswerException();
        else return answer;
    }

     /**
      * OVERVIEW: this method allows to manage the reconnection choice. According to the user choice, the player can be
      * redirected to the lobby of a new game or, if he/she previously was disconnected from a game, he/she can rejoin it.
      * @authors Irene Lo Presti, Andrea Giacalone
      */
    private void manageReconnectionChoice(){
        System.out.println("\nCONTINUE A GAME [C]");
        System.out.println("NEW GAME [N]");
        char answer;
        do{
            try{
                answer = getReconnectionChoice();
                break;
            }catch(InvalidReconnectionAnswerException e){
                System.out.println(e);
            }
        }while(true);

        if(answer == 'N') manageNewLobbyConnection();
        else manageExistingGameConnection();

    }

     /**
      * OVERVIEW: this method allows to manage the connection of the player to the lobby of a new game.
      * @authors Irene Lo Presti, Andrea Giacalone
      */
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
                getOwner().getServerHandler().sendMessageToServer(new LobbyUpdateRequest());

            }
        }

    }

     /**
      * OVERVIEW: this method allows to manage the reconnection of the player to the game where he/she was disconnected.
      * @authors Irene Lo Presti, Andrea Giacalone
      */
    private void manageExistingGameConnection(){
        ReconnectionRequest reconnectionRequest = new ReconnectionRequest(insertedNickname, false);
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
                getOwner().getServerHandler().sendMessageToServer(reconnectionRequest);
            }
        }
        if(reconnectionAnswer.msg != null){
            System.out.println(reconnectionAnswer.msg);
            manageNewLobbyConnection();
        }


    }

     /**
      * OVERVIEW: this setter method allows to set the mailbox wrapping the reconnection answer sent by the server.
      * @param reconnectionAnswer: the reconnection answer sent by the server.
      * @authors Irene Lo Presti, Andrea Giacalone
      */
    public void setReconnectionAnswer(ReconnectionAnswer reconnectionAnswer) {
        this.reconnectionAnswer = reconnectionAnswer;
    }



}