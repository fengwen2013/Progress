package com.lol.fwen.progress.data;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class FeedRequest implements SocialNetWorkRequest {
    public static final int GENERAL_LOGIN = 0;
    public static final int FACKBOOK_LOGIN = 1;
    public static final int TWITTER_LOGIN = 2;
    public static final int LINKEDIN_LOGIN = 3;
    public static final AtomicInteger executeSum = new AtomicInteger(0);

    boolean selected;
    public List<Feed> feedList;

    @Override
    public void execute() {
        feedList = new LinkedList<>();
        FeedRequest.executeInc();
    }

    @Override
    public List<Feed> getList() {
        return (feedList);
    }

    public int getSelf() {
        return (FeedRequest.GENERAL_LOGIN);
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
