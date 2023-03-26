package it.polimi.ingsw;


import java.util.ArrayList;

public class Shelf {
    private Color[][] grid;
    private int[][] beenThere;


    public Shelf(){
        this.grid = new Color[6][5];
        //l'indice (0,0) è quello della prima cella in alto a sinistra
        this.beenThere = new int[6][5];

        for(int r=0; r<6; r++)
            for(int c=0; c<5; c++){
                grid[r][c] = Color.BLANK;
                beenThere[r][c] = 0;
            }
    }

    //OVERVIEW: inserimento della/e tessera/e selezionata/e (dentro il vettore littleHand)
    // nella propria libreria nella colonna di indice columnIndex
    public void insert(int columnIndex, ArrayList<Tile> littleHand) throws NotEnoughSpaceInChosenColumnException{
        if(columnFreeSpace(columnIndex) < littleHand.size()) throw new NotEnoughSpaceInChosenColumnException();
        //controllo se l'indice è corretto e la shelf non è piena prima di chiamare questa funz
        for(int r=5; r>=0; r--){
            if(grid[r][columnIndex]==Color.BLANK){
                for(int i=0; i<littleHand.size(); i++){ grid[r+i][columnIndex] = littleHand.get(i).color;}
                break;
            }
        }
    }

    //OVERVIEW: controllo numero tessere dello stesso colore (color) adiacenti e restituisce il totale
    public int spotCheck(){
        int score=0;
        int dimension;
        for(int r=0; r<5; r++)
            for(int c=0; c<6; c++){
                if(beenThere[r][c]==0 && !grid[r][c].equals(Color.BLANK)){
                    dimension = spotDimention(grid[r][c],r,c,0);
                    switch(dimension){
                        case 1: break;
                        case 2: break;
                        case 3: score = score +2;
                            break;
                        case 4: score = score +3;
                            break;
                        case 5: score = score +5;
                            break;
                        default: score = score +8;
                    }
                }
            }
        return score;
    }

    //OVERVIEW: conta il numero di celle del colore color adiacenti a partire dalla cella
    // di indice (startRow, startColumn)
    private int spotDimention(Color color, int startRow, int startColumn, int counter){
        beenThere[startRow][startColumn] = 1;
        //right
        if(startColumn+1<5 && beenThere[startRow][startColumn+1]!=1 && grid[startRow][startColumn+1].equals(color))
            counter = 1 + spotDimention(color, startRow, startColumn+1, counter);

        //left
        if(startColumn-1>=0 && beenThere[startRow][startColumn-1]!=1 && grid[startRow][startColumn-1].equals(color))
            counter = 1 + spotDimention(color, startRow, startColumn-1, counter);

        //down
        if(startRow+1<6 && beenThere[startRow+1][startColumn]!=1 && grid[startRow+1][startColumn].equals(color))
            counter = 1 + spotDimention(color, startRow+1, startColumn, counter);

        //up
        if(startRow-1>=0 && beenThere[startRow-1][startColumn]!=1 && grid[startRow-1][startColumn].equals(color))
            counter = 1 + spotDimention(color, startRow-1, startColumn, counter);

        return counter;
    }

    //OVERVIEW: ritorna il numero di celle libere della colonna di indice columnIndex
    private int columnFreeSpace(int columnIndex){
        int r=0;
        while(grid[r][columnIndex]==Color.BLANK && r<6)
            r++;
        return r;
    }

    //OVERVIEW: controlla se la libreria è piena
    public boolean isShelfFull(){
        for(int c=0; c<6; c++)
            if(grid[0][c]==Color.BLANK) return false;
        return true;
    }

    //OVERVIEW: restituisce il numero massimo di tessere pescabili in tutta la shelf
    public int maxTilesPickable(){
        int freeSpace=0;
        int r;
        for(int c=0; c<6; c++) {
            r=0;
            while (r < 5 && grid[r][c] == Color.BLANK){
                r++;
                if(r==3) return 3;
            }
            if (freeSpace < r) freeSpace = r;
        }
        return freeSpace;
    }
}