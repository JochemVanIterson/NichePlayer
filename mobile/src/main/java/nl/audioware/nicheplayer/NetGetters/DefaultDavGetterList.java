package nl.audioware.nicheplayer.NetGetters;

import com.thegrizzlylabs.sardineandroid.DavResource;
import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

class DefaultDavGetterList implements Runnable{
    private String UserName;
    private String Password;
    private String URL;
    private Sardine sardine;

    public DefaultDavGetterList(JSONObject vars){
        UserName = vars.optString("username", "");
        Password = vars.optString("pw", "");
        URL = vars.optString("url", "");

        sardine = new OkHttpSardine();
        sardine.setCredentials(UserName, Password);
    }
    @Override
    public void run() {
        try {
            done(sardine.list(URL));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void done(List<DavResource> returnData){

    }
}