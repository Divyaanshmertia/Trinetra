package com.example.trinetra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class IncidentDetailsActivity extends AppCompatActivity {

    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private TextView mVotesTextView;
    private Button mUpvoteButton;
    private Button mDownvoteButton;

    private int mVotes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_details);

        mTitleTextView = findViewById(R.id.title_text_view);
        mDescriptionTextView = findViewById(R.id.description_text_view);
        mVotesTextView = findViewById(R.id.votes_text_view);
        mUpvoteButton = findViewById(R.id.upvote_button);
        mDownvoteButton = findViewById(R.id.downvote_button);

        // Get the incident details from the intent.
        Intent intent = getIntent();
        String incidentTitle = intent.getStringExtra("title");
        String incidentDescription = intent.getStringExtra("description");

        // Display the incident details in the UI.
        mTitleTextView.setText(incidentTitle);
        mDescriptionTextView.setText(incidentDescription);

        // Set the initial votes count.
        mVotesTextView.setText(String.valueOf(mVotes));

        // Set up the upvote button click listener.
        mUpvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVotes++;
                mVotesTextView.setText(String.valueOf(mVotes));
            }
        });

        // Set up the downvote button click listener.
        mDownvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVotes++;
                mVotesTextView.setText(String.valueOf(mVotes));
            }
        });
    }
}
