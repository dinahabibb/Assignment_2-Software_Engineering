package model;

/**
 * Represents an expense transaction.
 * Extends Transaction by adding extra notes specific to expenses.
 */
public class Expense extends Transaction {

    /** Additional notes or comments about the expense */
    private String notes;

    /**
     * Creates a new Expense transaction.
     *
     * @param ID unique transaction ID
     * @param amount expense amount
     * @param date transaction date
     * @param description description of the expense
     * @param category expense category
     * @param type transaction type (Expense)
     * @param paymentMethod method used for payment
     * @param notes additional notes for the expense
     * @param userEmail owner of the transaction
     */
    public Expense(int ID, double amount, String date, String description,
                   String category, String type, String paymentMethod,
                   String notes, String userEmail) {
        super(ID, amount, date, description, category, type, paymentMethod, userEmail);
        this.notes = notes;
    }

    /** @return additional notes for this expense */
    public String getNotes() {
        return notes;
    }

    /**
     * Updates the notes for this expense.
     *
     * @param notes new notes value
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
