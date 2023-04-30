package it.polimi.ingsw.Server.Model;

public class CommonCardInfo {
    private String name;
    private Tile[][] schema;
    private int times;
    private String description;


    public CommonCardInfo(String name){
        this.name = name;
        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("src/txtfiles/CommonGoalCardsInfo.txt");
        for(int w=0; w<120; w=w+10){
            String row = ReadFileByLines.getLine();
            if(row.equals(name))
                for (int i = w+1; i < w+6; i++) {

                    row = ReadFileByLines.getLine();

                    String[] values = row.replaceAll("\\{", "")
                            .replaceAll("}", "")
                            .split(", ");

                    for (int j = 0; j < 6; j++)
                        schema[i][j] = Tile.valueOf(values[j]);
                }

        }
        this.times = Integer.parseInt(ReadFileByLines.getLine());
        this.description = ReadFileByLines.getLine();

    }

    public Tile[][] getSchema(){
        return this.schema;
    }

    public int getTimes(){
        return times;
    }

    public String getDescription(){
        return description;
    }
}
