package glucopro.sqlhelper;

public class GlucometerInput {
	
	int gmid,value, pre, year, month, day, hour, min;
	String created_at;

	public GlucometerInput() {
    }

	public GlucometerInput(int value,int pre,int year, int month, int day, int hour, int min){
		
		this.value = value;
		this.pre = pre;
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.min = min;
		
	}
	public void setcreatedate(String created) {
        this.created_at = created;
    } 

	
	public int getGmid() {
		return gmid;
	}



	public void setGmid(int gmid) {
		this.gmid = gmid;
	}



	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getPre() {
		return pre;
	}

	public void setPre(int pre) {
		this.pre = pre;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}
	

}
