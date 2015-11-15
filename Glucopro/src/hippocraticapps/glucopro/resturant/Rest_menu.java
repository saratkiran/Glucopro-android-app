package hippocraticapps.glucopro.resturant;

import hippocraticapps.glucopro.R;
import hippocraticapps.glucopro.activities.MealInput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Rest_menu extends Activity{
	public static String string_final;
	
	TextView outputText;
	TextView uiUpdate;
	   
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rest_menu_f);
 
        try {
			find();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
        
    }
	
	private void find() throws Exception {
		 //uiUpdate = (TextView) findViewById(R.id.output);
		
		//ListView listView = (ListView) findViewById(R.id.menu);
		
		 String serverURL = "http://129.123.7.140:10000/Restaurant/RestaurantMenuServlet?path=";
		 //Toast.makeText(getApplicationContext(), "msg", Toast.LENGTH_LONG).show();
		// URL yahoo = new URL(serverURL);
		// BufferedReader in = new BufferedReader(new InputStreamReader(yahoo.openStream()));

		 String inputLine = "start";
		/* ArrayList<String> list = new ArrayList<String>();
		 while ((inputLine = in.readLine()) != null){			 
				    	
		 }*/
		// in.close();
		 
		 uiUpdate.setText("Output : "+ inputLine);
		 
		/* ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, list);
		 listView.setAdapter(adapter);
         adapter.notifyDataSetChanged();*/
		 
         // Create Object and call AsyncTask execute Method
         new LongOperation().execute(serverURL);    
        
	}
	
		
	private class LongOperation  extends AsyncTask<String, Void, Void> {
		
        
        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(Rest_menu.this);
         
       // TextView uiUpdate = (TextView) findViewById(R.id.output);
         
        protected void onPreExecute() {
            // NOTE: You can call UI Element here.
             
            //UI Element
            //uiUpdate.setText("Output : ");
            Dialog.setMessage("Downloading source..");
            Dialog.show();
        }
        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {
                 
                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.
                 
                // Server url call by GET method
                HttpGet httpget = new HttpGet(urls[0]);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                Content = Client.execute(httpget, responseHandler);
                 
            } catch (ClientProtocolException e) {
                Error = e.getMessage();
                cancel(true);
            } catch (IOException e) {
                Error = e.getMessage();
                cancel(true);
            }
             
            return null;
        }
        
        protected void onPostExecute(Void unused){
            // NOTE: You can call UI Element here.
             
            // Close progress dialog
            Dialog.dismiss();
             
            if (Error != null) {
                 
                //uiUpdate.setText("Output : "+Error);
                 
            } else {
            	String value = Content;
            	sendcontent(value);
            	 String[] values = new String[] { "Android List View", 
                         "Adapter implementation",
                         "Simple List View In Android",
                         "Create List View Android", 
                         "Android Example", 
                         "List View Source Code", 
                         "List View Array Adapter", 
                         "Android Example List View" 
                        };
            	//this.setListAdapter(new ArrayAdapter<String>(this, R.id.menu, R.id.label, values));           	
                //uiUpdate.setText("Output : "+lines[1]);
    
             }
        }
		private void sendcontent(String value) {
			// TODO Auto-generated method stub
			string_final = value;
		}
		
	}


	
}

