package com.codepath.apps.twitterclient.models;

/**
 * Created by victorhom on 10/29/16.
 */

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// parse json and store store, encapsulate state logic or display logic
public class Tweet implements Parcelable {
    public final static String NOW = "just now";
    private String body;
    private long uid; // unique id for the tweet
    private User user;
    private String createdAt;
    private ArrayList<String> links = new ArrayList<>();
    private boolean retweet;

    public boolean isRetweet() {
        return retweet;
    }

    public int getLikes() {
        return likes;
    }

    public int getRetweetNumber() {
        return retweetNumber;
    }

    private int likes;
    private int retweetNumber;

    public Tweet(String message, Tweet lastTweet) {
        this.body = message;
        this.createdAt = NOW;
        this.mediaUrl = "";
        this.uid = lastTweet.getUid();
        this.user = lastTweet.getUser();
        this.links = new ArrayList<>();
    }

    public Tweet() {
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    private String mediaUrl = "";

    // Pattern for gathering http:// links from the Text
    private static final Pattern urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    public ArrayList<String> getLinks() {
        return links;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    // deserialize the json that is coming in
    // Tweet.fromJson({...}) => <Tweet>
    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        // Extract the values from the json, store them

        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
            tweet.links = gatherLinks(tweet.body);
            tweet.mediaUrl = getMediaUrl(jsonObject);
            tweet.likes = jsonObject.getInt("favorite_count");
            tweet.retweet = jsonObject.getBoolean("retweeted");
            tweet.retweetNumber = jsonObject.getInt("retweet_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }


    private static String getMediaUrl(JSONObject j) {
        try {
            if (!j.getBoolean("retweeted")) {
                JSONObject entities = j.getJSONObject("entities");
                JSONArray mediaEntries = entities.getJSONArray("media");
                JSONObject media =  (JSONObject) mediaEntries.get(0);
                if (media.getString("media_url_https").indexOf(".png") > -1 || media.getString("media_url_https").indexOf(".jpg") > -1) {
                    return media.getString("media_url_https");
                } else {
                    return "";
                }

            }
        } catch (JSONException e) {
//            e.printStackTrace();
            return "";
        }
        return "";
    }

    private static ArrayList<String> gatherLinks(String body) {
        ArrayList<String> foundLinks = new ArrayList<>();
        // Matcher matching the pattern
        Matcher m = urlPattern.matcher(body);

        while (m.find()) {
            int start = m.start();
            int end = m.end();
            foundLinks.add((String) body.subSequence(start, end));
        }
        return foundLinks;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> a = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Tweet t = Tweet.fromJSON(jsonArray.getJSONObject(i));
                a.add(t);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return a;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.body);
        dest.writeLong(this.uid);
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.createdAt);
        dest.writeStringList(this.links);
        dest.writeString(this.mediaUrl);
    }

    protected Tweet(Parcel in) {
        this.body = in.readString();
        this.uid = in.readLong();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.createdAt = in.readString();
        this.links = in.createStringArrayList();
        this.mediaUrl = in.readString();
    }

    public static final Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
