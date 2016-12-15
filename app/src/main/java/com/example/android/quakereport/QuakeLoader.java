package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by vivek on 11/27/2016.
 */

public class QuakeLoader extends AsyncTaskLoader<List<quake>> {

    private static final String LOG_TAG = QuakeLoader.class.getName();
    private String mUrl;

    public QuakeLoader(Context context, String mUrl) {
        super(context);
        this.mUrl = mUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<quake> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        List<quake> earthquakes = QueryUtils.fetchEarthqukeData(mUrl);
        return earthquakes;
    }
}
