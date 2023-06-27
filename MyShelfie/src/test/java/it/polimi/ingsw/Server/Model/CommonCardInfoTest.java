package it.polimi.ingsw.Server.Model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.utils.ColorCode.*;

/**
 * Tests for CommonCardInfo class
 * @author Matteo Lussana
 */
class CommonCardInfoTest {

    CommonCardInfo commonCardInfo = new CommonCardInfo("M8PatternStrategy");


    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    /**
     * VisualTest for attribute Schema: ensures that the class is correctly
     * reading from the file, then it prints the Schema
     */
    @Test
    void getSchema() {
        Tile[][] schema = commonCardInfo.getSchema();
        String circle = "\u25CF";

        // Printing column's indexes...
        System.out.print("\u2798" + "\t");
        for (int i = 0; i < schema[0].length; i++)
            System.out.print( (i+1) + "\t" );
        System.out.println();

        for (int i = 0; i < schema.length; i++) {
            // Printing row's indexes...
            System.out.print( (i+1) + "\t" );
            // Printing the shelf...
            for (int j = 0; j < schema[0].length; j++) {
                switch (schema[i][j]) {
                    case NOT_VALID -> System.out.print(" ");
                    case BLANK -> System.out.print(BLANK.code + circle + RESET.code);
                    case PINK -> System.out.print(PINK.code + circle + RESET.code);
                    case GREEN -> System.out.print(GREEN.code + circle + RESET.code);
                    case BLUE -> System.out.print(BLUE.code + circle + RESET.code);
                    case LIGHTBLUE -> System.out.print(LIGHTBLUE.code + circle + RESET.code);
                    case WHITE -> System.out.print(WHITE.code + circle + RESET.code);
                    case YELLOW -> System.out.print(YELLOW.code + circle + RESET.code);
                }
                System.out.print("\t");
            }
            System.out.println();
        }
    }

    /**
     * VisualTest for attribute Times: ensures that the class is correctly
     * reading from the file, then it prints "xTimes times"
     */
    @Test
    void getTimes() {
        System.out.println("x"+commonCardInfo.getTimes()+" times");
    }

    /**
     * VisualTest for attribute Description: ensures that the class is correctly
     * reading from the file, then it prints the Description
     */
    @Test
    void getDescription() {
        System.out.println(commonCardInfo.getDescription());
    }
}