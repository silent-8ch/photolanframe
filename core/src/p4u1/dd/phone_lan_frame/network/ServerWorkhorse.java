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
    PrintWriter out;
    float ping;
    public ServerWorkhorse(Socket arg0, MessageHolder arg1) {
        socket = arg0;
        messageHolder = arg1;
    }
    public boolean running = true;
    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            String inputLine, outputLine;
            //KnockKnockProtocol kkp = new KnockKnockProtocol();
            //outputLine = kkp.processInput(null);
            //out.println(outputLine);

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
                    //e.printStackTrace();
                }

                messageHolder.addMessage(inputLine);
                    //outputLine = kkp.processInput(inputLine);
                //out.println(outputLine);
                //if (outputLine.equals("Bye"))
                  //  break;
            }
            socket.close();
        } catch (IOException e) {
            //e.printStackTrace();
            Gdx.app.log("phlusko", "Server Dying");
            running = false;
        }

    }
}
