package com.github.duc010298.android.entity.socket;

public class CustomAppMessage {
    private String command;
    private String sendToImei;
    private Object content;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getSendToImei() {
        return sendToImei;
    }

    public void setSendToImei(String sendToImei) {
        this.sendToImei = sendToImei;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object object) {
        this.content = content;
    }
}
