package com.example.unserakus;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton buttonRegister;
    RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Pastikan sesuai dengan nama file layout

        // Inisialisasi view
        etName = findViewById(R.id.name);
        etEmail = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        etConfirmPassword = findViewById(R.id.password);
        buttonRegister = findViewById(R.id.button_register);
        queue = Volley.newRequestQueue(this);


        // Set listener untuk tombol register
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegister();
            }
        });
    }

    private void handleRegister() {
        // Ambil nilai dari field
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validasi dasar
        if (name.isEmpty()) {
            etName.setError("Nama tidak boleh kosong");
            etName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            etEmail.setError("Email tidak boleh kosong");
            etEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Password tidak cocok");
            etConfirmPassword.requestFocus();
            return;
        }

        registerUser(name, email, password);

    }

    // Fungsi untuk mengirim POST request ke /api/register
    private void registerUser(String name, String username, String password) {
        String url = "https://78b48412132d.ngrok-free.app/api/register"; // Ganti dengan URL endpoint nyata

        // Buat body JSON
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", name);
            jsonBody.put("username", username); // Menggunakan email sebagai username
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error membuat request", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buat JsonObjectRequest untuk POST
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Sukses: status code 201 (Volley otomatis handle ini jika server return 201)
                        Toast.makeText(RegisterActivity.this, "Registrasi berhasil!", Toast.LENGTH_LONG).show();
                        // Tambahkan aksi lanjutan, seperti navigasi ke LoginActivity
                        // startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        // finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Gagal: ambil field "detail" dari response JSON jika ad
                        String errorMessage = "Registrasi gagal";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                JSONObject errorResponse = new JSONObject(new String(error.networkResponse.data));
                                if (errorResponse.has("detail")) {
                                    errorMessage = errorResponse.getString("detail");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
        );

        queue.add(jsonObjectRequest);
    }


}
