package hippocraticapps.glucopro.resturant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.view.View;
import android.widget.AdapterView.OnItemClickListener;


import hippocraticapps.glucopro.R;
import android.widget.AdapterView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.StrictMode;
import android.app.ListActivity;

public class Category_menu extends Activity{
	ListView listView ;
	String sample = "";
	String category;
	ArrayList<String> answer = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_menu_f);
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		Intent i = getIntent();
		category = i.getStringExtra("category");
		String serverURL = "http://129.123.7.140:10000/Restaurant/RestaurantMenuServlet?path="+category;
		answer = getlist(serverURL);

		listView = (ListView) findViewById(android.R.id.list);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, answer);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new OnItemClickListener() {
  		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
  			String product = ((TextView) arg1).getText().toString();
  			Intent i = new Intent(getApplicationContext(), Product_menu.class);
  			System.out.println(product);
  			String total = category + "/" +product;
  			String no_space = total.replaceAll(" ", "%20");
  			String product_str = no_space.replaceAll("&", "%26");
  			i.putExtra("product", product_str);
  			startActivity(i);
  			
  			//String serverURL1 = "http://129.123.7.140:10000/Restaurant/RestaurantMenuServlet?path=" + product;
  			//answer = getlist(serverURL1);

  		}
			// TODO Auto-generated method stub
			
		});
		
	}

	public ArrayList<String> getlist(String serverURL) {
		System.out.println(serverURL);
		ArrayList<String> list = new ArrayList<String>();
		//TextView uiUpdate = (TextView) findViewById(R.id.output);
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httppost = new HttpGet(serverURL);
		HttpResponse response;
		try {
			response = httpclient.execute(httppost);
		
		        HttpEntity ht = response.getEntity();

		        BufferedHttpEntity buf = new BufferedHttpEntity(ht);

		        InputStream is = buf.getContent();


		        BufferedReader r = new BufferedReader(new InputStreamReader(is));

		        StringBuilder total = new StringBuilder();
		        String line;
		        while ((line = r.readLine()) != null) {
		            list.add(line);
		            System.out.println(line);
		            }
		       // ListView listView = new ListView(this);
		        
		        /*listView = (ListView) findViewById(android.R.id.list);
		        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, list);
		        //ListView listViewItems = new ListView(this);
		        listView.setAdapter(adapter);
		        adapter.notifyDataSetChanged();
		        listView.setOnItemClickListener(new OnItemClickListenerListViewItem());
		        */
		        
		       
		        /* // listView.setAdapter(adapter);
		        // adapter.notifyDataSetChanged();
		         listView.setOnItemClickListener(new OnItemClickListener() {
			          public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			               
			              // selected item
			              String product = ((TextView) view).getText().toString();
			               
			              // Launching new Activity on selecting single List Item
			             // Intent i = new Intent(getApplicationContext(), SingleListItem.class);
			              // sending data to new activity
			              //i.putExtra("product", product);
			            //  startActivity(i);
			             
			          }
			        });*/
		        //uiUpdate.setText(total);
		// System.out.println(sample);		 
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		return list;
	}
}
