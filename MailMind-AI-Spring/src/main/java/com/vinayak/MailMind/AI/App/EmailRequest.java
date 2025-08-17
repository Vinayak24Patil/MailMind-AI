package com.vinayak.MailMind.AI.App;

import lombok.Data;

@Data
public class EmailRequest {
    private String emailContent;
    private String tone;

    // Default constructor (needed for JSON -> Object mapping)
    public EmailRequest() {}

    // Constructor
    public EmailRequest(String emailContent, String tone) {
        this.emailContent = emailContent;
        this.tone = tone;
    }

    // Getter and Setter for emailContent
    public String getEmailContent() {
        return emailContent;
    }

    public void setEmailContent(String emailContent) {
        this.emailContent = emailContent;
    }

    // Getter and Setter for tone
    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }
}
