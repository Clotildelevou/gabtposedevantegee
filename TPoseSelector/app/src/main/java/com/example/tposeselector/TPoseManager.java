package com.example.tposeselector;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TPoseManager {

    private static final String API_URL = "http://raspberrypi:5000";

    public static JSONArray videos() {

        try {
            URL obj = new URL(API_URL + "/videos");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            if(con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e("GetVideos", "Problem with TPoseManager API");
                return null;
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();

            return new JSONArray(response.toString());
        }
        catch (IOException | JSONException e) { e.printStackTrace(); }

        return null;
    }

    public static String video_url(int id) {
        return API_URL + "/video/" + id;
    }

    private enum Action {

        UPLOAD("upload"),
        DELETE("delete");

        public String request;

        Action(String request) {
            this.request = request;
        }
    }

    private static void videos_action(Action a, ArrayList<Integer> ids) {
        try {
            URL obj = new URL(API_URL + "/videos/" + a.request);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            JSONArray json = new JSONArray(ids);
            con.getOutputStream().write(json.toString().getBytes(StandardCharsets.UTF_8));
            if(con.getResponseCode() != HttpURLConnection.HTTP_OK)
                Log.e("GetVideos", "Problem with TPoseManager API");
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public static void upload_videos(ArrayList<Integer> ids) {
        videos_action(Action.UPLOAD, ids);
    }

    public static void delete_videos(ArrayList<Integer> ids) {
        videos_action(Action.DELETE, ids);
    }
}
