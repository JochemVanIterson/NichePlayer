package nl.audioware.nicheplayer.SQL;

import android.provider.BaseColumns;

public class SQLContract {
    private SQLContract(){}

    /* Inner class that defines the table contents */
    public static class SongEntry implements BaseColumns {
        public static final String TABLE_NAME = "songs";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_ARTIST = "artist";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_OFFLINE_FILES= "offline_files";
    }
}
