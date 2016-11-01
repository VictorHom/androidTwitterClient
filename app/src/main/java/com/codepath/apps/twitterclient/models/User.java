package com.codepath.apps.twitterclient.models;

import android.os.Parcel;
import android.os.Parcelable;

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
public class User implements Parcelable {

    // list the attributes
    // deserialize the user json
    private String name;
    private long uid;

    public String getDescription() {
        return description;
    }

    private String description;


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
            u.description = jsonObject.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return u;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.uid);
        dest.writeString(this.screenName);
        dest.writeString(this.profileImageUrl);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.uid = in.readLong();
        this.screenName = in.readString();
        this.profileImageUrl = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
