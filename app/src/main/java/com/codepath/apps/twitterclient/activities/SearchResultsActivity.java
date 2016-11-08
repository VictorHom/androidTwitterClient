package com.codepath.apps.twitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.fragments.TimelineFragment;
import com.codepath.apps.twitterclient.networks.TwitterClient;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultsActivity extends AppCompatActivity {
    private TwitterClient client;
    @BindView(R.id.include)
    Toolbar menubar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String query = intent.getStringExtra("QUERY");
        if (query.length() > 0) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.searchline, TimelineFragment.newInstance(query));
            ft.commit();
        }
    }
}
