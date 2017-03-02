package test.com.test.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import test.com.test.util.FieldsJSON;

/**
 * Created by s.bartashevich on 3/1/2017.
 */

public class User implements Parcelable {
    private String name="";
    private String surname="";
    private String photoUrl="";
    public long id;
    public  ArrayList<Vehicle>vehicles;
    public User(JSONObject jo) throws Exception{
        vehicles=new ArrayList<Vehicle>();
            id=jo.getLong(FieldsJSON.USER_ID);

            JSONObject owner=jo.getJSONObject(FieldsJSON.USER_OWNER);
            name=owner.getString(FieldsJSON.USER_NAME);
            surname=owner.getString(FieldsJSON.USER_SURNAME);
            try {
                photoUrl = owner.getString(FieldsJSON.PHOTO);
            }catch(Exception e){}
            JSONArray jVehicles=jo.getJSONArray(FieldsJSON.USER_VEHICLES);
            for (int i=0;i<jVehicles.length();i++){
                try {
                    Vehicle vehicle = new Vehicle(jVehicles.getJSONObject(i));
                    vehicles.add(vehicle);
                }catch(Exception e){e.printStackTrace();}
            }


    }

    public String getURL(){
        return photoUrl;
    }
    public String getFullName(){
        return name+" "+surname;
    }

    public User(Parcel source) {
        id = source.readLong();
        name = source.readString();
        surname = source.readString();
        photoUrl = source.readString();
        vehicles=new ArrayList<Vehicle>();
        source.readTypedList(vehicles, Vehicle.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(photoUrl);
        if (vehicles==null){
            vehicles=new ArrayList<Vehicle>();
        }
        dest.writeTypedList(vehicles);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }

    };
}
