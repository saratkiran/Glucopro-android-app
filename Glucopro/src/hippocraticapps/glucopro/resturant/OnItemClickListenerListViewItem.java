package hippocraticapps.glucopro.resturant;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Here you can control what to do next when the user selects an item
 */
public class OnItemClickListenerListViewItem implements OnItemClickListener {

	@Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Context context = view.getContext();
		//
		//TextView textViewItem = ((TextView) view.findViewById(R.id.textViewItem));
		 String product = ((TextView) view).getText().toString();
		 Toast.makeText(context, product , Toast.LENGTH_SHORT).show();
		 System.out.println(product);
		 Rest_menu_f variable = new Rest_menu_f();
		 
		 String server = "http://129.123.7.140:10000/Restaurant/RestaurantMenuServlet?path=" + product;
		 variable.getlist(server);
		 
        // get the clicked item name
       // String listItemText = textViewItem.getText().toString();
        
        // get the clicked item ID
        //String listItemId = textViewItem.getTag().toString();
        
        // just toast it
        //Toast.makeText(context, "Item: " + listItemText + ", Item ID: " + listItemId, Toast.LENGTH_SHORT).show();

       // ((MainActivity) context).alertDialogStores.cancel();
        
    }
	
}
