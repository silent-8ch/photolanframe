package p4u1.dd.phone_lan_frame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.IOException;
import java.net.Socket;

import p4u1.dd.phone_lan_frame.PhoneLanFrameGame;
import p4u1.dd.phone_lan_frame.network.BroadcastClient;
import p4u1.dd.phone_lan_frame.network.BroadcastServer;
import p4u1.dd.phone_lan_frame.network.ClientMessages;
import p4u1.dd.phone_lan_frame.network.ClientWorkhorse;
import p4u1.dd.phone_lan_frame.network.PhotoLanServer;
import p4u1.dd.phone_lan_frame.network.ServerMessages;

/**
 * Created by Paul on 1/10/2017.
 */

public class ConnectionScreen implements Screen{

    public static int port = 6969;
    OrthographicCamera camera;
    PhoneLanFrameGame game;
    SpriteBatch batch;
    Texture clock;

    boolean is_waiting = true;
    boolean searching = false;
    boolean amServer;
    BroadcastClient broadcastClient;
    long searchStart;
    int lineHeight;

    public ConnectionScreen (PhoneLanFrameGame arg0) {
        this.game = arg0;
        batch = new SpriteBatch();
        clock = new Texture("img/clock.png");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);
    }

    @Override
    public void show() {
        findServer();
    }

    public void findServer() {
        searching = true;
        broadcastClient = new BroadcastClient();
        new Thread(broadcastClient).start();
        searchStart = System.currentTimeMillis();
        Gdx.app.log("phlusko", "Finding Server");
    }

    @Override
    public void render(float delta) {
        logic();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(0, 0, 0, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(clock, 256, 176);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public void logic () {
        if (searching) {
            long currentTime = System.currentTimeMillis();
            //Gdx.app.log("phlusko", "No Servers Found . " + (currentTime - searchStart));
            if (currentTime - searchStart > 5000) {
                broadcastClient.stopSearch();
                Gdx.app.log("phlusko", "No Servers Found");
                searching = false;
                searchStart = 0;
                Thread thread = new Thread(new BroadcastServer());
                thread.start();
                game.setGameServer(new PhotoLanServer());
                if (game.getServer().serverStarted) {
                    amServer = true;
                    Gdx.app.log("phlusko", "Start Server");
                    game.setScreen(new PhotoLanScreen(game, new ServerMessages(game.server)));
                    //game.setScreen(new p4u1.dd.phone_lan_frame.demos.Blah());
                    searching = false;
                    amServer = true;
                    is_waiting = true;
                } else {
                    searching = true;
                    searchStart = System.currentTimeMillis();
                }
            } else {
                String serverIP = broadcastClient.getServerIP();
                if (serverIP != null) {
                    //Gdx.app.log("phlusko", "Found Server at: " + serverIP);
                    searchStart = 0;
                    try {
                        game.client = new ClientWorkhorse(new Socket(serverIP, port));
                        new Thread(game.client).start();
                        game.setScreen(new PhotoLanScreen(game, new ClientMessages(game.client)));
                        searching = false;
                        amServer = false;
                    } catch (IOException e) {
                        //e.printStackTrace();
                        Gdx.app.log("phlusko", "Bad ju ju");
                    }
                }
            }
        }
    }
}
