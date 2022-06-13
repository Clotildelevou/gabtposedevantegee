package com.example.tposeselector;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.VideoView;

import com.example.tposeselector.databinding.ActivityVideoPlayerBinding;

public class VideoPlayer extends AppCompatActivity {

    private static final String API_URL = "http://raspberrypi:5000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityVideoPlayerBinding binding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        VideoView videoView = findViewById(R.id.videoView);
        videoView.setVideoPath(TPoseManager.video_url(getIntent().getIntExtra("id", 0)));
        videoView.start();
    }
}