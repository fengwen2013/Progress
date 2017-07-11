package com.lol.fwen.progress.asynctask;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lol.fwen.progress.data.Feed;
import com.lol.fwen.progress.data.FeedRequest;
import com.lol.fwen.progress.R;
import com.lol.fwen.progress.data.SocialNetWorkRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;


public class SendRequestAsyncTask extends AsyncTask<Void, Void, List<Feed>> {
    ListView listView;
    ArrayAdapter adapter;
    Activity activity;
    Set<SocialNetWorkRequest> requestSet;

    public SendRequestAsyncTask(ListView listView, ArrayAdapter adapter, Set<SocialNetWorkRequest> requestSet) {
        this.listView = listView;
        this.adapter = adapter;
        this.requestSet = requestSet;
        activity = (Activity)listView.getContext();
    }

    @Override
    protected List<Feed> doInBackground(Void... voids) {
        Log.v("SendRequestAsyncTask", "doInBackground");
        List<List<Feed>> lists = new LinkedList<>();

        Log.e("dobackground", "start: " + FeedRequest.executeGet());
        for (SocialNetWorkRequest snRequest : requestSet) {
            try {
                snRequest.execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.e("doInBackground", "spin wait");
        while (FeedRequest.executeGet() != 0) {}

        Log.e("doInBackground", "all finished");

        for (SocialNetWorkRequest snRequest : requestSet) {
            lists.add(snRequest.getList());
        }

        return (merge(lists));
    }

    @Override
    protected void onPostExecute(List<Feed> feeds) {
        if (listView.getAdapter() == null) {
            listView.setAdapter(adapter);
        }

        for (int i = feeds.size() - 1; i >= 0; i--) {
            adapter.insert(feeds.get(i), 0);
        }

        adapter.notifyDataSetChanged();
        Snackbar.make(activity.findViewById(R.id.activity_feed),
                "Loaded " + feeds.size() + " feed(s)", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private List<Feed> merge(List<List<Feed>> lists) {
        PriorityQueue<Pair> heap = new PriorityQueue();
        List<Feed> list = new LinkedList<>();

        for (List<Feed> l : lists) {
            if (l.size() != 0) {
                heap.add(new Pair(l.get(0), l));
                l.remove(0);
            }
        }

        while (!heap.isEmpty()) {
            Pair p = heap.poll();
            List<Feed> l = p.list;

            Log.e("heap", p.feed.getCreatedTime().toString());
            list.add(p.feed);
            if (l.size() != 0) {
                p.feed = l.get(0);
                heap.add(p);
                l.remove(0);
            }
        }

        return (list);
    }

    class Pair implements Comparable<Pair>{
        Feed feed;
        List<Feed> list;

        public Pair(Feed feed, List<Feed> list) {
            this.feed = feed;
            this.list = list;
        }

        @Override
        public int compareTo(@NonNull Pair pair) {
            return (pair.feed.compareTo(this.feed));
        }
    }
}
