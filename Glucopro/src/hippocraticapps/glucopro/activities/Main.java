package hippocraticapps.glucopro.activities;
import java.util.ArrayList;
import hippocraticapps.glucopro.R;
import hippocraticapps.glucopro.adapters.ImageAdapter;
import hippocraticapps.glucopro.adapters.LabeledImage;
import hippocraticapps.glucopro.database.GlucoDBAdapter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.widget.AdapterView.OnItemClickListener;

public class Main extends Activity {
    private GridView gridView;
    private ArrayList<LabeledImage> gridItems;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final Main thisActivity = this;
        
        GlucoDBAdapter.getInstance(getApplicationContext());

        gridItems = new ArrayList<LabeledImage>();
        gridItems.add(new LabeledImage("Test",             R.drawable.glucohealth_connect, Test.class));
        gridItems.add(new LabeledImage("Meal Input",       R.drawable.meal_input,          MealInput.class));
        gridItems.add(new LabeledImage("Graphs 'n Charts", R.drawable.graphs_and_charts,   Charts.class));
        gridItems.add(new LabeledImage("Information",      R.drawable.information,         UserInformation.class));
        gridItems.add(new LabeledImage("Settings",         R.drawable.gear_settings,       Settings.class));

        gridView = (GridView)findViewById(R.id.mainGridView);
        gridView.setAdapter(new ImageAdapter(getApplicationContext(), gridItems));
        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String label = ((TextView)v.findViewById(R.id.gridItemLabel)).getText().toString();
                Log.i("NEW ACTIVITY", label);
                LabeledImage labeledImage = null;
                for (int j = 0; j < gridItems.size(); j++)
                    if (gridItems.get(j).getLabel().equals(label))
                        labeledImage = gridItems.get(j);

                if (labeledImage == null)
                    throw new IllegalStateException("Not supposed to be null!");

                Intent intent = new Intent();
                intent.setClass(thisActivity, labeledImage.getActivity());
                startActivity(intent);
            }
        });
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    }
}
