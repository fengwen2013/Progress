package com.lol.fwen.progress.newsapi;

/**
 * Created by fwen on 7/12/17.
 */

public interface NewsApiCallBack {
    void onSuccess(NewsApiResponse response);
    void onFail();
}
