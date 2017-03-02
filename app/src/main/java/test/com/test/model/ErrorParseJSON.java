package test.com.test.model;

/**
 * Created by s.bartashevich on 3/1/2017.
 */

public class ErrorParseJSON implements CustomError{
    private String description;


    public ErrorParseJSON(String d){
        description=d;
    }

    public String getShortDescription(){
        return CustomError.ERROR_SHORT_PARSE_JSON;
    }
    public String getDescription(){
        return description;
    }
    public int getCode(){
        return CustomError.ERROR_PARSE_JSON;
    }
}
