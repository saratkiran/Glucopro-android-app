package hippocraticapps.glucopro.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GlucoDBAdapter {
    public static final String ADPTR_LOGTAG = GlucoDBAdapter.class.getSimpleName() + "_TAG";
    public static final String QUERY_LOGTAG = "QUERY_TAG";

    public static final String DB_NAME                  = "gluco_pro.db";
    public static final int    DB_VERSION               = 1;
    public static final String BLOOD_SUGAR_TABLE        = "blood_sugar_table";
    public static final String INSULIN_CORRECTION_TABLE = "insulin_correction_table";
    public static final String MEAL_DATA_TABLE          = "meal_data_table";


    // ********** blood_sugar table constants ******************
    // constants for blood_sugar table column names
    public static final String SUGAR_ID_COL_NAME        = "id";
    public static final String SUGAR_SHIFT_COL_NAME     = "ShiftId";
    public static final String SUGAR_PRE_POST_COL_NAME  = "PrePost";
    public static final String SUGAR_LEVEL_COL_NAME     = "Level";
    public static final String SUGAR_TIME_COL_NAME      = "Time";
    public static final String[] SUGAR_COL            = new String[]{
                                                                SUGAR_ID_COL_NAME,
                                                                SUGAR_SHIFT_COL_NAME,
                                                                SUGAR_PRE_POST_COL_NAME,
                                                                SUGAR_LEVEL_COL_NAME,
                                                                SUGAR_TIME_COL_NAME
                                                                };

    // constants for blood_sugar table column numbers
    public static final int SUGAR_ID_COL_NUM            = 0;
    public static final int SUGAR_SHIFT_COL_NUM         = 1;
    public static final int SUGAR_PRE_POST_COL_NUM      = 2;
    public static final int SUGAR_LEVEL_COL_NUM         = 3;
    public static final int SUGAR_TIME_COL_NUM          = 4;

    // ********** insulin_correction table constants ******************
    // constants for insulin_correction table column names
    public static final String CORRECT_ID_COL_NAME      = "id";
    public static final String CORRECT_SHIFT_COL_NAME   = "ShiftId";
    public static final String CORRECT_DOSE_COL_NAME    = "Dose";
    public static final String CORRECT_TIME_COL_NAME    = "Time";
    public static final String CORRECT_FAST_COL_NAME    = "Fast";
    public static final String[] INSULIN_COL            = new String[]{
                                                                CORRECT_ID_COL_NAME,
                                                                CORRECT_SHIFT_COL_NAME,
                                                                CORRECT_DOSE_COL_NAME,
                                                                CORRECT_TIME_COL_NAME,
                                                                CORRECT_FAST_COL_NAME
                                                                };

    // constants for insulin_correction table column numbers
    public static final int CORRECT_ID_COL_NUM          = 0;
    public static final int CORRECT_SHIFT_COL_NUM       = 1;
    public static final int CORRECT_DOSE_COL_NUM        = 2;
    public static final int CORRECT_TIME_COL_NUM        = 3;
    public static final int CORRECT_FAST_COL_NUM        = 4;

    // ********** meal_data table constants ******************
    // constants for meal_data table column names
    public static final String MEAL_ID_COL_NAME         = "id";
    public static final String MEAL_SHIFT_COL_NAME      = "ShiftId";
    public static final String MEAL_CARB_COL_NAME       = "CarbIntake";
    public static final String MEAL_TIME_COL_NAME       = "Time";

    // constants for meal_data table column numbers
    public static final int MEAL_ID_COL_NUM             = 0;
    public static final int MEAL_SHIFT_COL_NUM          = 1;
    public static final int MEAL_DOSE_COL_NUM           = 2;
    public static final int MEAL_TIME_COL_NUM           = 3;

    private SQLiteDatabase       mDb = null;
    private DBOpenHelper mDbHelper = null;


    private static class DBOpenHelper extends SQLiteOpenHelper {
        static final String HELPER_LOGTAG = DBOpenHelper.class.getSimpleName() + "_TAG";

        public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }



        // table creation string constant
        static final String SUGAR_TABLE_CREATE =
                "create table " + BLOOD_SUGAR_TABLE +
                " (" +
                SUGAR_ID_COL_NAME           + " integer primary key autoincrement, " +
                SUGAR_SHIFT_COL_NAME        + " integer not null, " +
                SUGAR_PRE_POST_COL_NAME     + " integer not null, " +
                SUGAR_LEVEL_COL_NAME        + " float not null, " +
                SUGAR_TIME_COL_NAME         + " bigint not null " +
                ");";

        static final String INSULIN_CORRECTION_CREATE =
                "create table " + INSULIN_CORRECTION_TABLE +
                " (" +
                CORRECT_ID_COL_NAME         + " integer primary key autoincrement, " +
                CORRECT_SHIFT_COL_NAME      + " integer not null, " +
                CORRECT_DOSE_COL_NAME       + " float not null, " +
                CORRECT_TIME_COL_NAME       + " bigint not null, " +
                CORRECT_FAST_COL_NAME       + " integer not null " +
                ");";

        static final String MEAL_DATA_CREATE =
                "create table " + MEAL_DATA_TABLE +
                " (" +
                MEAL_ID_COL_NAME            + " integer primary key autoincrement, " +
                MEAL_SHIFT_COL_NAME         + " text not null, " +
                MEAL_CARB_COL_NAME          + " text not null, " +
                MEAL_TIME_COL_NUM           + " blob not null " +
                ");";



        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(HELPER_LOGTAG, SUGAR_TABLE_CREATE);
            db.execSQL(SUGAR_TABLE_CREATE);

            db.execSQL(INSULIN_CORRECTION_CREATE);

            //db.execSQL(MEAL_DATA_CREATE); We won't need this information until later.
            Log.d(ADPTR_LOGTAG,"Insulin Table Created");
        }



        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                int newVersion) {
            Log.d(ADPTR_LOGTAG, "Upgrading from version " +
                    oldVersion + " to " +
                    newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SUGAR_TABLE_CREATE);
            db.execSQL("DROP TABLE IF EXISTS " + INSULIN_CORRECTION_CREATE);
            //db.execSQL("DROP TABLE IF EXISTS " + MEAL_DATA_CREATE);
            onCreate(db);
        }
    } // end of DBOpenHelper class



    private GlucoDBAdapter(Context context) {
        mDbHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSION);
    }
    
    
    
    private static GlucoDBAdapter instance_;
    public static GlucoDBAdapter getInstance(Context context) {
    	if (instance_ == null)
    		instance_ = new GlucoDBAdapter(context);
    	return instance_;
    }
    
    
    
    public static GlucoDBAdapter getInstance() {
    	if (instance_ == null)
    		throw new IllegalStateException("Has not been initiated yet!");
    	return instance_;
    }



    public void open() throws SQLiteException {
        try {
            mDb = mDbHelper.getWritableDatabase();
            Log.d(ADPTR_LOGTAG, "WRITEABLE DB CREATED");
        }
        catch ( SQLiteException ex ) {
            Log.d(ADPTR_LOGTAG, "READABLE DB CREATED");
            mDb = mDbHelper.getReadableDatabase();
        }
    }



    public SQLiteDatabase getReadableDatabase() {
        try {
            return mDbHelper.getReadableDatabase();
        }
        catch ( SQLiteException ex ) {
            ex.printStackTrace();
            return null;
        }
    }



    public void close() {
        mDb.close();
    }



    /*
     * Insert your static queries here.
     */

    public void insertSugarEntry(SugarRecord r){
        String insertString =   "INSERT INTO "+BLOOD_SUGAR_TABLE+
                                " VALUES ( null, "+r.shiftID+", "+r.pre+", "+r.level+", "+r.time+")";
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(insertString);
        return;
    }



    public SugarRecord[] getNSugarEntries(Integer n){
        //String SugarRecordQuery = "SELECT * FROM "+BLOOD_SUGAR_TABLE+" WHERE 1 LIMIT "+n;
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d(ADPTR_LOGTAG, "SUGAR RECORD QUERY OPEN");
        //Cursor crsr = db.query(BLOOD_SUGAR_TABLE, SUGAR_COL, null, null, null, null, n.toString()); Old queries, didn't work so well.
        //Cursor crsr = db.rawQuery("SELECT * FROM "+BLOOD_SUGAR_TABLE+" LIMIT "+n.toString()+" ;", null);
        Cursor crsr =db.rawQuery("SELECT * FROM (SELECT * FROM "+BLOOD_SUGAR_TABLE+" ORDER BY "+SUGAR_ID_COL_NAME+" DESC LIMIT "+n.toString()+") ORDER BY "+SUGAR_ID_COL_NAME+" DESC ;",null);

        SugarRecord[] nEntries = new SugarRecord[crsr.getCount()];

        if ( crsr.getCount() != 0 ) {
            crsr.moveToFirst();
            int i = 0;
            while ( crsr.isAfterLast() == false && i < n) {
                int id = crsr.getInt(crsr.getColumnIndex(SUGAR_ID_COL_NAME));
                int shiftID = crsr.getInt(crsr.getColumnIndex(SUGAR_SHIFT_COL_NAME));
                int pre = crsr.getInt(crsr.getColumnIndex(SUGAR_PRE_POST_COL_NAME));
                float level = crsr.getFloat(crsr.getColumnIndex(SUGAR_LEVEL_COL_NAME));
                long time = crsr.getLong(crsr.getColumnIndex(SUGAR_TIME_COL_NAME));
                Log.d("ARRAY", "Inserted Sugar Record");


                nEntries[i] = new SugarRecord( id, shiftID, pre, level, time ); // Insert the result into the slot of the array

                // NEXT!
                crsr.moveToNext();
                i++;
            }
        }
        crsr.close();

        return nEntries;
    }



    public void insertInsulinEntry(InsulinRecord r){
        String insertString =   "INSERT INTO "+INSULIN_CORRECTION_TABLE+
                                " VALUES ( null, "+r.shiftID+", "+r.dose+", "+r.time+", "+r.fast+")";
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(insertString);
        Log.d("INSERTION", "set record with value: "+r.dose);
        return;
    }



    public InsulinRecord[] getNInsulinEntries(Integer n){
    	//String SugarRecordQuery = "SELECT * FROM "+BLOOD_SUGAR_TABLE+" WHERE 1 ORDER BY "++" LIMIT "+n;
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d(ADPTR_LOGTAG, "INSULIN RECORD QUERY OPEN, FETCHING "+n.toString()+" ROWS");
        //Cursor crsr = db.query(INSULIN_CORRECTION_TABLE, INSULIN_COL, null, null, null, null, n.toString());
        //Cursor crsr =db.rawQuery("SELECT * FROM "+INSULIN_CORRECTION_TABLE+" ORDER BY "+CORRECT_ID_COL_NAME+" DESC LIMIT "+n.toString()+" ;", null);
        Cursor crsr =db.rawQuery("SELECT * FROM (SELECT * FROM "+INSULIN_CORRECTION_TABLE+" ORDER BY "+CORRECT_ID_COL_NAME+" DESC LIMIT "+n.toString()+") ORDER BY "+CORRECT_ID_COL_NAME+" DESC ;",null);

        InsulinRecord[] nEntries = new InsulinRecord[crsr.getCount()];

        if ( crsr.getCount() != 0 ) {
            Log.d("CURSOR","the cursor count is:"+crsr.getCount());
            crsr.moveToFirst();
            int i = 0;
            while ( crsr.isAfterLast() == false && i < n) {
                int id = crsr.getInt(crsr.getColumnIndex(CORRECT_ID_COL_NAME));
                int shiftID = crsr.getInt(crsr.getColumnIndex(CORRECT_SHIFT_COL_NAME));
                float dose = crsr.getFloat(crsr.getColumnIndex(CORRECT_DOSE_COL_NAME));
                long time = crsr.getLong(crsr.getColumnIndex(CORRECT_TIME_COL_NAME));
                int fast = crsr.getInt(crsr.getColumnIndex(CORRECT_FAST_COL_NAME));
                Log.d("ARRAY", "Retrieved record with value: "+dose+" and ID: "+id);


                nEntries[i] = new InsulinRecord( id, shiftID, dose, time, fast ); // Insert the result into the slot of the array

                // NEXT!
                crsr.moveToNext();
                i++;
            }
        }
        crsr.close();

        return nEntries;
    }



    public void truncateBase(){
        SQLiteDatabase db = this.getReadableDatabase();
        //db.execSQL("DROP TABLE IF EXISTS " + SUGAR_TABLE_CREATE);
        //db.execSQL("DROP TABLE IF EXISTS " + INSULIN_CORRECTION_CREATE);
        mDbHelper.onCreate(db);
        return;
    }
}
