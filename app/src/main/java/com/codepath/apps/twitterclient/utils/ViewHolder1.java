package com.codepath.apps.twitterclient.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.activities.ProfileActivity;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), ContextCompat.getColor(c, R.color.twitterblue),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Intent intent = new Intent(c, ProfileActivity.class);
                                intent.putExtra("TWITTERHANDLE", text.replace("@",""));
                                c.startActivity(intent);
                            }
                        }).into(tvTweetBody);
        ivProfile.setImageResource(android.R.color.transparent);
        Glide.with(m).load(data.getUser().getProfileImageUrl()).into(ivProfile);
        if (!data.getCreatedAt().equals(Tweet.NOW)) {
            tvDate.setText(Helper.getRelativeTimeAgo(data.getCreatedAt()));
        } else {
            // show just now!
            tvDate.setText(data.getCreatedAt());
        }
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
