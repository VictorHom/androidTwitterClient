package com.codepath.apps.twitterclient.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victorhom on 10/29/16.
 */
// taking tweet objects and turning into views displayed on the list
public class TweetsArrayAdapter extends RecyclerView.Adapter<TweetsArrayAdapter.ViewHolder>{

    Context mContext;
    ArrayList<Tweet> mTweets;

    public TweetsArrayAdapter(Context context, ArrayList<Tweet> tweets) {
        this.mContext = context;
        this.mTweets = tweets;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    public void addAll(List<Tweet> tweets) {
        mTweets.addAll(tweets);
        notifyDataSetChanged();
    }

    @Override
    public TweetsArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TweetsArrayAdapter.ViewHolder holder, int position) {

        Tweet tweet = mTweets.get(position);

        TextView tweetUser = holder.tvProfileName;
        TextView tweetBody = holder.tvTweetBody;
        ImageView tweetProfilePicture = holder.ivProfile;

        tweetUser.setText(tweet.getUser().getScreenName());
        tweetBody.setText(tweet.getBody());
        tweetProfilePicture.setImageResource(android.R.color.transparent);
        Picasso.with(mContext).load(tweet.getUser().getProfileImageUrl()).into(tweetProfilePicture);

    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivProfile;
        TextView tvProfileName;
        TextView tvTweetBody;

        public ViewHolder(View itemView) {
            super(itemView);

            ivProfile = (ImageView) itemView.findViewById(R.id.ivProfile);
            tvProfileName = (TextView) itemView.findViewById(R.id.tvProfileName);
            tvTweetBody = (TextView) itemView.findViewById(R.id.tvTweetBody);
        }
    }
}
