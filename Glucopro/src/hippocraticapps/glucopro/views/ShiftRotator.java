package hippocraticapps.glucopro.views;

import hippocraticapps.glucopro.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ShiftRotator extends View {

    private Paint background_, circle_, text_, offsetCrosshair_, offsetSelector_;
    private final float TICK_SIZE   = 40f;
    private final float TEXT_OFFSET = 10f;
    private final float SELECTOR_PROTRUSION = 120f;
    private final float SELECTOR_RADIUS = 10f;
    private float offsetHour_ = 0f;
    private Canvas canvas_;
    

    public ShiftRotator(Context context, AttributeSet atrs) {
        super(context, atrs);

        // http://www.tayloredmktg.com/rgb/
        background_ = new Paint();
        background_.setColor(getResources().getColor(R.color.settings_background));

        circle_ = new Paint();
        circle_.setColor(getResources().getColor(R.color.settings_shift_circle));
        circle_.setStrokeWidth(2);
        circle_.setStyle(Paint.Style.STROKE);

        text_ = new Paint();
        text_.setColor(getResources().getColor(R.color.settings_text));
        text_.setTextSize(52f);

        offsetCrosshair_ = new Paint();
        offsetCrosshair_.setColor(getResources().getColor(R.color.settings_offset_crosshair));
        offsetCrosshair_.setStrokeWidth(3);
        offsetCrosshair_.setStyle(Paint.Style.STROKE);

        offsetSelector_ = new Paint();
        offsetSelector_.setColor(getResources().getColor(R.color.settings_offset_selector));
    }



    public void draw(Canvas canvas) {
        canvas.drawPaint(background_);
        canvas_ = canvas;

        int centerX = canvas.getWidth() / 2,  centerY = canvas.getHeight() / 2;
        int r = canvas.getWidth() / 3;

        //draw circle with tick marks
        canvas.drawCircle(centerX, centerY, r, circle_);
        for (int hour = 0; hour < 12; hour++) {
            float x = (float)Math.cos(2 * Math.PI * (hour / 12.0f) - Math.PI / 2);
            float y = (float)Math.sin(2 * Math.PI * (hour / 12.0f) - Math.PI / 2);
            float insideR = r - TICK_SIZE, outsideR = r + TICK_SIZE;
            canvas.drawLine(centerX + x * insideR, centerY + y * insideR,
                    centerX + x * outsideR, centerY + y * outsideR, circle_);

            canvas.drawText("" + hour, centerX + x * (outsideR + TEXT_OFFSET),
                    centerY + y * (outsideR + TEXT_OFFSET), text_);
        }

        //draw offset crosshairs with little selector balls
        for (int j = 0; j <= 4; j++) {
            float ux = (float)Math.cos(2 * Math.PI * (offsetHour_ + j * 3) / 12.0f);
            float uy = (float)Math.sin(2 * Math.PI * (offsetHour_ + j * 3) / 12.0f);
            float selectorR = r + SELECTOR_PROTRUSION;
            float x = centerX + ux * selectorR, y = centerY + uy * selectorR;
            canvas.drawLine(centerX, centerY, x, y, offsetCrosshair_);
            canvas.drawCircle(x, y, SELECTOR_RADIUS, offsetSelector_);
        }
    }



    public boolean onTouchEvent(MotionEvent me) {
        Point mouseLoc = new Point((int)me.getX(), (int)me.getY());
        
        int centerX = canvas_.getWidth() / 2,  centerY = canvas_.getHeight() / 2;
        float angle = (float)Math.atan2(mouseLoc.y - centerY, mouseLoc.x - centerX);        
        offsetHour_ = (float)((angle + Math.PI) / (2 * Math.PI)) * 12f;
        offsetHour_ = Math.round(offsetHour_ * 2) / 2f;
        invalidate(); //repaint the area

        return true;
    }
}
