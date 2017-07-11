package com.lol.fwen.progress;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.lol.fwen.progress.data.FacebookRequest;
import com.lol.fwen.progress.data.Feed;
import com.lol.fwen.progress.data.FeedRequest;
import com.lol.fwen.progress.data.ImageCache;
import com.lol.fwen.progress.data.NewsRequest;
import com.lol.fwen.progress.data.SocialNetWorkRequest;
import com.lol.fwen.progress.data.TwitterRequest;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class FeedActivity
        extends AppCompatActivity
        implements FeedListFragment.OnListFragmentInteractionListener {
    CallbackManager callbackManager;
    ImageCache imageCache;
    TwitterLoginButton tLoginButton;
    Set<SocialNetWorkRequest> requestSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.activity_feed);

        LoginButton fLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        Fragment newFragment = new FeedListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        fLoginButton.setReadPermissions(Arrays.asList("user_posts", "email"));
        fLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d("flogin", "onSuccess");
                requestSet.add(new FacebookRequest());
                Toast.makeText(FeedActivity.this, "Facebook login success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                // App code
                Log.d("flogin", "onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("flogin", "onError");
            }
        });

        tLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        tLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                Log.v("tlogin", "success");
                Toast.makeText(FeedActivity.this, "Twitter login success", Toast.LENGTH_SHORT).show();
                requestSet.add(new TwitterRequest());
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Log.v("tlogin", "fail");
                Toast.makeText(FeedActivity.this, "Twitter login fail", Toast.LENGTH_SHORT).show();
            }
        });

        transaction.replace(R.id.feed_fragment, newFragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    private void initData() {
        callbackManager = CallbackManager.Factory.create();
        Twitter.initialize(this);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("CONSUMER_KEY", "CONSUMER_SECRET"))
                .debug(true)
                .build();
        Twitter.initialize(config);
        imageCache = ImageCache.getInstance();
        requestSet = initRequest();
    }

    private Set<SocialNetWorkRequest> initRequest() {
        HashSet<SocialNetWorkRequest> set = new HashSet<>();

        if (checkLogin(FeedRequest.FACKBOOK_LOGIN)) {
            set.add(new FacebookRequest());
        }

        if (checkLogin(FeedRequest.TWITTER_LOGIN)){
            set.add(new TwitterRequest());
        }

        set.add(new NewsRequest("google-news"));
        return (set);
    }

    private boolean checkLogin(int vendor) {
        switch (vendor) {
            case FeedRequest.FACKBOOK_LOGIN:
                return (AccessToken.getCurrentAccessToken() != null);
            case FeedRequest.TWITTER_LOGIN:
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                return (session != null);

        }

        return (false);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("onActivityResult", "" + requestCode);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        tLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onListFragmentItemClick(Feed item) {
        // Create new fragment and transaction
        Fragment newFragment = new DetailFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putParcelable(DetailFragment.ARG_FEED, item);
        newFragment.setArguments(args);
        transaction.add(R.id.feed_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        Log.d("callback", "callback");
    }


    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
