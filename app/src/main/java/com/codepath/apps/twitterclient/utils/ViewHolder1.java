package com.codepath.apps.twitterclient.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.models.Tweet;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by victorhom on 10/31/16.
 */
public class ViewHolder1 extends RecyclerView.ViewHolder{

    @BindView(R.id.ivProfile) ImageView ivProfile;
    @BindView(R.id.tvProfileName) TextView tvProfileName;
    @BindView(R.id.tvTweetBody) TextView tvTweetBody;
    @BindView(R.id.tvDate) TextView tvDate;
    Tweet t;
    Context c;

    public ViewHolder1(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setData(Tweet data, Context m) {
        t = data;
        c = m;
        tvProfileName.setText(data.getUser().getScreenName());
        tvTweetBody.setText(data.getBody());
        ivProfile.setImageResource(android.R.color.transparent);
        Glide.with(m).load(data.getUser().getProfileImageUrl()).into(ivProfile);
        if (!data.getCreatedAt().equals(Tweet.NOW)) {
            tvDate.setText(Helper.getRelativeTimeAgo(data.getCreatedAt()));
        } else {
            // show just now!
            tvDate.setText(data.getCreatedAt());
        }
    }

}
