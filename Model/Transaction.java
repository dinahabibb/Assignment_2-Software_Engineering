package Model;
public class Transaction {
    private int ID;
    private double amount ;
    private String date ;
    private String description;
    private String category;
    private String type ;
    private String paymentMethod ;

    public Transaction(int ID , double amount , String date , String description , String category , String type , String paymentMethod){
        this.ID=ID ;
        this.amount=amount;
        this.date=date;
        this.description=description;
        this.category=category;
        this.type=type;
        this.paymentMethod=paymentMethod;
    }

    public int getID(){
     return ID;
    }
    public double getAmount(){
     return amount;
    }
    public String getDate(){
     return date;
    }
    public String getDescription(){
     return description;
    }
    public String getCategory(){
     return category;
    }
    public String getType(){
     return type;
    }
    public String getPaymentMethod(){
     return paymentMethod;
    }

    public void setAmount(double amount){
        this.amount=amount;
    }
    public void setID(int ID){
        this.ID=ID;
    }
    public void setDescription(String description){
        this.description=description;
    }
    public void setCategory(String category){
        this.category=category;
    }
    public void setDate(String date){
        this.date=date;
    }
    public void setPaymentMethod(String paymentMethod){
        this.paymentMethod=paymentMethod;
    }
}
