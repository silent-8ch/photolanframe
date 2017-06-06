package p4u1.dd.phone_lan_frame.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Paul on 1/13/2017.
 */

public class PhoneActor extends Actor {
    public static ShapeRenderer shape;
    boolean flipped = false;
    public PhoneActor() {
        setPosition(0,0);
        setBounds(0,0,80,60);
        setColor(Color.WHITE);

    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();
        if (shape != null) {
            shape.begin(ShapeRenderer.ShapeType.Line);
            shape.setColor(getColor());
            if (flipped) {
                shape.rect(getX(), getY(), 60, 80);

            } else {
                shape.rect(getX(), getY(), 80, 60);
            }
            shape.end();
        }
        batch.begin();
    }

    public static void setShape(ShapeRenderer shape) {
        PhoneActor temp = new PhoneActor();
        temp.shape = shape;
    }
    public void flip() {
        flipped = !flipped;
        if (flipped) {
            setBounds(getX(),getY(), 60, 80);
        } else {
            setBounds(getX(),getY(), 80, 60);
        }
    }
}
