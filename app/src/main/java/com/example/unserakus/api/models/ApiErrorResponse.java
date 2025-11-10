package com.example.unserakus.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * POJO untuk GSON mem-parsing respons error dari server.
 * Sesuai format: {"detail": "Pesan error..."}
 */
public class ApiErrorResponse {

    @SerializedName("detail")
    private String detail;

    public String getDetail() {
        return detail;
    }
}