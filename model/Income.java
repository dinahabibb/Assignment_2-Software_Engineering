package model;

/**
 * Represents an income transaction.
 * Extends Transaction by adding a source of income.
 */
public class Income extends Transaction {

    /** Source of the income (e.g., salary, freelance, etc.) */
    private String source;

    /**
     * Creates a new Income transaction.
     *
     * @param ID unique transaction ID
     * @param amount income amount
     * @param date transaction date
     * @param description description of the income
     * @param category income category
     * @param type transaction type (Income)
     * @param paymentMethod method used for payment
     * @param source source of income
     * @param userEmail owner of the transaction
     */
    public Income(int ID, double amount, String date, String description,
                  String category, String type, String paymentMethod,
                  String source, String userEmail) {
        super(ID, amount, date, description, category, type, paymentMethod, userEmail);
        this.source = source;
    }

    /** @return source of income */
    public String getSource() {
        return source;
    }

    /**
     * Updates the income source.
     *
     * @param source new source value
     */
    public void setSource(String source) {
        this.source = source;
    }
}
