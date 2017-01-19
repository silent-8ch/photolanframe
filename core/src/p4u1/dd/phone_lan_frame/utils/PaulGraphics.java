package p4u1.dd.phone_lan_frame.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Paul on 12/27/2016.
 */

public class PaulGraphics {

    public static float width = Gdx.graphics.getWidth();
    public static float height = Gdx.graphics.getHeight();
    public static final float GAME_WIDTH = 800f;
    public static final float GAME_HEIGHT = 600f;

    static public Vector2 pixelToCoord(Vector2 arg0) {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        //Gdx.app.log("phlusko", "touch : " + width+ "," + height);
        //Gdx.app.log("phlusko", "touch : " + GAME_WIDTH+ "," + GAME_HEIGHT);
        float x = (arg0.x * GAME_WIDTH) / width;
        float y = GAME_HEIGHT - (((arg0.y) * GAME_HEIGHT) / height);

        return new Vector2(x, y);
    }
    static public Vector2 pixelToCoord(float arg0, float arg1) {
        return PaulGraphics.pixelToCoord(new Vector2(arg0, arg1));
    }

    static public Vector2 pixelToCoord(int arg0, int arg1) {
        return PaulGraphics.pixelToCoord(new Vector2(arg0, arg1));
    }

    static public float getAngleFromVectors(Vector2 arg0, Vector2 center) {
        Vector2 ray = center.cpy().sub(arg0);
        ray = ray.nor();
        double angle = Math.acos(ray.y);
        angle = Math.toDegrees(angle);
        if (arg0.x < center.x) {
            angle = 360 - angle;
        }
        return (float)Math.floor(angle);
    }

    static public float getAngleFromArcLength(float length, float radius) {
        float circ = 2*((float)Math.PI)*radius;
        float angle = 360 * (length / circ);
        return angle;
    }

    static public Vector2 getOrtho(Vector2 arg0, Vector2 arg1){
        Vector2 ray = arg0.cpy().sub(arg1);
        ray.scl(1/ray.len());
        Vector2 ortho = new Vector2(-ray.y, ray.x);
        return ortho;
    }
}
