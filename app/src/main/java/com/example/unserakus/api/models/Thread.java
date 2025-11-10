package com.example.unserakus.api.models;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Thread {
    @SerializedName("id")
    private int id;

    @SerializedName("owner")
    private User owner;

    @SerializedName("text")
    private String text;

    @SerializedName("comments")
    private List<CommentMinimal> comments;

    @SerializedName("likes_count")
    private int likesCount;

    @SerializedName("comments_count")
    private int commentsCount;

    @SerializedName("liked")
    private boolean liked;

    // Getter
    public int getId() { return id; }
    public User getOwner() { return owner; }
    public String getText() { return text; }
    public List<CommentMinimal> getComments() {
        if (comments == null) {
            return  new ArrayList<>();
        }

        return  comments;
    }
    public int getLikesCount() { return likesCount; }
    public int getCommentsCount() { return commentsCount; }
    public boolean isLiked() { return liked; }
}