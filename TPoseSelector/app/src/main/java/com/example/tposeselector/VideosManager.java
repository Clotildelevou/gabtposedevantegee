package com.example.tposeselector;

import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class VideosManager {

    private static final String API_URL = "192.168.1.40";

    private final ArrayList<Video> videos = new ArrayList<>();
    private final VideoArrayAdapter adapter;

    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    // List update rate in seconds
    private int rate;

    VideosManager(MainActivity main) {
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

        HttpGet request = new HttpGet(API_URL + "/videos");

        try (CloseableHttpResponse response = httpClient.execute(request)) {

            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++)
                addVideos(new Video(jsonArray.getJSONObject(i)));
        }
        catch(IOException | JSONException e) {
            e.printStackTrace();
        }

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

    public void addVideos(Video toAdd) {
        ArrayList<Video> l = new ArrayList<>();
        l.add(toAdd);
        addVideos(l);
    }

    public void addVideos(ArrayList<Video> toAdd) {
        videos.addAll(toAdd);
        adapter.notifyDataSetChanged();
    }
}
