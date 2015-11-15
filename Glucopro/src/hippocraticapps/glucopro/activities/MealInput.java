package hippocraticapps.glucopro.activities;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.List;
import java.util.Random;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;


import glucopro.sqlhelper.*;
import glucopro.sqlmodel.Databasemanager;

import hippocraticapps.glucopro.R;
import hippocraticapps.glucopro.gps.Resturants_gps;
import hippocraticapps.glucopro.gps.googleapi;
import hippocraticapps.glucopro.resturant.Populatedb;
import hippocraticapps.glucopro.resturant.Rest_DatabaseManager;
import hippocraticapps.glucopro.resturant.Rest_menu;
import hippocraticapps.glucopro.resturant.Rest_menu_f;
import hippocraticapps.glucopro.resturant.listview;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MealInput extends Activity {
	protected static final String TAG = "DataAdapter";
	private SQLiteDatabase mDb; 
	TextView outputText;
	public static final String URL = "http://129.123.7.140:10000/Restaurant/RestaurantMenuServlet?path=";
	@Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meal_input);
        
        // Button b = (Button)findViewById(R.id.ImageView01);
        //final TextView text = (TextView)findViewById(R.id.meal_input_input);
        
    }
    
    	public void barcode_click(View v){
    		Intent intenter = new Intent();
            intenter.setClass(this, Rest_menu_f.class);
            startActivity(intenter);
            
        	return;
    		// Server Request URL
            //String serverURL = "http://androidexample.com/media/webservice/getPage.php";
             
            // Create Object and call AsyncTask execute Method
            //new LongOperation().execute(serverURL);
    	}
    	
        public void rest_click(View v) {
        	//Toast.makeText(getApplicationContext(), "Connecting to Bluetooth...", 
 				//   Toast.LENGTH_SHORT).show();
        	Intent intenter = new Intent();
            intenter.setClass(this, RestInput.class);
            startActivity(intenter);
            
        	return;
        	
        }
        
     //   b.setOnClickListener(new OnClickListener() {
		//	@Override
		//	public void onClick(View arg0) {
				//int value = Integer.parseInt(text.getText().toString());
				//Toast.makeText(getApplicationContext(), "Entered " + value + " into our records.", 
				//		   Toast.LENGTH_SHORT).show();
				
				//TODO: enter MealRecord into database
				
			//	Intent intent = new Intent();
                //intent.setClass(thisActivity, Recommend.class);
              //  startActivity(intent);
                
               // thisActivity.finish();
			//}
        //});
    
}
