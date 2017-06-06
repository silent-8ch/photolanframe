package p4u1.dd.phone_lan_frame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import p4u1.dd.phone_lan_frame.PhoneLanFrameGame;
import p4u1.dd.phone_lan_frame.network.MessageAdapter;
import p4u1.dd.phone_lan_frame.utils.PaulGraphics;

import static com.badlogic.gdx.Gdx.*;

/**
 * Created by Paul on 1/13/2017.
 */

public class OrientationScreen implements Screen, GestureDetector.GestureListener, InputProcessor{
    MessageAdapter messageAdapter;
    int partySize = 0;
    PhoneLanFrameGame game;
    ShapeRenderer shape;
    SpriteBatch batch;
    OrthographicCamera camera;
    boolean pinching = false;
    float pinch_ratio;
    float scale = 100;
    float temp_float = -1;
    ArrayList<PhoneIcon> phones = new ArrayList<PhoneIcon>();
    Vector2 firstTouch;
    PhotoLanScreen previous;
    float left, right, top, bottom;
    boolean noBounds = true;
    Vector2 worldCorner = new Vector2();
    Vector2 oldCorner;

    public OrientationScreen(PhoneLanFrameGame arg0, PhotoLanScreen arg1) {
        previous = arg1;
        Gdx.input.setCatchBackKey(true);
        messageAdapter = arg1.message_adapter;
        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        game = arg0;
        requestPartyInfo();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        GestureDetector gd = new GestureDetector(this);
        InputMultiplexer multiplex = new InputMultiplexer();
        multiplex.addProcessor(this);
        multiplex.addProcessor(gd);
        Gdx.input.setInputProcessor(multiplex);
    }

    @Override
    public void show() {
    }
    @Override
    public void render(float delta) {
        logic();
        shape.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        PhoneActor.setShape(shape);

        gl.glClearColor(0, 0, 255, 255);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        game.font.setColor(Color.WHITE);
        game.font.draw(batch, messageAdapter.getOrder() + "", 20, 120);
        batch.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.YELLOW);
        float half_scale = scale / 2f;
        shape.rect(400 - half_scale, 550, scale, 50);
        shape.rect(400 - half_scale, 0, scale, 50);
        shape.rect(0, 300 - half_scale, 50, scale);
        shape.rect(750, 300 - half_scale, 50, scale);
        shape.end();

