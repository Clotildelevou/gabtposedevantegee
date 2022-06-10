package com.example.tposeselector;

import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class VideosManager {

    private static final String API_URL = "http://raspberrypi:5000";

    private final ArrayList<Video> videos = new ArrayList<>();
    private final VideoArrayAdapter adapter;
    private final MainActivity mainActivity;

    private int rate; // List update rate in seconds

    VideosManager(MainActivity main) {
        mainActivity = main;
        adapter = new VideoArrayAdapter(main,
                R.layout.video_item,
                videos);
        ListView listView = main.findViewById(R.id.VideosList);
        listView.setAdapter(adapter);
    }

    public void start(int rate) {
        this.rate = rate;
        new Thread(this::worker).start();
    }

    private void getVideos() {

        try {
            URL obj = new URL(API_URL + "/videos");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            if(con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e("GetVideos", "Problem with TPoseManager API");
                return;
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();

            mainActivity.runOnUiThread(() -> {
                try {
                    JSONArray jsonArray = new JSONArray(response.toString());
                    addVideos(jsonArray);
                } catch (JSONException e) { e.printStackTrace(); }
            });
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    private void worker() {
        while (true) {
            getVideos();

            try {
                Thread.sleep(rate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addVideos(JSONArray jsonArray) throws JSONException {

        for (int i = 0; i < jsonArray.length(); i++) {

            Video v = new Video(jsonArray.getJSONObject(i));
            if (videos.stream().anyMatch(vid -> vid.id == v.id))
                continue;

            videos.add(v);
        }

        adapter.notifyDataSetChanged();
    }
}
