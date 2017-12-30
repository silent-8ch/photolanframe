package p4u1.dd.phone_lan_frame.network;

import com.badlogic.gdx.Gdx;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;

import p4u1.dd.phone_lan_frame.demos.LineDemo;
import p4u1.dd.phone_lan_frame.ui.ConnectionScreen;
import p4u1.dd.phone_lan_frame.utils.PhonePositions;

/**
 * Created by Paul on 12/28/2016.
 */

public class PhotoLanServer implements Runnable{

    public ServerSocket server;
    public boolean serverStarted = false;
    public volatile ArrayList<ServerWorkhorse> workhorses = new ArrayList<ServerWorkhorse>();
    public volatile MessageHolder messageHolder = new MessageHolder();
    public volatile MessageHolder leaderMessages = new MessageHolder();
    public volatile boolean ready = false;
    PrintWriter out;// = new PrintWriter(players[1].horse.socket.getOutputStream(), true);
    boolean running = true;
    //public LineDemo demo1;
    int ping;
    public volatile PhonePositions world;

    public PhotoLanServer() {
        try {
            server = new ServerSocket(ConnectionScreen.port);
            serverStarted = true;
            new Thread(new ServerCoral(this)).start();
            new Thread(this).start();
        } catch (IOException e) {
            //e.printStackTrace();
            Gdx.app.log("phlusko", "cant make server");
            //Gdx.app.exit();
        }
        world = new PhonePositions();
        world.addPhone();
        updateLanPositions();
        //demo1 = new LineDemo(this);
    }


    @Override
    public void run() {
        while (running) {
            logic();
        }
    }



    public void logic() {
        while (messageHolder.getListSize() > 0) {
            processMessage(messageHolder.popMessage());
        }
        if (workhorses.size() > 0) {
            //demo1.update();
        }
    }

    public void processMessage(String arg0) {
        try {
            JSONObject data = null;
            data = new JSONObject(arg0);
            String type = data.getString("type");
            if (type.contentEquals("ready")) {
                updateLanPositions();
            }
            if (type.contentEquals("partyInfoRequest")) {
                JSONObject response = world.getWorldJSON();
                response.put("type", "partyInfo");
                response.put("party_size", workhorses.size() + 1);
                broadcast(response.toString());
            }
            if (type.contentEquals("gotoOrientation")) {
                broadcast(arg0);
            }
            if (type.contentEquals("gotoPhoto")) {
                broadcast(arg0);
            }
            if (type.contentEquals("phoneMoved")) {
                world.movePhone(data);
                sendPartyInfo();
            }
            if (type.contentEquals("playerScale")) {
                world.scalePlayer(data);
                sendPartyInfo();
            }
            if (type.contentEquals("playerScale")) {
                int to = data.getInt("order");
                JSONObject response = world.getWorldJSON();
                response.put("type", "partyInfo");
                response.put("party_size", workhorses.size() + 1);
                sendMessage(data.toString(), to);
            }
            Gdx.app.log("phlusko", "server received: " + data.toString());
        } catch (JSONException e) {
            //e.printStackTrace();
        }
    }

    public void sendPartyInfo() {
        try {
            JSONObject response = world.getWorldJSON();
            response.put("type", "partyInfo");
            response.put("party_size", workhorses.size() + 1);
            broadcast(response.toString());
        } catch (JSONException e) {
            //e.printStackTrace();
        }


    }

    public void updateLanPositions() {
        try {
            JSONObject data = new JSONObject();
            data.put("type", "getPosition");

            data.put("position", 0);
            leaderMessages.addMessage(data.toString());
            int position_count = 1;
            for(Iterator<ServerWorkhorse> i = workhorses.iterator(); i.hasNext(); ) {
                ServerWorkhorse item = i.next();
                data.put("position", position_count);
                item.out.println(data.toString());
                position_count++;
            }
            sendPartyInfo();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String arg0, int arg1) {
        if (arg1 == 0){
            leaderMessages.addMessage(arg0);
        } else {
            workhorses.get(arg1 -1).out.println(arg0);
        }

    }

    public void broadcast(String arg0) {
        leaderMessages.addMessage(arg0);
        for(Iterator<ServerWorkhorse> i = workhorses.iterator(); i.hasNext(); ) {
            ServerWorkhorse item = i.next();
            item.out.println(arg0);
        }
        //sendMessage(arg0);
    }

    public void sendMessage(String arg0){
        Gdx.app.log("sending message", arg0);
        out.println(arg0);
    }

    public void sendReady() {
        JSONObject data = new JSONObject();
        try {
            data.put("type", "getReady");
            for(Iterator<ServerWorkhorse> i = workhorses.iterator(); i.hasNext(); ) {
                ServerWorkhorse item = i.next();
                item.out.println(data.toString());
                //System.out.println(item);
            }
            leaderMessages.addMessage(data.toString());
        } catch (JSONException e) {
            //e.printStackTrace();
        }
    }

}
