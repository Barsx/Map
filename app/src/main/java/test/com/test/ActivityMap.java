package test.com.test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import test.com.test.model.Coordinates;
import test.com.test.model.CustomData;
import test.com.test.net.AsyncGetRoute;
import test.com.test.util.FieldsJSON;
import test.com.test.model.User;
import test.com.test.model.Vehicle;
import test.com.test.net.AsyncListener;
import test.com.test.net.DataLoader;
import test.com.test.ui.adapters.MarkerAdapter;

public class ActivityMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int delay = 30000; //time between updates of vehicles positions
    private long userID;
    private ArrayList<Vehicle>vehicles; //current vehicles of user
    private boolean markerWas;
    private MarkerAdapter adapter; //adapter for markers popup
    private RunThread rp; //thread
    public Coordinates coordinates;
    LocListener locListener;
    LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        markerWas=false;
        coordinates=null;
        Bundle extras = getIntent().getExtras();

        if (extras != null) { //receiving user and vehicles from ActivityMain
            if(extras.getParcelable("user")!=null) {
                User user = extras.getParcelable("user");
                userID=user.id;
                vehicles=user.vehicles;

            }
        }
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locListener = new LocListener();

       /* lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locListener);   // requires correct request for permissions
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,locListener);*/
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        adapter=new MarkerAdapter(ActivityMap.this.getLayoutInflater(),vehicles,this);
     /*   mMap.setInfoWindowAdapter(adapter);
        mMap.setMyLocationEnabled(true); // requires correct request for permissions*/
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {  //make route to marker when it's popup is clicked
                for (int i = 0; i < vehicles.size(); i++) {
                    final Vehicle vehicle = vehicles.get(i);
                    if (marker.equals(vehicle.marker)) {
                        Coordinates c=new Coordinates(56.5870975,25.0097271);
                        makeRoute(vehicle,c);

                    }
                }
            }
        });
        rp=new RunThread();
        rp.start();

    }

    Polyline line_start;
    Polyline line_end;
    public void makeRoute(Vehicle vehicle,final Coordinates c){
       if (line_start!=null){
           line_start.remove();
       }
        if (line_end!=null){
            line_end.remove();
        }
        if (c!=null) {
            AsyncListener asyncListener = new AsyncListener() {

                @Override
                public void onTaskCompleted(CustomData o) {
                    System.out.println("!!!! completed");
                /*    if (o.error == null) {*/
                        ArrayList<ArrayList<LatLng>> objs=(ArrayList<ArrayList<LatLng>>)o.data;
                        ArrayList<LatLng> start=objs.get(0); //receiving points from request and adding to map

                        PolylineOptions lineOptions = new PolylineOptions();
                        lineOptions.addAll(start);
                        lineOptions.width(2);
                        lineOptions.color(Color.RED);
                        line_start=mMap.addPolyline(lineOptions);


                        ArrayList<LatLng> end=objs.get(1); //receiving points from request and adding to map
                        PolylineOptions endlineOptions = new PolylineOptions();
                        endlineOptions.addAll(end);
                        endlineOptions.width(2);
                        endlineOptions.color(Color.BLUE);
                      //  line_end=mMap.addPolyline(endlineOptions);

                        moveCamera(c,2000);
                 /*   } else {
                        //error
                    }*/

                }

                @Override
                public void onTaskPrepared() {

                }
            };

            new AsyncGetRoute(asyncListener, vehicle,c).execute();
        }
    }

    public class LocListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location.getAccuracy()<1000){ //updating user's coordinates
                if (coordinates==null){
                    coordinates=new Coordinates(location.getLatitude(),location.getLongitude());
                }else{
                    coordinates.latitude=location.getLatitude();
                    coordinates.longitude=location.getLongitude();
                }
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {


        }

    }

    @Override
    public void onBackPressed() { //returning to ActivityMain
        Intent intent = new Intent(ActivityMap.this, ActivityMain.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onDestroy(){
        if (rp!=null){  //we don't need working thread more
            rp.exit=true;
        }
    /*    try {
            lm.removeUpdates(locListener);

        } catch (Exception e) {
        }*/
        super.onDestroy();
    }

    public class RunThread extends Thread {
       boolean exit;
        public void run() {
            exit=false;
            while(!exit) {
                updateData();  //updating data and sleeping untill next update
                try {
                    sleep(delay);
                }catch(Exception e){}
            }

        }

    }
    Vehicle vehicle;
    void updateData(){

        String request="http://mobi.connectedcar360.net/api/?op=getlocations&userid="+userID;

    //    final CustomData result= DataLoader.getData(request); //receiving data from API
         final CustomData result= new CustomData("{\"data\":[{\"vehicleid\":3,\"lat\":56.97417,\"lon\":24.136508},{\"vehicleid\":4,\"lat\":57.000104,\"lon\":24.131895}]}",null); //receiving data from API
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
        if (result.error==null){ //if no errord uccured
            String data=(String)result.data;
            try {
                JSONObject jObject = new JSONObject(data);
                JSONArray jArray=jObject.getJSONArray(FieldsJSON.DATA);
                JSONObject arrayItem;
                long itemID;
                String coordinatesBuf_la;
                String coordinatesBuf_lo;
                for (int i=0;i<jArray.length();i++) {
                    arrayItem=jArray.getJSONObject(i);
                    itemID=arrayItem.getLong(FieldsJSON.VEHICLE_ID);
                    vehicle=null;
                    for (int k=0;k<vehicles.size();k++){  //cheching which vehicle should we update
                        if (vehicles.get(k).getID()==itemID){
                            vehicle=vehicles.get(k);
                        }
                    }
                    if (vehicle!=null) { //updating vehicle
                        try {
                            coordinatesBuf_la=arrayItem.getString(FieldsJSON.LATITUDE);
                            coordinatesBuf_lo=arrayItem.getString(FieldsJSON.LONGITUDE);
                            if (coordinatesBuf_la!=null&&coordinatesBuf_lo!=null) {                 // checking that coordinates are not null
                                if (!coordinatesBuf_la.equals("null")&&!coordinatesBuf_lo.equals("null")) {
                                    vehicle.coordinates.latitude = Double.parseDouble(coordinatesBuf_la);

                                    vehicle.coordinates.longitude = Double.parseDouble(coordinatesBuf_lo);

                                            if (vehicle.addedToMap) { //marker is adding only if it wasn't added
                                                if (vehicle.marker == null) {
                                                    vehicle.addMarker(mMap, getApplicationContext());

                                                    if (!markerWas) {
                                                        moveCamera(vehicle.coordinates,1000);

                                                    }
                                                } else {
                                                    vehicle.marker.setPosition(new LatLng(vehicle.coordinates.latitude, vehicle.coordinates.longitude));
                                                }
                                            } else {

                                                vehicle.addMarker(mMap, getApplicationContext());
                                                if (!markerWas) {
                                                    moveCamera(vehicle.coordinates,1000);
                                                }
                                            }

                                }
                            }
                        }catch(Exception e){e.printStackTrace();}
                    }else{
                        //vehicle not in list
                    }

                }

            }catch(Exception e){e.printStackTrace();}

        }else{

        }
            }
        });
    }
    private void moveCamera(Coordinates coordinates,int time){ //moving camera to vehicle with animation during 1sec
        markerWas=true;
        LatLng center = new LatLng(coordinates.latitude, coordinates.longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(center)      // Sets the center of the map to Mountain View
                .zoom(12)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),time,null);

    }
}
