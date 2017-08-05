package com.lol.fwen.progress.data;

import android.util.Log;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.List;

import retrofit2.Call;

public class TwitterRequest extends FeedRequest {
    public static String dateStrPattern = "EEE MMM dd HH:mm:ss Z yyyy";
    StatusesService statusesService;
    static Long sinceId = null;
    Callback<List<Tweet>> callback;

    public TwitterRequest() {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();

        statusesService = twitterApiClient.getStatusesService();
        callback = new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                //Do something with result
                Log.v("twitter request", "start");
                List<Tweet> list = result.data;

                for (Tweet t : list) {
                    feedList.add(new Feed(t));
                    //Log.e("twitter", t.user.name);
                }

                if (feedList.size() != 0) {
                    sinceId = Long.parseLong(feedList.get(0).getId());
                }

                FeedRequest.executeDec();
                Log.v("twitter request", "end: " + FeedRequest.executeGet());
            }

            public void failure(TwitterException exception) {
                FeedRequest.executeDec();
                Log.e("twitter request", "fail end");
            }
        };

    }

    @Override
    public void execute() {
        super.execute();
        Log.v("Twitter", "Execute");
        Call<List<Tweet>> call = statusesService.homeTimeline(25, sinceId, null, null, null, null, null);
        call.enqueue(callback);
    }

    @Override
    public RequestType getSelf() {
        return (RequestType.TWITTER);
    }
}
