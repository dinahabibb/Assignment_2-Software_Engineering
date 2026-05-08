package model;

/**
 * Represents a budget for a specific category within a time period.
 * Tracks spending, limits, and status alerts.
 */
public class Budget {

    /** Unique identifier for the budget */
    private int id;

    /** Category name (e.g., Food, Transport) */
    private String category;

    /** Maximum allowed spending limit */
    private double limitAmount;

    /** Budget start date */
    private String startDate;

    /** Budget end date */
    private String endDate;

    /** Alert threshold percentage for warnings */
    private int alertThreshold;

    /** Current total spending under this budget */
    private double currentSpending;

    /** Current status of the budget (On Track, Near Limit, Exceeded) */
    private String status;

    /**
     * Creates a new Budget instance.
     */
    public Budget(int id, String category, double limitAmount,
                  String startDate, String endDate,
                  int alertThreshold, double currentSpending,
                  String status) {
        this.id = id;
        this.category = category;
        this.limitAmount = limitAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.alertThreshold = alertThreshold;
        this.currentSpending = currentSpending;
        this.status = status;
    }

    /** @return budget ID */
    public int getId() {
        return id;
    }

    /** @return budget category */
    public String getCategory() {
        return category;
    }

    /** @return spending limit */
    public double getLimitAmount() {
        return limitAmount;
    }

    /** @return current spending amount */
    public double getCurrentSpending() {
        return currentSpending;
    }

    /** @return current budget status */
    public String getStatus() {
        return status;
    }

    /** @return alert threshold percentage */
    public int getAlertThreshold() {
        return alertThreshold;
    }

    /** @return start date */
    public String getStartDate() {
        return startDate;
    }

    /** @return end date */
    public String getEndDate() {
        return endDate;
    }

    /** Sets a new spending limit */
    public void setLimitAmount(double limitAmount) {
        this.limitAmount = limitAmount;
    }

    /** @return true if spending exceeds limit */
    public boolean isExceeded() {
        return currentSpending > limitAmount;
    }

    /** @return true if spending is near the alert threshold */
    public boolean isNearLimit() {
        if (limitAmount == 0) return false;
        double percentage = (currentSpending / limitAmount) * 100;
        return percentage >= alertThreshold;
    }

    /** @return remaining budget amount */
    public double getRemainingAmount() {
        return limitAmount - currentSpending;
    }

    /** @return spending percentage of the budget */
    public double getSpendingPercentage() {
        if (limitAmount == 0) return 0;
        return (currentSpending / limitAmount) * 100;
    }

    /** Adds an amount to current spending and updates status */
    public void updateSpending(double amount) {
        this.currentSpending += amount;
        updateStatus();
    }

    /** Updates budget status based on current spending */
    private void updateStatus() {
        if (isExceeded()) {
            this.status = "Exceeded";
        } else if (isNearLimit()) {
            this.status = "Near Limit";
        } else {
            this.status = "On Track";
        }
    }
}
