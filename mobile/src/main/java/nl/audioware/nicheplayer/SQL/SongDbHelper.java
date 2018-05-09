package nl.audioware.nicheplayer.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

import nl.audioware.nicheplayer.Objects.Song;

public class SongDbHelper extends SQLiteOpenHelper {
    SQLiteDatabase db;
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Songs.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SQLContract.SongEntry.TABLE_NAME + " (" +
                    SQLContract.SongEntry._ID + " INTEGER PRIMARY KEY," +
                    SQLContract.SongEntry.COLUMN_NAME_TITLE + " TEXT," +
                    SQLContract.SongEntry.COLUMN_NAME_ARTIST + " TEXT," +
                    SQLContract.SongEntry.COLUMN_NAME_URL + " TEXT," +
                    SQLContract.SongEntry.COLUMN_NAME_OFFLINE_FILES+ " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SQLContract.SongEntry.TABLE_NAME;

    public SongDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.d("SH_Create", "Created");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void replaceSong(Song song){
        Log.d("SH_Replace",
            "id: " +song.getUid()+"\n"+
                  "title: " +song.getTitle()+"\n"+
                  "artist: " +song.getArtist()+"\n"+
                  "url: " +song.getUrl()+"\n"+
                  "offlineFiles: "+song.getOfflineFilesJson().toString()
        );

        ContentValues values = new ContentValues();
        values.put(SQLContract.SongEntry._ID, song.getUid());
        values.put(SQLContract.SongEntry.COLUMN_NAME_TITLE, song.getTitle());
        values.put(SQLContract.SongEntry.COLUMN_NAME_ARTIST, song.getArtist());
        values.put(SQLContract.SongEntry.COLUMN_NAME_URL, song.getUrl());

        db.replace(SQLContract.SongEntry.TABLE_NAME, null, values);
    }

    public void replaceSongs(ArrayList<Song> songs){
        for (Song song:songs) {
            replaceSong(song);
        }
    }

    public Song getSongByID(Context context, int id){
        String[] projection = {
                BaseColumns._ID,
                SQLContract.SongEntry.COLUMN_NAME_TITLE,
                SQLContract.SongEntry.COLUMN_NAME_ARTIST,
                SQLContract.SongEntry.COLUMN_NAME_URL,
                SQLContract.SongEntry.COLUMN_NAME_OFFLINE_FILES
        };

        String sortOrder = BaseColumns._ID + " ASC";
        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(SQLContract.SongEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder, "1");
        Song returnSong = new Song(context, cursor);
        cursor.close();
        return returnSong;
    }

    public ArrayList<Song> getSongs(Context context){
        ArrayList<Song> returnArray = new ArrayList<>();
        String[] projection = {
                BaseColumns._ID,
                SQLContract.SongEntry.COLUMN_NAME_TITLE,
                SQLContract.SongEntry.COLUMN_NAME_ARTIST,
                SQLContract.SongEntry.COLUMN_NAME_URL,
                SQLContract.SongEntry.COLUMN_NAME_OFFLINE_FILES
        };

        String sortOrder = BaseColumns._ID + " ASC";
        Cursor cursor = db.query(SQLContract.SongEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);
        while(cursor.moveToNext()) {
            returnArray.add(new Song(context, cursor));
        }
        cursor.close();
        return returnArray;
    }
}