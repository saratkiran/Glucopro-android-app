package hippocraticapps.glucopro.resturant;

import hippocraticapps.glucopro.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ListView;
import android.view.Menu;
import org.apache.cordova.DroidGap;;

public class listview extends DroidGap {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.loadUrl("file:///android_asset/www/index.html");
    }
}

