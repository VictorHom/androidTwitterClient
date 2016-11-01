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

/**
 *  {
 "coordinates": null,
 "favorited": false,
 "truncated": false,
 "created_at": "Wed Aug 29 17:12:58 +0000 2012",
 "id_str": "240859602684612608",
 "entities": {
 "urls": [
 {
 "expanded_url": "/blog/twitter-certified-products",
 "url": "https://t.co/MjJ8xAnT",
 "indices": [
 52,
 73
 ],
 "display_url": "dev.twitter.com/blog/twitter-c\u2026"
 }
 ],
 "hashtags": [

 ],
 "user_mentions": [

 ]
 },
 "in_reply_to_user_id_str": null,
 "contributors": null,
 "text": "Introducing the Twitter Certified Products Program: https://t.co/MjJ8xAnT",
 "retweet_count": 121,
 "in_reply_to_status_id_str": null,
 "id": 240859602684612608,
 "geo": null,
 "retweeted": false,
 "possibly_sensitive": false,
 "in_reply_to_user_id": null,
 "place": null,
 "user": {
 "profile_sidebar_fill_color": "DDEEF6",
 "profile_sidebar_border_color": "C0DEED",
 "profile_background_tile": false,
 "name": "Twitter API",
 "profile_image_url": "http://a0.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3_normal.png",
 "created_at": "Wed May 23 06:01:13 +0000 2007",
 "location": "San Francisco, CA",
 "follow_request_sent": false,
 "profile_link_color": "0084B4",
 "is_translator": false,
 "id_str": "6253282",
 "entities": {
 "url": {
 "urls": [
 {
 "expanded_url": null,
 "url": "",
 "indices": [
 0,
 22
 ]
 }
 ]
 },
 "description": {
 "urls": [

 ]
 }
 },
 "default_profile": true,
 "contributors_enabled": true,
 "favourites_count": 24,
 "url": "",
 "profile_image_url_https": "https://si0.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3_normal.png",
 "utc_offset": -28800,
 "id": 6253282,
 "profile_use_background_image": true,
 "listed_count": 10775,
 "profile_text_color": "333333",
 "lang": "en",
 "followers_count": 1212864,
 "protected": false,
 "notifications": null,
 "profile_background_image_url_https": "https://si0.twimg.com/images/themes/theme1/bg.png",
 "profile_background_color": "C0DEED",
 "verified": true,
 "geo_enabled": true,
 "time_zone": "Pacific Time (US & Canada)",
 "description": "The Real Twitter API. I tweet about API changes, service issues and happily answer questions about Twitter and our API. Don't get an answer? It's on my website.",
 "default_profile_image": false,
 "profile_background_image_url": "http://a0.twimg.com/images/themes/theme1/bg.png",
 "statuses_count": 3333,
 "friends_count": 31,
 "following": null,
 "show_all_inline_media": false,
 "screen_name": "twitterapi"
 },
 "in_reply_to_screen_name": null,
 "source": "YoruFukurou",
 "in_reply_to_status_id": null
 }
 */
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
