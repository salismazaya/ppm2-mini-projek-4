package com.example.unserakus.api.models;

import com.google.gson.annotations.SerializedName;

public class CommentMinimal {

    // TAMBAHKAN INI
    @SerializedName("id")
    private int id;

    @SerializedName("user")
    private User user;

    @SerializedName("text")
    private String text;

    // TAMBAHKAN GETTER INI
    public int getId() { return id; }

    // Getter
    public User getUser() { return user; }
    public String getText() { return text; }
}