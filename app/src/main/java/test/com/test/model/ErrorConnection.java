package test.com.test.model;

/**
 * Created by s.bartashevich on 3/1/2017.
 */

public class ErrorConnection implements CustomError{
    private String description;


    public ErrorConnection(String d){
        description=d;
    }

    public String getShortDescription(){
        return CustomError.ERROR_SHORT_CONNECTION;
    }
    public String getDescription(){
        return description;
    }
    public int getCode(){
        return CustomError.ERROR_CONNECTION;
    }
}
