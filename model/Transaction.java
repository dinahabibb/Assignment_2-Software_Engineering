package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a financial transaction in the system.
 * Can be either income or expense and stores all related details.
 */
public class Transaction {

    /** Unique transaction ID */
    private int ID;

    /** Transaction amount */
    private double amount;

    /** Date of the transaction */
    private String date;

    /** Description of the transaction */
    private String description;

    /** Category of the transaction (Food, Salary, etc.) */
    private String category;

    /** Type of transaction (Income or Expense) */
    private String type;

    /** Payment method used */
    private String paymentMethod;

    /** Email of the user who owns this transaction */
    private String userEmail;

    /**
     * Global list storing all transactions in memory.
     */
    public static List<Transaction> allTransactions = new ArrayList<>();

    /**
     * Creates a new Transaction.
     *
     * @param ID unique transaction ID
     * @param amount transaction amount
     * @param date transaction date
     * @param description transaction description
     * @param category transaction category
     * @param type transaction type (Income / Expense)
     * @param paymentMethod payment method used
     * @param userEmail owner of the transaction
     */
    public Transaction(int ID, double amount, String date, String description,
                       String category, String type, String paymentMethod,
                       String userEmail) {
        this.ID = ID;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.category = category;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.userEmail = userEmail;
    }

    // ---------------- Getters ----------------

    /** @return transaction ID */
    public int getID() {
        return ID;
    }

    /** @return transaction amount */
    public double getAmount() {
        return amount;
    }

    /** @return transaction date */
    public String getDate() {
        return date;
    }

    /** @return transaction description */
    public String getDescription() {
        return description;
    }

    /** @return transaction category */
    public String getCategory() {
        return category;
    }

    /** @return transaction type */
    public String getType() {
        return type;
    }

    /** @return payment method */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /** @return user email */
    public String getUserEmail() {
        return userEmail;
    }

    // ---------------- Setters ----------------

    /** Sets transaction amount */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /** Sets transaction ID */
    public void setID(int ID) {
        this.ID = ID;
    }

    /** Sets transaction description */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Sets transaction category */
    public void setCategory(String category) {
        this.category = category;
    }

    /** Sets transaction date */
    public void setDate(String date) {
        this.date = date;
    }

    /** Sets payment method */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /** Sets user email */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * Calculates total income from all transactions.
     *
     * @return total income amount
     */
    public static double getTotalIncome() {
        double total = 0;
        for (Transaction t : allTransactions) {
            if ("income".equalsIgnoreCase(t.type)) {
                total += t.amount;
            }
        }
        return total;
    }

    /**
     * Calculates total expenses from all transactions.
     *
     * @return total expense amount
     */
    public static double getTotalExpenses() {
        double total = 0;
        for (Transaction t : allTransactions) {
            if ("expense".equalsIgnoreCase(t.type)) {
                total += t.amount;
            }
        }
        return total;
    }

    /**
     * Returns a formatted string representation of the transaction.
     *
     * @return readable transaction summary
     */
    @Override
    public String toString() {
        return type.toUpperCase() + " | " + category + " | " +
               amount + " EGP | " + description;
    }
}
