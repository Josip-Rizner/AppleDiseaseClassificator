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

import java.util.List;

public class RecommendationSystemsAdapter extends RecyclerView.Adapter<RecommendationSystemsAdapter.SystemRecommendationViewHolder>{

    private final RecommendationSystemRecyclerViewOnClickInterface recommendationSystemRecyclerViewOnClickInterface;
    private Context context;
    private List<RecommendationSystem> recommendationSystems;

    public RecommendationSystemsAdapter(Context context, List<RecommendationSystem> recommendationSystems, RecommendationSystemRecyclerViewOnClickInterface recommendationSystemRecyclerViewOnClickInterface){
        this.context = context;
        this.recommendationSystems = recommendationSystems;
        this.recommendationSystemRecyclerViewOnClickInterface = recommendationSystemRecyclerViewOnClickInterface;
    }

    public static class SystemRecommendationViewHolder extends RecyclerView.ViewHolder{

        public TextView tvName, tvClassifiedDisease, tvLocation;
        public ImageView ivImage;
        public RecommendationSystem recommendationSystem;

        public SystemRecommendationViewHolder(@NonNull View itemView, RecommendationSystemRecyclerViewOnClickInterface recommendationSystemRecyclerViewOnClickInterface) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvSystemRecommendationName);
            ivImage = itemView.findViewById(R.id.ivSystemRecommendationImage);
            tvClassifiedDisease = itemView.findViewById(R.id.tvClassifiedDisease);
            tvLocation = itemView.findViewById(R.id.tvLocation);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recommendationSystemRecyclerViewOnClickInterface != null){
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){
                            recommendationSystemRecyclerViewOnClickInterface.onRecommendationSystemItemClick(recommendationSystem);
                        }
                    }
                }
            });

        }
    }


    @NonNull
    @Override
    public SystemRecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View listItem = layoutInflater.inflate(R.layout.recyclerview_system_recommendation_item, parent, false);

        RecommendationSystemsAdapter.SystemRecommendationViewHolder systemRecommendationViewHolder = new RecommendationSystemsAdapter.SystemRecommendationViewHolder(listItem, recommendationSystemRecyclerViewOnClickInterface);

        return systemRecommendationViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SystemRecommendationViewHolder holder, int position) {
        RecommendationSystem recommendationSystemItem = recommendationSystems.get(position);
        holder.recommendationSystem = recommendationSystemItem;

        holder.tvName.setText(recommendationSystemItem.getName());
        holder.tvClassifiedDisease.setText(recommendationSystemItem.getClassifiedDisease());
        holder.tvLocation.setText(recommendationSystemItem.getLatitude() + " / " + recommendationSystemItem.getLongitude());

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
