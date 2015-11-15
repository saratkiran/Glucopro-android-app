package hippocraticapps.glucopro.database;


public class SugarTable implements DatabaseTable {

	@Override
	public void insert(GlucoDBAdapter adptr, GlucoRecord record) {
		// TODO Auto-generated method stub
		adptr.insertSugarEntry( (SugarRecord)record );
		return;
	}



	public static SugarRecord[] peekRange( GlucoDBAdapter adptr,int maxSize ){
		//make an adapter query call to get the nth entry from the top, starting newest first
		return adptr.getNSugarEntries(maxSize); // return array of records
	}
}
