package glucopro.sqlmodel;


import glucopro.sqlhelper.*;
import glucopro.sqlmodel.*;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class Databasemanager extends SQLiteOpenHelper {
	
	private static final String LOG = "DatabaseManager";
	
	 private static final int DATABASE_VERSION = 2;
	 
	 private static final String DATABASE_NAME = "NutritionManager";
	 
	 // table names
	 	private static final String TABLE_UserIntake = "userintake";
	    private static final String TABLE_UserTable = "usertable";
	    private static final String TABLE_GlucoEntry = "glucoentry";
	    private static final String TABLE_product = "product_table";
	    private static final String TABLE_calories = "calories_table";
	    private static final String TABLE_csp = "csp_table";
	    private static final String TABLE_fat = "totalfat_table";
	    private static final String TABLE_carboh = "totalcarbohydrate_table";
	    private static final String TABLE_vit1 = "vit1_table";
	    private static final String TABLE_vit2 = "vit2_table";
	    private static final String TABLE_vit3 = "vit3_table";
	    private static final String TABLE_images = "images_table";
	    
	    // glucoentry table values
	    
	    private static final String KEY_GMID = "glucoid";
	    private static final String KEY_VALUE = "gvalues";
	    private static final String KEY_PRE = "gpre";
	    private static final String KEY_YEAR = "gyear";
	    private static final String KEY_MONTH = "gmonth";
	    private static final String KEY_DAY = "gday";
	    private static final String KEY_HOUR = "ghour";
	    private static final String KEY_MIN = "gmin";
	    
	    
	    // user table
	    //include common columns
	    private static final String KEY_ID = "userid";
	    private static final String KEY_IDintake = "fuserid";
	    private static final String KEY_CREATED_AT = "created_at";
	    
	    private static final String KEY_AGE = "age";
	    private static final String KEY_NAME = "name";
	    private static final String KEY_DISEASE = "disease";
	    
	    // userintake table
	   
	    private static final String KEY_PID = "pid";
	    private static final String KEY_SERVINGS = "servings";
	    private static final String KEY_SSIZE = "ssize";
	    private static final String KEY_TAKENTIME = "takentime";
	    
	       	    
	    // Product table 
	    //includes common columns	
	    
	    private static final String KEY_PNAME = "product_name";
	    private static final String KEY_CHOL_SOD_PRO_ID = "ch_s_pr_id";
	    private static final String KEY_TOTALFATID = "fat_id";
	    private static final String KEY_TCARBOHID = "charbohydate_id";
	    private static final String KEY_VIT1ID = "vit_acd_ca_iron_id";
	    private static final String KEY_VIT2ID = "thi_rib_nia_phos_id";
	    private static final String KEY_VIT3ID = "mag_zinc_copp_id";
	    private static final String KEY_CALORIESID = "calories_id";
	    private static final String KEY_SERVINGSIZE = "sevings size";
	    private static final String KEY_SSIZEGRAMS = "ssize in grams";
	    private static final String KEY_SPERCONTAINER = "spcontainer";
	    
	    
	    //calories table
	    
	    private static final String KEY_CALORIES = "calories_type" ;
	    private static final String KEY_CGRAMS = "cal_grams";
	    private static final String KEY_CVALUETYPE ="cal_value_type";
	    private static final String KEY_CPERCENT = "cal_percentage";
	    
	    // total fat table

	    
	    private static final String KEY_FAT = "fat_type" ;
	    private static final String KEY_FGRAMS = "fat_grams";
	    private static final String KEY_FVALUETYPE ="fat_value_type";
	    private static final String KEY_FPERCENT = "fat_percentage";
	    
	    //CHOL_SOD_PRO table
	    
	    private static final String KEY_CSP = "csp_type" ;
	    private static final String KEY_CSPGRAMS = "csp_grams";
	    private static final String KEY_CSPVALUETYPE ="csp_value_type";
	    private static final String KEY_CSPPERCENT = "csp_percentage";
	    
	    //total carbohydrate table
	    
	    private static final String KEY_CARBOHYDRATE = "carbohydrate_type" ;
	    private static final String KEY_CHGRAMS = "carbohydrate_grams";
	    private static final String KEY_CHVALUETYPE ="carbohydrate_value_type";
	    private static final String KEY_CHPERCENT = "carbohydrate_percentage";
	    
	    //VIT1 table
	    
	    private static final String KEY_VIT1 = "vit1_type" ;
	    private static final String KEY_VIT1GRAMS = "vit1_grams";
	    private static final String KEY_VIT1VALUETYPE ="vit1_value_type";
	    private static final String KEY_VIT1PERCENT = "vit1_percentage";
	    
	    //VIT2 table
	    
	    private static final String KEY_VIT2 = "vit2_type" ;
	    private static final String KEY_VIT2GRAMS = "vit2_grams";
	    private static final String KEY_VIT2VALUETYPE ="vit2_value_type";
	    private static final String KEY_VIT2PERCENT = "vit2_percentage";
	    
	    //VIT3 table
	    
	    private static final String KEY_VIT3 = "vit3_type" ;
	    private static final String KEY_VIT3GRAMS = "vit3_grams";
	    private static final String KEY_VIT3VALUETYPE ="vit3_value_type";
	    private static final String KEY_VIT3PERCENT = "vit3_percentage";
	    
	    //image table
	    
	    private static final String KEY_BARCODE = "images_barcode";
	    private static final String KEY_IMAGE1 = "image_front";
	    private static final String KEY_IMAGE2 = "image_side1";
	    private static final String KEY_IMAGE3 = "image_side2";
	    
	    
	    //delete this
	    private static final String TABLE_CONTACTS = "contacts";
	    private static final String KEY_IDD = "id";
	    private static final String KEY_NAMED = "name";
	    private static final String KEY_IMAGE = "image";

	    String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
	    	    + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
	    	    + KEY_IMAGE + " BLOB" + ")";
	    	

	    private static final String CREATE_TABLE_GLUCOENTRY = "CREATE TABLE " 
	    		+ TABLE_GlucoEntry + "(" + KEY_GMID + " INTEGER PRIMARY KEY," + KEY_VALUE
	            + " INTEGER," + KEY_PRE  + " INTEGER," + KEY_YEAR + " INTEGER," + KEY_MONTH + " INTEGER," + KEY_DAY + 
	            " INTEGER," + KEY_HOUR + " INTEGER," + KEY_MIN + " INTEGER," + KEY_CREATED_AT
	            + " DATETIME" + ")";	
	 
	    
	    
	     		
	       
	    private static final String CREATE_TABLE_USERTABLE = "CREATE TABLE " 
	    		+ TABLE_UserTable + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_AGE
	            + " INTEGER," + KEY_NAME + " TEXT," + KEY_DISEASE + " TEXT," +KEY_CREATED_AT
	            + " DATETIME" + ")";
	    
	    private static final String CREATE_TABLE_USERINTAKE = "CREATE TABLE "
	            + TABLE_UserIntake + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PID
	            + " TEXT," + KEY_SERVINGS + " INTEGER," + KEY_SSIZE + " INTEGER," +KEY_TAKENTIME
	            + " DATETIME" + ")";
	    
	    private static final String CREATE_TABLE_PRODUCT = "CREATE TABLE "
	            + TABLE_product + "(" + KEY_PID + " INTEGER PRIMARY KEY," + KEY_PNAME
	            + " TEXT,"+ KEY_SERVINGSIZE + "TEXT," + KEY_SSIZEGRAMS + "INTEGER," + KEY_SPERCONTAINER + " INTEGER," + KEY_CALORIESID + " INTEGER, "+ KEY_CHOL_SOD_PRO_ID + " INTEGER," + KEY_TOTALFATID + " INTEGER," + KEY_TCARBOHID
	            + " INTEGER, " +  KEY_VIT1ID + " INTEGER," + KEY_VIT2ID + " INTEGER," + KEY_VIT3ID + " INTEGER" + ")";
	    
	    
	    private static final String CREATE_TABLE_CALORIES = "CREATE TABLE "
	            + TABLE_calories + "(" + KEY_CALORIESID + " INTEGER PRIMARY KEY," + KEY_CALORIES
	            + " TEXT," + KEY_CGRAMS + " INTEGER," + KEY_CVALUETYPE + " TEXT," +KEY_CPERCENT
	            + " INTEGER " + ")";
	    
	    private static final String CREATE_TABLE_FAT = "CREATE TABLE "
	            + TABLE_fat + "(" + KEY_TOTALFATID + " INTEGER PRIMARY KEY," + KEY_FAT
	            + " TEXT," + KEY_FGRAMS + " INTEGER," + KEY_FVALUETYPE + " TEXT," +KEY_FPERCENT
	            + " INTEGER " + ")";
	  
	    private static final String CREATE_TABLE_CARBOH = "CREATE TABLE "
	            + TABLE_carboh + "(" + KEY_TCARBOHID + " INTEGER PRIMARY KEY," + KEY_CARBOHYDRATE
	            + " TEXT," + KEY_CHGRAMS + " INTEGER," + KEY_CHVALUETYPE + " TEXT," +KEY_CHPERCENT
	            + " INTEGER " + ")";
	    
	    private static final String CREATE_TABLE_CSP = "CREATE TABLE "
	            + TABLE_csp + "(" + KEY_CHOL_SOD_PRO_ID + " INTEGER PRIMARY KEY," + KEY_CSP
	            + " TEXT," + KEY_CSPGRAMS + " INTEGER," + KEY_CSPVALUETYPE + " TEXT," +KEY_CSPPERCENT
	            + " INTEGER " + ")";
	    
	    private static final String CREATE_TABLE_VIT1 = "CREATE TABLE "
	            + TABLE_vit1 + "(" + KEY_VIT1ID + " INTEGER PRIMARY KEY," + KEY_VIT1
	            + " TEXT," + KEY_VIT1GRAMS + " INTEGER," + KEY_VIT1VALUETYPE + " TEXT," +KEY_VIT1PERCENT
	            + " INTEGER " + ")";
	    
	    private static final String CREATE_TABLE_VIT2 = "CREATE TABLE "
	            + TABLE_vit2 + "(" + KEY_VIT2ID + " INTEGER PRIMARY KEY," + KEY_VIT2
	            + " TEXT," + KEY_VIT2GRAMS + " INTEGER," + KEY_VIT2VALUETYPE + " TEXT," +KEY_VIT2PERCENT
	            + " INTEGER " + ")";
	    
	    private static final String CREATE_TABLE_VIT3 = "CREATE TABLE "
	            + TABLE_vit3 + "(" + KEY_VIT3ID + " INTEGER PRIMARY KEY," + KEY_VIT3
	            + " TEXT," + KEY_VIT3GRAMS + " INTEGER," + KEY_VIT3VALUETYPE + " TEXT," +KEY_VIT3PERCENT
	            + " INTEGER " + ")";
	    
	    private static final String CREATE_TABLE_IMAGES = "CREATE TABLE "
	            + TABLE_images + "(" + KEY_PID + " INTEGER PRIMARY KEY," + KEY_BARCODE
	            + " BLOB," + KEY_IMAGE1 + " BLOB," + KEY_IMAGE2 + " BLOB," +KEY_IMAGE3
	            + " BLOB " + ")";
	 

	    
	    public Databasemanager(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
	        

	        @Override
	        public void onCreate(SQLiteDatabase db) {
	     
	            // creating required tables
	            db.execSQL(CREATE_TABLE_USERTABLE);
	            db.execSQL(CREATE_TABLE_USERINTAKE);
	            db.execSQL(CREATE_TABLE_GLUCOENTRY);
	           
	           
	        }
	     
	        @Override
	        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	            // on upgrade drop older tables
	            db.execSQL("DROP TABLE IF EXISTS " + TABLE_UserIntake);
	            db.execSQL("DROP TABLE IF EXISTS " + TABLE_UserTable);
	            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GlucoEntry);
	     
	            // create new tables
	            onCreate(db);
	        
	    
	        }
	        
	       /*public void addimage(Imagestore imagestore) {
	        	  SQLiteDatabase db = this.getWritableDatabase();

	        	  ContentValues values = new ContentValues();
	        	  
	        	  values.put(KEY_IMAGE, imagestore.getImage());

	        	  // Inserting Row
	        	  db.insert(TABLE_CONTACTS, null, values);
	        	  db.close(); // Closing database connection
	        	 }
			*/
	        	
	        
	   public void glucoentry(GlucometerInput gi){
		   SQLiteDatabase db = this.getWritableDatabase();
		   ContentValues values = new ContentValues();
		   
	        values.put(KEY_VALUE, gi.getValue());
	        values.put(KEY_PRE, gi.getPre());
	        values.put(KEY_YEAR, gi.getYear());
	        values.put(KEY_MONTH, gi.getMonth());
	        values.put(KEY_DAY, gi.getDay());
	        values.put(KEY_HOUR, gi.getHour());
	        values.put(KEY_MIN, gi.getMin());
	        values.put(KEY_CREATED_AT, getDateTime());
	        db.insert(TABLE_GlucoEntry, null, values);
	 
		   
	   }
	   public long createuser(UserTable us){
		   SQLiteDatabase db = this.getWritableDatabase();
		   
	        ContentValues values = new ContentValues();
	        values.put(KEY_ID, us.getid());
	        values.put(KEY_AGE, us.getage());
	        values.put(KEY_NAME, us.getname());
	        values.put(KEY_DISEASE, us.getdisease());
	        values.put(KEY_CREATED_AT, getDateTime());
	        
	       long user_id = db.insert(TABLE_UserTable, null, values);
	       return us.getid();
		   
	   }
	   
	   public void createuserintake(UserIntake ui){
		   SQLiteDatabase db = this.getWritableDatabase();
		   
	        ContentValues values = new ContentValues();
	        values.put(KEY_ID, ui.getid());
	        values.put(KEY_PID, ui.getpid());
	        values.put(KEY_SERVINGS, ui.getservings());
	        values.put(KEY_SSIZE, ui.getssize());
	        values.put(KEY_TAKENTIME, getDateTime());
	        
	        db.insert(TABLE_UserIntake, null, values);
		   
	   }
	   
	   public List<GlucometerInput> getALLreadings() {
		   List<GlucometerInput> ut =new ArrayList<GlucometerInput>();
		   String selectQuery = "SELECT  * FROM " + TABLE_GlucoEntry;
		   Log.e(LOG, selectQuery);
		   SQLiteDatabase db = this.getReadableDatabase();
		    Cursor c = db.rawQuery(selectQuery, null);
		    
		    if (c.moveToFirst()) {
		        do {
		            GlucometerInput td = new GlucometerInput();
		            td.setGmid(c.getInt((c.getColumnIndex(KEY_GMID))));
		            td.setValue((c.getInt(c.getColumnIndex(KEY_VALUE))));
		            td.setPre((c.getInt(c.getColumnIndex(KEY_PRE))));
		            td.setYear((c.getInt(c.getColumnIndex(KEY_YEAR))));
		            td.setMonth((c.getInt(c.getColumnIndex(KEY_MONTH))));
		            td.setDay((c.getInt(c.getColumnIndex(KEY_DAY))));
		            td.setHour((c.getInt(c.getColumnIndex(KEY_HOUR))));
		            td.setMin((c.getInt(c.getColumnIndex(KEY_MIN))));
		            td.setcreatedate(c.getString(c.getColumnIndex(KEY_CREATED_AT)));
		 
		            
		            ut.add(td);
		        } while (c.moveToNext());
		    }
		    return ut;
		  
	   }
	   
	   public List<UserTable> getAllusers() {
		   List<UserTable> ut =new ArrayList<UserTable>();
		   String selectQuery = "SELECT  * FROM " + TABLE_UserTable;
		   Log.e(LOG, selectQuery);
		   SQLiteDatabase db = this.getReadableDatabase();
		    Cursor c = db.rawQuery(selectQuery, null);
		    
		    if (c.moveToFirst()) {
		        do {
		            UserTable td = new UserTable();
		            td.setid(c.getInt((c.getColumnIndex(KEY_ID))));
		            td.setname((c.getString(c.getColumnIndex(KEY_NAME))));
		            td.setage((c.getInt(c.getColumnIndex(KEY_AGE))));
		            td.setdisease((c.getString(c.getColumnIndex(KEY_DISEASE))));
		            td.setcreatedat(c.getString(c.getColumnIndex(KEY_CREATED_AT)));
		 
		            
		            ut.add(td);
		        } while (c.moveToNext());
		    }
		    return ut;
		  
	   }
	   
	   // detele later
	   
	  
	   
	   
	   // to here
	   
	   public List<UserIntake> getuserintake() {
		   List<UserIntake> utr =new ArrayList<UserIntake>();
		   String selectQuery = "SELECT  * FROM " + TABLE_UserIntake;
		   Log.e(LOG, selectQuery);
		   SQLiteDatabase db = this.getReadableDatabase();
		    Cursor c = db.rawQuery(selectQuery, null);
		    
		    if (c.moveToFirst()) {
		        do {
		            UserIntake td1 = new UserIntake();
		            td1.setid(c.getInt((c.getColumnIndex(KEY_ID))));
		            td1.setpid((c.getInt(c.getColumnIndex(KEY_PID))));
		            td1.setservings((c.getInt(c.getColumnIndex(KEY_SERVINGS))));
		            td1.setssize((c.getInt(c.getColumnIndex(KEY_SSIZE))));
		            td1.setcreatedat(c.getString(c.getColumnIndex(KEY_TAKENTIME)));
		 
		            
		            utr.add(td1);
		        } while (c.moveToNext());
		    }
		    return utr;
		  
	   }
		   
	   
	  /* for saving images into the database delete at last
	   * @SuppressWarnings("deprecation")
	    public String getPath(Uri uri) {
	        String[] projection = { MediaStore.Images.Media.DATA };
	        Cursor cursor = managedQuery(uri, projection, null, null, null);
	        int column_index = cursor
	                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        cursor.moveToFirst();
	        return cursor.getString(column_index);
	    }
	   */
	   
	   public void closeDB() {
	        SQLiteDatabase db = this.getReadableDatabase();
	        if (db != null && db.isOpen())
	            db.close();
	    }
	 
		   
		 
	   private String getDateTime() {
	        SimpleDateFormat dateFormat = new SimpleDateFormat(
	                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	        Date date = new Date();
	        return dateFormat.format(date);
	    }
}
	   

