package hippocraticapps.glucopro.adapters;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import hippocraticapps.glucopro.R;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<LabeledImage> labeledImages;


    public ImageAdapter(Context context, List<LabeledImage> labeledImages) {
        this.context = context;
        this.labeledImages = labeledImages;
    }



    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {
            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.main_gridcell, null);

            // set value into textview
            TextView textView = (TextView)gridView.findViewById(R.id.gridItemLabel);
            textView.setText(labeledImages.get(position).getLabel());

            // set image based on selected text
            ImageView imageView = (ImageView)gridView.findViewById(R.id.gridItemImage);
            imageView.setImageResource(labeledImages.get(position).getImageResource());
        }
        else
            gridView = (View) convertView;

        return gridView;
    }



    @Override
    public int getCount() {
        return labeledImages.size();
    }



    @Override
    public Object getItem(int position) {
        return null;
    }



    @Override
    public long getItemId(int position) {
        return 0;
    }
}
