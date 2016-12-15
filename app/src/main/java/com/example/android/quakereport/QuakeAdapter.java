package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vivek on 11/15/2016.
 */

public class QuakeAdapter extends ArrayAdapter<quake> {

    private static final String TAG = QuakeAdapter.class.getSimpleName();
    private static final String LOCATION_SEPERATOR = "of";

    public QuakeAdapter(Context context, ArrayList<quake> quakes) {
        super(context, 0, quakes);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView==null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.quake_list,parent,false);
        }

        quake currentQuake = getItem(position);

        TextView magTextView = (TextView) listItemView.findViewById(R.id.mag_text_view);

        GradientDrawable magnitudeCircle = (GradientDrawable) magTextView.getBackground();
        int magnitudeColor = getMagnitudeColor(currentQuake.getMag());
        magnitudeCircle.setColor(magnitudeColor);

        String formattedMag = formatMag(currentQuake.getMag());
        magTextView.setText(formattedMag);

        String place; // 72km nw of
        String loc;  //california

        String location = currentQuake.getPlace();

        if(location.contains("km")) {
            String parts[] = location.split(LOCATION_SEPERATOR);
             place = parts[0] + LOCATION_SEPERATOR;
            loc = parts[1];
        } else {
            place = getContext().getString(R.string.near_the);
            loc = location;

        }

        TextView placeTextView = (TextView) listItemView.findViewById(R.id.place_text_view);

        placeTextView.setText(place);

        TextView locTextView = (TextView) listItemView.findViewById(R.id.loc_text_view);

        locTextView.setText(loc);


        Date dateObject = new Date(currentQuake.getTimeInMs());


        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date_text_view);
        String formatteddate = formatDate(dateObject);
        dateTextView.setText(formatteddate);

        TextView timeTextView = (TextView) listItemView.findViewById(R.id.time_text_view);
        String formattedtime = formatTime(dateObject);
        timeTextView.setText(formattedtime);




        return  listItemView;
    }

    private String formatDate(Date dateObject){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormatter.format(dateObject);
    }


    private String formatTime(Date dateObject){
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        return timeFormatter.format(dateObject);
    }
    private String formatMag(Double mag) {
        DecimalFormat formatter = new DecimalFormat("0.0");
        return formatter.format(mag);
    }
    private int getMagnitudeColor(Double mag) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(mag);
        switch(magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(),magnitudeColorResourceId);
        }
}
