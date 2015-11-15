package hippocraticapps.glucopro.activities;
import hippocraticapps.glucopro.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;

public class Charts extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.charts);
    }
}
