package hippocraticapps.glucopro.views;

import java.util.ArrayList;

import android.app.Activity;
import java.util.Random;
import hippocraticapps.glucopro.R;
import hippocraticapps.glucopro.database.GlucoDBAdapter;
import hippocraticapps.glucopro.database.SugarRecord;
import hippocraticapps.glucopro.database.SugarTable;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class Chart extends View {

    private Paint background_, point_, line_, graphAxis_;
    private SugarRecord[] records_;
    private static final float POINT_RADIUS = 5f;
    private static final float Y_SCALE = 200;
    private static final float Y_SHIFT = 200;
    private static final float CHART_MARGINS = 200;
    private static final float AXIS_MARGIN = 50;
    
    public Chart(Context context, AttributeSet atrs) {
        super(context, atrs);

        background_ = new Paint();
        background_.setColor(getResources().getColor(R.color.settings_background));
        
        point_ = new Paint();
        point_.setColor(getResources().getColor(R.color.settings_shift_circle));
        point_.setStrokeWidth(10);
        point_.setStyle(Paint.Style.STROKE);
        
        line_ = new Paint();
        line_.setColor(getResources().getColor(R.color.settings_offset_crosshair));
        line_.setStrokeWidth(6);
        line_.setStyle(Paint.Style.STROKE);
        
        graphAxis_ = new Paint();
        graphAxis_.setColor(getResources().getColor(R.color.settings_offset_crosshair));
        graphAxis_.setStrokeWidth(2);
        graphAxis_.setStyle(Paint.Style.STROKE);
        
        records_ = SugarTable.peekRange(GlucoDBAdapter.getInstance(), 28);
        Toast.makeText(context, records_.length + " records found.", Toast.LENGTH_SHORT).show();
    }



    public void draw(Canvas canvas) {
        canvas.drawPaint(background_);
        
        float minTime = Long.MAX_VALUE, maxTime = Long.MIN_VALUE;
        float minVal  = Long.MAX_VALUE, maxVal  = Long.MIN_VALUE;
        float sum = 0;
        for (SugarRecord record : records_) {
            minTime = Math.min(minTime, record.time);
            maxTime = Math.max(maxTime, record.time);
            
            minVal  = Math.min(minVal, record.level);
            maxVal  = Math.max(maxVal, record.level);
            sum += record.level;
        }
        
        TextView label = (TextView)((Activity)getContext()).findViewById(R.id.charts_label);
        String str = getResources().getString(R.string.charts_default_label);
        label.setText(str + "\nLow: " + minVal + "     High: " + maxVal + "\nAverage: " + (sum / (float)records_.length));
        
        ArrayList<PointF> points = new ArrayList<PointF>(records_.length);
        float scaleX = (getWidth() - 2 * CHART_MARGINS) / (maxTime - minTime);
        float scaleY = Y_SCALE / (maxVal - minVal);
        for (SugarRecord record : records_)
        	points.add(new PointF((record.time - minTime) * scaleX + CHART_MARGINS, getHeight() - (record.level - minVal) * scaleY - Y_SHIFT));
        
        for (int j = 0; j < points.size(); j++) {
            if (j < points.size() - 1)
        		canvas.drawLine(points.get(j).x, points.get(j).y,
        			points.get(j + 1).x, points.get(j + 1).y, line_);
            
            canvas.drawCircle(points.get(j).x, points.get(j).y, POINT_RADIUS, point_);
        }
        
        
        canvas.drawLine(CHART_MARGINS - AXIS_MARGIN, getHeight() - Y_SHIFT + AXIS_MARGIN, CHART_MARGINS - AXIS_MARGIN, getHeight() - Y_SHIFT - Y_SCALE - AXIS_MARGIN, graphAxis_);
        canvas.drawLine(CHART_MARGINS - AXIS_MARGIN, getHeight() - Y_SHIFT + AXIS_MARGIN, getWidth() - CHART_MARGINS + AXIS_MARGIN, getHeight() - Y_SHIFT, graphAxis_);
    }
}
