package nl.audioware.nicheplayer.NetGetters;

import android.content.Context;
import android.util.Log;

import com.thegrizzlylabs.sardineandroid.DavResource;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DGListFolder {
    Thread davGetter;
    Context context;
    JSONObject requestHash = new JSONObject();

    public DGListFolder(Context context, String user, String pw, String url){
        try {
            requestHash.put("username", user);
            requestHash.put("pw", pw);
            requestHash.put("url", url);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        davGetter = new Thread(
            new DefaultDavGetterList(requestHash){
                @Override
                void done(List<DavResource> returnData){
                    for (DavResource res : returnData){
                        Log.d("DavResponse", res.getName());
                    }
                }
            }, "DGListFolder"
        );
    }

    public void get(){
        davGetter.start();
    }
}
