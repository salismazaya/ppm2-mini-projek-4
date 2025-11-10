package com.example.unserakus.api.models;


import com.google.gson.annotations.SerializedName;

public class Comment {
    @SerializedName("id")
    private int id;

    @SerializedName("user")
    private User user;

    @SerializedName("thread")
    private int thread;

    @SerializedName("text")
    private String text;

    // Getter
    public int getId() { return id; }
    public User getUser() { return user; }
    public int getThread() { return thread; }
    public String getText() { return text; }
}