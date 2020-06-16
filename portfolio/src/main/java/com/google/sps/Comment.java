package com.google.sps.data;

public class Comment{
    private long id;
    private String comment;
    private long timestamp;

    public Comment(long id, String comment, long timestamp) {
        this.id = id;
        this.comment = comment;
        this.timestamp = timestamp;
    }
}