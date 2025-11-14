package com.example.unserakus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unserakus.LoadingAlert;
import com.example.unserakus.MainActivity;
import com.example.unserakus.R;
import com.example.unserakus.SharedPreferencesHelper;
import com.example.unserakus.api.models.ApiError; // Import kelas ApiError Anda
import com.example.unserakus.api.ApiService; // Import ApiService Anda
import com.example.unserakus.api.models.LoginResponse; // Import LoginResponse Anda
import com.example.unserakus.api.models.User;
import com.example.unserakus.storages.Prefences;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvGoToRegister;

    // Ganti 'null' dengan token jika Anda menyimpannya
    ApiService apiService;
    LoadingAlert loadingAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadingAlert = new LoadingAlert(LoginActivity.this);

        // Inisialisasi ApiService tanpa token
        apiService = new ApiService(this, "");

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(v -> handleLogin());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    // ... (di dalam LoginActivity.java)

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username dan password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingAlert.startLoading();

        apiService.login(username, password, new ApiService.ApiResponseListener<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                // 1. Dapatkan dan simpan token
                String token = response.getToken();
                SharedPreferencesHelper.saveToken(LoginActivity.this, token);
                Log.d("LOGIN_SUCCESS", "Token disimpan: " + token);

                loadingAlert.dismissDialog();

                // 2. Sekarang, ambil data user menggunakan token baru
                fetchAndSaveUser(token);
            }

            @Override
            public void onError(ApiError error) {
                // TODO: Sembunyikan loading indicator
                String errorMessage = error.getDetailMessage();
                Log.e("LOGIN_ERROR", "Error: " + errorMessage);

                loadingAlert.dismissDialog();

                Toast.makeText(LoginActivity.this, "Login Gagal: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Setelah token didapat, panggil /api/user/ untuk menyimpan data user
     */
    private void fetchAndSaveUser(String token) {
        // Buat instance service BARU dengan token
        ApiService userApiService = new ApiService(this, token);

        userApiService.getCurrentUser(new ApiService.ApiResponseListener<User>() {
            @Override
            public void onSuccess(User user) {
                // 3. Simpan data user
                SharedPreferencesHelper.saveUser(LoginActivity.this, user);
                Log.d("LOGIN_SUCCESS", "User disimpan: " + user.getUsername());

                // TODO: Sembunyikan loading indicator

                // 4. Pindah ke MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(ApiError error) {
                // TODO: Sembunyikan loading indicator
                // Jika login berhasil tapi ambil user gagal (seharusnya tidak terjadi)
                Log.e("LOGIN_ERROR", "Gagal mengambil data user: " + error.getDetailMessage());
                Toast.makeText(LoginActivity.this, "Login berhasil, tapi gagal mengambil data user", Toast.LENGTH_SHORT).show();
            }
        });
    }


}