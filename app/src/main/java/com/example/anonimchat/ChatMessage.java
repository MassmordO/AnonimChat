package com.example.anonimchat;

import java.util.Date;

public class ChatMessage {
    private String messageText;
    public ChatMessage(String messageText) {
        this.messageText = messageText;
        // Initialize to current time
    }
    public ChatMessage(){
    }
    public String getMessageText() {
        return messageText;
    }
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

}
