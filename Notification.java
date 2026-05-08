import java.util.Date;

/**
 * Stores alerts sent to the user (budget warnings, goal completions).
 * Based on SDS Class Diagram - Notification class
 * Person 3
 */
public class Notification {

    private int id;
    private int userId;
    private String type;      // e.g. "BUDGET_EXCEEDED", "BUDGET_NEAR_LIMIT", "GOAL_COMPLETE"
    private String message;
    private boolean isRead;
    private Date timestamp;

    public Notification(int id, int userId, String type, String message) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.isRead = false;
        this.timestamp = new Date();
    }

    // Called in sequence diagram: markAsRead():void
    public void markAsRead() {
        this.isRead = true;
    }

    // Called in sequence diagram: getMessage():String
    public String getMessage() {
        return message;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getType() { return type; }
    public boolean isRead() { return isRead; }
    public Date getTimestamp() { return timestamp; }

    public void setMessage(String message) { this.message = message; }
}
