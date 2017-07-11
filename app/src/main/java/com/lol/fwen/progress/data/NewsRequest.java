package com.lol.fwen.progress.data;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
                    Log.e("NewsApi request", "start");

                    JSONObject jObjResponse = response.getJSONObject();
                    JSONArray jArray = jObjResponse.getJSONArray("articles");

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jObject = jArray.getJSONObject(i);
                        Feed feed = new Feed(jObject, feedSrc);

                        if (feed.getCreatedTime().after(prevTime)) {
                            feedList.add(feed);
                        }

                        Log.e("data[]", feed.toString());

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

    class NewsApiRequest {
        NewsApiCallBack callBack;
        StringBuilder urlstr = new StringBuilder("https://newsapi.org/v1/articles?sortBy=top&apiKey=c27cd277d5674439bc263995aefa4117");

        public NewsApiRequest(Bundle params, NewsApiCallBack callBack) {
            String source = params.getString("source");

            urlstr.append("&source=");
            urlstr.append(source);
            this.callBack = callBack;
        }

        public void executeAsync() {
            URL url;
            HttpURLConnection urlConnection = null;
            NewsApiResponse newsApiResponse = new NewsApiResponse();

            try {
                url = new URL(urlstr.toString());

                urlConnection = (HttpURLConnection) url
                        .openConnection();

                if (urlConnection.getResponseCode() == 200) {
                    InputStream in = urlConnection.getInputStream();

                    BufferedReader bread = new BufferedReader(new InputStreamReader(in));

                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = bread.readLine()) != null) {
                        response.append(inputLine);
                    }

                    newsApiResponse.setData(response.toString());
                    in.close();
                    Log.e("news", newsApiResponse.data);
                    callBack.onSuccess(newsApiResponse);
                } else {
                    callBack.onFail();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

    }

    class NewsApiResponse {
        String data;

        public JSONObject getJSONObject() {
            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                return (jsonObject);
            }
        }

        public void setData(String s) {
            data = s;
        }
    }

    interface NewsApiCallBack {
        void onSuccess(NewsApiResponse response);
        void onFail();
    }
}