package com.lol.fwen.progress.newsapi;

import org.json.JSONException;
import org.json.JSONObject;

public class NewsApiResponse {
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
