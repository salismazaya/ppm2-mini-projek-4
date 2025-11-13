package com.example.unserakus.api.models;


import static com.example.unserakus.api.ApiService.BASE_URL;

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

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("title")
    private String title;

    @SerializedName("media_id")
    private String media_id;

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

    public String getCreatedAt() {
        return created_at;
    }

    public String getTitle() {
        return title;
    }

    public String getMediaUrl() {
        if (media_id == null) {
            return null;
        }
        return BASE_URL + "/file?id=" + media_id;
    }
}