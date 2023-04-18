package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Model.Exceptions.OutOfBoardException;

import java.util.Scanner;

public class PickFromBoardView extends View{

    @Override
    public void run(){

    }

    private int getInitialRow(){
        int r;
        System.out.print("Please insert the row of the initial position: ");
        Scanner scanner = new Scanner(System.in);
        r = scanner.nextInt();
        return r;
    }

    private int getInitialColumn() throws OutOfBoardException {
        int c;
        System.out.print("Please insert the column of the initial position: ");
        Scanner scanner = new Scanner(System.in);
        c = scanner.nextInt();
        return c;
    }
}
