package com.lol.fwen.progress.newsapi;

import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewsApiRequest {
    NewsApiCallBack callBack;
    StringBuilder urlstr = new StringBuilder("https://newsapi.org/v1/articles?sortBy=top&apiKey=");
    String apikey = "";

    public NewsApiRequest(Bundle params, NewsApiCallBack callBack) {
        String source = params.getString("source");

        apikey = NewsApiConfig.apiKey;
        urlstr.append(apikey);
        urlstr.append("&source=");
        urlstr.append(source);
        this.callBack = callBack;
    }

    public void executeAsync() {
        new Runnable() {
            @Override
            public void run() {
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
        }.run();


    }

}


