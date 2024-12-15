package models;

public class MessageModel {
    private String senderId;  // The ID of the sender (Firebase user ID)
    private String message;   // The message content
    private long timestamp;   // Timestamp of when the message was sent

    // Default constructor required for Firebase Realtime Database
    public MessageModel() {}

    // Constructor to initialize the message object
    public MessageModel(String senderId, String message, long timestamp) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getter and Setter methods

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
