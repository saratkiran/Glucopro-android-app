package hippocraticapps.glucopro.database;

import java.util.Date;

public class SugarRecord extends GlucoRecord {
    // static storage vars
    public int id;
    public int shiftID;
    public int pre;
    public float level;
    public long  time;


    public String parseTimeUnix() { //will return a string of the date represented by the timestamp.
        // We may want to change this later to a custom format. This will do for now.
        // Saves you a ton of time on the front end, Jesse.
        Date time = new Date(this.time);
        return time.toString();
    }



    public SugarRecord(int id, int shiftID, int pre, float level, long time) {
        // set values
        this.id = id;
        this.shiftID = shiftID;
        this.pre = pre;
        this.level = level;
        this.time = time;
    }
}
