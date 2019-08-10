package com.ntrllog.notificationreminder;

public class Notification {

    private String content;
    private int id;

    Notification(String content, int id) {
        this.content = content;
        this.id = id;
    }

    String getContent() {
        return this.content;
    }

    void setContent(String s) {
        this.content = s;
    }

    int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.getId() + ": " + this.getContent();
    }

}
