package com.example.unserakus.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response; // DIUBAH: Import ini mungkin diperlukan
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.unserakus.api.models.ApiError;
import com.google.gson.reflect.TypeToken;

import com.example.unserakus.api.models.Comment;
import com.example.unserakus.api.models.Like;
import com.example.unserakus.api.models.LoginResponse; // Diperlukan dari revisi sebelumnya

import com.example.unserakus.api.models.Thread;
import com.example.unserakus.api.models.User;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiService {

    private static final String BASE_URL = "https://ppm2-mini-projek-backend.onrender.com/api";
    private final RequestQueue requestQueue;
    private final String authToken;

    /**
     * DIUBAH: Listener generik sekarang mengembalikan ApiError.
     */
    public interface ApiResponseListener<T> {
        void onSuccess(T response);
        void onError(ApiError error); // DIUBAH dari VolleyError
    }

    /**
     * DIUBAH: Listener sederhana sekarang mengembalikan ApiError.
     */
    public interface ApiSuccessListener {
        void onSuccess();
        void onError(ApiError error); // DIUBAH dari VolleyError
    }

    public ApiService(Context context, String token) {
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.authToken = token;
    }

    private Map<String, String> getAuthHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Token " + authToken);
        return headers;
    }

    // --- Helper Error Listener ---
    // TAMBAHAN: Helper ini membungkus VolleyError menjadi ApiError

    /**
     * Membuat ErrorListener yang mengonversi VolleyError ke ApiError kustom.
     */
    private <T> Response.ErrorListener createErrorListener(final ApiResponseListener<T> listener) {
        return error -> listener.onError(new ApiError(error));
    }

    /**
     * Membuat ErrorListener yang mengonversi VolleyError ke ApiError kustom.
     */
    private Response.ErrorListener createErrorListener(final ApiSuccessListener listener) {
        return error -> listener.onError(new ApiError(error));
    }

    // --- Comments ---

    public void createComment(int threadId, String text, final ApiResponseListener<Comment> listener) {
        String url = BASE_URL + "/comments/";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("thread", threadId);
            requestBody.put("text", text);
        } catch (Exception e) {
            listener.onError(new ApiError(new VolleyError("JSON Body creation error", e)));
            return;
        }

        GsonRequest<Comment> request = new GsonRequest<>(
                Request.Method.POST,
                url,
                requestBody.toString(),
                Comment.class,
                getAuthHeaders(),
                listener::onSuccess,
                createErrorListener(listener) // DIUBAH
        );
        requestQueue.add(request);
    }

    public void deleteComment(int id, final ApiSuccessListener listener) {
        String url = BASE_URL + "/comments/" + id + "/";
        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> listener.onSuccess(),
                createErrorListener(listener) // DIUBAH
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return getAuthHeaders();
            }
        };
        requestQueue.add(request);
    }

    // --- Likes ---

    public void createLike(int threadId, final ApiResponseListener<Like> listener) {
        String url = BASE_URL + "/likes/";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("thread", threadId);
        } catch (Exception e) {
            listener.onError(new ApiError(new VolleyError("JSON Body creation error", e)));
            return;
        }

        GsonRequest<Like> request = new GsonRequest<>(
                Request.Method.POST,
                url,
                requestBody.toString(),
                Like.class,
                getAuthHeaders(),
                listener::onSuccess,
                createErrorListener(listener) // DIUBAH
        );
        requestQueue.add(request);
    }

    public void deleteLike(int id, final ApiSuccessListener listener) {
        String url = BASE_URL + "/likes/" + String.valueOf(id) + "/";
        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> listener.onSuccess(),
                createErrorListener(listener) // DIUBAH
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return getAuthHeaders();
            }
        };
        requestQueue.add(request);
    }

    // --- Auth (Login/Register) ---

    public void login(String username, String password, final ApiResponseListener<LoginResponse> listener) {
        String url = BASE_URL + "/login/";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", username);
            requestBody.put("password", password);
        } catch (Exception e) {
            listener.onError(new ApiError(new VolleyError("JSON Body creation error", e)));
            return;
        }

        GsonRequest<LoginResponse> request = new GsonRequest<>(
                Request.Method.POST,
                url,
                requestBody.toString(),
                LoginResponse.class,
                null,
                listener::onSuccess,
                createErrorListener(listener) // DIUBAH
        );
        requestQueue.add(request);
    }

    public void register(String username, String password, String name, final ApiSuccessListener listener) {
        String url = BASE_URL + "/register/";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", username);
            requestBody.put("password", password);
            requestBody.put("name", name);
        } catch (Exception e) {
            listener.onError(new ApiError(new VolleyError("JSON Body creation error", e)));
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> listener.onSuccess(),
                createErrorListener(listener) // DIUBAH
        ) {
            @Override
            public String getBodyContentType() { return "application/json; charset=utf-8"; }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody.toString().getBytes("utf-8");
                } catch (java.io.UnsupportedEncodingException uee) {
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(com.android.volley.NetworkResponse response) {
                Log.d("REGISTER_INFO", String.valueOf(response.statusCode));
                if (response.statusCode == 201) {
                    return Response.success("Created", com.android.volley.toolbox.HttpHeaderParser.parseCacheHeaders(response));
                }
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(request);
    }

    // --- Threads ---

    public void listThreads(String orderBy, final ApiResponseListener<List<Thread>> listener) {
        String url = BASE_URL + "/threads/?order=" + orderBy;
        Type listType = new TypeToken<List<Thread>>() {}.getType();

        GsonRequest<List<Thread>> request = new GsonRequest<>(
                Request.Method.GET,
                url,
                null,
                listType,
                getAuthHeaders(),
                listener::onSuccess,
                createErrorListener(listener) // DIUBAH
        );
        requestQueue.add(request);
    }

    public void listThreads(final ApiResponseListener<List<Thread>> listener) {
        listThreads("recent", listener);
    }

    public void createThread(String text, final ApiResponseListener<Thread> listener) {
        String url = BASE_URL + "/threads/";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("text", text);
        } catch (Exception e) {
            listener.onError(new ApiError(new VolleyError("JSON Body creation error", e)));
            return;
        }

        GsonRequest<Thread> request = new GsonRequest<>(
                Request.Method.POST,
                url,
                requestBody.toString(),
                Thread.class,
                getAuthHeaders(),
                listener::onSuccess,
                createErrorListener(listener) // DIUBAH
        );
        requestQueue.add(request);
    }

    public void retrieveThread(int id, final ApiResponseListener<Thread> listener) {
        String url = BASE_URL + "/threads/" + id + "/";
        GsonRequest<Thread> request = new GsonRequest<>(
                Request.Method.GET,
                url,
                null,
                Thread.class,
                getAuthHeaders(),
                listener::onSuccess,
                createErrorListener(listener) // DIUBAH
        );
        requestQueue.add(request);
    }

    public void updateThread(int id, String text, final ApiResponseListener<Thread> listener) {
        String url = BASE_URL + "/threads/" + id + "/";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("text", text);
        } catch (Exception e) {
            listener.onError(new ApiError(new VolleyError("JSON Body creation error", e)));
            return;
        }

        GsonRequest<Thread> request = new GsonRequest<>(
                Request.Method.PUT,
                url,
                requestBody.toString(),
                Thread.class,
                getAuthHeaders(),
                listener::onSuccess,
                createErrorListener(listener) // DIUBAH
        );
        requestQueue.add(request);
    }

    public void partialUpdateThread(int id, String text, final ApiResponseListener<Thread> listener) {
        String url = BASE_URL + "/threads/" + id + "/";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("text", text);
        } catch (Exception e) {
            listener.onError(new ApiError(new VolleyError("JSON Body creation error", e)));
            return;
        }

        GsonRequest<Thread> request = new GsonRequest<>(
                Request.Method.PATCH,
                url,
                requestBody.toString(),
                Thread.class,
                getAuthHeaders(),
                listener::onSuccess,
                createErrorListener(listener) // DIUBAH
        );
        requestQueue.add(request);
    }

    public void deleteThread(int id, final ApiSuccessListener listener) {
        String url = BASE_URL + "/threads/" + id + "/";
        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> listener.onSuccess(),
                createErrorListener(listener) // DIUBAH
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return getAuthHeaders();
            }
        };
        requestQueue.add(request);
    }

    // --- User Profile ---

    /**
     * GET /api/user/
     * Mengambil detail data user yang sedang login saat ini.
     */
    public void getCurrentUser(final ApiResponseListener<User> listener) {
        String url = BASE_URL + "/user/";

        GsonRequest<User> request = new GsonRequest<>(
                Request.Method.GET,
                url,
                null, // Tidak ada body untuk GET
                User.class, // Respons-nya adalah objek User
                getAuthHeaders(), // Membutuhkan token
                listener::onSuccess,
                createErrorListener(listener)
        );
        requestQueue.add(request);
    }

    /**
     * PUT /api/user/
     * Memperbarui detail user yang sedang login.
     * Sesuai dengan UserEditSerializer (username, first_name, last_name).
     */
    public void updateUser(String firstName, String lastName, String username, final ApiResponseListener<User> listener) {
        String url = BASE_URL + "/user/";

        JSONObject requestBody = new JSONObject();
        try {
            // Sesuai dengan UserEditSerializer
            requestBody.put("first_name", firstName);
            requestBody.put("last_name", lastName);
            requestBody.put("username", username);
        } catch (Exception e) {
            listener.onError(new ApiError(new VolleyError("JSON Body creation error", e)));
            return;
        }

        GsonRequest<User> request = new GsonRequest<>(
                Request.Method.PUT, // Sesuai permintaan Anda
                url,
                requestBody.toString(),
                User.class, // Asumsi server mengembalikan data user yang baru
                getAuthHeaders(), // Membutuhkan token
                listener::onSuccess,
                createErrorListener(listener)
        );
        requestQueue.add(request);
    }
}