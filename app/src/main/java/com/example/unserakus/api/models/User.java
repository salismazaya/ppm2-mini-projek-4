package com.example.unserakus.api.models;

import static com.example.unserakus.api.ApiService.BASE_URL;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("profile_picture_url")
    private String profilePictureUrl;

    // Getter dan Setter
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }

    public String getProfilePicture() {
        if (profilePictureUrl == null) {
            return null;
        }
        return BASE_URL.replace("/api", "") + profilePictureUrl;
    }
}