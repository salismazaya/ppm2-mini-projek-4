package com.example.unserakus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unserakus.MainActivity;
import com.example.unserakus.R;
import com.example.unserakus.api.models.ApiError; // Import kelas ApiError Anda
import com.example.unserakus.api.ApiService; // Import ApiService Anda
import com.example.unserakus.api.models.LoginResponse; // Import LoginResponse Anda
import com.example.unserakus.storages.Prefences;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvGoToRegister;

    // Ganti 'null' dengan token jika Anda menyimpannya
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username dan password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Tampilkan loading indicator

        apiService.login(username, password, new ApiService.ApiResponseListener<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                // TODO: Sembunyikan loading indicator
                String token = response.getToken();
                Log.d("LOGIN_SUCCESS", "Token: " + token);

                Prefences prefences = new Prefences(LoginActivity.this);
                prefences.setToken(token);

                // Pindah ke MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(ApiError error) {
                // TODO: Sembunyikan loading indicator
                String errorMessage = error.getDetailMessage();
                Log.e("LOGIN_ERROR", "Error: " + errorMessage);
                Toast.makeText(LoginActivity.this, "Login Gagal: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}