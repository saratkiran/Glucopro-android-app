package hippocraticapps.glucopro.gps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.app.ProgressDialog;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import hippocraticapps.glucopro.R;
import hippocraticapps.glucopro.activities.Main;
import hippocraticapps.glucopro.resturant.Category_menu;
import hippocraticapps.glucopro.resturant.Rest_menu_f;

public class Resturants_gps extends Activity {
	
    private String latitude;
    private String longitude;
    private String provider;
    private final String APIKEY = "AIzaSyDw5J3g6ZC14Y-fJTjNjMfXje2BruWuLeM"; //REPLACE WITH YOUR OWN GOOGLE PLACES API KEY
    private int radius;
    private String type = "food";
    private StringBuilder query = new StringBuilder();
    private ArrayList<Place> places = new ArrayList<Place>();
    private ListView listView;
    MyLocation myLocation = new MyLocation();
    MyLocation.LocationResult locationResult;
    ProgressDialog progressDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gpslist);
        Intent i = getIntent();
    	String rad = i.getStringExtra("category");
    	int rad_val = Integer.parseInt(rad);
    	radius = rad_val;
        LocationManager locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);
        LocationListener myLocationListener = new MyLocationListener();

        locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                progressDialog.dismiss();
                new GetCurrentLocation().execute(latitude, longitude);
            }
        };

        MyRunnable myRun = new MyRunnable();
        myRun.run();

        progressDialog = ProgressDialog.show(Resturants_gps.this, "Finding your location",
                "Please wait...", true);
    }

    public static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        InputSource is = new InputSource(new StringReader(xml));

        return builder.parse(is);
    }

    private class GetCurrentLocation extends AsyncTask<Object, String, Boolean> {

        @Override
        protected Boolean doInBackground(Object... myLocationObjs) {
            if(null != latitude && null != longitude) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            assert result;
            query.append("https://maps.googleapis.com/maps/api/place/nearbysearch/xml?");
            query.append("location=" +  latitude + "," + longitude + "&");
            query.append("radius=" + radius + "&");
            query.append("types=" + type + "&");
            query.append("sensor=true&"); //Must be true if queried from a device with GPS
            query.append("key=" + APIKEY);
            new QueryGooglePlaces().execute(query.toString());
        }
    }

    /**
     * Based on: http://stackoverflow.com/questions/3505930
     */
    private class QueryGooglePlaces extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(args[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                Log.e("ERROR", e.getMessage());
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage());
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                Document xmlResult = loadXMLFromString(result);
                NodeList nodeList =  xmlResult.getElementsByTagName("result");
                for(int i = 0, length = nodeList.getLength(); i < length; i++) {
                    Node node = nodeList.item(i);
                    if(node.getNodeType() == Node.ELEMENT_NODE) {
                        Element nodeElement = (Element) node;
                        Place place = new Place();
                        Node name = nodeElement.getElementsByTagName("name").item(0);
                        Node vicinity = nodeElement.getElementsByTagName("vicinity").item(0);
                        Node rating = nodeElement.getElementsByTagName("rating").item(0);
                        Node reference = nodeElement.getElementsByTagName("reference").item(0);
                        Node id = nodeElement.getElementsByTagName("id").item(0);
                        Node geometryElement = nodeElement.getElementsByTagName("geometry").item(0);
                        NodeList locationElement = geometryElement.getChildNodes();
                        Element latLngElem = (Element) locationElement.item(1);
                        Node lat = latLngElem.getElementsByTagName("lat").item(0);
                        Node lng = latLngElem.getElementsByTagName("lng").item(0);
                        float[] geometry =  {Float.valueOf(lat.getTextContent()),
                                Float.valueOf(lng.getTextContent())};
                        int typeCount = nodeElement.getElementsByTagName("type").getLength();
                        String[] types = new String[typeCount];
                        for(int j = 0; j < typeCount; j++) {
                            types[j] = nodeElement.getElementsByTagName("type").item(j).getTextContent();
                        }
                        place.setVicinity(vicinity.getTextContent());
                        place.setId(id.getTextContent());
                        place.setName(name.getTextContent());
                        if(null == rating) {
                            place.setRating(0.0f);
                        } else {
                            place.setRating(Float.valueOf(rating.getTextContent()));
                        }
                        place.setReference(reference.getTextContent());
                        place.setGeometry(geometry);
                        place.setTypes(types);
                        places.add(place);
                    }
                }
                PlaceAdapter placeAdapter = new PlaceAdapter(Resturants_gps.this, R.layout.gpsrow, places);
                listView = (ListView)findViewById(R.id.httptestlist_listview);
                listView.setAdapter(placeAdapter);
                listView.setOnItemClickListener(new OnItemClickListener() {
              		@Override
            		public void onItemClick(AdapterView<?> arg0, View arg1,
            				int arg2, long arg3) {
              			 TextView name = (TextView) arg1.findViewById(R.id.htttptestrow_name);
              			String cat = ((TextView) name).getText().toString();
              			//String sample = agr1;
              			//final String text = tv.getText()
              			if (android.os.Build.VERSION.SDK_INT > 9) {
              			    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
              			    StrictMode.setThreadPolicy(policy);
              			}
              			int count =0;
              			Intent i = new Intent(getApplicationContext(), Category_menu.class);
              			//System.out.println(cat);
              			String serverURL = "http://129.123.7.140:10000/Restaurant/RestaurantMenuServlet?path=";
              			
              				//System.out.println(serverURL);
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
              				           // System.out.println(line);
              				            }
              				        
              				        for(String s: list){
              				        	if(cat.contains(s))
              				        	{
              				        		count++;
              				        		String no_space = cat.replaceAll(" ", "%20");
              				    			String my_new_str = no_space.replaceAll("&", "%26");
              				        		i.putExtra("category", my_new_str);
              				    			startActivity(i);
              				    			break;
              				        		
              				        	}
              				        	else{
              				        		//System.out.println(s);
              				        	}
              				        }
              				        if(count ==0){
              				        	new AlertDialog.Builder(Resturants_gps.this)
              				          .setTitle("Restaurant finder")
              				          .setMessage("Did not find the restaurant automatically. Do you want to search manually")
              				          .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
              				              public void onClick(DialogInterface dialog, int which) {
              				            	Intent intenter = new Intent();
              				            	Intent i = new Intent(getApplicationContext(), Rest_menu_f.class);
              				              	startActivity(i);
              				                  // continue with delete
              				              }
              				           })
              				          .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
              				              public void onClick(DialogInterface dialog, int which) { 
              				                  // do nothing
              				            	Intent intenter = new Intent();
              				            	Intent i = new Intent(getApplicationContext(), Main.class);
              				              	startActivity(i);
              				              }
              				           })
              				          .setIcon(android.R.drawable.ic_dialog_alert)
              				           .show();
              				        }
              				} catch (ClientProtocolException e) {
              					// TODO Auto-generated catch block
              					e.printStackTrace();
              				} catch (IOException e) {
              					// TODO Auto-generated catch block
              					e.printStackTrace();
              				}
          
              			
              			
              			//String no_space = cat.replaceAll(" ", "%20");
              			//String my_new_str = no_space.replaceAll("&", "%26");
              			//i.putExtra("category", my_new_str);
              			//startActivity(i);
              			
              			//String serverURL1 = "http://129.123.7.140:10000/Restaurant/RestaurantMenuServlet?path=" + product;
              			//answer = getlist(serverURL1);

              		}
            			// TODO Auto-generated method stub
            			
            		});

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage());
            }
        }
    }

    private class Place {
        public String vicinity;
        public float[] geometry; //array(0 => lat, 1 => lng)
        public String id;
        public String name;
        public float rating;
        public String reference;
        public String[] types;

        public String getVicinity() {
            return vicinity;
        }

        public void setVicinity(String vicinity) {
            this.vicinity = vicinity;
        }

        public float[] getGeometry() {
            return geometry;
        }

        public void setGeometry(float[] geometry) {
            this.geometry = geometry;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String[] getTypes() {
            return types;
        }

        public void setTypes(String[] types) {
            this.types = types;
        }
    }

    private class PlaceAdapter extends ArrayAdapter<Place> {
        public Context context;
        public int layoutResourceId;
        public ArrayList<Place> places;

        public PlaceAdapter(Context context, int layoutResourceId, ArrayList<Place> places) {
            super(context, layoutResourceId, places);
            this.layoutResourceId = layoutResourceId;
            this.places = places;
        }

        @Override
        public View getView(int rowIndex, View convertView, ViewGroup parent) {
            View row = convertView;
            if(null == row) {
                LayoutInflater layout = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE
                );
                row = layout.inflate(R.layout.gpsrow, null);
            }
            Place place = places.get(rowIndex);
            if(null != place) {
                TextView name = (TextView) row.findViewById(R.id.htttptestrow_name);
                TextView vicinity = (TextView) row.findViewById(
                        R.id.httptestrow_vicinity);
                if(null != name) {
                    name.setText(place.getName());
                }
                if(null != vicinity) {
                    vicinity.setText(place.getVicinity());
                }
            }
            return row;
        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    }

    public class MyRunnable implements Runnable {
        public MyRunnable() {
        }

        public void run() {
            myLocation.getLocation(getApplicationContext(), locationResult);
        }
    }
}