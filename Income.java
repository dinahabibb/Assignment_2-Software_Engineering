public class Income extends Transaction{
    private String source ;

    public Income(int ID , double amount , String date , String description , String category , String type , String paymentMethod, String source){
        super(ID, amount, date, description, category, type, paymentMethod) ;
        this.source=source ;
    }
    public String getSource(){
    return source;
    } 
    public void setSource(String source){
        this.source=source ;
    }
    
}
