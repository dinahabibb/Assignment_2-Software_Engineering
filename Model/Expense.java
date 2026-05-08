package Model;
public class Expense extends Transaction{
   private String notes ;
 public Expense(int ID , double amount , String date , String description , String category , String type , String paymentMethod , String notes){
    super(ID, amount, date, description, category, type, paymentMethod) ;
    this.notes=notes ;
 }
 public String getNotes(){
    return notes;
 }
 public void setNotes(String notes){
    this.notes=notes;
 }
}
