package com.example.unserakus.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unserakus.R;
import com.example.unserakus.api.models.ApiError; // Import
import com.example.unserakus.api.ApiService; // Import

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etUsername, etPassword, etConfirmPassword;
    Button btnRegister;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiService = new ApiService(this, "");

        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validasi Sederhana
        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Password tidak cocok");
            return;
        }

        // TODO: Tampilkan loading indicator

        apiService.register(username, password, name, new ApiService.ApiSuccessListener() {
            @Override
            public void onSuccess() {
                // TODO: Sembunyikan loading indicator
                Log.d("REGISTER_SUCCESS", "Registrasi berhasil");
                Toast.makeText(RegisterActivity.this, "Registrasi berhasil, silakan login", Toast.LENGTH_LONG).show();
                finish(); // Kembali ke halaman Login
            }

            @Override
            public void onError(ApiError error) {
                // TODO: Sembunyikan loading indicator
                String errorMessage = error.getDetailMessage();
                Log.e("REGISTER_ERROR", "Error: " + errorMessage);
                Toast.makeText(RegisterActivity.this, "Registrasi Gagal: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}