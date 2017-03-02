package test.com.test.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import test.com.test.model.CustomData;
import test.com.test.model.ErrorConnection;

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
