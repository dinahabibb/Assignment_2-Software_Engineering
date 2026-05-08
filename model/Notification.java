package model;

import java.util.Date;

/**
 * Represents a user notification in the system.
 * Used to inform users about alerts, updates, or system messages.
 */
public class Notification {

    /** Unique notification ID */
    private int id;

    /** ID of the user who receives the notification */
    private int userId;

    /** Type of notification (e.g., ALERT, INFO, WARNING) */
    private String type;

    /** Notification message content */
    private String message;

    /** Indicates whether the notification has been read */
    private boolean isRead;

    /** Timestamp when the notification was created */
    private Date timestamp;

    /**
     * Creates a new Notification.
     *
     * @param id notification ID
     * @param userId recipient user ID
     * @param type notification type
     * @param message notification message
     */
    public Notification(int id, int userId, String type, String message) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.isRead = false;
        this.timestamp = new Date();
    }

    /**
     * Marks this notification as read.
     */
    public void markAsRead() {
        this.isRead = true;
    }

    /** @return notification message */
    public String getMessage() {
        return message;
    }

    /** @return notification ID */
    public int getId() {
        return id;
    }

    /** @return user ID */
    public int getUserId() {
        return userId;
    }

    /** @return notification type */
    public String getType() {
        return type;
    }

    /** @return true if notification is read */
    public boolean isRead() {
        return isRead;
    }

    /** @return timestamp of notification creation */
    public Date getTimestamp() {
        return timestamp;
    }

    /** Used by DataManager to restore message from file */
    public void setMessage(String message) {
        this.message = message;
    }

    /** Used by DataManager to restore read status */
    public void setRead(boolean read) {
        this.isRead = read;
    }

    /** Used by DataManager to restore timestamp */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
