package hippocraticapps.glucopro.resturant;

import java.io.IOException; 

import android.content.ContentValues;
import android.content.Context; 
import android.database.Cursor; 
import android.database.SQLException; 
import android.database.sqlite.SQLiteDatabase; 
import android.util.Log; 
 
public class TestAdapter  
{ 
    protected static final String TAG = "DataAdapter";
    private final Context mContext; 
    private SQLiteDatabase mDb; 
    private Rest_DatabaseManager mDbHelper; 
    
    public TestAdapter(Context context)  
    { 
        this.mContext = context; 
        mDbHelper = new Rest_DatabaseManager(mContext); 
    } 
    
    public TestAdapter createDatabase() throws SQLException  
    { 
        try  
        { 
            mDbHelper.createDataBase(); 
        }  
        catch (IOException mIOException)  
        { 
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase"); 
            throw new Error("UnableToCreateDatabase"); 
        } 
        return this; 
    }
    
    public TestAdapter open() throws SQLException  
    { 
        try  
        { 
            mDbHelper.openDataBase(); 
            mDbHelper.close(); 
            mDb = mDbHelper.getReadableDatabase(); 
        }  
        catch (SQLException mSQLException)  
        { 
            Log.e(TAG, "open >>"+ mSQLException.toString()); 
            throw mSQLException; 
        } 
        return this; 
    } 
 
    public void close()  
    { 
        mDbHelper.close(); 
    } 
 
     public Cursor getTestData() 
     { 
         try 
         { 
             String sql ="SELECT _id,Lat,Longt FROM bus_table"; 
             Cursor mCur = mDb.rawQuery(sql, null); 
             if (mCur!=null) 
             { 
            	 Log.d("venusss",mCur.toString());
                mCur.moveToNext(); 
                
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 

                /*
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                mCur.moveToNext(); 
                */
             }
             return mCur; 
         } 
         catch (SQLException mSQLException)  
         { 
             Log.e(TAG, "getTestData >>"+ mSQLException.toString()); 
             throw mSQLException; 
         } 
     }     
} 


/*

public boolean SaveEmployee(String name, String email) 
{
	try
	{
		ContentValues cv = new ContentValues();
		cv.put("Name", name);
		cv.put("Email", email);
		
		mDb.insert("Employees", null, cv);

		Log.d("SaveEmployee", "informationsaved");
		return true;
		
	}
	catch(Exception ex)
	{
		Log.d("SaveEmployee", ex.toString());
		return false;
	}
}*/