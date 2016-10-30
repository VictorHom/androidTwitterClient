package com.codepath.apps.twitterclient.models;

/**
 * Created by victorhom on 10/29/16.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
public class Tweet {
    private String body;
    private long uid; // unique id for the tweet
    private User user;
    private String createdAt;
    // list out attributes

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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
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



}
