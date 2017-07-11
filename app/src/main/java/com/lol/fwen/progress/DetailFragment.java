package com.lol.fwen.progress;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lol.fwen.progress.asynctask.DownloadImageTask;
import com.lol.fwen.progress.data.Feed;

public class DetailFragment extends Fragment {
    public static final String ARG_FEED = "feed_obj";

    private Feed feed;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        feed = (Feed) getArguments().getParcelable(ARG_FEED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        // Inflate the layout for this fragment

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
            new DownloadImageTask(imageView, ((FeedActivity)getActivity()).imageCache).execute(feed.getImageURL());
        }

        if (feed.hasAuthorIcon()) {
            ImageView imageView = (ImageView) view.findViewById(R.id.authorPic);
            new DownloadImageTask(imageView, ((FeedActivity)getActivity()).imageCache).execute(feed.getAuthorIcon());
        }

        return view;
    }
}
