package p4u1.dd.phone_lan_frame.demos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import p4u1.dd.phone_lan_frame.network.PhotoLanServer;

/**
 * Created by Paul on 1/11/2017.
 */

public class LineDemo {

    PhotoLanServer server;
    volatile int maxHeight;
    volatile int currentHeight = -1;
    volatile float lineStart;
    volatile long delay = 20;
    volatile long lastUpdate;
    public LineDemo(PhotoLanServer arg0) {
        server = arg0;
        maxHeight = 600 + (600 * server.workhorses.size());
        currentHeight = 0;
    }
    public void startLines() {
        maxHeight = 600 + (600 * server.workhorses.size());
        lineStart = System.currentTimeMillis();
        currentHeight = ((int) System.currentTimeMillis() - (int) lineStart) % maxHeight;
        try {
            JSONObject data = null;
            data = new JSONObject();
            data.put("type", "startLine");
            data.put("maxHeight", maxHeight);
            server.broadcast(data.toString());
            lastUpdate = TimeUtils.millis();
        } catch (JSONException e) {
            //e.printStackTrace();
        }
    }
    public void update() {

        if (currentHeight > -1) {
            maxHeight = 600 + (600 * server.workhorses.size());
            currentHeight = ((int) System.currentTimeMillis() - (int) lineStart) % maxHeight;
            //Gdx.app.log("phlusko", "hrm" + currentHeight);
            Gdx.app.log("phlusko", "hrm" + (TimeUtils.timeSinceMillis(lastUpdate)));
            if (TimeUtils.timeSinceMillis(lastUpdate) > delay) {
                lastUpdate = System.currentTimeMillis();
                try {
                    JSONObject data = null;
                    data = new JSONObject();
                    data.put("type", "linePosition");
                    data.put("position", currentHeight);
                    server.broadcast(data.toString());
                    Gdx.app.log("phlusko", "updating" + currentHeight);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
