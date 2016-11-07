package com.codepath.apps.twitterclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.adapters.TabsFragmentPagerAdapter;
import com.codepath.apps.twitterclient.fragments.ComposeTweetFragment;
import com.codepath.apps.twitterclient.fragments.TimelineFragment;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.networks.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.OnDataPass{

    private TwitterClient client;
    @BindView(R.id.include) Toolbar menubar;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabsFragmentPagerAdapter tFPA;


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
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_compose) {
            showComposeDialog();
            return true;
        } else if(id == R.id.menu_profile) {
            client.getVerifyCredentials(HandleVerification());
            return true;
        }else if (id == R.id.birdicon) {
            Intent intent = new Intent(getApplication(), TimelineActivity.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    // there is probably information on the logged in user somewhere
    // but I didn't see it, so making a network request for it :(
    private JsonHttpResponseHandler HandleVerification() {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                User user = User.fromJson(response);
                Intent intent = new Intent(getApplication(), ProfileActivity.class);
                intent.putExtra(User.USER, user);
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        setSupportActionBar(menubar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.twitterbird);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tFPA = new TabsFragmentPagerAdapter(getSupportFragmentManager(),
                TimelineActivity.this);
        viewPager.setAdapter(tFPA);

        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        client = TwitterApplication.getRestClient();

    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    private void showComposeDialog() {
        Bundle bundle = new Bundle();
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment fa = ComposeTweetFragment.newInstance();
        fa.setArguments(bundle);
        fa.show(getSupportFragmentManager(),"compose");
    }
    // TODO: get to update the recycler view
    // TODO: also scroll to top
    @Override
    public void getUpdate(boolean compose, String message) {
//        aTweets.clear();
//        populateTimeline();
        // make the date be 'just now'
        // take the profile picture from the first tweet
        // not pictures
//        aTweets.addStandalonePost(message);
//        rvTweets.getLayoutManager().scrollToPosition(0);
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        tab.select();
        TimelineFragment f = (TimelineFragment) tFPA.getRegisteredFragment(0);
        f.addTweet(message); //adds to adapter and scrolls to top

    }
}
