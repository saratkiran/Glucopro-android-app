package hippocraticapps.glucopro.activities;
import hippocraticapps.glucopro.R;
import hippocraticapps.glucopro.bluetooth.BluetoothChat;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class Test extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
    }
    
    
    
    public void launchGlucoBridge(View v) {
    	
    	Toast.makeText(getApplicationContext(), "Connecting to Bluetooth...", 
				   Toast.LENGTH_SHORT).show();
    	
    	Intent intent = new Intent();
        intent.setClass(this, BluetoothChat.class);
        startActivity(intent);
        
    	return;
    }
}
