package test.com.test.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;

import test.com.test.util.DatabaseHelper;

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
   private int max;
    private String url;
    Context ctx;
    private BitmapListener listener;
    private int position;

    public interface BitmapListener {
        void onFailed();
        void onBitmapLoaded(Bitmap b,int pos);
    }

    public ImageDownloaderTask(BitmapListener lst,int w,String ur,Context c,int pos) {
        listener=lst;
        this.max=w;
        this.url=ur;
        ctx=c;
        position=pos;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
    	DatabaseHelper dbh=new DatabaseHelper(ctx);
    	
    	Bitmap b=dbh.getBitmap(url);
    	if (b==null){
    			try{
    			b=downloadBitmap(url);
    			

	            b= cropBitmap(b,max);
		        if (b!=null){
		        	dbh.addEntry(url, b);
		        }
    			}catch(Exception e){}
    	}else{
           // b=cropBitmap(b,max);
        }
        return b;
    }


    public static Bitmap cropBitmap(Bitmap b,int max) {
        int w=b.getWidth();
        int h=b.getHeight();
        int new_w;
        int new_h;

        if (w>h){
            new_w=max;
            new_h=(int)(((double)max)*h/w);
        }else{
            new_h=max;
            new_w=(int)(((double)max)*w/h);
        }
        b=getResizedBitmap(b,new_w,new_h);
        return b;
    }


    public static Bitmap getResizedBitmap(Bitmap bm,int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BITMAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);

        return resizedBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (listener!=null){ //returning result
            if (bitmap==null){
                listener.onFailed();
            }else{
                listener.onBitmapLoaded(bitmap,position);
            }
        }

    }


    public static  Bitmap  downloadBitmap(String url) {
        Bitmap bitmap = null;
        InputStream stream = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        try {
            stream = getHttpConnection(url);
            bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
            stream.close();
        }
        catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("downloadImage"+ e1.toString());
        }
        return bitmap;
    }
    public static  InputStream getHttpConnection(String urlString)  throws IOException {

        InputStream stream = null;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("downloadImage" + ex.toString());
        }
        return stream;
    }

}
