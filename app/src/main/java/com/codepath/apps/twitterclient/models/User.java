package com.codepath.apps.twitterclient.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by victorhom on 10/29/16.
 */
public class User implements Parcelable {
    public final static String USER = "USER";
    // list the attributes
    // deserialize the user json
    private String name;
    private long uid;
    private int friendsCount;
    private int followersCount;
    private String backgroundImageUrl;
    private String description;
    private String screenName;
    private String profileImageUrl;

    public String getDescription() {
        return description;
    }

    public int getFriendsCount() { return friendsCount; }

    public int getFollowersCount() { return followersCount; }

    public String getBackgroundImageUrl() { return backgroundImageUrl; }

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

        // decided to wrap into a different try-catch since I am mainly using this
        // for the profile fragments; it might be excessive - test that the objects from the request
        // can fit the same model
        try {
            u.friendsCount = jsonObject.getInt("friends_count");
            u.followersCount = jsonObject.getInt("followers_count");
            u.backgroundImageUrl = jsonObject.getString("profile_background_image_url_https");
        }catch (JSONException e) {
            e.printStackTrace();
        }

        return u;
    }

    public User() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.uid);
        dest.writeString(this.description);
        dest.writeInt(this.friendsCount);
        dest.writeInt(this.followersCount);
        dest.writeString(this.backgroundImageUrl);
        dest.writeString(this.screenName);
        dest.writeString(this.profileImageUrl);
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.uid = in.readLong();
        this.description = in.readString();
        this.friendsCount = in.readInt();
        this.followersCount = in.readInt();
        this.backgroundImageUrl = in.readString();
        this.screenName = in.readString();
        this.profileImageUrl = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
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
