package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Messages.FinishGameAnswer;
import it.polimi.ingsw.Server.Messages.FinishGameRequest;
import it.polimi.ingsw.Server.Messages.ScoreBoardMsg;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Scanner;

public class EndgameView extends View {
    private final ScoreBoardMsg msg;
    Object lock = new Object();

    private String farewellFromServer;

    public EndgameView(ScoreBoardMsg msg) {
        this.msg = msg;
    }

    @Override
    public void run() {

        //sorting arrayList
        for (int i = 0; i < msg.playerName.size(); i++)
            for (int j = 0; j < msg.playerName.size() - 1; j++) {
                if (msg.totalScore.get(j) < msg.totalScore.get(j + 1) && i != j) {
                    Collections.swap(msg.totalScore, j, j + 1);
                    Collections.swap(msg.playerName, j, j + 1);
                }
            }

        System.out.println("---------------------------------");
        System.out.println("SCORE BOARD:");
        for (int i = 0; i < msg.playerName.size(); i++) {
            System.out.println((i + 1) + "° - " + msg.playerName.get(i) + " with " + msg.totalScore.get(i) + " pt.");
        }

        System.out.println("---------------------------------");
        System.out.println("[press enter to continue]");

        Scanner scanner = new Scanner(System.in);
        String goOn;

        goOn = scanner.nextLine();

        if (goOn != null) {
            synchronized (lock) {
                FinishGameRequest finishGameRequest = new FinishGameRequest(msg.playerNickname);
                if (!getOwner().isRMI()) {
                    getOwner().getServerHandler().sendMessageToServer(finishGameRequest);
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    try {
                        getOwner().getRemoteServer().sendMessageToServer(finishGameRequest, getOwner().getClient());
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
                    System.out.println(farewellFromServer);
                    getOwner().setTrueTerminate();
            }
        }
    }

    public void setFarewellFromServer(String farewellFromServer) {
        this.farewellFromServer = farewellFromServer;
    }

    @Override
    public void notifyView() {
        synchronized (lock) {
            lock.notify();
        }
    }
}


