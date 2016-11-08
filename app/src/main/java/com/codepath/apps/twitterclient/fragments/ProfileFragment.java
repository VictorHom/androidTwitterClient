package com.codepath.apps.twitterclient.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.networks.TwitterClient;
import com.codepath.apps.twitterclient.utils.Helper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    @BindView(R.id.ivBackground)
    ImageView ivBackground;
    @BindView(R.id.ivProfile)
    ImageView ivProfile;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvTwitterHandle)
    TextView tvTwitterHandle;
    @BindView(R.id.tvBlurb)
    TextView tvBlurb;
    @BindView(R.id.tvFollowerNumber)
    TextView tvFollowerNumber;
    @BindView(R.id.tvFollowingNumber)
    TextView tvFollowingNumber;

    private Unbinder unbinder;
    private TwitterClient client;
    private User user;


    public static final String HANDLE = "HANDLE";
    private static final String DEFAULT_VALUE = "heyvicter";
    private String twitterHandle;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(String twitterHandle) {
        Bundle args = new Bundle();
        args.putString(HANDLE, twitterHandle);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            twitterHandle = bundle.getString(HANDLE, DEFAULT_VALUE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        client = TwitterApplication.getRestClient();
        if (twitterHandle != null) {
            client.getProfileInformation(handleGetProfile(), twitterHandle);
            client.getProfileBanner(handleProfileBanner(), twitterHandle);
        }
    }

    private JsonHttpResponseHandler handleGetProfile() {
        return new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    user = (User) User.fromJson(response.getJSONObject(0));
                    setProfileUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Helper.toastInternetIssues(getContext());
            }
        };
    }

    private JsonHttpResponseHandler handleProfileBanner() {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject sizes = response.getJSONObject("sizes");
                    JSONObject mobile = sizes.getJSONObject("1500x500");
                    String url = mobile.getString("url");
                    ivBackground.setImageResource(android.R.color.transparent);
                    ivBackground.setScaleType(ImageView.ScaleType.FIT_XY);
                    Glide.with(getContext()).load(url).into(ivBackground);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Helper.toastInternetIssues(getContext());
            }
        };
    }

    private void setProfileUI() {
        // the background profile is handled by handleProfileBanner in a separate network request
        ivProfile.setImageResource(android.R.color.transparent);
        Glide.with(getContext()).load(user.getProfileImageUrl()).into(ivProfile);
        tvName.setText(user.getName());
        tvTwitterHandle.setText("@" + user.getScreenName());
        tvBlurb.setText(user.getDescription());
        tvFollowerNumber.setText(String.valueOf(user.getFollowersCount()));
        tvFollowingNumber.setText(String.valueOf(user.getFriendsCount()));
    }
}
