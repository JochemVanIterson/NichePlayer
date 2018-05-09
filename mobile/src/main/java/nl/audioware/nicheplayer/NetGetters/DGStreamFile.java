package nl.audioware.nicheplayer.NetGetters;

import android.content.Context;
import android.util.Log;

import com.thegrizzlylabs.sardineandroid.DavResource;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

public class DGStreamFile {
    Thread davGetter;
    Context context;
    JSONObject requestHash = new JSONObject();

    public DGStreamFile(Context context, String user, String pw, String url){
        try {
            requestHash.put("username", user);
            requestHash.put("pw", pw);
            requestHash.put("url", url);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        davGetter = new Thread(
            new DefaultDavGetterStream(requestHash){
                @Override
                void done(InputStream returnData){

                }
            }, "DGListFolder"
        );
    }

    public void get(){
        davGetter.start();
    }
}
