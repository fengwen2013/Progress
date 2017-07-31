package com.lol.fwen.progress.UI;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lol.fwen.progress.R;
import com.lol.fwen.progress.asynctask.DownloadImageTask;
import com.lol.fwen.progress.data.Feed;
import com.lol.fwen.progress.data.ImageCache;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter {
    private final String TAG = "RecyclerViewAdapter";

    private List<Feed> data;
    private ImageCache imageCache;

    public RecyclerViewAdapter(ImageCache imageCache) {
        data = new ArrayList<>();
        this.imageCache = imageCache;
    }

    public void addListAndNotify(List<Feed> newData) {
        addList(newData);
        notifyDataSetChanged();
    }

    public List<Feed> getList() {
        return (data);
    }

    public void addList(List<Feed> newData) {
        Log.d(TAG, "addList");
        ArrayList<Feed> tmp = new ArrayList<>();

        tmp.addAll(newData);
        tmp.addAll(data);
        data = tmp;
    }

    public void setList(List<Feed> newData) {
        data = newData;
    }

    public Feed getItem(int position) {
        return (data.get(position));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreatedViewHolder");

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Feed feed = getItem(position);

        ((ViewHolder)holder).setView(feed, imageCache);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((RecyclerViewFragment.OnFragmentInteractionListener)view.getContext()).
                        onFragmentItemClick(feed);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (data.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        TextView contentTextView;
        TextView authorTextView;
        ImageView srcIconImageView;
        ImageView authorIconImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            timeTextView = (TextView) itemView.findViewById(R.id.item_time);
            contentTextView = (TextView) itemView.findViewById(R.id.item_content);
            authorTextView = (TextView) itemView.findViewById(R.id.item_author);
            srcIconImageView = (ImageView) itemView.findViewById(R.id.item_src_icon);
            authorIconImageView = (ImageView) itemView.findViewById(R.id.authorPic_item);
        }

        public void setView(Feed feed, ImageCache imageCache) {
            timeTextView.setText(feed.getCreatedTime().toString());
            contentTextView.setText(feed.getContent());
            authorTextView.setText(feed.getAuthor());
            srcIconImageView.setImageResource(Feed.getSrcImage(feed));

            if (feed.hasAuthorIcon()) {
                new DownloadImageTask(authorIconImageView, imageCache).execute(feed.getAuthorIcon());
            } else {
                authorIconImageView.setImageResource(R.drawable.empty);
            }
        }
    }
}
