package glucopro.sqlhelper;

public class UserTable {
	int userid;
    String name;
    int age;
    String disease;
    String created_at;
    
    public UserTable() {
    }
    
    public UserTable(int userid, String name, int age,String disease)
    {
    	this.userid = userid;
    	this.name = name;
    	this.age = age;
    	this.disease = disease;
    }
    
    public void setid(int id) {
        this.userid = id;
    }
    public void setname(String name) {
        this.name = name;
    }
    
    public void setdisease(String name) {
        this.disease = name;
    }
    
     public void setage(int age){
            this.age = age;
    }
     
     public void setcreatedat(String created) {
         this.created_at = created;
     } 
     
     public int getid() {
         return this.userid;
     }
     
     public String getname() {
         return this.name;
     }
     
     public String getdisease() {
         return this.disease;
     }
     
     public int getage() {
         return this.age;
     }
}
