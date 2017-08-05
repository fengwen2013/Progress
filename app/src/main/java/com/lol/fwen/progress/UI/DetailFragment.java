package com.lol.fwen.progress.UI;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lol.fwen.progress.R;
import com.lol.fwen.progress.asynctask.DownloadImageAsyncTask;
import com.lol.fwen.progress.data.Feed;

public class DetailFragment extends Fragment {
    private final String TAG = "DetailFragment";
    public static final String ARG_FEED = "feed_obj";

    private Feed feed;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        if (savedInstanceState != null) {
            feed = savedInstanceState.getParcelable(ARG_FEED);
        } else {
            feed = getArguments().getParcelable(ARG_FEED);
        }

        TextView idView = (TextView) view.findViewById(R.id.detailFragment_author);
        TextView contentView = (TextView) view.findViewById(R.id.detailFragment_content);
        TextView detailView = (TextView) view.findViewById(R.id.detailFragment_time);
        ImageView itemSrcIcon = (ImageView) view.findViewById(R.id.detailFragment_src_icon);

        idView.setText(feed.getAuthor());
        contentView.setText(feed.getContent());
        detailView.setText(feed.getCreatedTime().toString());

        itemSrcIcon.setImageResource(Feed.getSrcImage(feed));
        if (feed.hasImage()) {
            ImageView imageView = (ImageView) view.findViewById(R.id.detailFragment_image);
            new DownloadImageAsyncTask(imageView, ((FeedActivity)getActivity()).imageCache).execute(feed.getImageURL());
        }

        if (feed.hasAuthorIcon()) {
            ImageView imageView = (ImageView) view.findViewById(R.id.authorPic);
            new DownloadImageAsyncTask(imageView, ((FeedActivity)getActivity()).imageCache).execute(feed.getAuthorIcon());
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(ARG_FEED, feed);
        super.onSaveInstanceState(savedInstanceState);
    }
}
