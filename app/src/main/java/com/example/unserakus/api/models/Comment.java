package com.example.unserakus.api.models;

import static com.example.unserakus.api.ApiService.BASE_URL;

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

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("media_id")
    private String media_id;

    // Getter
    public int getId() { return id; }
    public User getUser() { return user; }
    public int getThread() { return thread; }
    public String getText() { return text; }

    public String getMediaUrl() {
        if (media_id == null) {
            return null;
        }
        return BASE_URL + "/file?id=" + media_id;
    }
}