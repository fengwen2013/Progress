package com.lol.fwen.progress.data;

import android.os.Bundle;
import android.util.Log;

import com.lol.fwen.progress.newsapi.NewsApiCallBack;
import com.lol.fwen.progress.newsapi.NewsApiRequest;
import com.lol.fwen.progress.newsapi.NewsApiResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Collections;
import java.util.Date;

public class NewsRequest extends FeedRequest {
    String newsSource = "";
    Date prevTime = new Date();

    NewsApiRequest request;

    public NewsRequest(String newsSource) {
        Bundle params = new Bundle();
        final Feed.FeedSrc feedSrc = getSrc(newsSource);

        prevTime.setTime(0);
        params.putString("source", newsSource);
        this.newsSource = newsSource;
        request = new NewsApiRequest(params, new NewsApiCallBack() {
            @Override
            public void onSuccess(NewsApiResponse response) {
                try {
                    Log.v("NewsApi request", "start");

                    JSONObject jObjResponse = response.getJSONObject();
                    JSONArray jArray = jObjResponse.getJSONArray("articles");

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jObject = jArray.getJSONObject(i);
                        Feed feed = new Feed(jObject, feedSrc);

                        if (feed.getCreatedTime().after(prevTime)) {
                            feedList.add(feed);
                        }

                        //Log.v("data[]", feed.toString());

                    }

                    Collections.sort(feedList, Collections.reverseOrder());
                    if (feedList.size() > 0) {
                        prevTime = feedList.get(0).getCreatedTime();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                FeedRequest.executeDec();
                Log.e("newapi request", "end: " + FeedRequest.executeGet());
            }

            @Override
            public void onFail() {
                FeedRequest.executeDec();
            }
        });
    }

    private Feed.FeedSrc getSrc(String newsSource) {
        Feed.FeedSrc feedSrc = Feed.FeedSrc.DEFAULT;

        switch (newsSource) {
            case "google-news" :
                feedSrc = Feed.FeedSrc.NEWSAPI_GOOGLE;
                break;
            case "bloomberg":
                feedSrc = Feed.FeedSrc.NEWSAPI_BLOOMBERG;
                break;
        }

        return (feedSrc);
    }

    @Override
    public void execute() {
        super.execute();
        request.executeAsync();
    }

    @Override
    public RequestType getSelf() {
        return (RequestType.NEWSAPI);
    }
}