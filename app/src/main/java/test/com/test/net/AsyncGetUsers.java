package test.com.test.net;
import android.os.AsyncTask;
import test.com.test.util.DataParser;
import test.com.test.model.CustomData;


public class AsyncGetUsers extends AsyncTask<Void, Void, Void> {

	AsyncListener listener;
	String request;
	CustomData result;
	  
	public AsyncGetUsers(AsyncListener lst) {
		super(); 
		this.request="http://mobi.connectedcar360.net/api/?op=list";;
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
		String res="{\"data\": [{\"userid\": 1,\"owner\": {\"name\": \"Aigars\",\"surname\": \"Mūrnieks\",\"foto\": \"http://vignette2.wikia.nocookie.net/batman/images/8/86/BatmanGeorgeClooney.jpg/revision/latest?cb=20080301232020\"},\"vehicles\": [{\"vehicleid\": 1,\"make\": \"Volvo\",\"model\": \"S60\",\"year\": \"2005\",\"color\": \"#000000\",\"vin\": \"1GC2GUAL4A1132419\",\"foto\": \"http://volvoperformanceshop.com/images/header_volvo_rotation5.jpg\"}]},{\"userid\": 2,\"owner\": {\"name\": \"Pauls\",\"surname\": \"Riekstiņš\",\"foto\": \"https://static.squarespace.com/static/51b3dc8ee4b051b96ceb10de/51ce6099e4b0d911b4489b79/51ce61a3e4b0d911b449b106/1351960297005/1000w/adam_west_batman.jpeg\"},\"vehicles\": [{\"vehicleid\": 2,\"make\": \"Saab\",\"model\": \"93\",\"year\": \"2008\",\"color\": \"#ff23ff\",\"vin\": \"2HGFG11877H504035\",\"foto\": \"http://photoautotuning.com/wp-content/uploads/2010/06/2350307946_864fbbe1fe.jpg\"}]},{\"userid\": 3,\"owner\": {\"name\": \"Raivis\",\"surname\": \"Zemītis\",\"foto\": \"http://vignette3.wikia.nocookie.net/batman/images/5/58/Batman_(Ben_Affleck).jpg/revision/latest?cb=20160825113055\"},\"vehicles\": [{\"vehicleid\": 3,\"make\": \"Toyota\",\"model\": \"RAV4\",\"year\": \"2007\",\"color\": \"#c2b280\",\"vin\": \"5TFEM5F18EX043734\",\"foto\": \"https://s-media-cache-ak0.pinimg.com/736x/a0/68/0d/a0680da9e0730971bf817ae7da78d2d5.jpg\"},{\"vehicleid\": 4,\"make\": \"Mercedes-Benz\",\"model\": \"Citaro O530L\",\"year\": \"2009\",\"color\": \"#0000ff\",\"vin\": \"5TEVL52N34Z376975\",\"foto\": \"https://upload.wikimedia.org/wikipedia/lv/7/79/Mercedes_Benz_O530.jpg\"}]},{}]}";

		result=new CustomData(res,null);
	//	result=DataLoader.getData(request);
		return null;
	}

	protected void onPostExecute(Void v) {

		if(listener!=null) {
			if (result.error==null) {
				listener.onTaskCompleted(DataParser.getUsers((String)result.data));
			}else{
				listener.onTaskCompleted(result);
			}
		}
	}
}
