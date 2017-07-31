package com.lol.fwen.progress.newsapi;


import android.content.Context;

import com.lol.fwen.progress.UI.FeedActivity;
import com.lol.fwen.progress.R;

public class NewsApiConfig {
    public static String apiKey = "";

    public static void init(Context context) {
        FeedActivity activity = (FeedActivity) context;

        apiKey = context.getResources().getString(R.string.newsapi_key);
    }
}
