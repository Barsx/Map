package test.com.test.util;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
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

    static boolean data_was;
    public static CustomData getRoute(JsonParser jsonParser) { //parsing route points
        ArrayList<LatLng> start = new ArrayList<LatLng>();
        ArrayList<LatLng> end = new ArrayList<LatLng>();

        ArrayList<ArrayList<LatLng>> routes = new ArrayList<ArrayList<LatLng>>();
        routes.add(start);
        routes.add(end);
        ErrorParseJSON error = null;
        try {
            JsonToken token=null;
            data_was=false;


            while (token != JsonToken.END_OBJECT&&token!=JsonToken.END_ARRAY) {
                token= jsonParser.nextToken();

                if (token == JsonToken.START_OBJECT) {
                    System.out.println("!!!! 1 a route found " + " "+jsonParser.getCurrentName());
                    skipObject(jsonParser, token,routes);
                }else
                if (token == JsonToken.START_ARRAY) {
                    System.out.println("!!!! 1 a skipping array "+" " + jsonParser.getCurrentName());
                    if (FieldsJSON.ROUTES.equals(jsonParser.getCurrentName())){
                        if (data_was){
                            skipArray(jsonParser, token,routes);
                        }else{
                            data_was=true;
                            fillArray(jsonParser, token,routes);
                        }

                    }else {
                        skipArray(jsonParser, token,routes);
                    }
                }



            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new CustomData(routes,error);
    }
    private static void skipObject(JsonParser jsonParser,JsonToken token,ArrayList<ArrayList<LatLng>> data)throws Exception{
        token= jsonParser.nextToken();
        while (token != JsonToken.END_OBJECT&&token != JsonToken.END_ARRAY) {
            token= jsonParser.nextToken();
            if (token == JsonToken.START_OBJECT) {
                System.out.println("!!!! o skipping object -o" +" "+ jsonParser.getCurrentName());
                skipObject(jsonParser, token,data);
            }else
            if (token == JsonToken.START_ARRAY) {
                System.out.println("!!!! a skipping array -o"+" " + jsonParser.getCurrentName());
                if (FieldsJSON.ROUTES.equals(jsonParser.getCurrentName())){
                    if (data_was){
                        skipArray(jsonParser, token,data);
                    }else{
                        data_was=true;
                        fillArray(jsonParser, token,data);

                    }

                }else {
                    skipArray(jsonParser, token,data);
                }
            }

        }
    }
    private static void skipArray(JsonParser jsonParser,JsonToken token,ArrayList<ArrayList<LatLng>> data)throws Exception{
        token= jsonParser.nextToken();
        while (token != JsonToken.END_OBJECT&&token != JsonToken.END_ARRAY) {
            token= jsonParser.nextToken();
            if (token == JsonToken.START_OBJECT) {
                System.out.println("!!!! o skipping object -a " +" "+ jsonParser.getCurrentName());
                skipObject(jsonParser, token,data);
            }else
            if (token == JsonToken.START_ARRAY) {
                System.out.println("!!!! a skipping array -a"+" " + jsonParser.getCurrentName());
                if (FieldsJSON.ROUTES.equals(jsonParser.getCurrentName())){
                    if (data_was){

                        skipArray(jsonParser, token,data);
                    }else{
                        data_was=true;
                        fillArray(jsonParser, token,data);

                    }

                }else {
                    skipArray(jsonParser, token,data);
                }
            }


        }
    }

    private static void fillArray(JsonParser jsonParser,JsonToken token,ArrayList<ArrayList<LatLng>> data)throws Exception{
        token= jsonParser.nextToken();
        while (token != JsonToken.END_OBJECT&&token != JsonToken.END_ARRAY) {
            token= jsonParser.nextToken();

            if (token == JsonToken.START_OBJECT) {
                System.out.println("!!!! a filling object -a" +" "+ jsonParser.getCurrentName());
                fillObject(jsonParser,token,data);
            }
            if (token == JsonToken.START_ARRAY) {

                System.out.println("!!!! o filling array -a"+" " + jsonParser.getCurrentName());
                fillArray(jsonParser, token,data);

            }


        }
    }
    private static void fillObject(JsonParser jsonParser,JsonToken token,ArrayList<ArrayList<LatLng>> data)throws Exception{
        token= jsonParser.nextToken();
        while (token != JsonToken.END_OBJECT&&token != JsonToken.END_ARRAY) {
            token= jsonParser.nextToken();

            if (token == JsonToken.START_OBJECT) {

                System.out.println("!!!! a filling object -o" +" "+ jsonParser.getCurrentName());
                if (FieldsJSON.LOCATION_START.equals(jsonParser.getCurrentName())){
                    //  fillStart(jsonParser, token);
                    parseStart(jsonParser,token,data.get(0));
                }else
                if (FieldsJSON.LOCATION_END.equals(jsonParser.getCurrentName())){
                    //  fillEnd(jsonParser, token);
                    parseEnd(jsonParser,token,data.get(1));
                }else {

                    fillObject(jsonParser,token,data);
                }

            }
            if (token == JsonToken.START_ARRAY) {
                System.out.println("!!!! o filling array -o" + " " + jsonParser.getCurrentName());
                if (FieldsJSON.LEGS.equals(jsonParser.getCurrentName())){
                    fillLegsA(jsonParser, token,data);
                }else {

                    fillArray(jsonParser, token,data);
                }

            }


        }
    }
    private static void fillLegsO(JsonParser jsonParser,JsonToken token,ArrayList<ArrayList<LatLng>> data)throws Exception{
        token= jsonParser.nextToken();
        while (token != JsonToken.END_OBJECT&&token != JsonToken.END_ARRAY) {
            token= jsonParser.nextToken();

            if (token == JsonToken.START_OBJECT) {
                System.out.println("!!!! a legsA object " +" "+ jsonParser.getCurrentName());
                if (FieldsJSON.LOCATION_START.equals(jsonParser.getCurrentName())){
                    //  fillStart(jsonParser, token);
                    parseStart(jsonParser,token,data.get(0));
                }else
                if (FieldsJSON.LOCATION_END.equals(jsonParser.getCurrentName())){
                    //  fillEnd(jsonParser, token);
                    parseEnd(jsonParser,token,data.get(1));
                }else {

                    fillLegsO(jsonParser, token,data);
                }
                // fillLegsO(jsonParser, token);
            }
            if (token == JsonToken.START_ARRAY) {

                System.out.println("!!!! o legsA array "+" " + jsonParser.getCurrentName());
                fillLegsA(jsonParser,token,data);

            }


        }
    }

    public static void parseStart(JsonParser parser,JsonToken token,ArrayList<LatLng> data) throws Exception {
        double la=-100;
        double lo=-100;

        while ((token = parser.nextToken()) != JsonToken.END_OBJECT) {

            if (token == JsonToken.FIELD_NAME) {
                String fieldName = parser.getCurrentName();
                parser.nextToken();

                System.out.println(fieldName);
                if(FieldsJSON.POINTS_LATITUDE.equalsIgnoreCase(fieldName)){
                    la=Double.parseDouble(parser.getText());
                    System.out.println("!!! start la: " + parser.getText());
                }
                if(FieldsJSON.POINTS_LONGITUDE.equalsIgnoreCase(fieldName)){
                    lo=Double.parseDouble(parser.getText());
                    System.out.println("!!! start lo: " + parser.getText());
                }

            }
        }
        if (la>-95&&lo>-95) {
            System.out.println("!!! adding start : " + la+" "+lo);
            data.add(new LatLng(la, lo));
        }

    }
    public static void parseEnd(JsonParser parser,JsonToken token,ArrayList<LatLng> data) throws Exception {

        double la=-100;
        double lo=-100;

        while ((token = parser.nextToken()) != JsonToken.END_OBJECT) {

            if (token == JsonToken.FIELD_NAME) {
                String fieldName = parser.getCurrentName();
                parser.nextToken();

                System.out.println(fieldName);
                if(FieldsJSON.POINTS_LATITUDE.equalsIgnoreCase(fieldName)){
                    la=Double.parseDouble(parser.getText());
                    System.out.println("!!! end la: " + parser.getText());
                }
                if(FieldsJSON.POINTS_LONGITUDE.equalsIgnoreCase(fieldName)){
                    lo=Double.parseDouble(parser.getText());
                    System.out.println("!!! end lo: " + parser.getText());
                }

            }
        }
        if (la>-95&&lo>-95) {
            System.out.println("!!! adding end : " + la+" "+lo);
            data.add(new LatLng(la, lo));
        }


    }


    private static void fillLegsA(JsonParser jsonParser,JsonToken token,ArrayList<ArrayList<LatLng>> data)throws Exception{
        token= jsonParser.nextToken();
        while (token != JsonToken.END_OBJECT&&token != JsonToken.END_ARRAY) {
            token= jsonParser.nextToken();

            if (token == JsonToken.START_OBJECT) {
                System.out.println("!!!! a legsO object " +" "+ jsonParser.getCurrentName());
                if (FieldsJSON.LOCATION_START.equals(jsonParser.getCurrentName())){
                    //  fillStart(jsonParser, token);
                    parseStart(jsonParser,token,data.get(0));
                }else
                if (FieldsJSON.LOCATION_END.equals(jsonParser.getCurrentName())){
                    //  fillEnd(jsonParser, token);
                    parseEnd(jsonParser,token,data.get(1));
                }else {

                    fillLegsO(jsonParser, token,data);
                }
            }
            if (token == JsonToken.START_ARRAY) {

                System.out.println("!!!! o legsO array "+" " + jsonParser.getCurrentName());
                fillLegsA(jsonParser,token,data);

            }


        }
    }

 /*       public static CustomData getRoute(String data){ //parsing route points
            ErrorParseJSON error=null;
            ArrayList<LatLng> route=new ArrayList<LatLng>();
            try {
                JSONObject jObject = new JSONObject(data);
                JSONArray jRoutes=jObject.getJSONArray(FieldsJSON.ROUTES);
                if (jRoutes.length()>0){
                    JSONArray jLegs=((JSONObject)jRoutes.get(0)).getJSONArray(FieldsJSON.LEGS);
                    for (int i=0;i<jLegs.length();i++){
                        System.out.println("!!!! object" + jLegs.get(i).toString());
                    }

                }
            }catch(Exception e){
                error=new ErrorParseJSON(DataLoader.stackTraceToString(e));
                e.printStackTrace();
            }

            return new CustomData(route,error);
        }
*/

}
