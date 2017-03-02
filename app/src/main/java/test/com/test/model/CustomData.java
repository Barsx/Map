package test.com.test.model;

/**
 * Created by s.bartashevich on 3/1/2017.
 */

public class CustomData {
    public Object data;
    public CustomError error;
    public CustomData(Object o,CustomError error){
        this.error=error;
        this.data=o;
    }


}
