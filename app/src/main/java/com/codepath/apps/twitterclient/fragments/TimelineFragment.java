package com.codepath.apps.twitterclient.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.adapters.TweetsArrayAdapter;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.networks.TwitterClient;
import com.codepath.apps.twitterclient.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;

/**
 * Created by victorhom on 11/5/16.
 */
public class TimelineFragment extends Fragment {
    private static final float TOOLBAR_ELEVATION = 200.0f;
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private boolean updateTimeline;
    MenuItem miActionProgressItem;
    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @BindView(R.id.actionProgress)
    ProgressBar pbLoader;

    @BindView(R.id.fabicon)
    android.support.design.widget.FloatingActionButton fabicon;

    private EndlessRecyclerViewScrollListener scrollListener;
    private Unbinder unbinder;
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String USER_ID = "USER_ID";
    private static final int DEFAULT_VALUE = 1;
    private static final int DEFAULT_USER_ID = -1;
    private int tabPage;
    private long userId;

    public static Fragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        TimelineFragment fragment = new TimelineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // To view other user timelines besides the signed in user;
    public static Fragment newInstance(int page, Long twitterId) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putLong(USER_ID, twitterId);
        TimelineFragment fragment = new TimelineFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            tabPage = bundle.getInt(ARG_PAGE, DEFAULT_VALUE);
            userId = bundle.getLong(USER_ID, DEFAULT_USER_ID);
        }
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        rvTweets.setAdapter(aTweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvTweets.setLayoutManager(linearLayoutManager);
        super.onViewCreated(view, savedInstanceState);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // triggered only when new data needs to be appended to the list
                // add whatever code is needed to append new items to the bottom of the list
                loadAdditionalPages(page);
            }
        };

        rvTweets.addOnScrollListener(scrollListener);
        client = TwitterApplication.getRestClient(); //singleton client

        populateTimeline();
        swipeRefresh();
        //setRVClicks();
        setFabiconListener();
    }

    // send api request
    // fill the recycler view by creating the tweet objects from json
    private void populateTimeline() {
        if (tabPage == 1) {
            getHomeTimeline();
        } else if (tabPage == 2) {
            getMentionsTimeline();
        } else {
            getHomeTimeline();
        }

    }

    private void getHomeTimeline() {
        if (userId > -1) {
            // pass in request for the user id
            client.getHomeTimelineUser(new JsonHttpResponseHandler(){
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
            }, userId);
        } else {
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

    }

    private void getMentionsTimeline() {
        client.getMentionsTimeline(new JsonHttpResponseHandler(){
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
        if (tabPage == 1) {
            getAdditionalTimeline(page);
        } else if (tabPage == 2) {
            getAdditionalMentionsTimeline(page);
        } else {
            getAdditionalTimeline(page);
        }
    }

    private void getAdditionalTimeline(int page) {
        if (userId > -1) {
            client.getAdditionalTimelineUser(new JsonHttpResponseHandler(){
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
            }, page, userId);
        } else {
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

    }

    private void getAdditionalMentionsTimeline(int page) {
        client.getAdditionalMentionsTimeline(new JsonHttpResponseHandler(){
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

    private void setFabiconListener(){
            // trying to change background color of floating action button
            fabicon.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.twitterblue));
            fabicon.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.twitterblue)));
            fabicon.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    ComposeTweetFragment fa = ComposeTweetFragment.newInstance();
                    fa.setArguments(bundle);
                    fa.show(getActivity().getSupportFragmentManager(),"compose");
                }
            });
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

    public void showProgressBar() {
        // Show progress item
        pbLoader.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        // Hide progress item
        pbLoader.setVisibility(View.GONE);
    }

    // methods for the activity to call
    public void addTweet(String message) {
        aTweets.addStandalonePost(message);
        rvTweets.getLayoutManager().scrollToPosition(0);
    }

    @OnClick(R.id.fabicon)
    public void onClickComposeDialog(View view) {
        System.out.println("fabicon is clicked");
    }

}
