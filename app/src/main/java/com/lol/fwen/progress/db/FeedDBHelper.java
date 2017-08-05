package com.lol.fwen.progress.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lol.fwen.progress.data.Feed;

import java.util.List;

import static com.lol.fwen.progress.db.FeedDBContract.*;
import static com.lol.fwen.progress.db.FeedDBContract.FeedTable.*;

public class FeedDBHelper extends SQLiteOpenHelper{
    public FeedDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DELETE_TABLE);
        onCreate(sqLiteDatabase);
    }

    // TO DO
    public boolean addFeed(Feed feed) {
        return (false);
    }

    // TO DO
    public int getMostRecentFeed(int n) {
        return (0);
    }

    public void addFeedList(List<Feed> list) {
        for (Feed feed : list) {
            addFeed(feed);
        }
    }
}
