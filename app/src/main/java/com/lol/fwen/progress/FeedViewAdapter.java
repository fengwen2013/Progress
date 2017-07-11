package com.lol.fwen.progress;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lol.fwen.progress.asynctask.DownloadImageTask;
import com.lol.fwen.progress.data.Feed;
import com.lol.fwen.progress.data.ImageCache;

import java.util.List;

public class FeedViewAdapter extends ArrayAdapter<Feed> {
    ImageCache imageCache = null;

    public FeedViewAdapter(Context context, int resource, List<Feed> values) {
        super(context, resource, values);
        imageCache = ((FeedActivity)context).imageCache;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Feed item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_item, parent, false);
        }

        // Lookup view for data population
        TextView itemTime = (TextView) convertView.findViewById(R.id.item_time);
        TextView itemContent = (TextView) convertView.findViewById(R.id.item_content);
        TextView itemAuthor = (TextView) convertView.findViewById(R.id.item_author);
        ImageView itemSrcIcon = (ImageView) convertView.findViewById(R.id.item_src_icon);
        ImageView itemAuthorIcon = (ImageView) convertView.findViewById(R.id.authorPic_item);

        // Populate the data into the template view using the data object
        itemTime.setText(item.getCreatedTime().toString());
        itemContent.setText(item.getContent());
        itemAuthor.setText(item.getAuthor());
        itemSrcIcon.setImageResource(Feed.getSrcImage(item));

        if (item.hasAuthorIcon()) {
            new DownloadImageTask(itemAuthorIcon, ((FeedActivity)getContext()).imageCache).execute(item.getAuthorIcon());
        } else {
            itemAuthorIcon.setImageResource(R.drawable.empty);
        }

        // Return the completed view to render on screen
        return convertView;
    }

}
