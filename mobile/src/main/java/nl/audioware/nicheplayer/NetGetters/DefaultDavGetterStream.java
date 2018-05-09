package nl.audioware.nicheplayer.NetGetters;

import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

class DefaultDavGetterStream implements Runnable{
    private String UserName;
    private String Password;
    private String URL;
    private Sardine sardine;

    public DefaultDavGetterStream(JSONObject vars){
        UserName = vars.optString("username", "");
        Password = vars.optString("pw", "");
        URL = vars.optString("url", "");

        sardine = new OkHttpSardine();
        sardine.setCredentials(UserName, Password);
    }
    @Override
    public void run() {
        try {
            done(sardine.get(URL));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void done(InputStream returnData){

    }
}