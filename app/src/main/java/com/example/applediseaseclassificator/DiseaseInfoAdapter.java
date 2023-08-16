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

public class DiseaseInfoAdapter  extends RecyclerView.Adapter<DiseaseInfoAdapter.DiseaseInfoViewHolder>{

    private List<DiseaseInfo> diseaseInfo;
    private Context context;

    public DiseaseInfoAdapter(Context context, List<DiseaseInfo> diseaseInfo){
        this.context = context;
        this.diseaseInfo = diseaseInfo;
    }


    public static class DiseaseInfoViewHolder extends RecyclerView.ViewHolder{

        public TextView tvDiseaseName, tvDiseaseInfo, tvDiseaseTreatmentRecommendation;
        public ImageView ivDiseaseImage;
        public DiseaseInfo diseaseInfo;

        public DiseaseInfoViewHolder(@NonNull View itemView) {
            super(itemView);

            ivDiseaseImage = itemView.findViewById(R.id.ivDiseaseImage);
            tvDiseaseName = itemView.findViewById(R.id.tvDiseaseName);
            tvDiseaseInfo = itemView.findViewById(R.id.tvDiseaseInfo);
            tvDiseaseTreatmentRecommendation = itemView.findViewById(R.id.tvDiseaseTreatmentRecommendation);


        }
    }

    @NonNull
    @Override
    public DiseaseInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View listItem = layoutInflater.inflate(R.layout.recyclerview_disease_info_item, parent, false);

        DiseaseInfoAdapter.DiseaseInfoViewHolder diseaseInfoViewHolder = new DiseaseInfoAdapter.DiseaseInfoViewHolder(listItem);

        return diseaseInfoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DiseaseInfoViewHolder holder, int position) {
        DiseaseInfo info = diseaseInfo.get(position);
        holder.diseaseInfo = info;

        holder.tvDiseaseName.setText(info.getName());
        holder.tvDiseaseInfo.setText(info.getDescription());
        holder.tvDiseaseTreatmentRecommendation.setText(info.getTreatmentRecommendation());

        Picasso.with(context)
                .load(info.getImageUrl())
                .fit()
                .centerCrop()
                .into(holder.ivDiseaseImage);
    }


    @Override
    public int getItemCount() {
        return diseaseInfo.size();
    }
}
