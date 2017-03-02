package test.com.test.model;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import test.com.test.R;
import test.com.test.util.FieldsJSON;

/**
 * Created by s.bartashevich on 3/1/2017.
 */

public class Vehicle implements Parcelable{
    private String vendor="";
    private String model="";
    private String photoUrl="";
    private long id;
    private int color;
    public Coordinates coordinates;
    public boolean addedToMap;
    public Marker marker;
    public Bitmap bitmap;

    public Vehicle(JSONObject jo) {
        try{
            vendor=jo.getString(FieldsJSON.VEHICLE_VENDOR);
            model=jo.getString(FieldsJSON.VEHICLE_MODEL);
            color= Color.parseColor(jo.getString(FieldsJSON.COLOR));
            try{
                photoUrl=jo.getString(FieldsJSON.PHOTO);
            }catch(Exception e){}
            id=jo.getLong(FieldsJSON.VEHICLE_ID);

        }catch (Exception e){e.printStackTrace();}
        coordinates=new Coordinates(0,0);
        addedToMap=false;
        bitmap=null;
    }
    public void addMarker(GoogleMap mMap,final Context context){
        LatLng lalo = new LatLng(coordinates.latitude, coordinates.longitude);
        Marker m= mMap.addMarker(new MarkerOptions().position(lalo).title(getFullName()).icon(getMarkerIcon(color)).snippet(context.getString(R.string.color)+" #"+Integer.toHexString( color & 0x00ffffff )));
        addedToMap=true;
        marker=m;

    }

    public BitmapDescriptor getMarkerIcon(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }
    public Vehicle(Parcel source) {
        id = source.readLong();
        vendor = source.readString();
        model = source.readString();
        photoUrl = source.readString();
        color=source.readInt();
        coordinates=new Coordinates(source.readDouble(),source.readDouble());
        addedToMap=false;
    }

    public String getFullName(){
        return vendor+" "+model;
    }

    public String getVendor(){
        return vendor;
    }
    public String getModel(){
        return model;
    }
    public String getPhotoURL(){
        return photoUrl;
    }
    public long getID(){
        return id;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(vendor);
        dest.writeString(model);
        dest.writeString(photoUrl);
        dest.writeInt(color);
        dest.writeDouble(coordinates.latitude);
        dest.writeDouble(coordinates.longitude);
    }

    public static final Creator<Vehicle> CREATOR = new Creator<Vehicle>() {
        @Override
        public Vehicle[] newArray(int size) {
            return new Vehicle[size];
        }

        @Override
        public Vehicle createFromParcel(Parcel source) {
            return new Vehicle(source);
        }
    };
}
