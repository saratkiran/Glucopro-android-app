package hippocraticapps.glucopro.activities;

import hippocraticapps.glucopro.R;
import hippocraticapps.glucopro.gps.Resturants_gps;
import hippocraticapps.glucopro.resturant.Category_menu;
import hippocraticapps.glucopro.resturant.Rest_menu_f;
import hippocraticapps.glucopro.resturant.listview;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class RestInput extends Activity {
	public TextView text;
	public LinearLayout linear;
	public SeekBar seek;
	public int seek_value =0;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rest_input);
       text=new TextView(this); 
       

	}
	public void rest_wgps(View v){
		final AlertDialog.Builder alert = new AlertDialog.Builder(this); 
		 
	        LinearLayout linear=new LinearLayout(this); 
	       
		    linear.setOrientation(1);
		    
		    //TextView text=new TextView(this); 
		    //text.setText("Hello Android"); 
		    //text.setPadding(10, 10, 10, 10); 

		    SeekBar seek=new SeekBar(this); 

	    alert.setTitle("Radius"); 
	    alert.setMessage("Set the radius from your location :"); 

	    linear.addView(seek); 
	    linear.addView(text); 
	
	    
	    alert.setView(linear);
	    
	   seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
	            //Do something here with new value
	        	//String value = Integer.toString(progress);
	        	String value = Integer.toString(progress*30) + " meters";
	        	text.setText(value);
	        	seek_value = progress;
	        }

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
	    });
	    	
	   
	    //text.setText(value);

	    alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() 
	    { 
	        public void onClick(DialogInterface dialog,int id)  
	        { 
	        	
	            
	            System.out.println(seek_value);
	            int value = seek_value*30;
	  			Intent i = new Intent(getApplicationContext(), Resturants_gps.class);
	  			String my_new_str = Integer.toString(value);
	  			//Toast.makeText(getApplicationContext(), my_new_str,Toast.LENGTH_LONG).show(); 
	  			i.putExtra("category", my_new_str);
	            startActivity(i);
	            finish(); 
	        } 
	    }); 

	    alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener()  
	    { 
	        public void onClick(DialogInterface dialog,int id)  
	        { 
	           // Toast.makeText(getApplicationContext(), "Cancel Pressed",Toast.LENGTH_LONG).show(); 
	            finish(); 
	        } 
	    }); 

	    alert.show(); 
		
        
    	return;
	}
	
	
	public void rest_manual(View v){
		Intent intenter = new Intent();
        intenter.setClass(this, Rest_menu_f.class);
        startActivity(intenter);
        
    	return;
	}

}
