package com.codepath.apps.twitterclient.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by victorhom on 10/29/16.
 */

//{
//        "profile_sidebar_fill_color": "DDEEF6",
//        "profile_sidebar_border_color": "C0DEED",
//        "profile_background_tile": false,
//        "name": "Twitter API",
//        "profile_image_url": "http://a0.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3_normal.png",
//        "created_at": "Wed May 23 06:01:13 +0000 2007",
//        "location": "San Francisco, CA",
//        "follow_request_sent": false,
//        "profile_link_color": "0084B4",
//        "is_translator": false,
//        "id_str": "6253282",
//        "entities": {
//        "url": {
//        "urls": [
//        {
//        "expanded_url": null,
//        "url": "",
//        "indices": [
//        0,
//        22
//        ]
//        }
//        ]
//        },
public class User {

    // list the attributes
    // deserialize the user json
    private String name;
    private long uid;

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    private String screenName;
    private String profileImageUrl;

    //deserialize the user json => User
    public static User fromJson(JSONObject jsonObject) {
        User u = new User();

        try {
            u.name = jsonObject.getString("name");
            u.uid = jsonObject.getLong("id");
            u.screenName = jsonObject.getString("screen_name");
            u.profileImageUrl = jsonObject.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return u;
    }
}
