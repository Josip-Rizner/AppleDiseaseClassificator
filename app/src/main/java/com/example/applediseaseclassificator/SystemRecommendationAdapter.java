package com.example.applediseaseclassificator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

public class SystemRecommendationAdapter extends RecyclerView.Adapter<SystemRecommendationAdapter.SystemRecommendationViewHolder>{

    private Context context;
    private List<RecommendationSystem> recommendationSystems;

    public SystemRecommendationAdapter(Context context, List<RecommendationSystem> recommendationSystems){
        this.context = context;
        this.recommendationSystems = recommendationSystems;
    }

    public static class SystemRecommendationViewHolder extends RecyclerView.ViewHolder{

        public TextView tvName;
        public ImageView ivImage;

        public SystemRecommendationViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvSystemRecommendationName);
            ivImage = itemView.findViewById(R.id.ivSystemRecommendationImage);
        }
    }


    @NonNull
    @Override
    public SystemRecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View listItem = layoutInflater.inflate(R.layout.recyclerview_system_recommendation_item, parent, false);

        SystemRecommendationAdapter.SystemRecommendationViewHolder systemRecommendationViewHolder = new SystemRecommendationAdapter.SystemRecommendationViewHolder(listItem);

        return systemRecommendationViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SystemRecommendationViewHolder holder, int position) {
        RecommendationSystem recommendationSystemItem = recommendationSystems.get(position);

        holder.tvName.setText(recommendationSystemItem.getName());
        Picasso.with(context)
                .load(recommendationSystemItem.getStartingImageReference())
                .fit()
                .centerCrop()
                .into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return recommendationSystems.size();
    }

}
