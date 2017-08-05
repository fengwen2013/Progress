package com.lol.fwen.progress.asynctask;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.lol.fwen.progress.UI.RecyclerViewAdapter;
import com.lol.fwen.progress.data.Feed;
import com.lol.fwen.progress.data.FeedRequest;
import com.lol.fwen.progress.data.FeedRequest.RequestType;
import com.lol.fwen.progress.data.SocialNetWorkRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class DownloadDataAsyncTask extends AsyncTask<Void, Void, Integer> {
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    Set<SocialNetWorkRequest> requestSet;
    private String TAG = "DownloadDataAsyncTask";

    public DownloadDataAsyncTask(RecyclerView recyclerView, RecyclerViewAdapter adapter,
                                 Set<SocialNetWorkRequest> requestSet) {
        this.recyclerView = recyclerView;
        this.adapter = adapter;
        this.requestSet = requestSet;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground");
        List<List<Feed>> lists = new LinkedList<>();

        Log.d(TAG, "dobackground - start: " + FeedRequest.executeGet());
        for (SocialNetWorkRequest snRequest : requestSet) {
            try {
                snRequest.execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "doInBackground - spin wait");
        while (FeedRequest.executeGet() != 0) {
        }

        Log.d(TAG, "doInBackground - all finished");
        int nFeeds = 0;

        for (SocialNetWorkRequest snRequest: requestSet) {
            lists.add(snRequest.getList());
            nFeeds += snRequest.getList().size();
        }

        if (nFeeds > 0) {
            lists.add(adapter.getList());
            List<Feed> res = merge(lists);
            adapter.setList(res);
        }

        return (nFeeds);
    }

    @Override
    protected void onPostExecute(Integer nFeeds) {
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(adapter);
        }

        adapter.notifyDataSetChanged();
        Snackbar.make(recyclerView,
                "Loaded " + nFeeds + " feed(s)", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private List<Feed> merge(List<List<Feed>> lists) {
        PriorityQueue<DownloadDataAsyncTask.Pair> heap = new PriorityQueue();
        List<Feed> list = new LinkedList<>();

        for (List<Feed> l : lists) {
            if (l.size() != 0) {
                heap.add(new DownloadDataAsyncTask.Pair(l.get(0), l));
                l.remove(0);
            }
        }

        while (!heap.isEmpty()) {
            DownloadDataAsyncTask.Pair p = heap.poll();
            List<Feed> l = p.list;

            //Log.d("heap", p.feed.getCreatedTime().toString());
            list.add(p.feed);
            if (l.size() != 0) {
                p.feed = l.get(0);
                heap.add(p);
                l.remove(0);
            }
        }

        return (list);
    }

    class Pair implements Comparable<DownloadDataAsyncTask.Pair> {
        Feed feed;
        List<Feed> list;

        public Pair(Feed feed, List<Feed> list) {
            this.feed = feed;
            this.list = list;
        }

        @Override
        public int compareTo(@NonNull DownloadDataAsyncTask.Pair pair) {
            return (pair.feed.compareTo(this.feed));
        }
    }
}
