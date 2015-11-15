package hippocraticapps.glucopro.database;

import java.util.Date;

public class MealRecord extends GlucoRecord {
    public int id;
    public int shiftID;
    public float carbs;
    public long  time;


    public String parseTimeUnix() { //will return a string of the date represented by the timestamp.
        // We may want to change this later to a custom format. This will do for now.
        // Saves you a ton of time on the front end, Jesse.
        Date time = new Date(this.time);
        return time.toString();
    }



    public MealRecord(int id, int shiftID, float carbs, long time) {
        // set values
        this.id = id;
        this.shiftID = shiftID;
        this.carbs = carbs;
        this.time = time;
    }
}
