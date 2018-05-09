package nl.audioware.nicheplayer.Objects;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import nl.audioware.nicheplayer.SQL.SQLContract;

public class Song {
    private int uid;
    private String title;
    private String artist;
    private String url;
    private ArrayList<File> offlineChunkFiles = new ArrayList<>();
    private File cacheFile;

    public Song(Context context, JSONObject object){
        uid = object.optInt("id");
        title = object.optString("title");
        artist = object.optString("artist");
        url = object.optString("url");

        //JSONArray JSONOfflineFiles = object.optJSONArray("offlineFiles");
        File SongsFolder = new File(context.getFilesDir(), "songdata/");
        if(!SongsFolder.exists())SongsFolder.mkdir();
        //for (int i = 0; i < JSONOfflineFiles.length(); i++) {
        //    offlineChunkFiles.add(new File(SongsFolder, JSONOfflineFiles.optString(i)));
        //}
    }

    public Song(Context context, Cursor cursor){
        uid = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID));
        title = cursor.getString(cursor.getColumnIndexOrThrow(SQLContract.SongEntry.COLUMN_NAME_TITLE));
        artist = cursor.getString(cursor.getColumnIndexOrThrow(SQLContract.SongEntry.COLUMN_NAME_ARTIST));
        url = cursor.getString(cursor.getColumnIndexOrThrow(SQLContract.SongEntry.COLUMN_NAME_URL));
    }

    public static ArrayList<Song> JSON2Array(Context context, JSONArray jsonArray){
        ArrayList<Song> returnArray = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            returnArray.add(new Song(context, jsonArray.optJSONObject(i)));
        }
        return returnArray;
    }

    public int getUid(){return uid;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getUrl(){return url;}
    public ArrayList<File> getOfflineFiles(){return offlineChunkFiles;}
    public JSONArray getOfflineFilesJson(){
        JSONArray returnArray = new JSONArray();
        if(offlineChunkFiles == null || offlineChunkFiles.size()==0)return returnArray;
        for (File file:offlineChunkFiles) {
            returnArray.put(file.getName());
        }
        return returnArray;
    }
    public File getCacheFile(){return cacheFile;}

    public void setOfflineChunkFiles(ArrayList<File> offlineChunkFiles){this.offlineChunkFiles = offlineChunkFiles;}

    public File mergeFiles(Context context) {
        File returnFile = new File(context.getCacheDir(), UUID.randomUUID().toString() + ".mp3");
        FileOutputStream fos;
        FileInputStream fis;
        byte[] fileBytes;
        int bytesRead = 0;
        try {
            fos = new FileOutputStream(returnFile, true);
            for (File file : offlineChunkFiles) {
                fis = new FileInputStream(file);
                fileBytes = new byte[(int) file.length()];
                bytesRead = fis.read(fileBytes, 0, (int) file.length());
                assert (bytesRead == fileBytes.length);
                assert (bytesRead == (int) file.length());
                fos.write(fileBytes);
                fos.flush();
                fileBytes = null;
                fis.close();
                fis = null;
            }
            fos.close();
            fos = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        cacheFile = returnFile;
        return returnFile;
    }
    public void splitFiles(Context context, File inputFile){
        cacheFile = inputFile;
        int Chunk_Size = 1024*1024;
        FileInputStream fis;
        String newName;
        FileOutputStream chunk;
        int fileSize = (int) cacheFile.length();
        int nChunks = 0, read = 0, readLength = Chunk_Size;
        byte[] byteChunk;
        try {
            fis = new FileInputStream(cacheFile);
            File SongsFolder = new File(context.getFilesDir(), "songdata/");
            //StupidTest.size = (int)cacheFile.length();
            while (fileSize > 0) {
                if (fileSize <= Chunk_Size) {
                    readLength = fileSize;
                }
                byteChunk = new byte[readLength];
                read = fis.read(byteChunk, 0, readLength);
                fileSize -= read;
                assert(read==byteChunk.length);
                nChunks++;
                newName = UUID.randomUUID().toString();
                File ChunkFile = new File(SongsFolder, newName);
                offlineChunkFiles.add(ChunkFile);
                chunk = new FileOutputStream(ChunkFile);
                chunk.write(byteChunk);
                chunk.flush();
                chunk.close();
                byteChunk = null;
                chunk = null;
            }
            fis.close();
            fis = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}