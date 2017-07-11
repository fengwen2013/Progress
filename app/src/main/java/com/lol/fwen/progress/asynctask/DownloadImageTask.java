package com.lol.fwen.progress.asynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.lol.fwen.progress.data.ImageCache;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    ImageCache imageCache;

    public DownloadImageTask(ImageView bmImage, ImageCache imageCache) {
        this.bmImage = bmImage;
        this.imageCache = imageCache;
    }

    protected Bitmap doInBackground(String[] urls) {
        String url = urls[0];
        Bitmap image = null;

        try {
            if (imageCache.hasImage(url)) {
                image = imageCache.getImage(url);
            } else {
                InputStream in = new java.net.URL(url).openStream();
                image = BitmapFactory.decodeStream(in);
                imageCache.addImage(url, image);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        return (image);
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
