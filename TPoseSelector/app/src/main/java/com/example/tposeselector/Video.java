package com.example.tposeselector;

import org.json.JSONException;
import org.json.JSONObject;

public class Video {

    public String date;
    public String duration;
    public int id;

    private String formatDate(String date) {
        return date;
    }

    private String formatDuration(String duration) {
        return duration;
    }

    public Video(JSONObject json) {
        try {
            this.duration = formatDuration(json.getString("duration"));
            this.date = formatDate(json.getString("date"));
            this.id = Integer.parseInt(json.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
