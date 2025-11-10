package com.example.unserakus.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model untuk menangani respons JSON dari endpoint login.
 * Contoh: {"token": "abc123xyz"}
 */
public class LoginResponse {

    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }
}