package com.lol.fwen.progress.data;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONObject;

public class FacebookRequest extends FeedRequest {

    GraphRequest graphRequest;
    String prevTime = "";
    Bundle params;

    public FacebookRequest() {
        params = new Bundle();
        params.putString("fields", "description, message,created_time,id, full_picture,status_type,source, name");

        graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/feed", params, HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        try {
                            Log.v("facebook request", "start");
                            JSONObject jObjResponse = new JSONObject(String.valueOf(response.getJSONObject()));
                            JSONArray jArray = jObjResponse.getJSONArray("data");

                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject jObject = jArray.getJSONObject(i);
                                Feed feed = new Feed(jObject, Feed.FeedSrc.FACEBOOK);

                                feedList.add(feed);
                                //Log.v("data[]", jObject.toString());
                                if (i == 0) {
                                    prevTime = String.valueOf(feed.getCreatedTime().getTime() / 1000);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            FeedRequest.executeDec();
                            Log.e("facebook request", "end: " + FeedRequest.executeGet());
                        }
                    }
                }
        );
    }

    @Override
    public void execute() {
        Log.v("FaceBook", "Execute");
        super.execute();
        params.putString("since", prevTime);
        params.putString("limits", "20");
        Log.e("prevTime", prevTime);
        graphRequest.setParameters(params);
        graphRequest.executeAsync();
    }

    @Override
    public int getSelf() {
        return (FeedRequest.FACKBOOK_LOGIN);
    }
}
