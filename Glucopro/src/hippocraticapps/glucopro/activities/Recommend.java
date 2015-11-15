package hippocraticapps.glucopro.activities;
import java.util.Random;
import hippocraticapps.glucopro.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class Recommend extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation);
        
        TextView text = (TextView)findViewById(R.id.recommendationText);
        text.setText("We recommend a dose of " + new Random(System.currentTimeMillis()).nextInt(10));
        //TODO: actually calculate the recommendation
        
        Button b = (Button)findViewById(R.id.recommendation_ok);
        final Recommend thisActivity = this;
        
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(getApplicationContext(), "Good for you.", 
						   Toast.LENGTH_SHORT).show();
				
				thisActivity.finish();
			}
        });
    }
}
