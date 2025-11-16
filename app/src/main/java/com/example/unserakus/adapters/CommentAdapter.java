package com.example.unserakus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unserakus.R;
import com.example.unserakus.api.models.CommentMinimal;
import com.example.unserakus.api.models.User;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<CommentMinimal> commentList;

    // --- Bagian Baru ---
    private int loggedInUserId;
    private int threadOwnerId;
    private OnCommentActionsListener listener;

    public interface OnCommentActionsListener {
        void onDeleteClick(CommentMinimal comment, int position);
    }
    // --- Akhir Bagian Baru ---

    // Constructor DIUBAH
    public CommentAdapter(List<CommentMinimal> commentList, int loggedInUserId, int threadOwnerId, OnCommentActionsListener listener) {
        this.commentList = commentList;
        this.loggedInUserId = loggedInUserId;
        this.threadOwnerId = threadOwnerId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentMinimal comment = commentList.get(position);

        User user = comment.getUser();
        if (user != null) {
            String name = user.getFirstName() + " " + user.getLastName();
            holder.tvName.setText(name.trim());
            holder.tvUsername.setText("@" + user.getUsername());
        }

        holder.tvCommentText.setText(comment.getText());
        holder.tvTimePost.setText(comment.createdAtTimeAgo());

        if (loggedInUserId == user.getId() || loggedInUserId == threadOwnerId) {
            holder.btnDeleteComment.setVisibility(View.VISIBLE);
            holder.btnDeleteComment.setOnClickListener(v -> {
                listener.onDeleteClick(comment, holder.getAdapterPosition());
            });
        } else {
            holder.btnDeleteComment.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    // ViewHolder DIUBAH
    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvUsername, tvCommentText, tvTimePost;
        ImageButton btnDeleteComment; // BARU

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
            btnDeleteComment = itemView.findViewById(R.id.btnDeleteComment);
            tvTimePost = itemView.findViewById(R.id.tvTimePost);
        }
    }
}