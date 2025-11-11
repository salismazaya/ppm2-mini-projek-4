package com.example.unserakus.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unserakus.R;
import com.example.unserakus.SharedPreferencesHelper;
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
    private int loggedInUserId; // BARU
    private int threadOwnerId = -1; // BARU (default -1)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_detail);

        // Ambil token
        String token = SharedPreferencesHelper.getToken(this);
        apiService = new ApiService(this, token);

        // Ambil ID user yang login
        loggedInUserId = SharedPreferencesHelper.getLoggedInUserId(this);

        threadId = getIntent().getIntExtra(EXTRA_THREAD_ID, -1);


        initViews();
        // setupRecyclerView(); // DIHAPUS DARI SINI

        loadThreadDetails(); // Setup RecyclerView akan dipanggil di dalam ini

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

    /**
     * Setup RecyclerView dipanggil SETELAH kita mendapatkan threadOwnerId
     */
    private void setupRecyclerView() {
        Log.d("OWNER_THREAD", String.valueOf(loggedInUserId));
        commentAdapter = new CommentAdapter(
                commentList,
                loggedInUserId,
                threadOwnerId,
                (comment, position) -> {
                    // Implementasi OnDeleteClick
                    new AlertDialog.Builder(this)
                            .setTitle("Hapus Komentar")
                            .setMessage("Apakah Anda yakin ingin menghapus komentar ini?")
                            .setPositiveButton("Hapus", (dialog, which) -> {
                                handleDeleteComment(comment, position);
                            })
                            .setNegativeButton("Batal", null)
                            .show();
                }
        );
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentAdapter);
    }


    private void loadThreadDetails() {
        // TODO: Tampilkan loading

        apiService.retrieveThread(threadId, new ApiService.ApiResponseListener<Thread>() {
            @Override
            public void onSuccess(Thread thread) {
                // TODO: Sembunyikan loading

                User owner = thread.getOwner();
                if (owner != null) {
                    threadOwnerId = owner.getId();

                    String name = owner.getFirstName() + " " + owner.getLastName();
                    tvName.setText(name.trim());
                    tvUsername.setText("@" + owner.getUsername());
                }
                tvThreadText.setText(thread.getText());

                commentList.clear();
                commentList.addAll(thread.getComments());

                // Panggil setupRecyclerView SEKARANG, setelah kita punya threadOwnerId
                if (commentAdapter == null) {
                    setupRecyclerView();
                } else {
                    commentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(ApiError error) {
                // ... (error handling)
            }
        });
    }

    private void handleDeleteComment(CommentMinimal comment, int position) {
        // TODO: Tampilkan loading

        apiService.deleteComment(comment.getId(), new ApiService.ApiSuccessListener() {
            @Override
            public void onSuccess() {
                // TODO: Sembunyikan loading
                Toast.makeText(ThreadDetailActivity.this, "Komentar dihapus", Toast.LENGTH_SHORT).show();
                // Update list secara lokal
                commentList.remove(position);
                commentAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onError(ApiError error) {
                // TODO: Sembunyikan loading
                Toast.makeText(ThreadDetailActivity.this, "Gagal hapus: " + error.getDetailMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSendComment() {
        // ... (fungsi ini tidak berubah)
        String commentText = etComment.getText().toString().trim();
        if (commentText.isEmpty()) {
            return;
        }

        apiService.createComment(threadId, commentText, new ApiService.ApiResponseListener<Comment>() {
            @Override
            public void onSuccess(Comment newComment) {
                etComment.setText("");
                // Cukup panggil loadThreadDetails untuk refresh
                loadThreadDetails();
                Toast.makeText(ThreadDetailActivity.this, "Komentar terkirim", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(ApiError error) {
                Toast.makeText(ThreadDetailActivity.this, "Gagal mengirim: " + error.getDetailMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}