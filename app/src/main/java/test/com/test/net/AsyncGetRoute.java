package test.com.test.net;



import android.os.AsyncTask;

import test.com.test.model.Coordinates;
import test.com.test.model.Vehicle;
import test.com.test.util.Constants;
import test.com.test.util.DataParser;
import test.com.test.model.CustomData;


public class AsyncGetRoute extends AsyncTask<Void, Void, Void> {

	AsyncListener listener;
	String request;
	CustomData result;
	  
	public AsyncGetRoute(AsyncListener lst, Vehicle vehicle, Coordinates coordinates) {
		super();
		request="https://roads.googleapis.com/v1/nearestRoads?points="+vehicle.coordinates.latitude+","+vehicle.coordinates.longitude+"|"+coordinates.latitude+","+coordinates.longitude+"&key=" + Constants.GOOGLE_KEY;

		result=null;
		listener=lst;
	}

	@Override
	protected final void onPreExecute() {
		if(listener!=null) {
            listener.onTaskPrepared();
        }
		
	}  

	protected Void doInBackground(Void... params) {

		result=DataLoader.getData(request);
		return null;
	}

	protected void onPostExecute(Void v) {

		if(listener!=null) {
			if (result.error==null) {
				listener.onTaskCompleted(DataParser.getRoute((String)result.data));
			}else{
				listener.onTaskCompleted(result);
			}
		}
	}
}
