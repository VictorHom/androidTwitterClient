package com.codepath.apps.twitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.adapters.TweetsArrayAdapter;
import com.codepath.apps.twitterclient.fragments.ComposeTweetFragment;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.networks.TwitterClient;
import com.codepath.apps.twitterclient.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.twitterclient.utils.ItemClickSupport;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.OnDataPass{

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private boolean updateTimeline;
    @BindView(R.id.rvTweets) RecyclerView rvTweets;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.include) Toolbar menubar;
    MenuItem miActionProgressItem;;
    @BindView(R.id.fabicon) android.support.design.widget.FloatingActionButton fabicon;

    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        // no share item on the articles page
        MenuItem composeItem = menu.findItem(R.id.menu_compose);
        composeItem.setVisible(true);
        composeItem.setEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_compose) {
            showComposeDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        setSupportActionBar(menubar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.twitterbird);

        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        rvTweets.setAdapter(aTweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // triggered only when new data needs to be appended to the list
                // add whatever code is needed to append new items to the bottom of the list
                loadAdditionalPages(page);
            }
        };

        rvTweets.addOnScrollListener(scrollListener);

        // create the array list (data source);
        // construct the adapter from the data source
        // connect adapter to the list view

        client = TwitterApplication.getRestClient(); //singleton client

        populateTimeline();
        swipeRefresh();


        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // do it
                        Tweet t = tweets.get(position);
                        // somewhere inside an Activity
                        Intent i = new Intent(TimelineActivity.this, TweetDetailActivity.class);
                        i.putExtra("myData", t); // using the (String name, Parcelable value) overload!
                        startActivity(i); // dataToSend is now passed to the new Activity

                    }
                }
        );

        fabicon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                FragmentManager fm = getSupportFragmentManager();
                ComposeTweetFragment fa = ComposeTweetFragment.newInstance();
                fa.setArguments(bundle);
                fa.show(getSupportFragmentManager(),"compose");
            }
        });
    }


    // send api request
    // fill the listview by creating the tweet objects from json
    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                aTweets.addAll(Tweet.fromJSONArray(response));
                client.setMaxID(aTweets.getLastMaxId());
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                // need to handler failures
//                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    private void loadAdditionalPages(int page) {
        showProgressBar();
        client.getAdditionalTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                aTweets.addAll(Tweet.fromJSONArray(response));
                client.setMaxID(aTweets.getLastMaxId());
                hideProgressBar();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                // need to handler failures
//                Log.d("DEBUG", errorResponse.toString());
            }
        }, page);

    }

    private void swipeRefresh() {
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                aTweets.clear();
                populateTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void showComposeDialog() {
        Bundle bundle = new Bundle();
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment fa = ComposeTweetFragment.newInstance();
        fa.setArguments(bundle);
        fa.show(getSupportFragmentManager(),"compose");
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }


    @Override
    public void getUpdate(boolean compose, String message) {
//        aTweets.clear();
//        populateTimeline();
        // make the date be 'just now'
        // take the profile picture from the first tweet
        // not pictures
        aTweets.addStandalonePost(message);
        rvTweets.getLayoutManager().scrollToPosition(0);
    }
}
