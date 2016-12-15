/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class EarthquakeActivity extends AppCompatActivity implements LoaderCallbacks<List<quake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final String USGS_REQUEST_URL = "http://earthquake.usgs.gov/fdsnws/event/1/query";
    private static final int EARTHQUAKE_LOADER_ID = 1;
    private TextView mEmptyStateView;

    private QuakeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);


        // final ArrayList<quake> earthquakes = QueryUtils.extractEarthquakes();


        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);


        //To display the Empty state of our app
        mEmptyStateView = (TextView) findViewById(R.id.empty_text_view);
        earthquakeListView.setEmptyView(mEmptyStateView);

        // Create a new {@link ArrayAdapter} of earthquakes
        mAdapter = new QuakeAdapter(this, new ArrayList<quake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                quake currentEarthquake = mAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentEarthquake.getUrl()));
                startActivity(intent);
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network_info = connMgr.getActiveNetworkInfo();

        if(network_info != null &&  network_info.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID,null,this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateView.setText(R.string.no_internet);

        }
    }

    @Override
    public Loader<List<quake>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri  = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format","geojson");
        uriBuilder.appendQueryParameter("limit","30");
        uriBuilder.appendQueryParameter("minmag",minMagnitude);
        uriBuilder.appendQueryParameter("orderby",orderBy);

        return new QuakeLoader(this,uriBuilder.toString());

    }

    @Override

    public void onLoadFinished(Loader<List<quake>> loader, List<quake> quakes) {

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mEmptyStateView.setText(R.string.no_quake_data);

        mAdapter.clear();

        if (quakes != null && !quakes.isEmpty()) {
            mAdapter.addAll(quakes);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<quake>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
        if(id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}