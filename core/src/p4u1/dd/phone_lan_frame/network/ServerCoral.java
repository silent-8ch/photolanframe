package p4u1.dd.phone_lan_frame.network;

import com.badlogic.gdx.Gdx;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Paul on 1/10/2017.
 */

public class ServerCoral implements Runnable {
    public PhotoLanServer photoLanServer;
    public ServerCoral (PhotoLanServer arg0) {
        photoLanServer = arg0;
    }

    @Override
    public void run() {
        while (true) try {
            ServerWorkhorse horse = new ServerWorkhorse(photoLanServer.server.accept(), photoLanServer.messageHolder);
            photoLanServer.workhorses.add(horse);
            horse.out = new PrintWriter(horse.socket.getOutputStream(), true);
            new Thread(horse).start();
            photoLanServer.world.addPhone(photoLanServer.workhorses.size());

            Gdx.app.log("phlusko", "Client Connected from: " + horse.socket.getInetAddress().getHostName());

            photoLanServer.ready = true;
            JSONObject data;
            data = new JSONObject();
            try {
                data.put("type", "ready");
                photoLanServer.messageHolder.addMessage(data.toString());
                data.put("type", "ping");
                data.put("timestamp", System.currentTimeMillis());
                horse.out.println(data.toString());
            } catch (JSONException e) {
            }
        } catch (IOException e) {
        }
    }
}