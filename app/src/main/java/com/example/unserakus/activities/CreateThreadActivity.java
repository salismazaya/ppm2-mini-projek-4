package com.example.unserakus.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unserakus.R;
import com.example.unserakus.api.models.ApiError;
import com.example.unserakus.api.ApiService;
import com.example.unserakus.api.models.Thread;
import com.example.unserakus.storages.Prefences;

public class CreateThreadActivity extends AppCompatActivity {

    EditText etThreadText;
    Button btnPostThread;
    ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_thread);


        Prefences prefences = new Prefences(CreateThreadActivity.this);
        String token = prefences.getToken();

        apiService = new ApiService(this, token);

        etThreadText = findViewById(R.id.etThreadText);
        btnPostThread = findViewById(R.id.btnPostThread);

        btnPostThread.setOnClickListener(v -> handlePostThread());
    }

    private void handlePostThread() {
        String text = etThreadText.getText().toString().trim();

        if (text.isEmpty()) {
            Toast.makeText(this, "Thread tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Tampilkan loading

        apiService.createThread(text, new ApiService.ApiResponseListener<Thread>() {
            @Override
            public void onSuccess(Thread response) {
                // TODO: Sembunyikan loading
                Log.d("CREATE_THREAD", "Thread berhasil dibuat");
                Toast.makeText(CreateThreadActivity.this, "Thread diposting!", Toast.LENGTH_SHORT).show();
                finish(); // Tutup activity dan kembali ke Home
            }

            @Override
            public void onError(ApiError error) {
                // TODO: Sembunyikan loading
                Log.e("CREATE_THREAD", "Error: " + error.getDetailMessage());
                Toast.makeText(CreateThreadActivity.this, "Gagal: " + error.getDetailMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}