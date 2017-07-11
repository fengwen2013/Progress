package com.lol.fwen.progress.data;

import android.graphics.Bitmap;
import android.util.LruCache;

public class ImageCache {
    private LruCache<String, Bitmap> memoryCache;
    private int size = 0;

    public ImageCache(){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        size = maxMemory / 16;
        memoryCache = new LruCache<String, Bitmap>(size) {
            protected int sizeOf(String key, Bitmap bitmap) {
                return (bitmap.getByteCount() / 1024);
            }
        };
    }

    public static ImageCache getInstance() {
        return (new ImageCache());
    }

    public void addImage(String url, Bitmap image) {
        if (!hasImage(url)) {
            memoryCache.put(url, image);
        }
    }

    public Bitmap getImage(String url) {
        if (url == null || url.length() == 0) {
            return (null);
        }

        return (memoryCache.get(url));
    }

    public boolean hasImage(String url) {
        return (getImage(url) != null);
    }
}
