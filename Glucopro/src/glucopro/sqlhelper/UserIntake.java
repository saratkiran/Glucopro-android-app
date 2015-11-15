package glucopro.sqlhelper;

public class UserIntake {
	
	int pid,servings,ssize;
	long userid;
	String takentime;
	
	public UserIntake() {}
	
	public UserIntake(long userid,int pid,int servings,int ssize){
		this.userid = userid;
		this.pid = pid;
		this.servings = servings;
		this.ssize = ssize;
		
	}

	 public void setid(int id) {
	        this.userid = id;
	    }
	    public void setpid(int pid) {
	        this.pid = pid;
	    }
	    
	    public void setservings(int servings) {
	        this.servings = servings;
	    }
	    
	     public void setssize(int ssize){
	            this.ssize = ssize;
	    }
	     
	     public void setcreatedat(String created) {
	         this.takentime = created;
	     } 
	 public long getid() {
	         return this.userid;
	     }
	     
	     public long getpid() {
	         return this.pid;
	     }
	     
	     public long getservings() {
	         return this.servings;
	     }
	     
	     public long getssize() {
	         return this.ssize;
	     }
	     
	     public String getcreatedat()
	     {
	    	 return this.takentime;
	     }
	     
}
