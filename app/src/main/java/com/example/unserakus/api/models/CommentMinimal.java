package com.example.unserakus.api.models;

import com.google.gson.annotations.SerializedName;

public class CommentMinimal {
    @SerializedName("user")
    private User user;

    @SerializedName("text")
    private String text;

    // Getter
    public User getUser() { return user; }
    public String getText() { return text; }
}