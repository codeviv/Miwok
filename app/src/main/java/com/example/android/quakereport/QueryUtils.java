package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vivek on 11/18/2016.
 */

public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();
    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    public QueryUtils() {
    }

    public static URL createURL(String stringUrl) {
        URL url = null;
        try{
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG,"problem building the URl");
        }
        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch(IOException e){
                Log.e(LOG_TAG, "Problem retreiving the earthquake Json results.", e);
        } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
            }
            if(inputStream == null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static ArrayList<quake> extractFeatureFromJson(String earthquakeJSON) {
        ArrayList<quake> earthquakes = new ArrayList<>();
        if(TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }
    try {
            JSONObject root = new JSONObject(earthquakeJSON);
            JSONArray quakeArray = root.optJSONArray("features");

            for (int i = 0; i < quakeArray.length(); i++) {
                JSONObject quakeObject = quakeArray.getJSONObject(i);
                JSONObject properties = quakeObject.optJSONObject("properties");

                Double mag = properties.optDouble("mag");
                String place = properties.optString("place");
                long timeInMs = properties.optLong("time");

                String url = properties.optString("url");


                quake earthquake = new quake(mag, place, timeInMs, url);
                earthquakes.add(earthquake);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("QueryUtils", "problem parsing the earthquake json resuls", e);
        }

        return earthquakes;

    }
    public static List<quake> fetchEarthqukeData(String requestUrl) {
        URL url = createURL((requestUrl));

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG,"Problem making the HTTP request");
        }

        List<quake> earthquakes = extractFeatureFromJson(jsonResponse);

        return earthquakes;
        }
}