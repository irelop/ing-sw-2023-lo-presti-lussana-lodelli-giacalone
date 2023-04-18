package it.polimi.ingsw.Model.Exceptions;

public class EmptyBagException extends Exception {

    public EmptyBagException(){
        super();
    }

    @Override
    public String toString(){
        return "The bag is empty: you cannot refill any longer";
    }
}
