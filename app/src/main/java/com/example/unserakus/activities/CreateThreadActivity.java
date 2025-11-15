package com.example.unserakus.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unserakus.LoadingAlert;
import com.example.unserakus.R;
import com.example.unserakus.SharedPreferencesHelper;
import com.example.unserakus.api.models.ApiError;
import com.example.unserakus.api.ApiService;
import com.example.unserakus.api.models.Thread;
import com.example.unserakus.storages.Prefences;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CreateThreadActivity extends AppCompatActivity {
    EditText etThreadText;
    Button btnPostThread;
    ApiService apiService;
    ImageView ivPreview;
    Button btnPickImage;
    private byte[] selectedImageData = null;
    ActivityResultLauncher<Intent> imagePickerLauncher;
    LoadingAlert loadingAlert;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_thread);

        loadingAlert = new LoadingAlert(this);

        String token = SharedPreferencesHelper.getToken(getApplicationContext());

        apiService = new ApiService(this, token);

        etThreadText = findViewById(R.id.etThreadText);
        btnPostThread = findViewById(R.id.btnPostThread);

        ivPreview = findViewById(R.id.ivPreview);
        btnPickImage = findViewById(R.id.btnPickImage);

        // Setup Launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        try {
                            // Tampilkan Preview
                            ivPreview.setImageURI(uri);
                            ivPreview.setVisibility(View.VISIBLE);

                            // Konversi URI ke byte[]
                            InputStream iStream = getContentResolver().openInputStream(uri);
                            selectedImageData = getBytes(iStream);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        btnPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnPostThread.setOnClickListener(v -> {
            String text = etThreadText.getText().toString();
            loadingAlert.startLoading();

            // Panggil Multipart API
            apiService.createThreadMultipart(text, selectedImageData, new ApiService.ApiResponseListener<Thread>() {
                @Override
                public void onSuccess(Thread response) {
                    Toast.makeText(CreateThreadActivity.this, "Thread terkirim!", Toast.LENGTH_SHORT).show();
                    finish();
                    loadingAlert.dismissDialog();
                }
                @Override
                public void onError(ApiError error) {
                    loadingAlert.dismissDialog();
                }
            });
        });
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}