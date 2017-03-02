package test.com.test.util;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import test.com.test.net.DataLoader;
import test.com.test.model.CustomData;
import test.com.test.model.ErrorParseJSON;
import test.com.test.model.User;

/**
 * Created by s.bartashevich on 3/1/2017.
 */

public class DataParser {

    public static CustomData getUsers(String data){ //parsing users list
        ErrorParseJSON error=null;
        ArrayList<User> users=new ArrayList<User>();
        try {
            JSONObject jObject = new JSONObject(data);
            JSONArray jArray=jObject.getJSONArray(FieldsJSON.DATA);
            for (int i=0;i<jArray.length();i++){
                try {
                    User user = new User(jArray.getJSONObject(i));
                    users.add(user);
                }catch(Exception e){e.printStackTrace();} //user with wrong data is ignoring
            }
        }catch(Exception e){
            error=new ErrorParseJSON(DataLoader.stackTraceToString(e));
            e.printStackTrace();
        }

        return new CustomData(users,error);
    }

    public static CustomData getRoute(String data){ //parsing route points
        ErrorParseJSON error=null;
        ArrayList<LatLng> route=new ArrayList<LatLng>();
        try {
            JSONObject jObject = new JSONObject(data);
            JSONArray jArray=jObject.getJSONArray(FieldsJSON.POINTS);
            for (int i=0;i<jArray.length();i++){
                try {
                    JSONObject jPoint =jArray.getJSONObject(i).getJSONObject(FieldsJSON.POINTS_LOCATION);
                    LatLng point = new LatLng(jPoint.getDouble(FieldsJSON.POINTS_LATITUDE),jPoint.getDouble(FieldsJSON.POINTS_LONGITUDE));
                    route.add(point);
                }catch(Exception e){e.printStackTrace();} //user with wrong data is ignoring
            }
        }catch(Exception e){
            error=new ErrorParseJSON(DataLoader.stackTraceToString(e));
            e.printStackTrace();
        }

        return new CustomData(route,error);
    }


}
