package p4u1.dd.phone_lan_frame.network;


import com.badlogic.gdx.Gdx;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Paul on 12/20/2016.
 */

public class ServerWorkhorse implements Runnable {
    public Socket socket = null;
    public volatile MessageHolder messageHolder;
    public boolean running = true;
    PrintWriter out;
    float ping;

    public ServerWorkhorse(Socket arg0, MessageHolder arg1) {
        socket = arg0;
        messageHolder = arg1;
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));
            String inputLine, outputLine;

            while ((inputLine = in.readLine()) != null) {
                JSONObject data = null;
                try {
                    data = new JSONObject(inputLine);
                    String type = data.getString("type");
                    if (type.contentEquals("pong")) {
                        float timestamp = data.getLong("timestamp");
                        ping = System.currentTimeMillis() - timestamp;
                        Gdx.app.log("phlusko", "ping: " + ping);
                    }

                } catch (JSONException e) {
                }
                messageHolder.addMessage(inputLine);
            }
            socket.close();
        } catch (IOException e) {
            Gdx.app.log("phlusko", "Server Dying");
            running = false;
        }
    }
}
