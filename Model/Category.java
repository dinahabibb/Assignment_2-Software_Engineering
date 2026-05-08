package Model;
public class Category {
    private int ID ; 
    private String name ;
    private boolean isCustom ;

    public Category(int ID , String name , boolean isCustom){
        this.ID=ID ;
        this.name=name;
        this.isCustom=isCustom;
    }
    public String getName(){
        return name;
    }
    public void delete(){
        System.out.println(name + " deleted");
    }
    public int getID(){
        return ID;
    }
    public boolean isCustom(){
       return isCustom;
    }
    public void setname(String name){
        this.name=name ;
    }
    public void updateName(String newName){
        this.name= newName ;
    }
}
