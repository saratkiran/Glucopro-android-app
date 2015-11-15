package hippocraticapps.glucopro.database;

import android.util.Log;


public class InsulinTable implements DatabaseTable {

	@Override
	public void insert(GlucoDBAdapter adptr, GlucoRecord record) {
		// TODO Auto-generated method stub
		adptr.insertInsulinEntry( (InsulinRecord)record );
		return;
	}



	static InsulinRecord[] peekRange(GlucoDBAdapter adptr, int maxSize) {
		//make an adapter query call to get the nth entry from the top, starting newest first
		Log.d("InsulinRecord: ", "got here");
		return adptr.getNInsulinEntries(maxSize); // return array of records
	}
}
