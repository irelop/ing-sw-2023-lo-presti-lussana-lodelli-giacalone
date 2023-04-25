package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Messages.C2SMessage;
import it.polimi.ingsw.Server.Messages.LoginNicknameAnswer;
import it.polimi.ingsw.Server.Messages.LoginNicknameRequest;

import java.util.Scanner;

public class LoginView extends View{
    private Object lock;
    private boolean goOn = false;

    @Override
    public void run() {

    }

    public void askNicknameRequest(){
        synchronized (lock) {
            Scanner input = new Scanner(System.in);
            System.out.println("Please select your nickname:\n");
            String insertedNickname = input.nextLine().replace(" ", "");

            C2SMessage nicknameRequest = new LoginNicknameRequest(insertedNickname);
            try {
                lock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void showNicknameAnswer(LoginNicknameAnswer nicknameAnswer){
        switch (nicknameAnswer.getNicknameStatus()){
            case ACCEPTED ->{
                System.out.println("Great! So you are: " +nicknameAnswer.getParent().getInsertedNickname()+ "nice to meet you!");
                goOn = true;
            }
            case INVALID -> {
                System.out.println("Sorry, your nickname has already been chosen - please select another one");
            }
            case FIRST_ACCEPTED -> {
                System.out.println("Great! So you are: " + nicknameAnswer.getParent().getInsertedNickname()+ "nice to meet you!");
                goOn = true;
                askNumPlayersRequest();
            }
        }
    }
        //
        public void askNumPlayersRequest(){
            Scanner input = new Scanner(System.in);
            System.out.println("Please select the number of the players for the game\n");

            int insertedNumPlayers = input.nextInt();
        }

        public void notifyView(){
            this.lock.notify();
        }

    }

