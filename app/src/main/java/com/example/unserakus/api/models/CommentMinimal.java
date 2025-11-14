package com.example.unserakus.api.models;

import com.example.unserakus.Helpers;
import com.google.gson.annotations.SerializedName;

public class CommentMinimal {

    // TAMBAHKAN INI
    @SerializedName("id")
    private int id;

    @SerializedName("user")
    private User user;

    @SerializedName("text")
    private String text;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("file")
    private String file;

    public String getFile() {
        return file;
    }

    public int getId() { return id; }

    // Getter
    public User getUser() { return user; }
    public String getText() { return text; }

    public String createdAtTimeAgo() {
        try {
            return Helpers.toTimeAgo(createdAt);
        } catch (UnsupportedClassVersionError e) {
            return "Date Parsing Error";
        }
    }

}