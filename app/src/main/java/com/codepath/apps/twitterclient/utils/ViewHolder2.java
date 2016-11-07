package com.codepath.apps.twitterclient.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.activities.ProfileActivity;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by victorhom on 10/31/16.
 */
public class ViewHolder2 extends RecyclerView.ViewHolder{
    @BindView(R.id.ivProfile)
    ImageView ivProfile;
    @BindView(R.id.tvProfileName)
    TextView tvProfileName;
    @BindView(R.id.tvTweetBody)
    TextView tvTweetBody;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.media_image)
    ImageView mediaImage;
    Context c;
    Tweet t;

    public ViewHolder2(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setData(Tweet data, Context m) {
        c = m;
        t = data;
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

        Glide.with(m).load(data.getMediaUrl()).fitCenter().into(mediaImage);
    }

    @OnClick(R.id.ivProfile)
    public void openProfile(View view) {
        // to open profile
        //Toast.makeText(c, t.getUser().getName(), Toast.LENGTH_LONG).show();
//        User user = User.fromJson(response);
        Intent intent = new Intent(c, ProfileActivity.class);
        intent.putExtra(User.USER, t.getUser());
        c.startActivity(intent);
    }


}
