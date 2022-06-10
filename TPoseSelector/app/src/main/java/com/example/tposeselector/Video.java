package com.example.tposeselector;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Video {

    public String date;
    public String duration;
    public int id;

    private String formatDate(String date) {
        Timestamp timestamp = new Timestamp((long) Float.parseFloat(date));
        return new SimpleDateFormat("dd/MM/yy HH:mm").format(timestamp);
    }

    private String formatDuration(String duration) {
        int seconds = (int) Float.parseFloat(duration);
        if(seconds < 60)
            return seconds + "s";
        return seconds/60 + "min " + seconds%60;
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
