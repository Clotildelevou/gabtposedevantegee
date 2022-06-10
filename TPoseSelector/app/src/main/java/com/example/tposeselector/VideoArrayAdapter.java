package com.example.tposeselector;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class VideoArrayAdapter extends ArrayAdapter<Video> {

    private final int resource;
    private final Context context;

    public VideoArrayAdapter(@NonNull Context context, int resource, @NonNull List<Video> objects) {
        super(context, resource, objects);
        this.resource = resource;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout view;
        Video video = getItem(position);

        //Inflate the view
        if(convertView == null)
        {
            view = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater)getContext().getSystemService(inflater);
            vi.inflate(resource, view, true);
        }
        else
        {
            view = (LinearLayout) convertView;
        }

        //Get the text boxes from the listitem.xml file
        TextView date = view.findViewById(R.id.date);
        TextView duration = view.findViewById(R.id.duration);
        LinearLayout linearLayout = view.findViewById(R.id.linearLayout);

        //Assign the appropriate data from our alert object above
        date.setText(video.date);
        duration.setText(video.duration);

        linearLayout.setOnClickListener((View v) -> {
            Intent intent = new Intent(context, VideoPlayer.class);
            intent.putExtra("id", video.id);
            context.startActivity(intent);
        });

        return view;
    }
}
