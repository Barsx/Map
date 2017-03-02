package test.com.test.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import test.com.test.ActivityMap;
import test.com.test.R;
import test.com.test.model.Vehicle;
import test.com.test.net.ImageDownloaderTask;

/**
 * Created by s.bartashevich on 3/2/2017.
 */

public class MarkerAdapter implements GoogleMap.InfoWindowAdapter {
    //class adapter for markers popups
    LayoutInflater inflater=null;
    private ArrayList<Vehicle>vehicles;
    Context context;
    private ActivityMap activity;

    public MarkerAdapter(LayoutInflater inflater, ArrayList<Vehicle>v,ActivityMap a) {
        this.inflater=inflater;
        vehicles=v;
        this.context=a.getApplicationContext();
        activity=a;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return(null);
    }

    @Override
    public View getInfoContents(final Marker marker) {

        final View popup=inflater.inflate(R.layout.marker_tooltip, null);
        popup.setOnClickListener(null);
        TextView tv=(TextView)popup.findViewById(R.id.title);
        final ImageView image =(ImageView)popup.findViewById(R.id.icon);

        tv.setText(marker.getTitle());
        tv=(TextView)popup.findViewById(R.id.snippet);
        tv.setText(marker.getSnippet());


            for (int i = 0; i < vehicles.size(); i++) {
                final Vehicle vehicle = vehicles.get(i);
                if (marker.equals(vehicle.marker)) { //checking which vehicle corresponds to marker

                    if (vehicle.bitmap==null) {
                        ImageDownloaderTask.BitmapListener listener = new ImageDownloaderTask.BitmapListener() {
                            @Override
                            public void onFailed() {


                            }

                            @Override
                            public void onBitmapLoaded(Bitmap b, int pos) {
                                vehicle.bitmap = b;
                                marker.showInfoWindow(); //updating view

                            }
                        };
                        int valueInPixels = (int) context.getResources().getDimension(R.dimen.bitmap_map_icon_max_size);
                        new ImageDownloaderTask(listener, valueInPixels, vehicle.getPhotoURL(), context, 0).execute(); //start loading bitmap
                    }else{
                        image.setImageBitmap(vehicle.bitmap);
                    }

                }
            }


        return(popup);
    }
}