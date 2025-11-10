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
import com.example.unserakus.api.models.User; // Pastikan Anda mengimpor model User
import com.example.unserakus.storages.Prefences;

public class EditProfileActivity extends AppCompatActivity {

    // DIUBAH: etName menjadi etFirstName dan etLastName
    EditText etFirstName, etLastName, etUsername;
    Button btnSaveProfile;
    ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Prefences prefences = new Prefences(this);
        String token = prefences.getToken();
        apiService = new ApiService(this, token);

        // Inisialisasi View yang sudah direvisi
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etUsername = findViewById(R.id.etUsername);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        // Memuat data user saat ini
        loadCurrentUserData();

        btnSaveProfile.setOnClickListener(v -> handleSaveProfile());
    }

    /**
     * Memanggil GET /api/user/ untuk mengisi field
     */
    private void loadCurrentUserData() {
        // TODO: Tampilkan loading

        apiService.getCurrentUser(new ApiService.ApiResponseListener<User>() {
            @Override
            public void onSuccess(User user) {
                // TODO: Sembunyikan loading
                etFirstName.setText(user.getFirstName());
                etLastName.setText(user.getLastName());
                etUsername.setText(user.getUsername());
            }

            @Override
            public void onError(ApiError error) {
                // TODO: Sembunyikan loading
                Log.e("EditProfile", "Gagal load user: " + error.getDetailMessage());
                Toast.makeText(EditProfileActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Memanggil PUT /api/user/ untuk menyimpan perubahan
     */
    private void handleSaveProfile() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();

        if (firstName.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Nama Depan dan Username tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Tampilkan loading
        btnSaveProfile.setEnabled(false); // Nonaktifkan tombol sementara

        apiService.updateUser(firstName, lastName, username, new ApiService.ApiResponseListener<User>() {
            @Override
            public void onSuccess(User updatedUser) {
                // TODO: Sembunyikan loading
                btnSaveProfile.setEnabled(true);
                Toast.makeText(EditProfileActivity.this, "Profile berhasil diperbarui", Toast.LENGTH_SHORT).show();

                // TODO: Update data user di SharedPreferences jika perlu
                // (misal: jika nama/username disimpan di sana)

                finish(); // Tutup activity dan kembali ke ProfileFragment
            }

            @Override
            public void onError(ApiError error) {
                // TODO: Sembunyikan loading
                btnSaveProfile.setEnabled(true);
                Log.e("EditProfile", "Gagal update user: " + error.getDetailMessage());
                Toast.makeText(EditProfileActivity.this, "Gagal: " + error.getDetailMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}