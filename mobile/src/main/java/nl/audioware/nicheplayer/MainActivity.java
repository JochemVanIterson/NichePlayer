package nl.audioware.nicheplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nl.audioware.nicheplayer.SQL.SongDbHelper;
import nl.audioware.nicheplayer.NetGetters.NGGetFileList;
import nl.audioware.nicheplayer.Services.MediaPlayerService;

public class MainActivity extends AppCompatActivity {
    Button bPlay, bPause, bResume;
    private SongDbHelper dBHelper;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dBHelper = new SongDbHelper(this);
        dBHelper.getWritableDatabase();

        bPlay = findViewById(R.id.b_play);
        bPause = findViewById(R.id.b_pause);
        bResume = findViewById(R.id.b_resume);

        bPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(MainActivity.this, MediaPlayerService.class);
                serviceIntent.setAction(MediaPlayerService.ACTION_PLAY);
                serviceIntent.putExtra("songID", 2);
                startService(serviceIntent);
            }
        });

        bPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(MainActivity.this, MediaPlayerService.class);
                serviceIntent.setAction(MediaPlayerService.ACTION_PAUSE);
                startService(serviceIntent);
            }
        });

        bResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(MainActivity.this, MediaPlayerService.class);
                serviceIntent.setAction(MediaPlayerService.ACTION_RESUME);
                startService(serviceIntent);
            }
        });

        //new DGListFolder(this, "jonason123", "Sagara544", "https://jonason123.stackstorage.com/remote.php/webdav/muziek/").get();

        new NGGetFileList(this, "http://www.audioware.nl/webtest/nicheplayer/", "jochem.vaniterson", "1234567890").get();

        //initUi();

        // Example of a call to a native method
        //TextView tv = findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public void initUi(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "library").build();
                StringBuilder stringBuilder = new StringBuilder();
                //List<Song> songList = db.userDao().getAll();
                //if(songList.size()==0){
                //    stringBuilder.append("Library Empty");
                //} else {
                //    for (Song song : songList) {
                //        stringBuilder.append(song.getUid()).append(" - ");
                //        stringBuilder.append(song.getTitle()).append(" - ");
                //        stringBuilder.append(song.getArtist()).append("\n");
                //    }
                //}
                //Log.d("stringBuilder", stringBuilder.toString());
                TextView tv = findViewById(R.id.sample_text);
                tv.setText(stringBuilder.toString());
            }
        });
    }
}
