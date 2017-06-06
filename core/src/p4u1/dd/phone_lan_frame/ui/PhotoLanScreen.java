package p4u1.dd.phone_lan_frame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import p4u1.dd.phone_lan_frame.PhoneLanFrameGame;
import p4u1.dd.phone_lan_frame.network.MessageAdapter;

/**
 * Created by Paul on 1/10/2017.
 */

public class PhotoLanScreen implements Screen {
    int position = -1;
    int lineHeight = -1;
    int maxHeight;
    int offset = 0;
    float lineStart;
    boolean updated = false;
    Vector2 worldoffset;
    MessageAdapter message_adapter;
    PhoneLanFrameGame game;
    SpriteBatch batch;
    OrthographicCamera camera;
    ShapeRenderer shape;
    Texture photoTexture;
    Sprite photoSprite;

    public PhotoLanScreen(PhoneLanFrameGame arg1, MessageAdapter arg0) {
        this.message_adapter = arg0;
        shape = new ShapeRenderer();
        game = arg1;
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        photoTexture = new Texture(Gdx.files.internal("img/photo.png"));
        photoSprite = new Sprite(photoTexture);
    }
    @Override
    public void show() {
        JSONObject data = new JSONObject();
        try {
            data.put("type", "partyInfoRequest");
            data.put("order", position);
            message_adapter.sendMessage(data.toString());
        } catch (JSONException e) {
        }
    }

    @Override
    public void render(float delta) {
        logic();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shape.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(0, 0, 0, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (position > -1) {
            batch.begin();
            game.font.setColor(Color.WHITE);
            game.font.draw(batch, position + "", 20, 120);
            batch.end();
        }
        if (lineHeight > -1) {
            shape.begin(ShapeRenderer.ShapeType.Line);
            float lineActual = lineHeight - (position * 600);
            shape.setColor(Color.WHITE);
            shape.line(0, lineActual, 800, lineActual);
            shape.end();
        }
        batch.begin();
        if (updated) {
                photoSprite.setPosition(0 - worldoffset.x, 0 - worldoffset.y);
                photoSprite.draw(batch);
        }
        batch.end();
    }

    public void logic() {
        if (message_adapter.getMessageHolder().getListSize() > 0) {
            processMessage(message_adapter.getMessageHolder().popMessage());
        }
        if (lineHeight > -1) {
            lineHeight = (((int) System.currentTimeMillis() - (int) lineStart) + offset) % maxHeight;
        }
        if (Gdx.input.justTouched()) {
            JSONObject response = new JSONObject();
            try {
                response.put("type", "gotoOrientation");
                message_adapter.sendMessage(response.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            game.setScreen(new OrientationScreen(game, this));
        }
    }

    public void processMessage(String arg0) {
        Gdx.app.log("phlusko", "getting info back:" + arg0);
        try {
            JSONObject data = null;
            data = new JSONObject(arg0);
            String type = data.getString("type");
            if (type.contentEquals("getPosition")) {
                position = data.getInt("position");
                message_adapter.setOrder(position);
            }
            if (type.contentEquals("linePosition")) {
                this.offset = data.getInt("position");
                lineStart =   System.currentTimeMillis();
            }
            if (type.contentEquals("startLine")) {
                this.lineHeight = 0;
                this.maxHeight = data.getInt("maxHeight");
                lineStart =   System.currentTimeMillis();
            }
            if (type.contentEquals("ping")) {
                float timestamp = (float)data.getLong("timestamp");
                JSONObject pong = new JSONObject();
                pong.put("type", "pong");
                pong.put("timestamp", timestamp);
                this.message_adapter.sendMessage(pong.toString());
            }

            if (type.contentEquals("partyInfo")) {
                updateWindow(data);
            }
            if (type.contentEquals("gotoOrientation")) {
                game.setScreen(new OrientationScreen(game, this));
            }
        } catch (JSONException e) {
        }
    }



    public void updateWindow(JSONObject arg0) {
        try {
            JSONArray phone_datas = arg0.getJSONArray("phones");
            float worldWidth = arg0.getLong("world_width") * 10;
            float worldHeight = arg0.getLong("world_height") * 10;
            photoSprite.setSize(worldWidth, worldHeight);

            for (int i = 0; i < phone_datas.length(); i++) {
                JSONObject currPhone = phone_datas.getJSONObject(i);
                int order = currPhone.getInt("order");
                if (order == position) {
                    float scale = currPhone.getLong("scale");
                    float x = currPhone.getLong("x");
                    float y = currPhone.getLong("y");
                    float screenWidth  = 800 * (100 / scale);
                    float screenHeight = 600 * (100 / scale);
                    camera.setToOrtho(false, screenWidth, screenHeight);
                    updated = true;
                    worldoffset = new Vector2(x * 10, y * 10);
                }
            }
        } catch (JSONException e) {
        }
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
    }
}
