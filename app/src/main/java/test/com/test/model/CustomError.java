package test.com.test.model;

/**
 * Created by s.bartashevich on 3/1/2017.
 */

public interface CustomError {
    public static int ERROR_PARSE_JSON=1;
    public static int ERROR_CONNECTION=2;

    public static String ERROR_SHORT_PARSE_JSON="Parsing JSON error";
    public static String ERROR_SHORT_CONNECTION="Connection to server error";

    public String getDescription();
    public String getShortDescription();
    public int getCode();
}
