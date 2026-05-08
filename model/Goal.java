package model;

import java.util.Date;

/**
 * Represents a financial goal set by the user.
 * Tracks progress toward a target amount within a deadline.
 */
public class Goal {

    /** Unique identifier for the goal */
    private int id;

    /** Name of the goal */
    private String name;

    /** Target amount to achieve */
    private double targetAmount;

    /** Current saved/contributed amount */
    private double currentAmount;

    /** Deadline to achieve the goal */
    private Date deadline;

    /** Current status of the goal (In Progress / Completed) */
    private String status;

    /**
     * Creates a new Goal.
     *
     * @param id unique goal ID
     * @param name goal name
     * @param targetAmount target savings amount
     * @param currentAmount current saved amount
     * @param deadline goal deadline
     */
    public Goal(int id, String name, double targetAmount,
                double currentAmount, Date deadline) {
        this.id = id;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.status = "In Progress";
    }

    /**
     * Calculates progress percentage toward the goal.
     *
     * @return progress percentage (0–100)
     */
    public double getProgress() {
        if (targetAmount == 0) return 0;
        double progress = (currentAmount / targetAmount) * 100;
        return Math.min(progress, 100);
    }

    /**
     * Adds a contribution to the goal.
     *
     * @param amount amount to add
     * @return true if contribution is valid, false otherwise
     */
    public boolean addContribution(double amount) {
        if (amount <= 0) return false;

        currentAmount += amount;

        if (currentAmount >= targetAmount) {
            currentAmount = targetAmount;
            status = "Completed";
        }
        return true;
    }

    /**
     * Calculates required monthly savings to reach goal by deadline.
     *
     * @return monthly savings needed
     */
    public double getMonthlySavingsNeeded() {
        double remaining = targetAmount - currentAmount;
        if (remaining <= 0) return 0;

        Date today = new Date();
        long diffMs = deadline.getTime() - today.getTime();
        long diffDays = diffMs / (1000 * 60 * 60 * 24);
        double monthsLeft = diffDays / 30.0;

        if (monthsLeft <= 0) return remaining;
        return remaining / monthsLeft;
    }

    /**
     * Validates goal input data.
     *
     * @param name goal name
     * @param targetAmount target amount
     * @param deadline goal deadline
     * @return error message if invalid, null if valid
     */
    public static String validate(String name, double targetAmount, Date deadline) {
        if (name == null || name.trim().isEmpty()) {
            return "Goal name cannot be empty.";
        }
        if (targetAmount <= 0) {
            return "Target amount must be greater than 0.";
        }
        if (!deadline.after(new Date())) {
            return "Deadline must be a future date.";
        }
        return null;
    }

    /** @return true if goal is completed */
    public boolean isAchieved() {
        return "Completed".equals(status);
    }

    /** @return current status of goal */
    public String getStatus() {
        return status;
    }

    // ---------------- Getters ----------------

    /** @return goal ID */
    public int getId() { return id; }

    /** @return goal name */
    public String getName() { return name; }

    /** @return target amount */
    public double getTargetAmount() { return targetAmount; }

    /** @return current amount saved */
    public double getCurrentAmount() { return currentAmount; }

    /** @return deadline date */
    public Date getDeadline() { return deadline; }

    // ---------------- Setters ----------------

    /** Updates goal name */
    public void setName(String name) { this.name = name; }

    /** Updates target amount */
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }

    /** Updates current amount */
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }

    /** Updates deadline */
    public void setDeadline(Date deadline) { this.deadline = deadline; }
}
