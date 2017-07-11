package com.lol.fwen.progress.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lol.fwen.progress.R;
import com.twitter.sdk.android.core.models.Tweet;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Feed
        implements Parcelable, Comparable<Feed> {
    static String dateStrPattern = "EEE MMM dd yyyy HH:mm:ss";

    public enum FeedSrc {
        DEFAULT, FACEBOOK, TWITTER, LINKEDIN, NEWSAPI_BLOOMBERG, NEWSAPI_GOOGLE;
    }

    String id = "";
    String content = "";
    String url = "";
    String description = "";
    Date createdTime;
    String author = "";
    String author_icon = "";
    FeedSrc src = FeedSrc.DEFAULT;

    public Feed(JSONObject object, FeedSrc src) {
        try {
            if (src == FeedSrc.FACEBOOK) {
                String dateStr = object.getString("created_time");

                createdTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(dateStr);
                id = object.getString("id");
                if (object.has("full_picture")) {
                    url = object.getString("full_picture");
                }

                if (object.has("description")) {
                    description = object.getString("description");
                }

                if (object.has("message")) {
                    content = object.getString("message");
                }

                if (object.has("name")) {
                    author = object.getString("name");
                }
            } else if (src == FeedSrc.NEWSAPI_GOOGLE) {
                String dateStr = object.getString("publishedAt");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                createdTime = df.parse(dateStr);
                author = object.getString("author");
                content = object.getString("title") + " (src: " + object.getString("url") + ")";
                description = object.getString("description");
                url = object.getString("urlToImage");
            }

            this.src = src;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Feed(Tweet t) {
        this.id = t.idStr;
        this.content = t.text;
        this.author = t.user.name;
        this.author_icon = t.user.profileImageUrl;
        this.src = FeedSrc.TWITTER;

        try {
            this.createdTime = new SimpleDateFormat(TwitterRequest.dateStrPattern).parse(t.createdAt);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("parse", TwitterRequest.dateStrPattern + ": " + t.createdAt);
        }

    }

    protected Feed(Parcel in) {
        try {
            id = in.readString();
            content = in.readString();
            url = in.readString();
            description = in.readString();
            createdTime = new SimpleDateFormat(Feed.dateStrPattern).parse(in.readString());
            author = in.readString();
            author_icon = in.readString();
            src = FeedSrc.valueOf(in.readString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Feed() {
    }

    public Date getCreatedTime() {
        return (createdTime);
    }

    public String getId() {
        return (id);
    }

    public String getContent() {
        return (content);
    }

    public String getImageURL() {
        return (url);
    }

    public String getDescription() {
        return (description);
    }

    public String getAuthor() {
        return (author);
    }

    public String getAuthorIcon() {
        return (author_icon);
    }

    public boolean hasImage() {
        return (url != null && url.length() > 0);
    }

    public boolean hasAuthorIcon() {
        return (author_icon.length() > 0);
    }

    public String toString() {
        return "id: " + id + "; create_time: " + createdTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(content);
        dest.writeString(url);
        dest.writeString(description);
        dest.writeString(new SimpleDateFormat().format(createdTime));
        dest.writeString(author);
        dest.writeString(author_icon);
        dest.writeString(src.name());
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Feed> CREATOR = new Parcelable.Creator<Feed>() {
        @Override
        public Feed createFromParcel(Parcel in) {
            return new Feed(in);
        }

        @Override
        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };

    public static int getSrcImage(Feed item) {
        int rid = R.drawable.unknown_icon;

        switch (item.src) {
            case FACEBOOK :
                rid = R.drawable.facebook_icon;
                break;
            case TWITTER:
                rid = R.drawable.twitter_icon;
                break;
            case NEWSAPI_BLOOMBERG:
                rid = R.drawable.bloomberg_icon;
                break;
            case NEWSAPI_GOOGLE:
                rid = R.drawable.google_icon;
                break;
            case LINKEDIN:
                rid = R.drawable.linkedin_icon;
        }

        return (rid);
    }


    @Override
    public int compareTo(@NonNull Feed feed) {
        return (this.createdTime.compareTo(feed.createdTime));
    }
}
