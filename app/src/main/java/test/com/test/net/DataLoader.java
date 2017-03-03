package test.com.test.net;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import test.com.test.model.CustomData;
import test.com.test.model.CustomError;
import test.com.test.model.ErrorConnection;
import test.com.test.util.DataParser;

/**
 * Created by s.bartashevich on 3/1/2017.
 */

public class DataLoader {




    public static CustomData getData(String https_url){    //loading data from http
        System.out.println("!!!! request="+https_url);
        ErrorConnection error =null;
        String result="";
        URL url;
        HttpURLConnection urlConnection;

        try {
            url = new URL(https_url);
            urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                result = readStream(urlConnection.getInputStream());
                System.out.println("!!!! result="+result);
            }

        } catch (Exception e) {
            error=new ErrorConnection(stackTraceToString(e));
            e.printStackTrace();
        }
        return new CustomData(result,error);
    }



    public static CustomData getTrack(String https_url){    //loading data from http
        System.out.println("!!!! request="+https_url);
        CustomError error =null;
        ArrayList<ArrayList<LatLng>> track=new ArrayList<ArrayList<LatLng>>();
        URL url;
        HttpURLConnection urlConnection;

        try {
            url = new URL(https_url);
            urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
              //  CustomData dt=DataParser.getRoute(readStream(urlConnection.getInputStream()));

                    JsonFactory f = new JsonFactory();
                    InputStreamReader tmpReader = new InputStreamReader(urlConnection.getInputStream());
                    JsonParser jp = f.createJsonParser(tmpReader);
                    CustomData dt=DataParser.getRoute(jp);

                if (dt.error==null) {
                    track=(ArrayList<ArrayList<LatLng>>)dt.data;
                    ArrayList<ArrayList<LatLng>> objs=(ArrayList<ArrayList<LatLng>>)dt.data;
                    ArrayList<LatLng> start=objs.get(0); //receiving points from request and adding to map
                    for (int i=0;i<start.size();i++){
                        System.out.println("!!!! coords= " + " "+start.get(i).latitude+" "+start.get(i).longitude);
                    }
                }else{
                    error=dt.error;
                }

            }

        } catch (Exception e) {
            error=new ErrorConnection(stackTraceToString(e));
            e.printStackTrace();
        }
        return new CustomData(track,error);
    }




    public static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    public static String stackTraceToString(Throwable e) {
        String retValue = null;
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            retValue = sw.toString();
        } finally {
            try {
                if(pw != null)  pw.close();
                if(sw != null)  sw.close();
            } catch (IOException ignore) {}
        }
        return retValue;
    }


}
