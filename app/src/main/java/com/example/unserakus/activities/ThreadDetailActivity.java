package com.example.unserakus.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unserakus.R;
import com.example.unserakus.adapters.CommentAdapter;
import com.example.unserakus.api.models.ApiError;
import com.example.unserakus.api.ApiService;
import com.example.unserakus.api.models.Comment;
import com.example.unserakus.api.models.CommentMinimal;
import com.example.unserakus.api.models.Thread;
import com.example.unserakus.api.models.User;
import com.example.unserakus.storages.Prefences;

import java.util.ArrayList;
import java.util.List;

public class ThreadDetailActivity extends AppCompatActivity {

    public static final String EXTRA_THREAD_ID = "EXTRA_THREAD_ID";

    TextView tvName, tvUsername, tvThreadText;
    RecyclerView rvComments;
    EditText etComment;
    Button btnSendComment;

    ApiService apiService;
    CommentAdapter commentAdapter;
    List<CommentMinimal> commentList = new ArrayList<>();

    private int threadId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_detail);

        Prefences prefences = new Prefences(this);
        String token = prefences.getToken();
        apiService = new ApiService(this, token);

        // Ambil ID thread dari Intent
        threadId = getIntent().getIntExtra(EXTRA_THREAD_ID, -1);
        if (threadId == -1) {
            Toast.makeText(this, "Error: Thread ID tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();

        loadThreadDetails();

        btnSendComment.setOnClickListener(v -> handleSendComment());
    }

    private void initViews() {
        tvName = findViewById(R.id.tvName);
        tvUsername = findViewById(R.id.tvUsername);
        tvThreadText = findViewById(R.id.tvThreadText);
        rvComments = findViewById(R.id.rvComments);
        etComment = findViewById(R.id.etComment);
        btnSendComment = findViewById(R.id.btnSendComment);
    }

    private void setupRecyclerView() {
        commentAdapter = new CommentAdapter(commentList);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentAdapter);
    }

    private void loadThreadDetails() {
        apiService.retrieveThread(threadId, new ApiService.ApiResponseListener<Thread>() {
            @Override
            public void onSuccess(Thread thread) {
                User owner = thread.getOwner();
                if (owner != null) {
                    String name = owner.getFirstName() + " " + owner.getLastName();
                    tvName.setText(name.trim());
                    tvUsername.setText("@" + owner.getUsername());
                }
                tvThreadText.setText(thread.getText());

                commentList.clear();
                commentList.addAll(thread.getComments());
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(ApiError error) {
                // TODO: Sembunyikan loading
                Toast.makeText(ThreadDetailActivity.this, "Gagal memuat thread: " + error.getDetailMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSendComment() {
        String commentText = etComment.getText().toString().trim();
        if (commentText.isEmpty()) {
            return;
        }

        // TODO: Tampilkan loading kecil di tombol kirim

        apiService.createComment(threadId, commentText, new ApiService.ApiResponseListener<Comment>() {
            @Override
            public void onSuccess(Comment newComment) {
                // TODO: Sembunyikan loading
                etComment.setText(""); // Kosongkan input

                // Refresh list komentar
                // Cara simpel: load ulang semua data
                // Cara lebih baik: tambahkan comment baru ke list secara manual
                loadThreadDetails();

                Toast.makeText(ThreadDetailActivity.this, "Komentar terkirim", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(ApiError error) {
                // TODO: Sembunyikan loading
                Toast.makeText(ThreadDetailActivity.this, "Gagal mengirim: " + error.getDetailMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}