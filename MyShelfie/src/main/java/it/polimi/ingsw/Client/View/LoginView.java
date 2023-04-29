package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Messages.*;

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
public class LoginView extends View implements ObservableView{
    private Object lock; //in order to stop and continue the run() method computation.
    private boolean goOn; //in order to check if the nickname is valid and so we can go ahead.
    private LoginNicknameAnswer answerToShow; //the answer received by the server which needs to be shown.

    private View nextView; //the next view in the Machine State pattern: in this case it's the Waiting view

    public LoginView() {
        this.lock = new Object();
        this.goOn = false;
        this.answerToShow = null;
        this.nextView = new LobbyView();
    }


    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * OVERVIEW: the main method of the view.
     */
    @Override
    public void run() {
        synchronized (lock){

            while(!goOn){
                askNicknameRequest();
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                showNicknameAnswer(answerToShow);
            }
            //nextView = new WaitingView();

        }
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public void showTitleScreen(){

        System.out.println("\nWelcome to:\n");
        System.out.println("\n  __  __              _____   _              _    __   _        \n" +
                " |  \\/  |            / ____| | |            | |  / _| (_)       \n" +
                " | \\  / |  _   _    | (___   | |__     ___  | | | |_   _    ___ \n" +
                " | |\\/| | | | | |    \\___ \\  | '_ \\   / _ \\ | | |  _| | |  / _ \\\n" +
                " | |  | | | |_| |    ____) | | | | | |  __/ | | | |   | | |  __/\n" +
                " |_|  |_|  \\__, |   |_____/  |_| |_|  \\___| |_| |_|   |_|  \\___|\n" +
                "            __/ |                                               \n" +
                "           |___/                                                \n");
    }



    /**
     * OVERVIEW: this method allows the user to insert its nickname and checks its validity through a request forwarded
     *           to the server.
     */
    public void askNicknameRequest(){
        Scanner input = new Scanner(System.in);
        System.out.println("\nPlease select your nickname:\n");
        String insertedNickname = input.nextLine().replace(" ", "");

        C2SMessage nicknameRequest = new LoginNicknameRequest(insertedNickname);
        getOwner().getServerHandler().sendMessageToServer(nicknameRequest);
    }

    /**
     * OVERVIEW: this method allows to show the answer received by the server and, for the first player,
     *           calls the method responsible for the request of the number of players of the game.
     * @param nicknameAnswer
     */
    private void showNicknameAnswer(LoginNicknameAnswer nicknameAnswer){
        switch (nicknameAnswer.getNicknameStatus()){
            case ACCEPTED ->{
                System.out.println("\nGreat! So you are: " +nicknameAnswer.getParent().getInsertedNickname()+ "nice to meet you!\n");
                goOn = true;
            }
            case INVALID -> {
                System.out.println("\nSorry, your nickname has already been chosen - please select another one\n");
            }
            case FIRST_ACCEPTED -> {
                System.out.println("\nGreat! So you are: " + nicknameAnswer.getParent().getInsertedNickname()+ "nice to meet you!\n");
                goOn = true;
                askNumPlayersRequest();
            }
        }
    }

    /**
     * OVERVIEW: this method allows to ask the number of players of the game and to forward the request to the server.
     */
    public void askNumPlayersRequest(){
        Scanner input = new Scanner(System.in);
        int insertedNumPlayers;

        System.out.println("\nPlease select the number of the players for the game\n");
        do{
            insertedNumPlayers = input.nextInt();
            System.out.println("\nThe number inserted isn't correct: please insert another one\n");
        }while(insertedNumPlayers <=1 || insertedNumPlayers >4);

        C2SMessage numPlayersRequest = new LoginNumPlayersRequest(insertedNumPlayers);
        getOwner().getServerHandler().sendMessageToServer(numPlayersRequest);
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public void notifyView(){
        this.lock.notify();
    }

    public void setLoginNicknameAnswer(LoginNicknameAnswer answerToShow) {
        this.answerToShow = answerToShow;
    }


}