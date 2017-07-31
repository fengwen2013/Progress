package com.lol.fwen.progress.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FeedRequest implements SocialNetWorkRequest, Parcelable {
    public enum RequestType {
        GENERAL, FACKBOOK, TWITTER, LINKEDIN, NEWSAPI
    }

    public static final AtomicInteger executeSum = new AtomicInteger(0);

    boolean selected;
    public List<Feed> feedList;

    public FeedRequest() {}

    private FeedRequest(Parcel in) {
        feedList = new LinkedList<>();

        in.readList(feedList, List.class.getClassLoader());
    }

    @Override
    public void execute() {
        feedList = new LinkedList<>();
        FeedRequest.executeInc();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(feedList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<FeedRequest> CREATOR = new Parcelable.Creator<FeedRequest>() {
        @Override
        public FeedRequest createFromParcel(Parcel in) {
            return new FeedRequest(in);
        }

        @Override
        public FeedRequest[] newArray(int size) {
            return new FeedRequest[size];
        }
    };

    @Override
    public List<Feed> getList() {
        return (feedList);
    }

    @Override
    public RequestType getSelf() {
        return (RequestType.GENERAL);
    }

    public static void executeInc() {
        executeSum.incrementAndGet();
    }

    public static void executeDec() {
        executeSum.decrementAndGet();
    }

    public static int executeGet() {
        return (executeSum.get());
    }
}
