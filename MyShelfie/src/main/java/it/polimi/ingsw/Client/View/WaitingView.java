package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Client.View.Exceptions.InvalidChatChoiceException;
import it.polimi.ingsw.Server.Messages.ChatMsgRequest;
import it.polimi.ingsw.Server.Messages.ChatRecordAnswer;
import it.polimi.ingsw.Server.Messages.ChatRecordRequest;
import it.polimi.ingsw.Server.Model.ChatStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Scanner;

public class WaitingView extends View implements ObserverView {
    private ChatStorage chat = new ChatStorage();
    private final Object lock = new Object();
    @Override
    public void run() {

        synchronized (lock){
            try {
                String wait = " Please wait...";
                System.out.println(wait);
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void notifyView() {

        synchronized (lock){
            lock.notify();
        }
    }
}
