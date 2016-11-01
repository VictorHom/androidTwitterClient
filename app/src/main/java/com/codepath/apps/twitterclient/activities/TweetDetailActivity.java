package com.codepath.apps.twitterclient.activities;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.networks.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class TweetDetailActivity extends AppCompatActivity {
    @BindView(R.id.include)
    Toolbar menubar;
    @BindView(R.id.screenname)
    TextView screenName;
    @BindView(R.id.profilepic)
    ImageView profilePic;
    @BindView(R.id.tweetBody)
    TextView tweetBody;
    @BindView(R.id.retweetNumber) TextView retweetNumber;
    @BindView(R.id.likeNumber) TextView likeNumber;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.etReplyBox) TextView etReplyBox;
    MenuItem miActionProgressItem;
    private TwitterClient client;
    Tweet object;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        // no share item on the articles page
        MenuItem composeItem = menu.findItem(R.id.menu_compose);
        composeItem.setVisible(false);
        composeItem.setEnabled(false);
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
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        ButterKnife.bind(this);

        setSupportActionBar(menubar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.twitterbird);

        client = TwitterApplication.getRestClient();

        // get data that was passed in
        object = getIntent().getParcelableExtra("myData");

        screenName.setText(object.getUser().getScreenName());
        tweetBody.setText(object.getBody());
        profilePic.setImageResource(android.R.color.transparent);
        retweetNumber.setText(String.valueOf(object.getRetweetNumber()));
        likeNumber.setText(String.valueOf(object.getLikes()));
        description.setText(object.getUser().getDescription());
        Glide.with(getApplicationContext()).load(object.getUser().getProfileImageUrl()).into(profilePic);

    }
    // this doesn't work for immediately made tweets that were not taken from a network request
    @OnClick(R.id.reply_tweet)
    public void replyTweet() {
        String currentReply = etReplyBox.getText().toString();
        if (currentReply.length() > 0) {
            client.replyToTweet(new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Toast.makeText(getApplicationContext(), "The reply was successful", Toast.LENGTH_LONG).show();
                    etReplyBox.setText("");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toast.makeText(getApplicationContext(), "There were issues submitting this reply tweet", Toast.LENGTH_LONG).show();
                }
            }, currentReply, object.getUid());
        } else {
            Toast.makeText(this, "The tweet is empty. Please type something", Toast.LENGTH_LONG).show();
        }

    }
}
