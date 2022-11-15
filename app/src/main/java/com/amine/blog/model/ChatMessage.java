package com.amine.blog.model;

public class ChatMessage {
    private String text, messageId, senderUsername, receiverUsername;
    private MyTime time;
    private long timeInMill;

    public ChatMessage(){}

    public ChatMessage(String text, String messageId, String senderUsername, String receiverUsername, MyTime time,
                       long timeInMill) {
        this.text = text;
        this.messageId = messageId;
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.time = time;
        this.timeInMill = timeInMill;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public MyTime getTime() {
        return time;
    }

    public void setTime(MyTime time) {
        this.time = time;
    }

    public long getTimeInMill() {
        return timeInMill;
    }

    public void setTimeInMill(long timeInMill) {
        this.timeInMill = timeInMill;
    }
}
