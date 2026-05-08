package Model;
import java.util.Date;

public class Notification {

    private int id;
    private int userId;
    private String type;     
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

    
    public void markAsRead() {
        this.isRead = true;
    }

    
    public String getMessage() {
        return message;
    }

    // Getters
    public int getId()          { return id; }
    public int getUserId()      { return userId; }
    public String getType()     { return type; }
    public boolean isRead()     { return isRead; }
    public Date getTimestamp()  { return timestamp; }

    // Setters used by DataManager to restore notifications.csv
    public void setMessage(String message)      { this.message = message; }
    public void setRead(boolean read)           { this.isRead = read; }
    public void setTimestamp(Date timestamp)    { this.timestamp = timestamp; }
}
