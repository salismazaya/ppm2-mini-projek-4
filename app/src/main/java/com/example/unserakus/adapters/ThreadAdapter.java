package com.example.unserakus.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.unserakus.R;
import com.example.unserakus.api.models.Thread;
import com.example.unserakus.api.models.User;

import java.util.List;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ThreadViewHolder> {

    private List<Thread> threadList;
    private OnThreadActionsListener listener;
    private int loggedInUserId;

    private Context context;

    // Interface DIUBAH
    public interface OnThreadActionsListener {
        void onLikeClick(int position, Thread thread);
        void onCommentClick(Thread thread);
        void onDeleteClick(Thread thread, int position);
    }

    public ThreadAdapter(Context context, List<Thread> threadList, OnThreadActionsListener listener, int loggedInUserId) {
        this.threadList = threadList;
        this.listener = listener;
        this.loggedInUserId = loggedInUserId;
        this.context = context;
    }

    @NonNull
    @Override
    public ThreadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thread, parent, false);
        return new ThreadViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ThreadViewHolder holder, int position) {
        Thread thread = threadList.get(position);

        thread.createdAtTimeAgo();



        User owner = thread.getOwner();
        if (owner != null) {
            String name = owner.getFirstName() + " " + owner.getLastName();
            holder.tvName.setText(name.trim());
            holder.tvUsername.setText("@" + owner.getUsername());
        }

        String fileUrl = thread.getFile();

//        Glide.with(context).clear(holder.ivImage);
        holder.ivImage.setVisibility(View.GONE);
        holder.ivImage.setImageDrawable(null);

        if (fileUrl != null) {
            holder.ivImage.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext()).load(fileUrl).placeholder(R.drawable.ghost).into(holder.ivImage);

        }

        holder.tvThreadText.setText(thread.getText());
        holder.tvLikeCount.setText(String.valueOf(thread.getLikesCount()));
        holder.tvCommentCount.setText(String.valueOf(thread.getCommentsCount()));

        if (thread.isLiked()) {
            holder.btnLike.setImageResource(R.drawable.ic_like_filled);
            holder.btnLike.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_light));
        } else {
            holder.btnLike.setImageResource(R.drawable.ic_like_outline);
            holder.btnLike.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.darker_gray));
        }


        if (loggedInUserId == thread.getOwner().getId()) {
            holder.btnDeleteThread.setVisibility(View.VISIBLE);
            holder.btnDeleteThread.setOnClickListener(v -> {
                listener.onDeleteClick(thread, holder.getAdapterPosition());
            });
        } else {
            holder.btnDeleteThread.setVisibility(View.GONE);
        }


        holder.btnLike.setOnClickListener(v -> {
            listener.onLikeClick(holder.getAdapterPosition(), thread);
        });

        holder.btnComment.setOnClickListener(v -> {
            listener.onCommentClick(thread);
        });

        holder.itemView.setOnClickListener(v -> {
            listener.onCommentClick(thread);
        });
    }

    @Override
    public int getItemCount() {
        return threadList.size();
    }

    static class ThreadViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvUsername, tvThreadText, tvLikeCount, tvCommentCount;
        ImageButton btnLike, btnComment, btnDeleteThread;
        ImageView ivImage;

        public ThreadViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvThreadText = itemView.findViewById(R.id.tvThreadText);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnDeleteThread = itemView.findViewById(R.id.btnDeleteThread);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }
}