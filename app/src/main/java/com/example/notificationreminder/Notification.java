package com.example.notificationreminder;

public class Notification {

    private String content;
    private int id;

    public Notification(String content, int id) {
        this.content = content;
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String s) {
        this.content = s;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.getId() + ": " + this.getContent();
    }

}
