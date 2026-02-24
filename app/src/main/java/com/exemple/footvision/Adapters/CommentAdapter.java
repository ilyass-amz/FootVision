package com.exemple.footvision.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exemple.footvision.Models.Comment;
import com.exemple.footvision.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;
    private OnCommentClickListener listener;

    public interface OnCommentClickListener {
        void onCommentClick(Comment comment, int position, View view);
    }

    public CommentAdapter(Context context, List<Comment> commentList, OnCommentClickListener listener) {
        this.context = context;
        this.commentList = commentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment c = commentList.get(position);
        holder.tvUser.setText(c.getUser());
        holder.tvComment.setText(c.getComment());

        String avatar = c.getUser().length() >= 2 ?
                c.getUser().substring(0,2).toUpperCase() :
                c.getUser().substring(0,1).toUpperCase();
        holder.tvAvatar.setText(avatar);

        holder.itemView.setOnClickListener(v -> listener.onCommentClick(c, position, holder.itemView));
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvUser, tvComment;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvComment = itemView.findViewById(R.id.tvComment);
        }
    }
}