package nl.audioware.nicheplayer.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.thegrizzlylabs.sardineandroid.DavResource;
import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import nl.audioware.nicheplayer.Objects.Song;
import nl.audioware.nicheplayer.SQL.SongDbHelper;

public class MediaPlayerService extends Service{
    public static final String ACTION_PLAY = "nl.audioware.nicheplayer.MediaPlayerService.PLAY";
    public static final String ACTION_PAUSE = "nl.audioware.nicheplayer.MediaPlayerService.PAUSE";
    public static final String ACTION_RESUME= "nl.audioware.nicheplayer.MediaPlayerService.RESUME";
    MediaPlayer mMediaPlayer = null;
    InputStream inStream;
    FileOutputStream outStream;
    boolean bufferFilled = false;

    public ArrayList<Song> songArrayList;
    private SongDbHelper dBHelper;

    @Override
    public void onCreate(){
        dBHelper = new SongDbHelper(this);
        songArrayList = dBHelper.getSongs(this);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if(action==null){
            return flags;
        }
        if(action.equals(ACTION_PLAY)){
            if(mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            } else {
                mMediaPlayer.reset();
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
            } else {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            try {
                int playingSongId = -1;
                for (int i = 0; i < songArrayList.size(); i++) {
                    Song tmpSong = songArrayList.get(i);
                    if(tmpSong.getUid()==intent.getIntExtra("songID", -1)) {
                        playingSongId = i;
                        break;
                    }
                }
                if(playingSongId==-1){
                    return flags;
                }
                //String filename = "Chasing_Cars.mp3";
                //File tmpFile = new File(getFilesDir(), "SavedData/"+filename);
                if(songArrayList.get(playingSongId).getCacheFile()!=null){
                    Log.d("mergeFiles", songArrayList.get(playingSongId).getOfflineFiles().size() + " : " + songArrayList.get(playingSongId).getCacheFile());
                } else {
                    Log.d("mergeFiles", songArrayList.get(playingSongId).getOfflineFiles().size() + " : NULL");
                }

                if(songArrayList.get(playingSongId).getOfflineFiles().size()!=0 && (songArrayList.get(playingSongId).getCacheFile()==null || !songArrayList.get(playingSongId).getCacheFile().exists())){
                    Log.d("mergeFiles", "Merging");
                    songArrayList.get(playingSongId).mergeFiles(this);
                }

                if(songArrayList.get(playingSongId).getCacheFile()!=null && songArrayList.get(playingSongId).getCacheFile().exists()){
                    mMediaPlayer.setDataSource(songArrayList.get(playingSongId).getCacheFile().getAbsolutePath());
                    mMediaPlayer.prepare();
                } else {
                    //Init sardine
                    File tmpFile = new File(this.getCacheDir(), UUID.randomUUID().toString() + ".mp3");
                    Sardine sardine = new OkHttpSardine();
                    sardine.setCredentials("jonason123", "Sagara544");
                    downloadFile(this, sardine, tmpFile, playingSongId);
                    Log.d("tmpFile", tmpFile.getAbsolutePath());
                }
                //while(tmpFile.length()<=524288); // Wait till buffer is minimally filled

                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        //mMediaPlayer.seekTo(0);
                        mMediaPlayer.start();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(action.equals(ACTION_PAUSE)) {
            if(mMediaPlayer!=null) mMediaPlayer.pause();
        } else if(action.equals(ACTION_RESUME)) {
            if(mMediaPlayer!=null) mMediaPlayer.start();
        }
        return flags;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) mMediaPlayer.release();
        dBHelper.replaceSongs(songArrayList);
    }

    public void downloadFile(final Context context, final Sardine sardine, final File f, final int id){
        bufferFilled = false;
        new Thread(new Runnable() {
            public void run() {
                try {
                    DavResource fileInfo = sardine.list(songArrayList.get(id).getUrl()).get(0);
                    inStream = sardine.get(songArrayList.get(id).getUrl());
                    if(f.exists()){
                        f.delete();
                        Log.d("downloader", "file existed, removed");
                    }
                    if(!f.getParentFile().exists())f.getParentFile().mkdir();
                    f.createNewFile();
                    outStream = new FileOutputStream(f);
                    Long fileLength = fileInfo.getContentLength();
                    try {
                        byte data[] = new byte[16384];

                        long lastPercentNotify = -1, curPercent;
                        int count;
                        int total = 0;

                        while ((count = inStream.read(data, 0, data.length)) != -1){
                            total += count;
                            outStream.write(data, 0, count);
                            curPercent = (total * 100) / fileLength;

                            if (curPercent != lastPercentNotify){
                                lastPercentNotify = curPercent;
                            }
                        }
                    } finally {
                        inStream.close();
                        outStream.close();
                        songArrayList.get(id).splitFiles(context, f);
                        songArrayList.get(id).setOfflineChunkFiles(songArrayList.get(id).getOfflineFiles());
                        mMediaPlayer.setDataSource(f.getAbsolutePath());
                        mMediaPlayer.prepareAsync();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}