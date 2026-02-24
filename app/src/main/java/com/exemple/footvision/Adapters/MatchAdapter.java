package com.exemple.footvision.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exemple.footvision.Models.Match;
import com.exemple.footvision.R;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    interface OnItemClickListener {
        void onItemClick(Match match);
    }

    List<Match> matchList;
    OnItemClickListener listener;

    public MatchAdapter(List<Match> matchList, OnItemClickListener listener){
        this.matchList = matchList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matchList.get(position);
        holder.homeTeam.setText(match.getHomeTeam());
        holder.awayTeam.setText(match.getAwayTeam());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(match));
    }

    @Override
    public int getItemCount() { return matchList.size(); }

    static class MatchViewHolder extends RecyclerView.ViewHolder{
        TextView homeTeam, awayTeam;
        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            homeTeam = itemView.findViewById(R.id.homeTeam);
            awayTeam = itemView.findViewById(R.id.awayTeam);
        }
    }
}