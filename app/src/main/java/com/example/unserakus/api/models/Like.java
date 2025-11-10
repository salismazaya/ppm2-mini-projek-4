package com.example.unserakus.api.models;

import com.google.gson.annotations.SerializedName;

public class Like {
    // Sesuai spec, request body hanya 'thread',
    // tapi respons 201 mengembalikan skema 'Like' yang sama.
    @SerializedName("thread")
    private int thread;

    // Getter
    public int getThread() { return thread; }
}
