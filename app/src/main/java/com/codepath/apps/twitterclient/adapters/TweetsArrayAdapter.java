package com.codepath.apps.twitterclient.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.utils.ViewHolder1;
import com.codepath.apps.twitterclient.utils.ViewHolder2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victorhom on 10/29/16.
 */
// taking tweet objects and turning into views displayed on the list
public class TweetsArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context mContext;
    ArrayList<Tweet> mTweets;
    private long last_max_id;
    private final int NO_IMAGE = 0, IMAGE = 1;

    public TweetsArrayAdapter(Context context, ArrayList<Tweet> tweets) {
        this.mContext = context;
        this.mTweets = tweets;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        if (mTweets.get(position).getLinks().size() == 0) {
            return NO_IMAGE;
        } else if (mTweets.get(position).getLinks().size() > 0) {
            return IMAGE;
        }
        return -1;
    }

    // this is to handle loading additional post for tweitter as you scroll down the page
    public long getLastMaxId() {
        return last_max_id;
    }

    public void addAll(List<Tweet> tweets) {
        mTweets.addAll(tweets);
        Tweet lastTweet = tweets.get(tweets.size() - 1);
        last_max_id = lastTweet.getUid();
        notifyDataSetChanged();
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case NO_IMAGE:
                View v1 = inflater.inflate(R.layout.item_tweet_a, parent, false);
                viewHolder = new ViewHolder1(v1);
                break;
            case IMAGE:
                View v2 = inflater.inflate(R.layout.item_tweet_b, parent, false);
                viewHolder = new ViewHolder2(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.item_tweet_a, parent, false);
                viewHolder = new ViewHolder1(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case NO_IMAGE:
                ViewHolder1 vh1 = (ViewHolder1) holder;
                //configureViewHolder1(vh1, position);
                vh1.setData(mTweets.get(position), mContext);
                break;
            case IMAGE:
                ViewHolder2 vh2 = (ViewHolder2) holder;
                vh2.setData(mTweets.get(position), mContext);
                break;
            default:
                ViewHolder1 v = (ViewHolder1) holder;
                v.setData(mTweets.get(position), mContext);
                break;
        }
    }

//    @Override
//    public void onBindViewHolder(TweetsArrayAdapter.ViewHolder holder, int position) {
//
//        Tweet tweet = mTweets.get(position);
//
//        TextView tweetUser = holder.tvProfileName;
//        TextView tweetBody = holder.tvTweetBody;
//        ImageView tweetProfilePicture = holder.ivProfile;
//        TextView tweetDate = holder.tvDate;
//        // Helper.getRelativeTimeAgo(tweet.getCreatedAt());
//        tweetUser.setText(tweet.getUser().getScreenName());
//        tweetBody.setText(tweet.getBody());
//        tweetProfilePicture.setImageResource(android.R.color.transparent);
//        Glide.with(mContext).load(tweet.getUser().getProfileImageUrl()).into(tweetProfilePicture);
//        tweetDate.setText(Helper.getRelativeTimeAgo(tweet.getCreatedAt()));
//
//    }





//    public class ViewHolder extends RecyclerView.ViewHolder{
//        @BindView(R.id.ivProfile) ImageView ivProfile;
//        @BindView(R.id.tvProfileName) TextView tvProfileName;
//        @BindView(R.id.tvTweetBody) TextView tvTweetBody;
//        @BindView(R.id.tvDate) TextView tvDate;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }
}