        if (!noBounds) {
            shape.begin(ShapeRenderer.ShapeType.Line);
            shape.setColor(Color.GREEN);
            shape.rect(left, bottom, right - left, top - bottom);
            shape.end();
        }
        drawPhones();
    }

    @Override
    public void resize(int width, int height) {
    }

    public void drawPhones() {
        for (Iterator<PhoneIcon> i = phones.iterator(); i.hasNext();) {
            PhoneIcon curr = i.next();
            curr.drawMe(shape);
        }
    }

    public void logic() {
        setBounds();
        processMessages();
    }

    public void setBounds() {
        noBounds = true;
        for(Iterator<PhoneIcon> i = phones.iterator(); i.hasNext();) {
            PhoneIcon curr = i.next();
            if (noBounds || curr.loc.x < left) {
                left = curr.loc.x;
            }
            if (noBounds || curr.loc.x + curr.width  > right) {
                right = curr.loc.x + curr.width;
            }
            if (noBounds || curr.loc.y  < bottom) {
                bottom = curr.loc.y;
            }
            if (noBounds || curr.loc.y + curr.height  > top) {
                top = curr.loc.y + curr.height;
            }
            noBounds = false;
        }
        worldCorner.x = left;
        worldCorner.y = bottom;
    }

    public void requestPartyInfo() {
        try {
            JSONObject data = null;
            data = new JSONObject();
            data.put("type", "partyInfoRequest");
            messageAdapter.sendMessage(data.toString());
        } catch (JSONException e) {
        }
    }

    public void processMessages() {
        try {
            if (messageAdapter.getMessageHolder().getListSize() > 0) {
                String message = messageAdapter.getMessageHolder().popMessage();
                app.log("phlusko", "OrientationScreen got message: " + message);
                JSONObject data = new JSONObject(message);
                String type = data.getString("type");
                if (type.contentEquals("partyInfo")){
                    partySize = data.getInt("party_size");
                    updateParty(data);
                }
                if (type.contentEquals("gotoPhoto")){
                    game.setScreen(previous);
                }
            }
        } catch (JSONException e) {
        }
    }

    public void updateParty(JSONObject arg0) {
        try {
            phones = new ArrayList<PhoneIcon>();
            JSONArray phone_datas = arg0.getJSONArray("phones");
            float worldWidth = arg0.getLong("world_width");
            float worldHeight = arg0.getLong("world_height");
            Vector2 corner = getCorner(new Vector2(worldWidth, worldHeight));
            for (int i = 0; i < phone_datas.length(); i++) {
                JSONObject currPhone = phone_datas.getJSONObject(i);
                PhoneIcon temp = new PhoneIcon();
                float width = currPhone.getLong("width");
                float height = currPhone.getLong("height");
                int order = currPhone.getInt("order");
                float scale = currPhone.getLong("scale");
                float x = currPhone.getLong("x") + corner.x;
                float y = currPhone.getLong("y") + corner.y;
                temp.loc = new Vector2(x, y);
                temp.width = width;
                temp.height = height;
                temp.order = order;
                phones.add(temp);
            }
        } catch (JSONException e) {
        }
    }

    public Vector2 getCorner(Vector2 arg0) {
        float x = (PaulGraphics.GAME_WIDTH / 2) - (arg0.x / 2);
        float y = (PaulGraphics.GAME_HEIGHT / 2) - (arg0.y / 2);
        Vector2 corner = new Vector2(x, y);
        return corner;
    }

    public void updateParty() {
        phones = new ArrayList<PhoneIcon>();
        for (int i = 0; i < partySize; i++) {
            PhoneIcon temp = new PhoneIcon();
            temp.setLoc(400, 400 - (i * 90));
            phones.add(temp);
        }
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

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    public void touchPhones(float arg0, float arg1) {
        for (int i = 0; i < phones.size(); i++) {
            phones.get(i).getTouched(arg0, arg1);
        }
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        pinching = true;
        pinch_ratio = pointer1.dst(pointer2) / initialPointer1.dst(initialPointer2);
        if (temp_float == -1) {
            temp_float = scale;
        }
        scale = temp_float * pinch_ratio;
        Gdx.app.log("phlusko", "scale: " + scale);
        return false;
    }

    public void sendScale() {
        JSONObject data = new JSONObject();
        try {
            data.put("type", "playerScale");
            data.put("order", messageAdapter.getOrder());
            data.put("scale", scale);
            messageAdapter.sendMessage(data.toString());
        } catch (JSONException e) {
        }
    }

    @Override
    public void pinchStop() {
        sendScale();
        temp_float = -1;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.BACKSPACE) {
            JSONObject response = new JSONObject();
            try {
                response.put("type", "gotoPhoto");
                messageAdapter.sendMessage(response.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            game.setScreen(previous);
        }
        if (keycode == Input.Keys.UP) {
            scale += 5;
            sendScale();
        }
        if (keycode == Input.Keys.DOWN) {
            scale -= 5;
            sendScale();
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 touch = PaulGraphics.pixelToCoord(screenX, screenY);
        touchPhones(touch.x, touch.y);
        oldCorner = worldCorner.cpy();
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (Iterator<PhoneIcon> i = phones.iterator(); i.hasNext();) {
            PhoneIcon curr = i.next();

            if (curr.touched) {
                sendPhoneMoved(curr.order);
            }

            curr.touched = false;

        }
        return false;
    }

    public void sendPhoneMoved(int arg0) {
        JSONObject response = new JSONObject();
        try {
            response.put("type", "phoneMoved");
            response.put("order", arg0);
            response.put("x", phones.get(arg0).loc.x - oldCorner.x);
            response.put("y", phones.get(arg0).loc.y - oldCorner.y);
            messageAdapter.sendMessage(response.toString());
        } catch (JSONException e) {
        }

    }
    Vector2 firstLoc;

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector2 spot = PaulGraphics.pixelToCoord(screenX, screenY);
        for (Iterator<PhoneIcon> i = phones.iterator(); i.hasNext();) {
            PhoneIcon curr = i.next();
            if (curr.touched) {
                Vector2 delta = new Vector2(spot.x - firstTouch.x, spot.y - firstTouch.y);
                curr.loc = spot.cpy();
            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    private class PhoneIcon {
        boolean flipped = false;
        Vector2 loc;
        Color color = Color.WHITE;
        float width = 80;
        float height = 60;
        long startTouch;
        public boolean touched = false;
        public int order = 0;

        public PhoneIcon() {
            loc = new Vector2(0f, 0f);
        }

        public void drawMe(ShapeRenderer arg0) {
            arg0.begin(ShapeRenderer.ShapeType.Line);
            arg0.setColor(color);
            arg0.rect(loc.x, loc.y, width, height);
            arg0.end();
            batch.begin();
            game.smallfont.setColor(Color.WHITE);
            game.smallfont.draw(batch, order + "", loc.x + 10, loc.y + 50);
            batch.end();
        }

        public void flip() {
            flipped = !flipped;
            float temp_float = new Float(width);
            width = height;
            height = temp_float;

        }

        public void setLoc(float x, float y) {
            loc = new Vector2(x, y);
        }

        public void getTouched (float arg0, float arg1) {
            Vector2 hit = new Vector2(arg0, arg1);
            float right = loc.x + width;
            float left = loc.x;
            float top = loc.y + height;
            float bottom = loc.y;
            if (hit.x < right && hit.x > left && hit.y < top && hit.y > bottom) {
                this.touched = true;
                this.startTouch = TimeUtils.millis();
                firstLoc = loc.cpy();
                firstTouch = PaulGraphics.pixelToCoord(new Vector2(arg0, arg1)).sub(loc);
            }
        }
    }
}
