package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Client.View.Exceptions.InvalidChatChoiceException;
import it.polimi.ingsw.Server.Messages.*;
import it.polimi.ingsw.Server.Model.ChatStorage;


import java.time.format.DateTimeFormatter;

import java.util.Formatter;
import java.util.Scanner;

/**
 * GoalView class: this view is shown at the end of the turn of a player, allowing him/her to get track of his/her game
 * progresses. It also offers the possibility to chat with other players leaving them messages which may be helpful or
 * not throughout the game.
 *
 * @author Matteo Lussana, Andrea Giacalone
 */
public class GoalView extends View {

    private GoalAndScoreMsg msg;
    private Object lock;
    private ChatStorage chat;
    private boolean sendingResult;
    public GoalView(GoalAndScoreMsg msg) {
        this.msg = msg;
        this.lock = new Object();
        this.chat = null;
    }

    @Override
    public void run() {


        String goOn;

        Scanner scanner = new Scanner(System.in);

        System.out.println("---------------------------------");
        System.out.println("GOAL ACHIVED IN THIS TURN:");
        if (msg.commonGoalAchived == true) {
            System.out.println("Common Goal: yes");
        } else System.out.println("Common Goal: no");
        if (msg.personalGoalAchived == true) {
            System.out.println("Personal Goal: yes");
        } else System.out.println("Personal Goal: no");
        if (msg.youFullyShelf) System.out.println("You earned 1 pt. for be the first to complete the shelf, SIUM");

        System.out.println("Total score: " + msg.score);
        System.out.println("---------------------------------");
        System.out.println("[press enter to continue]");
        goOn = scanner.nextLine();

        if (goOn != null) {
            System.out.println("Before ending you turn, you can leave a message to everyone or to a lucky receiver");
            System.out.println("Please select (Y) if you want to chat or (N) otherwise");
            char answer = 0;
            do {

                try {
                    answer = getChatChoice();
                    break;
                } catch (InvalidChatChoiceException e) {
                    System.out.println(e);
                }
            }while (true);

            if (answer == 'Y') {
                manageChat();
            } else {

                //non l'abbiamo usata questa cosa....
                //forse va bene così BOH
                if (msg.lastTurn) {
                    GameIsEndingUpdateRequest gameIsEndingUpdateRequest = new GameIsEndingUpdateRequest();
                }
                finishTurn();
            }
        }
    }


    /**
     * OVERVIEW: this method allows to get the user input in order to manage different decisions throughout the chat.
     * @return answer: the choice inserted by the user.
     * @throws InvalidChatChoiceException : if a wrong option has been selected.
     */
    private char getChatChoice() throws InvalidChatChoiceException {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next().toUpperCase();
        if (input.length() > 1)
            throw new InvalidChatChoiceException();


        char answer = input.charAt(0);
        if (answer != 'Y' && answer != 'N')
            throw new InvalidChatChoiceException();
        else return answer;
    }



    private void manageChat(){
        Scanner scanner = new Scanner(System.in);
        boolean goOn = true;
        int remainingMsgAvailable = 3;



        synchronized (lock) {
            if(!getOwner().isRMI()) {
                getOwner().getServerHandler().sendMessageToServer(new ChatRecordRequest(getOwner().getNickname()));
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                getOwner().getServerHandler().sendMessageToServer(new ChatRecordRequest(getOwner().getNickname()));
            }
            showChat();


            System.out.println("\nYou have still "+ remainingMsgAvailable + "/3 remaining messages");
            while (goOn) {
                ChatMsgRequest chatMsgRequest;
                System.out.println();
                System.out.println("Type here your message:");
                String msg = scanner.nextLine();
                System.out.println("Do you want to broadcast your message?");
                char choice;
                do {
                    try {
                        choice = getChatChoice();
                        break;
                    } catch (InvalidChatChoiceException e) {
                        System.out.println(e);
                    }
                } while (true);

                if (choice == 'Y') {
                    chatMsgRequest = new ChatMsgRequest(getOwner().getNickname(), "EVERYONE", msg);
                } else {

                    boolean loop = true;       //in order to prevent clients from sending msg for themselves
                    do {
                        System.out.println("Who is the lucky receiver of your message?");
                        String receiver = scanner.nextLine().toUpperCase();
                        chatMsgRequest = new ChatMsgRequest(getOwner().getNickname(), receiver, msg);
                        if (receiver.equals(getOwner().getNickname())) {
                            System.out.println("sorry you cannot cheat sending messages to yourself");
                        } else {
                            loop = false;
                        }
                    }while(loop);



                }

                if (!getOwner().isRMI()) {
                    getOwner().getServerHandler().sendMessageToServer(chatMsgRequest);
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    getOwner().getServerHandler().sendMessageToServer(chatMsgRequest);
                }

                if(sendingResult) {
                    System.out.println("(message sent successfully)");
                    remainingMsgAvailable--;

                }else{
                    System.out.println("(unable to find your receiver)");
                }

                if(remainingMsgAvailable>0) {
                    System.out.println("You have still " + remainingMsgAvailable + "/3 remaining messages.");
                    System.out.println("Do you want to keep chatting? Type (Y) if yes, otherwise (N) if you want to end your turn");

                    do {
                        try {
                            choice = getChatChoice();
                            break;
                        } catch (InvalidChatChoiceException e) {
                            System.out.println(e);
                        }
                    } while (true);
                    if (choice == 'N') {
                        goOn = false;
                        finishTurn();
                    }
                }else {
                    System.out.println("You have just finished your available messages");
                    System.out.println("Ending your turn ...");
                    goOn = false;
                    finishTurn();
                }
            }
        }
    }


    public void setChat(ChatStorage chat) {
        this.chat = chat;
    }




    public void setSendingResult(boolean sendingResult) {
        this.sendingResult = sendingResult;
    }

    @Override
    public void notifyView() {
        synchronized (lock){
            lock.notify();
        }
    }

    private void finishTurn(){
        FinishTurnMsg finishTurnMsg = new FinishTurnMsg();
        if (!getOwner().isRMI()) {
            getOwner().getServerHandler().sendMessageToServer(finishTurnMsg);
        } else {
            getOwner().getServerHandler().sendMessageToServer(finishTurnMsg);
        }
    }

    private void showChat(){
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");   // in order to properly format the timestamp

        Formatter fmt = new Formatter();

        fmt.format(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - C H A T   R O O M - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - \n");
        fmt.format("%30s %30s %30s %30s\n","[CONTENT]","[SENDER]","[RECEIVER]","[TIME]");
        for (int i = 0; i < chat.getStorage().size(); i++) {
            fmt.format("%30s %30s %30s %30s\n",
                    chat.getStorage().get(i).getContent(),
                    chat.getStorage().get(i).getSender(),
                    chat.getStorage().get(i).getReceiver(),
                    chat.getStorage().get(i).getLocalTime().format(timeFormatter));
        }

        System.out.println(fmt);
    }
}