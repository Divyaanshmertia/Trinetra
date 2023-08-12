package com.example.trinetra;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.trinetra.model.Incident;

import java.util.List;

public class IncidentAdapter extends RecyclerView.Adapter<IncidentAdapter.IncidentViewHolder> {

    private List<Incident> incidentList;
    private Context context;

    public static class IncidentViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView descriptionTextView;
        public TextView dateTextView;
        public TextView upvoteTextView;
        public TextView downvoteTextView;
        public ImageView imageView;
        public VideoView videoView;
        public Button UpVote;
        public Button DpVote;
        public IncidentViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            upvoteTextView = itemView.findViewById(R.id.upvote_text_view);
            downvoteTextView = itemView.findViewById(R.id.downvote_text_view);
            imageView = itemView.findViewById(R.id.image_view);
            videoView = itemView.findViewById(R.id.video_view);
            UpVote = itemView.findViewById(R.id.upvote_button);
            DpVote = itemView.findViewById(R.id.downvote_button);
        }
    }

    public IncidentAdapter(List<Incident> incidentList, Context context) {
        this.incidentList = incidentList;
        this.context = context;
    }

    @NonNull
    @Override
    public IncidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_incident, parent, false);
        return new IncidentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncidentViewHolder holder, int position) {
        Incident incident = incidentList.get(position);
        holder.titleTextView.setText(incident.getTitle());
        holder.descriptionTextView.setText(incident.getDescription());
        holder.dateTextView.setText(incident.getDate());
        holder.upvoteTextView.setText(String.valueOf(incident.getUpvote()));
        holder.downvoteTextView.setText(String.valueOf(incident.getDownvote()));


        holder.UpVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Increase the upvote count for the incident.
                incident.setUpvote(1);
                if((incident.getDownvote())>0.0){
                    incident.setDownvote(0);
                }
                // Update the upvote text view.
                holder.upvoteTextView.setText(String.valueOf(incident.getUpvote()));
                Toast.makeText(context.getApplicationContext(), "Upvoted Successfully",Toast.LENGTH_SHORT).show();
            }
        });

        holder.DpVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Increase the downvote count for the incident.
                incident.setDownvote(1);
                if(incident.getUpvote()>0.0){
                    incident.setUpvote(0);
                }
                // Update the downvote text view.
                holder.downvoteTextView.setText(String.valueOf(incident.getDownvote()));
                Toast.makeText(context.getApplicationContext(), "Downvoted Successfully",Toast.LENGTH_SHORT).show();
            }
        });
        if (incident.getMediaUrl() != null) {
            if (incident.getMediaType().equals("image")) {
                holder.imageView.setVisibility(View.VISIBLE);
                holder.videoView.setVisibility(View.GONE);
                Glide.with(context).load(incident.getMediaUrl()).into(holder.imageView);
            } else if (incident.getMediaType().equals("video")) {
                holder.imageView.setVisibility(View.GONE);
                holder.videoView.setVisibility(View.VISIBLE);
                holder.videoView.setVideoURI(Uri.parse(incident.getMediaUrl()));
                holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                        holder.videoView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.parse(incident.getMediaUrl()), "video/*");
                                context.startActivity(intent);
                            }
                        });
                    }
                });
                holder.videoView.start();
            }
        } else {
            holder.imageView.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return incidentList.size();
    }
}
