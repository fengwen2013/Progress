package com.lol.fwen.progress.db;


import android.provider.BaseColumns;

public class FeedDBContract {
    public static final String DB_NAME = "feeds.db";
    public static final int DB_VERSION = 1;

    private FeedDBContract() {}

    public static class FeedTable implements BaseColumns {
        public static final String TABLE_NAME = "Feeds";

        public static final String CN_FID = "fid";
        public static final String CN_CONTENT = "content";
        public static final String CN_URL = "url";
        public static final String CN_DESCRIPTION = "description";
        public static final String CN_CREATEDTIME = "created";
        public static final String CN_AUTHOR = "author";
        public static final String CN_AUTHOR_ICON = "author_icon";
        public static final String CN_SRC = "src";

        public static final String[] PROJECTIONS_ALL = {
                CN_FID, CN_CONTENT, CN_URL, CN_DESCRIPTION,
                CN_CREATEDTIME, CN_AUTHOR, CN_AUTHOR_ICON, CN_SRC
        };

        public static final String CREATE_TABLE = "" +
                "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                CN_FID + " TEXT," +
                CN_CONTENT + " TEXT," +
                CN_URL + " TEXT," +
                CN_DESCRIPTION + " TEXT," +
                CN_CREATEDTIME + " DATETIME," +
                CN_AUTHOR + " TEXT," +
                CN_AUTHOR_ICON + " TEXT," +
                CN_SRC + " INTEGER)";

        public static final String DELETE_TABLE =  "" +
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

}
