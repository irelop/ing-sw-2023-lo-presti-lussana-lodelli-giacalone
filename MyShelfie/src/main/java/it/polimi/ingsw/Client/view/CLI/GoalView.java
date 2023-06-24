package it.polimi.ingsw.Client.view.CLI;

import it.polimi.ingsw.utils.Exceptions.InvalidChatChoiceException;
import it.polimi.ingsw.Server.Messages.*;
import it.polimi.ingsw.Server.chat.ChatStorage;


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

    private final GoalAndScoreMsg msg;
    private final Object lock;
    private ChatStorage chat;
    private boolean sendingResult;
    public GoalView(GoalAndScoreMsg msg) {
        this.msg = msg;
        this.lock = new Object();
        this.chat = null;
    }

    @Override
    public void run() {

        //showing the amount of points achieved by the player before ending the turn
        showScoreRecap();

        //delaying the action as an arcade game
        String goOn;
        Scanner scanner = new Scanner(System.in);
        goOn = scanner.nextLine();

        //managing the chat choice
        if (goOn != null) {
            System.out.println("Before ending you turn, you can leave a message to everyone or to a lucky receiver");
            System.out.println("Please select (Y) if you want to chat or (N) otherwise");
            char answer;
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
                finishTurn();
            }
        }
    }

    /**
     * OVERVIEW: this method allows to print and show the current player the result of his/her choices and goals achieved
     * until this turn.
     * @author Matteo Lussana
     */
    private void showScoreRecap(){
        System.out.println("---------------------------------");
        System.out.println("GOAL ACHIEVED IN THIS TURN:");
        if (msg.commonGoalAchieved) {
            System.out.println("Common Goal: yes");
        } else System.out.println("Common Goal: no");
        if (msg.personalGoalAchieved) {
            System.out.println("Personal Goal: yes");
        } else System.out.println("Personal Goal: no");
        if (msg.youFullyShelf) System.out.println("You earned 1 pt. for be the first to complete the shelf, SIUM");

        System.out.println("Total score: " + msg.score);
        System.out.println("---------------------------------");
        System.out.println("[press enter to continue]");
    }

    /**
     * OVERVIEW: this method allows to properly finish the turn.
     * @author Matteo Lussana
     */
    private void finishTurn(){
        FinishTurnMsg finishTurnMsg = new FinishTurnMsg();
        if (!getOwner().isRMI()) {
            getOwner().getServerHandler().sendMessageToServer(finishTurnMsg);
        } else {
            getOwner().getServerHandler().sendMessageToServer(finishTurnMsg);
        }
    }


    // - - - - - - - - - - - - - - - - - - - - - - C H A T   M E T H O D S-  - - - - - - - - - - - - - - - - - - - - -


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


    /**
     * OVERVIEW: this method allows to show the current record of chat messages in CLI unfolding their fields and printing
     * them in a formatted table.
     * @author Andrea Giacalone
     */
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


    /**
     * OVERVIEW: this method allows to manage the chat in CLI allowing the player to see the record
     * of the actual chat messages and to send public messages or private ones,
     * checking if the receiver actually exists. The player can send a maximum number of 3 messages and after that
     * the turn will be automatically considered finished.
     * @author Andrea Giacalone
     */
    private void manageChat(){
        Scanner scanner = new Scanner(System.in);
        boolean goOn = true;
        int remainingMsgAvailable = 3;  //the amount of available messages that the current player can send



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




    @Override
    public void notifyView() {
        synchronized (lock){
            lock.notify();
        }
    }


    //- - - - - - - - - - - - - - - - - S E T T E R S - - - - - - - - - - - - -  - -

    /**
     * OVERVIEW: this method allows to set the chat snapshot returned by the chat manager.
     * @param chat: the chat record.
     */
    public void setChat(ChatStorage chat) {
        this.chat = chat;
    }


    /**
     * OVERVIEW: this method allows to set the sending result of the message checked and returned by the chat manager.
     * @param sendingResult: the sending result of sending the message.
     */
    public void setSendingResult(boolean sendingResult) {
        this.sendingResult = sendingResult;
    }



}