package com.codepath.apps.twitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.fragments.ComposeTweetFragment;
import com.codepath.apps.twitterclient.fragments.ProfileFragment;
import com.codepath.apps.twitterclient.fragments.TimelineFragment;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.networks.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity implements ComposeTweetFragment.OnDataPass{

    User user;
    private TwitterClient client;
    @BindView(R.id.include)
    Toolbar menubar;

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
            // go to your home profile
            client.getVerifyCredentials(HandleVerification());
            // unless it's someone elses
//            Toast.makeText(this, "You're already on a profile",Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.birdicon) {
            Intent intent = new Intent(getApplication(), TimelineActivity.class);
            intent.putExtra(User.USER, user);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

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
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        user = intent.getParcelableExtra(User.USER);

        String twitterhandle = intent.getStringExtra("TWITTERHANDLE");

        setSupportActionBar(menubar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.twitterbird);

        client = TwitterApplication.getRestClient(); //singleton client

        if (user != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.profileLayout, ProfileFragment.newInstance(user.getScreenName()));
            ft.add(R.id.userhomeline, TimelineFragment.newInstance(0, user.getUid()));
            ft.commit();
        } else if (twitterhandle.length() > 0){
            // need to get information on the user before getting it
            client.getProfileInformation(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    try {
                        user = User.fromJson(response.getJSONObject(0));
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.add(R.id.profileLayout, ProfileFragment.newInstance(user.getScreenName()));
                        ft.add(R.id.userhomeline, TimelineFragment.newInstance(0, user.getUid()));
                        ft.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

                }
            }, twitterhandle);
        }



    }

    private void showComposeDialog() {
        Bundle bundle = new Bundle();
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment fa = ComposeTweetFragment.newInstance();
        fa.setArguments(bundle);
        fa.show(getSupportFragmentManager(),"compose");
    }

    @Override
    public void getUpdate(boolean compose, String message) {

    }
}
