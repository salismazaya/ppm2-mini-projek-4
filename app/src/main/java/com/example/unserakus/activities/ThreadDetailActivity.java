package com.example.unserakus.activities;

import android.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.unserakus.LoadingAlert;
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

    TextView tvName, tvUsername, tvThreadText, tvTimePost;
    ImageView ivImage;
    RecyclerView rvComments;
    EditText etComment;
    Button btnSendComment;

    ApiService apiService;
    CommentAdapter commentAdapter;
    List<CommentMinimal> commentList = new ArrayList<>();
    private int threadId;
    private int loggedInUserId; // BARU
    private int threadOwnerId = -1; // BARU (default -1)
    LoadingAlert loadingAlert;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_detail);

        loadingAlert = new LoadingAlert(this);

        // Ambil token
        String token = SharedPreferencesHelper.getToken(this);
        apiService = new ApiService(this, token);

        // Ambil ID user yang login
        loggedInUserId = SharedPreferencesHelper.getLoggedInUserId(this);

        threadId = getIntent().getIntExtra(EXTRA_THREAD_ID, -1);

        initViews();

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
        tvTimePost = findViewById(R.id.tvTimePost);
        ivImage = findViewById(R.id.ivImage);
    }

    private void deleteThread() {
        loadingAlert.startLoading();
        apiService.deleteThread(threadId, new ApiService.ApiSuccessListener() {
            @Override
            public void onSuccess() {
                loadingAlert.dismissDialog();
                Toast.makeText(ThreadDetailActivity.this, "Thread dihapus", Toast.LENGTH_SHORT).show();
                finish(); // balik ke list thread
            }

            @Override
            public void onError(ApiError error) {
                loadingAlert.dismissDialog();
                Toast.makeText(ThreadDetailActivity.this, "Gagal: " + error.getDetailMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        loadingAlert.startLoading();

        apiService.retrieveThread(threadId, new ApiService.ApiResponseListener<Thread>() {
            @Override
            public void onSuccess(Thread thread) {
                loadingAlert.dismissDialog();

                User owner = thread.getOwner();
                if (owner != null) {
                    threadOwnerId = owner.getId();

                    String name = owner.getFirstName() + " " + owner.getLastName();
                    tvName.setText(name.trim());
                    tvUsername.setText("@" + owner.getUsername());
                }
                tvThreadText.setText(thread.getText());

                String fileUrl = thread.getFile();
                ivImage.setVisibility(View.GONE);
                ivImage.setImageDrawable(null);

                if (fileUrl != null){
                    ivImage.setVisibility(View.VISIBLE);
                    Glide.with(ivImage).load(fileUrl).into(ivImage);
                }


                commentList.clear();
                commentList.addAll(thread.getComments());

                tvTimePost.setText(thread.createdAtTimeAgo());

//                ivImage.setVisibility(View.GONE);
                ivImage.setImageDrawable(null);

                if (thread.getFile() != null) {
                    Glide.with(getApplicationContext()).load(thread.getFile()).into(ivImage);
                    ivImage.setVisibility(TextView.VISIBLE);
                }

                // Panggil setupRecyclerView SEKARANG, setelah kita punya threadOwnerId
                if (commentAdapter == null) {
                    setupRecyclerView();
                } else {
                    commentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(ApiError error) {
                loadingAlert.dismissDialog();
            }
        });
    }

    private void handleDeleteComment(CommentMinimal comment, int position) {
        loadingAlert.startLoading();

        apiService.deleteComment(comment.getId(), new ApiService.ApiSuccessListener() {
            @Override
            public void onSuccess() {
                loadingAlert.dismissDialog();
                Toast.makeText(ThreadDetailActivity.this, "Komentar dihapus", Toast.LENGTH_SHORT).show();
                // Update list secara lokal
                commentList.remove(position);
                commentAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onError(ApiError error) {
                loadingAlert.dismissDialog();
                Toast.makeText(ThreadDetailActivity.this, "Gagal hapus: " + error.getDetailMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSendComment() {
        loadingAlert.startLoading();

        String commentText = etComment.getText().toString().trim();
        if (commentText.isEmpty()) {
            return;
        }

        apiService.createComment(threadId, commentText, new ApiService.ApiResponseListener<Comment>() {
            @Override
            public void onSuccess(Comment newComment) {
                loadingAlert.dismissDialog();
                etComment.setText("");

                loadThreadDetails();
                Toast.makeText(ThreadDetailActivity.this, "Komentar terkirim", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(ApiError error) {
                loadingAlert.dismissDialog();
                Toast.makeText(ThreadDetailActivity.this, "Gagal mengirim: " + error.getDetailMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}