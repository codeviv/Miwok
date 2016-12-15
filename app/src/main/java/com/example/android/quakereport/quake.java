package com.example.android.quakereport;

/**
 * Created by vivek on 11/15/2016.
 */

public class quake {
    private Double mMag;
    private String mPlace;
    private long mTimeInMs;
    private String mUrl;

    public quake(Double mag, String place, long timeInMs, String url) {
        mMag = mag;
        mPlace = place;
        mTimeInMs = timeInMs;
        mUrl = url;
    }
    public long getTimeInMs() {
        return mTimeInMs;
    }

    public Double getMag() {
        return mMag;
    }

    public String getPlace() {
        return mPlace;
    }

    public String getUrl() {
        return mUrl;
    }
}
