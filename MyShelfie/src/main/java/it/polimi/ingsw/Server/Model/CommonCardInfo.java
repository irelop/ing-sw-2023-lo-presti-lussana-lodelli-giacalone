package it.polimi.ingsw.Server.Model;

import java.io.Serializable;

public class CommonCardInfo implements Serializable {
    private String name;
    private Tile[][] schema;
    private int times;
    private String description;

    /**
     * Constructor for test files that set manually all the attributes
     * @param name: commonGoalCard name
     * @param schema: commonGoalCard grid schema
     * @param times: how many times schema must appear in the shelf
     * @param description: short commonGoalCard description
     */
    public CommonCardInfo(String name,Tile[][] schema,int times, String description) {
        this.name = name.replaceAll("it.polimi.ingsw.Server.Model.PatternStrategy.","");
        this.schema = schema;
        this.times = times;
        this.description = description;
    }
    //it.polimi.ingsw.Server.Model.PatternStrategy.
    public CommonCardInfo(String name){
        this.schema = new Tile[6][5];
        this.name = name.replaceAll("it.polimi.ingsw.Server.Model.PatternStrategy.","");;
        int index = 0;
        ReadFileByLines reader = new ReadFileByLines();
       // reader.readFrom("MyShelfie/src/txtfiles/CommonGoalCardsInfo.txt");
        reader.readFrom("MyShelfie/src/txtfiles/CommonGoalCardsInfo.txt");
        for(int w=0; w<120; w=w+10){
            String row = ReadFileByLines.getLineByIndex(w);
            if(row.equals(this.name)) {
                index = w;
                for (int i = w + 1; i < 6 + w + 1; i++) {

                    row = ReadFileByLines.getLineByIndex(i);

                    String[] values = row.replaceAll("\\{", "")
                            .replaceAll("}", "")
                            .split(", ");

                    for (int j = 0; j < 5; j++)
                        schema[i - w - 1][j] = Tile.valueOf(values[j]);
                }
            }

        }
        this.times = Integer.parseInt(ReadFileByLines.getLineByIndex(index+7));
        this.description = ReadFileByLines.getLineByIndex(index+8);
    }
    public Tile[][] getSchema(){
        return this.schema;
    }

    public int getTimes(){
        return times;
    }

    public String getDescription(){
        return description.replaceAll("\\. ",".\n");
    }
}
