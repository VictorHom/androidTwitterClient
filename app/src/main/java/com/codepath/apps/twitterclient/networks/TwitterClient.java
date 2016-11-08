package com.codepath.apps.twitterclient.networks;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes:
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 *
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "B9OHe03TWnpHvDOY0JeTtRqBW";       // Change this
	public static final String REST_CONSUMER_SECRET = "Bq2SXymOwdX7JP3drAIx6NFzXoVF0WopLcxZDNy3681h1IAePn"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://vhsimpletweets"; // Change this (here and in manifest)

	private long max_id = 0;

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void setMaxID(long id) {
		max_id = id;
	}

	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	public void getInterestingnessList(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */

//	statuses/user_timeline.json
//	every tweet has its own user object
//	count = 2
//	since_id=1
	public void getHomeTimeline(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		client.get(apiUrl, params, handler);
	}

	public void getAdditionalTimeline(AsyncHttpResponseHandler handler, long last_id) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("max_id", last_id - 1);
		client.get(apiUrl, params, handler);
	}

	// when this is called, the max_id should be reset;
	public void getHomeTimelineUser(AsyncHttpResponseHandler handler, Long twitterId) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("user_id", twitterId);
		params.put("count", 25);
		client.get(apiUrl, params, handler);
	}

	public void getAdditionalTimelineUser(AsyncHttpResponseHandler handler, long last_id, Long twitterId) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("user_id", twitterId);
		params.put("count", 25);
		params.put("max_id", last_id - 1);
		client.get(apiUrl, params, handler);
	}

	// have another one for composing the tweet
	public void submitTweet(AsyncHttpResponseHandler handler, String message) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", message);
		client.post(apiUrl, params, handler);
	}

	//POST https://api.twitter.com/1.1/statuses/update.json?
	// status=Maybe%20he%27ll%20finally%20find%20his%20keys.%20%23peterfalk
	public void replyToTweet(AsyncHttpResponseHandler handler, String message, long id) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("in_reply_to_status_id", id);
		params.put("status", message);
		client.post(apiUrl, params, handler);

	}

	//https://api.twitter.com/1.1/statuses/mentions_timeline.json
	public void getMentionsTimeline(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		client.get(apiUrl, params, handler);
	}

	public void getAdditionalMentionsTimeline(AsyncHttpResponseHandler handler, long last_id) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("max_id", last_id - 1);
		client.get(apiUrl, params, handler);
	}

	// GET userprofile
	// https://api.twitter.com/1.1/users/lookup.json?screen_name=twitterapi,twitter
	// array of objects
	public void getProfileInformation(AsyncHttpResponseHandler handler, String twitterHandle) {
		String apiUrl = getApiUrl("users/lookup.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", twitterHandle);
		client.get(apiUrl, params, handler);
	}

	public void getVerifyCredentials(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		client.get(apiUrl, handler);
	}

	public void getProfileBanner(AsyncHttpResponseHandler handler, String twitterHandle) {
		String apiUrl = getApiUrl("users/profile_banner.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", twitterHandle);
		client.get(apiUrl, params, handler);
	}

	//search/tweets.json
	public void getSearchResults(AsyncHttpResponseHandler handler, String query) {
		String apiUrl = getApiUrl("search/tweets.json");
		RequestParams params = new RequestParams();
		params.put("q", query);
		params.put("count", 25);
		params.put("result_type","mixed");
		client.get(apiUrl, params, handler);
	}

	public void getAdditionalSearchResults(AsyncHttpResponseHandler handler, String query, long last_id) {
		String apiUrl = getApiUrl("search/tweets.json");
		RequestParams params = new RequestParams();
		params.put("q", query);
		params.put("count", 25);
		params.put("result_type","mixed");
		params.put("max_id", last_id - 1);
		client.get(apiUrl, params, handler);
	}


}
