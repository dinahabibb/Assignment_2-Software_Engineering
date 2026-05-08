package Model;
import java.util.Date;

public class Goal {

    private int id;
    private String name;
    private double targetAmount;
    private double currentAmount;
    private Date deadline;
    private String status; // "In Progress" or "Completed"

    public Goal(int id, String name, double targetAmount, double currentAmount, Date deadline) {
        this.id = id;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.status = "In Progress";
    }

    public double getProgress() {
        if (targetAmount == 0) return 0;
        double progress = (currentAmount / targetAmount) * 100;
        return Math.min(progress, 100); 
    }

    public boolean addContribution(double amount) {
        if (amount <= 0) return false;
        currentAmount += amount;
        if (currentAmount >= targetAmount) {
            currentAmount = targetAmount;
            status = "Completed";
        }
        return true;
    }

    public double getMonthlySavingsNeeded() {
        double remaining = targetAmount - currentAmount;
        if (remaining <= 0) return 0;

        // Calculate months left until deadline
        Date today = new Date();
        long diffMs = deadline.getTime() - today.getTime();
        long diffDays = diffMs / (1000 * 60 * 60 * 24);
        double monthsLeft = diffDays / 30.0;

        if (monthsLeft <= 0) return remaining; // deadline passed or today
        return remaining / monthsLeft;
    }

    // validate: targetAmount > 0 and deadline is future date 
    public static String validate(String name, double targetAmount, Date deadline) {
        if (name == null || name.trim().isEmpty()) {
            return "Goal name cannot be empty.";
        }
        if (targetAmount <= 0) {
            return "Target amount must be greater than 0.";
        }
        Date today = new Date();
        if (!deadline.after(today)) {
            return "Deadline must be a future date.";
        }
        return null; //valid
    }

    public boolean isAchieved() {
        return "Completed".equals(status);
    }

    public String getStatus() {
        return status;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getTargetAmount() { return targetAmount; }
    public double getCurrentAmount() { return currentAmount; }
    public Date getDeadline() { return deadline; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }
}
