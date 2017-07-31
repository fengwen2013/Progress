package com.lol.fwen.progress.data;

import com.lol.fwen.progress.data.Feed;

import java.util.List;

public interface SocialNetWorkRequest {
    void execute() throws InterruptedException;
    List<Feed> getList();
    FeedRequest.RequestType getSelf();
}
