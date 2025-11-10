package com.example.unserakus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unserakus.R;
import com.example.unserakus.api.models.Thread;
import com.example.unserakus.api.models.User;

import java.util.List;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ThreadViewHolder> {

    private List<Thread> threadList;
    private OnThreadActionsListener listener;

    // Interface untuk menangani klik
    public interface OnThreadActionsListener {
        void onLikeClick(int position, Thread thread);
        void onCommentClick(Thread thread);
    }

    public ThreadAdapter(List<Thread> threadList, OnThreadActionsListener listener) {
        this.threadList = threadList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ThreadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thread, parent, false);
        return new ThreadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThreadViewHolder holder, int position) {
        Thread thread = threadList.get(position);

        // Set data
        User owner = thread.getOwner();
        if (owner != null) {
            String name = owner.getFirstName() + " " + owner.getLastName();
            holder.tvName.setText(name.trim());
            holder.tvUsername.setText("@" + owner.getUsername());
        }

        holder.tvThreadText.setText(thread.getText());
        holder.tvLikeCount.setText(String.valueOf(thread.getLikesCount()));
        holder.tvCommentCount.setText(String.valueOf(thread.getCommentsCount()));

        // Set status 'Like'
        if (thread.isLiked()) {
            holder.btnLike.setImageResource(R.drawable.ic_like_filled); // Buat drawable 'filled'
            holder.btnLike.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_light));
        } else {
            holder.btnLike.setImageResource(R.drawable.ic_like_outline);
            holder.btnLike.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.darker_gray));
        }

        // Set Listeners
        holder.btnLike.setOnClickListener(v -> {
            listener.onLikeClick(holder.getAdapterPosition(), thread);
        });

        holder.btnComment.setOnClickListener(v -> {
            listener.onCommentClick(thread);
        });

        // Klik pada item juga membuka detail
        holder.itemView.setOnClickListener(v -> {
            listener.onCommentClick(thread);
        });
    }

    @Override
    public int getItemCount() {
        return threadList.size();
    }

    // ViewHolder
    static class ThreadViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvUsername, tvThreadText, tvLikeCount, tvCommentCount;
        ImageButton btnLike, btnComment;

        public ThreadViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvThreadText = itemView.findViewById(R.id.tvThreadText);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
        }
    }
}