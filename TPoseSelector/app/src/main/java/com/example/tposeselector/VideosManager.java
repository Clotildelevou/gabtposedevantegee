package com.example.tposeselector;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class VideosManager {

    private final ArrayList<Video> videos = new ArrayList<>();
    private final VideoArrayAdapter adapter;
    private final MainActivity mainActivity;

    private int rate; // List update rate in seconds

    public VideosManager(MainActivity main) {

        mainActivity = main;

        adapter = new VideoArrayAdapter(main,
                R.layout.video_item,
                videos);
        ListView listView = main.findViewById(R.id.videos_list);
        listView.setAdapter(adapter);

        Button done = main.findViewById(R.id.done_button);
        done.setOnClickListener((View view) -> new Thread(this::doneClicked).start());
    }

    private void doneClicked() {

        ArrayList<Integer> upload = videos.stream()
                .filter(v -> v.upload)
                .map(v -> v.id)
                .collect(Collectors.toCollection(ArrayList::new));
        TPoseManager.upload_videos(upload);

        ArrayList<Integer> delete = videos.stream()
                .filter(v -> v.delete)
                .map(v -> v.id)
                .collect(Collectors.toCollection(ArrayList::new));
        TPoseManager.delete_videos(delete);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        videos.clear();
    }

    public void start(int rate) {
        this.rate = rate;
        new Thread(this::worker).start();
    }

    private void worker() {

        while (true) {

            JSONArray videos = TPoseManager.videos();
            mainActivity.runOnUiThread(() -> {
                addVideos(videos);
            });

            try {
                Thread.sleep(rate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void addVideos(JSONArray jsonArray) {

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Video v = new Video(jsonArray.getJSONObject(i));
                if (videos.stream().anyMatch(vid -> vid.id == v.id))
                    continue;
                videos.add(v);
            }
        } catch(JSONException | NullPointerException e) { e.printStackTrace(); }

        adapter.notifyDataSetChanged();
    }
}
