package com.codepath.apps.twitterclient.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.networks.TwitterClient;
import com.codepath.apps.twitterclient.utils.Helper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;


public class ComposeTweetFragment extends DialogFragment {

    private OnFragmentInteractionListener mListener;
    private TwitterClient client;
    OnDataPass dataPasser;


    @BindView(R.id.etTweetBox)
    EditText etTweetBox;

    @BindView(R.id.camera)
    ImageView camera;

    @BindView(R.id.map)
    ImageView map;

    @BindView(R.id.character_limit)
    TextView characterLimit;

    @BindView(R.id.submit_tweet)
    Button submitTweet;

    private Unbinder unbinder;

    public ComposeTweetFragment() {
        // Required empty public constructor
    }

    public static ComposeTweetFragment newInstance() {
        ComposeTweetFragment fragment = new ComposeTweetFragment();
//        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compose_tweet, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
//        dataPasser = (OnDataPass) context;
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
        super.onAttach(context);
        dataPasser = (OnDataPass) context;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    @Override
    public void onResume() {
        int width = getResources().getDimensionPixelSize(R.dimen.dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.dialog_height);
        getDialog().getWindow().setLayout(width, height);
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnTextChanged(R.id.etTweetBox)
    public void updateCharacterLimit() {
        characterLimit.setText(String.valueOf(140 - etTweetBox.getText().length()));
    }

    @OnClick(R.id.submit_tweet)
    public void submitTweet() {
        String currentTweet = etTweetBox.getText().toString();
        if (currentTweet.length() > 0) {
            client.submitTweet(submitTweetHandler(), currentTweet);
        } else {
            Toast.makeText(getActivity(), "The tweet is empty. Please type something", Toast.LENGTH_LONG).show();
        }

    }

    private JsonHttpResponseHandler submitTweetHandler() {
        return new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                closeFragment();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Helper.toastInternetIssues(getContext());
            }
        };
    }

    private void closeFragment() {
        //getActivity().getFragmentManager().beginTransaction().remove(ComposeTweetFragment.this).commit();
        passData(true, etTweetBox.getText().toString());
        getActivity().getSupportFragmentManager().beginTransaction().remove(ComposeTweetFragment.this).commit();
        //getDialog().dismiss();

    }

    @OnClick(R.id.map)
    public void selectMap() {
        Log.d("DEBUG", "map has been selected");
    }

    @OnClick(R.id.camera)
    public void selectCamera() {
        Log.d("DEBUG", "camera has been selected");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    public interface OnDataPass {
        public void getUpdate(boolean compose, String message);
    }

    public void passData(boolean compose, String message) {
        if (dataPasser != null) {
            dataPasser.getUpdate(compose, message);
        }
    }
}
