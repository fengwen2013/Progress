package com.lol.fwen.progress.UI;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.lol.fwen.progress.R;
import com.lol.fwen.progress.data.Feed;
import com.lol.fwen.progress.data.FeedRequest;
import com.lol.fwen.progress.data.FeedRequest.RequestType;
import com.lol.fwen.progress.data.ImageCache;
import com.lol.fwen.progress.newsapi.NewsApiConfig;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FeedActivity
        extends AppCompatActivity
        implements RecyclerViewFragment.OnFragmentInteractionListener {
    private String TAG = "FeedActivity";
    private String REQUESTS_KEY = "REQUEST_KEY";
    private String TAG_RECYCLER_VIEW_FRAGMENT = "RecyclerViewFragment";
    private String TAG_DETAIL_FRAGMENT = "DetailFragment";

    ImageCache imageCache;
    ImageButton tLoginButton;
    ImageButton fLoginButton;
    Set<RequestType> requestTypes;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private LoginManager loginManager;
    private TwitterAuthClient twitterAuthClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData(savedInstanceState);
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.activity_feed);

        fLoginButton = (ImageButton) findViewById(R.id.facebook_login_button);
        fLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFacebookLogin();
            }
        });

        updateFacebookBtn();
        tLoginButton = (ImageButton) findViewById(R.id.twitter_login_button);
        tLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTwitterLogin();
            }
        });

        updateTwitterBtn();

        RecyclerViewFragment recyclerViewFragment = (RecyclerViewFragment)getFragmentManager().
                findFragmentByTag(TAG_RECYCLER_VIEW_FRAGMENT);
        DetailFragment detailFragment = (DetailFragment)getFragmentManager().
                findFragmentByTag(TAG_DETAIL_FRAGMENT);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if (recyclerViewFragment == null) {
            recyclerViewFragment = new RecyclerViewFragment();
            transaction.add(recyclerViewFragment, TAG_RECYCLER_VIEW_FRAGMENT);
        }

        transaction.replace(R.id.feed_fragment, recyclerViewFragment);
        if (detailFragment != null) {
            transaction.add(R.id.feed_fragment, detailFragment);
        }

        transaction.commit();
    }

    private void handleTwitterLogin() {
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (twitterSession != null) {
            TwitterCore.getInstance().getSessionManager().clearActiveSession();
            requestTypes.remove(RequestType.TWITTER);
            tLoginButton.setImageResource(R.drawable.twitter_button_grey);
        } else {
            twitterAuthClient.authorize(FeedActivity.this, new com.twitter.sdk.android.core.Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> twitterSessionResult) {
                    Log.v("tlogin", "success");
                    Toast.makeText(FeedActivity.this, "Twitter login success", Toast.LENGTH_SHORT).show();
                    tLoginButton.setImageResource(R.drawable.twitter_button);
                    requestTypes.add(RequestType.TWITTER);
                }

                @Override
                public void failure(TwitterException e) {
                    Log.v("tlogin", "fail");
                    e.printStackTrace();
                    tLoginButton.setImageResource(R.drawable.twitter_button_grey);
                    Toast.makeText(FeedActivity.this, "Twitter login fail", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initData(Bundle savedInstanceState) {
        facebookSetup();
        twitterSetup();
        NewsAPISetup();

        imageCache = ImageCache.getInstance();
        if (savedInstanceState != null) {
            Object[] array = (Object[]) savedInstanceState.getSerializable(REQUESTS_KEY);
            requestTypes = new HashSet<>();

            for (Object type : array) {
                Log.d("Type", type + "");
                requestTypes.add((RequestType) type);
            }
        } else {
            requestTypes = initRequest();
        }
    }

    private void NewsAPISetup() {
        NewsApiConfig.init(this);
    }

    private void twitterSetup() {
        Twitter.initialize(this);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("CONSUMER_KEY", "CONSUMER_SECRET"))
                .debug(true)
                .build();
        Twitter.initialize(config);
        twitterAuthClient= new TwitterAuthClient();
    }

    private void facebookSetup() {
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,AccessToken currentAccessToken) {
                updateFacebookBtn();
            }
        };

        loginManager = LoginManager.getInstance();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d("flogin", "onSuccess");
                requestTypes.add(RequestType.FACKBOOK);
                updateFacebookBtn();
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
    }

    private void handleFacebookLogin() {
        if (AccessToken.getCurrentAccessToken() != null){
            loginManager.logOut();
            requestTypes.remove(RequestType.FACKBOOK);
        }else{
            accessTokenTracker.startTracking();
            loginManager.logInWithReadPermissions(this, Arrays.asList("user_posts", "email"));
        }
    }

    private void updateFacebookBtn() {
        if (AccessToken.getCurrentAccessToken() != null){
            fLoginButton.setImageResource(R.drawable.facebook_button);
        }else{
            fLoginButton.setImageResource(R.drawable.facebook_button_grey);
        }
    }

    private void updateTwitterBtn() {
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (twitterSession != null) {
            tLoginButton.setImageResource(R.drawable.twitter_button);
        } else {
            tLoginButton.setImageResource(R.drawable.twitter_button_grey);
        }
    }

    private Set<RequestType> initRequest() {
        Set<RequestType> set = new HashSet<>();

        if (checkLogin(FeedRequest.RequestType.FACKBOOK)) {
            set.add(FeedRequest.RequestType.FACKBOOK);
        }

        if (checkLogin(FeedRequest.RequestType.TWITTER)){
            set.add(FeedRequest.RequestType.TWITTER);
        }

        set.add(RequestType.NEWSAPI);
        return (set);
    }

    private boolean checkLogin(FeedRequest.RequestType vendor) {
        switch (vendor) {
            case FACKBOOK:
                return (AccessToken.getCurrentAccessToken() != null);
            case TWITTER:
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                return (session != null);

        }

        return (false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("onActivityResult", "" + requestCode);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        twitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(REQUESTS_KEY, requestTypes.toArray());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onFragmentItemClick(Feed item) {
        // Create new fragment and transaction
        Log.d(TAG, "onFragmentItemClick");
        Fragment newFragment = new DetailFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putParcelable(DetailFragment.ARG_FEED, item);
        newFragment.setArguments(args);
        transaction.add(R.id.feed_fragment, newFragment, TAG_DETAIL_FRAGMENT);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
