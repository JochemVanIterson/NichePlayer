package nl.audioware.nicheplayer.NetGetters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.audioware.nicheplayer.SQL.SongDbHelper;
import nl.audioware.nicheplayer.Objects.Song;

public class NGGetFileList {
    DefaultNetGetter netGetter;

    public NGGetFileList(final Activity activity, String BaseUrl, String User, String iv){
        String url = BaseUrl + "Scripts/FileList.php?action=Get";

        Map<String,String> params = new HashMap<String, String>();
        params.put("username", User);
        params.put("iv", iv);

        netGetter = new DefaultNetGetter(activity, url, params){
            @Override
            public void ActionDone(final String response, final Context context){
                Log.d("DataGetter", response);
                try {
                    JSONObject JsonResponse = new JSONObject(response);
                    JSONArray dataArray = JsonResponse.getJSONArray("data");
                    ArrayList<Song> songArrayList = Song.JSON2Array(activity, dataArray);
                    for (Song song:songArrayList) {
                        Log.d("NG_song", song.getTitle());
                    }
                    SongDbHelper dbHelper = new SongDbHelper(activity);
                    dbHelper.replaceSongs(songArrayList);
                    //if(activity instanceof MainActivity){
                    //    ((MainActivity)activity).initUi();
                    //}
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void get(String tag){
                super.get(tag);
            }
        };
    }

    public void get(){
        netGetter.get("DataGetter");
    }
}