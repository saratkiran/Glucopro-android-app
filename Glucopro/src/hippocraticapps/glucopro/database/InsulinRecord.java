package hippocraticapps.glucopro.database;

import java.util.Date;

public class InsulinRecord extends GlucoRecord{
    // static storage vars
    public int id;
    public int shiftID;
    public int fast;
    public float dose;
    public long  time;


    public String parseTimeUnix(){ //will return a string of the date represented by the timestamp.
        // We may want to change this later to a custom format. This will do for now.
        // Saves you a ton of time on the front end, Jesse.
        Date time = new Date(this.time);
        return time.toString();
    }



    public InsulinRecord( int id, int shiftID, float dose, long time, int fast ){
        // set values
        this.id = id;
        this.shiftID = shiftID;
        this.fast = fast;
        this.dose = dose;
        this.time = time;
    }
}
