package com.codepath.apps.twitterclient.fragments;

import android.content.Intent;
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
import com.codepath.apps.twitterclient.activities.TweetDetailActivity;
import com.codepath.apps.twitterclient.adapters.TweetsArrayAdapter;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.networks.TwitterClient;
import com.codepath.apps.twitterclient.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.twitterclient.utils.Helper;
import com.codepath.apps.twitterclient.utils.ItemClickSupport;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    public static final String QUERY = "QUERY";
    private static final int DEFAULT_VALUE = 1;
    private static final int DEFAULT_USER_ID = -1;
    private static final String DEFAULT_QUERY = "doge";
    private int tabPage;
    private long userId;
    private long previous_max_id;
    private String query;

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

    public static Fragment newInstance(String query) {
        Bundle args = new Bundle();
        args.putString(QUERY, query);
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
            query = bundle.getString(QUERY);
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
        setRVClicks();
        setFabiconListener();
    }

    // send api request
    // fill the recycler view by creating the tweet objects from json
    private void populateTimeline() {
        if (tabPage == 1 && query == null) {
            getHomeTimeline();
        } else if (tabPage == 2 && query == null) {
            getMentionsTimeline();
        } else {
            if (query.length() > 0) {
              getSearchResults();
            }
        }

    }

    private void getSearchResults() {
        client.getSearchResults(getSearchResultHandler(), query);
    }

    private JsonHttpResponseHandler getSearchResultHandler() {
        return new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    aTweets.addAll(Tweet.fromJSONArray(response.getJSONArray("statuses")));
                    client.setMaxID(aTweets.getLastMaxId());
                    previous_max_id = aTweets.getLastMaxId();
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Helper.toastInternetIssues(getContext());
                hideProgressBar();
            }
        };
    }

    private void getHomeTimeline() {
        Helper.toastInternetIssues(getContext());
        if (userId > -1) {
            // pass in request for the user id
            client.getHomeTimelineUser(getHomeTimelineUserHandler(), userId);
        } else {
            client.getHomeTimeline(getHomeTimelineHandler());

        }
    }

    private JsonHttpResponseHandler getHomeTimelineUserHandler() {
       return new JsonHttpResponseHandler(){
           @Override
           public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
               Helper.toastInternetIssues(getContext());
               aTweets.addAll(Tweet.fromJSONArray(response));
               client.setMaxID(aTweets.getLastMaxId());
               previous_max_id = aTweets.getLastMaxId();
               swipeContainer.setRefreshing(false);
           }

           @Override
           public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
               Helper.toastInternetIssues(getContext());
               hideProgressBar();
           }
       };
    }

    private JsonHttpResponseHandler getHomeTimelineHandler() {
        return new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response){
                try {
                    aTweets.addAll(Tweet.fromJSONArray(response));
                    client.setMaxID(aTweets.getLastMaxId());
                    previous_max_id = aTweets.getLastMaxId();
                    swipeContainer.setRefreshing(false);
                } catch(Exception e) {
                    Helper.toastInternetIssues(getContext());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Helper.toastInternetIssues(getContext());
                hideProgressBar();
            }
        };
    }

    private void getMentionsTimeline() {
        Helper.toastInternetIssues(getContext());
        client.getMentionsTimeline(getMentionsTimelineHandler());
    }

    private JsonHttpResponseHandler getMentionsTimelineHandler() {
        return new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                aTweets.addAll(Tweet.fromJSONArray(response));
                client.setMaxID(aTweets.getLastMaxId());
                previous_max_id = aTweets.getLastMaxId();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Helper.toastInternetIssues(getContext());
                hideProgressBar();
            }
        };
    }

    private void loadAdditionalPages(int page) {
        showProgressBar();
        if (tabPage == 1) {
            getAdditionalTimeline(page);
        } else if (tabPage == 2) {
            getAdditionalMentionsTimeline(page);
        } else {
            if (query.length() > 0) {
                getAdditionalSearchResults();
            }
        }
    }

    private void getAdditionalSearchResults() {
        client.getAdditionalSearchResults(getAdditionalSearchResultsHandler(),query,previous_max_id);
    }

    private JsonHttpResponseHandler getAdditionalSearchResultsHandler() {
        return new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    aTweets.addAll(Tweet.fromJSONArray(response.getJSONArray("statuses")));
                    client.setMaxID(aTweets.getLastMaxId());
                    previous_max_id = aTweets.getLastMaxId();
                    hideProgressBar();
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Helper.toastInternetIssues(getContext());
                hideProgressBar();
            }
        };
    }

    private void getAdditionalTimeline(int page) {
        if (userId > -1) {
            client.getAdditionalTimelineUser(getAdditionalTimelineUserHandler(), previous_max_id, userId);
        } else {
            client.getAdditionalTimeline(getAdditionalTimelineHandler(), previous_max_id);
        }
    }

    private JsonHttpResponseHandler getAdditionalTimelineUserHandler() {
        return new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                aTweets.addAll(Tweet.fromJSONArray(response));
                client.setMaxID(aTweets.getLastMaxId());
                previous_max_id = aTweets.getLastMaxId();
                hideProgressBar();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Helper.toastInternetIssues(getContext());
                hideProgressBar();
            }
        };
    }

    private JsonHttpResponseHandler getAdditionalTimelineHandler() {
        return new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                aTweets.addAll(Tweet.fromJSONArray(response));
                client.setMaxID(aTweets.getLastMaxId());
                previous_max_id = aTweets.getLastMaxId();
                hideProgressBar();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Helper.toastInternetIssues(getContext());
                hideProgressBar();
            }
        };
    }

    private void getAdditionalMentionsTimeline(int page) {
        client.getAdditionalMentionsTimeline(getAdditionalMentionsTimelineHandler(), previous_max_id);
    }

    private JsonHttpResponseHandler getAdditionalMentionsTimelineHandler() {
        return new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                aTweets.addAll(Tweet.fromJSONArray(response));
                client.setMaxID(aTweets.getLastMaxId());
                previous_max_id = aTweets.getLastMaxId();
                hideProgressBar();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Helper.toastInternetIssues(getContext());
                hideProgressBar();
            }
        };
    }

    private void setRVClicks() {
        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // do it
                        Tweet t = tweets.get(position);
                        // somewhere inside an Activity
                        Intent i = new Intent(getActivity(), TweetDetailActivity.class);
                        i.putExtra("myData", t); // using the (String name, Parcelable value) overload!
                        startActivity(i); // dataToSend is now passed to the new Activity

                    }
                }
        );
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
                previous_max_id = 1;
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


}
