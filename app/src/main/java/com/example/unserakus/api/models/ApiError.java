package com.example.unserakus.api.models;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

/**
 * Kelas wrapper kustom untuk error.
 * Kelas ini mengambil VolleyError, mencoba mem-parsing body JSON
 * untuk pesan "detail", dan menyediakannya melalui getDetailMessage().
 */
public class ApiError {
    private final String detailMessage;
    private final int statusCode;
    private final VolleyError originalError;

    public ApiError(VolleyError error) {
        this.originalError = error;
        this.statusCode = (error.networkResponse != null) ? error.networkResponse.statusCode : -1;
        this.detailMessage = parseDetailMessage(error);
    }

    /**
     * Mencoba mem-parsing body respons error untuk mengekstrak pesan "detail".
     */
    private String parseDetailMessage(VolleyError error) {
        // Cek jika ada respons jaringan dan data
        if (error.networkResponse == null || error.networkResponse.data == null) {
            return error.getMessage(); // Fallback ke pesan Volley standar
        }

        try {
            // Konversi data byte ke String
            String jsonString = new String(
                    error.networkResponse.data,
                    HttpHeaderParser.parseCharset(error.networkResponse.headers, "utf-8")
            );

            // Gunakan GSON untuk mem-parsing ke POJO ApiErrorResponse
            Gson gson = new Gson();
            ApiErrorResponse errorResponse = gson.fromJson(jsonString, ApiErrorResponse.class);

            // Jika parsing berhasil dan field "detail" ada, kembalikan itu
            if (errorResponse != null && errorResponse.getDetail() != null) {
                return errorResponse.getDetail();
            }

        } catch (UnsupportedEncodingException e) {
            // Error encoding, abaikan dan fallback
        } catch (com.google.gson.JsonSyntaxException e) {
            // Error JSON (misal: server kirim HTML, bukan JSON), abaikan dan fallback
        }

        // Fallback jika parsing gagal atau format tidak sesuai
        return error.getMessage();
    }

    /**
     * @return Pesan error "detail" yang bersih dari server, atau pesan Volley default jika gagal.
     */
    public String getDetailMessage() {
        return detailMessage;
    }

    /**
     * @return Kode status HTTP (misal: 400, 401, 404).
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @return Objek VolleyError asli jika Anda memerlukannya untuk debugging lanjutan.
     */
    public VolleyError getOriginalError() {
        return originalError;
    }

    @Override
    public String toString() {
        return "ApiError (Status " + statusCode + "): " + detailMessage;
    }
}
