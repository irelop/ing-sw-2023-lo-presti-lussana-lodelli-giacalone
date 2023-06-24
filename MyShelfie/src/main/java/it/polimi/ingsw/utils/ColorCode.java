package it.polimi.ingsw.utils;

/**
 * ColorCode enum: this enum class wraps the code associated to colors which can be shown in a CLI.
 */
public enum ColorCode {
    GREEN("\u001B[32m"),
    PINK("\u001B[35m"),
    BLUE("\u001B[34m"),
    LIGHTBLUE("\u001B[36m"),
    YELLOW("\u001B[33m"),
    WHITE("\u001B[37m"),
    BLANK("\u001B[30m"),
    NOT_VALID(" "),
    RESET("\u001B[0m");

    public final String code;

    ColorCode(String code){
        this.code = code;
    }
}
