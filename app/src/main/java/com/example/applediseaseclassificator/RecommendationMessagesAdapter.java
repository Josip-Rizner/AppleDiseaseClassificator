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

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class RecommendationMessagesAdapter extends RecyclerView.Adapter<RecommendationMessagesAdapter.RecommendationMessagesViewHolder> {

    private List<SimpleMessage> recommendationMessages;
    private Context context;

    public RecommendationMessagesAdapter(Context context, List<SimpleMessage> recommendationMessages) {
        this.context = context;
        this.recommendationMessages = recommendationMessages;
    }


    public static class RecommendationMessagesViewHolder extends RecyclerView.ViewHolder{

        public TextView tvMessage, tvUserSimpleRequest, tvUserRequestWithImage, tvSimpleAnswerWithImage, tvSimpleUserRequestWithImageTimestamp, tvSimpleAnswerWithImageTimestamp, tvSimpleUserAnswerTimestamp, tvMessageTimestamp;
        public ImageView ivUserRequestWithImage, ivSimpleAnswerWithImage;


        public RecommendationMessagesViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tvMessage = itemView.findViewById(R.id.tvMessage);
            this.tvMessageTimestamp = itemView.findViewById(R.id.tvMessageTimestamp);

            this.tvUserSimpleRequest = itemView.findViewById(R.id.tvSimpleUserAnswer);
            this.tvSimpleUserAnswerTimestamp = itemView.findViewById(R.id.tvSimpleUserAnswerTimestamp);

            this.tvUserRequestWithImage = itemView.findViewById(R.id.tvSimpleUserRequestWithImage);
            this.ivUserRequestWithImage = itemView.findViewById(R.id.ivSimpleUserRequestWithImage);
            this.tvSimpleUserRequestWithImageTimestamp = itemView.findViewById(R.id.tvSimpleUserRequestWithImageTimestamp);

            this.tvSimpleAnswerWithImage = itemView.findViewById(R.id.tvSimpleAnswerWithImage);
            this.ivSimpleAnswerWithImage = itemView.findViewById(R.id.ivSimpleAnswerWithImage);
            this.tvSimpleAnswerWithImageTimestamp = itemView.findViewById(R.id.tvSimpleAnswerWithImageTimestamp);
        }

    }


    @NonNull
    @Override
    public RecommendationMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = null;

        if (viewType >= 0 && viewType <= 9){
           listItem = layoutInflater.inflate(R.layout.recyclerview_info_msg_item, parent, false);
        }
        else if (viewType >= 10 && viewType <= 19){
            listItem = layoutInflater.inflate(R.layout.recyclerview_user_simple_answer_item, parent, false);
        }
        else if (viewType == 20 || viewType == 40){
            listItem = layoutInflater.inflate(R.layout.recyclerview_info_msg_with_image_item, parent, false);
        }
        else if (viewType == 30){
            listItem = layoutInflater.inflate(R.layout.recyclerview_user_simple_answer_with_image_item, parent, false);
        }

        RecommendationMessagesViewHolder recommendationMessagesViewHolder = new RecommendationMessagesViewHolder(listItem);
        return recommendationMessagesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationMessagesViewHolder holder, int position) {

        SimpleMessage message = recommendationMessages.get(position);

        if(message.getRecommendationMessageType() >= 0 && message.getRecommendationMessageType() <= 9){
            holder.tvMessage.setText(message.getMessage());
            holder.tvMessageTimestamp.setText(getDateTimeFromTimestamp(message.getTimestamp()));
        }
        else if(message.getRecommendationMessageType() >= 10 && message.getRecommendationMessageType() <= 19){
            holder.tvUserSimpleRequest.setText(message.getMessage());
            holder.tvSimpleUserAnswerTimestamp.setText(getDateTimeFromTimestamp(message.getTimestamp()));
        }
        else if(message.getRecommendationMessageType() == 20 || message.getRecommendationMessageType() == 40){
            holder.tvSimpleAnswerWithImage.setText(message.getMessage());
            holder.tvSimpleAnswerWithImageTimestamp.setText(getDateTimeFromTimestamp(message.getTimestamp()));

            Picasso.with(context)
                    .load(message.getImageReference())
                    .fit()
                    .centerCrop()
                    .into(holder.ivSimpleAnswerWithImage);

        }
        else if(message.getRecommendationMessageType() == 30){
            holder.tvUserRequestWithImage.setText(message.getMessage());
            holder.tvSimpleUserRequestWithImageTimestamp.setText(getDateTimeFromTimestamp(message.getTimestamp()));

            Picasso.with(context)
                    .load(message.getImageReference())
                    .fit()
                    .centerCrop()
                    .into(holder.ivUserRequestWithImage);

        }

    }

    @Override
    public int getItemViewType(int position) {
        return recommendationMessages.get(position).getRecommendationMessageType();
    }

    @Override
    public int getItemCount() {
        return recommendationMessages.size();
    }

    private String getDateTimeFromTimestamp(long timestamp){
        Timestamp ts = new Timestamp(timestamp);
        Date date = ts;

        return date.toString().substring(0, date.toString().lastIndexOf(":"));
    }

}
