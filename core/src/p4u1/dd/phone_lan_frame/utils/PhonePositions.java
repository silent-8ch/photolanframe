package p4u1.dd.phone_lan_frame.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Paul on 1/14/2017.
 */

public class PhonePositions {

    volatile Vector2 bigDimensions;

    volatile ArrayList<Phone> phones = new ArrayList<Phone>();

    public PhonePositions() {

    }

    public JSONObject getWorldJSON() {

        JSONObject response = new JSONObject();
        try {
            response.put("world_width", bigDimensions.x);
            response.put("world_height", bigDimensions.y);
            JSONArray phone_datas = new JSONArray();
            for(Iterator<Phone> i = phones.iterator(); i.hasNext();) {
                Phone curr = i.next();
                JSONObject phoneData = new JSONObject();
                phoneData.put("order", curr.order);
                phoneData.put("width", curr.dimensions.x);
                phoneData.put("height", curr.dimensions.y);
                phoneData.put("scale", curr.scale);
                phoneData.put("x", curr.loc.x);
                phoneData.put("y", curr.loc.y);
                phone_datas.put(phoneData);
            }
            response.put("phones", phone_datas);
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        return response;
    }

    public void scalePlayer(JSONObject arg0) {
        try {
            int order = arg0.getInt("order");
            float scale = arg0.getLong("scale");
            Phone temp = phones.get(order);
            temp.setScale(scale);
            boundWorld();
        } catch (JSONException e) {
//            e.printStackTrace();
        }
    }

    public void movePhone(JSONObject arg0) {
        try {
            int order = arg0.getInt("order");
            Phone temp = phones.get(order);
            Vector2 newLoc = new Vector2(arg0.getLong("x"), arg0.getLong("y"));
            temp.loc = newLoc;
            boundWorld();
        } catch (JSONException e) {
            //e.printStackTrace();
        }

    }

    public void addPhone() {
        Phone newPhone = new Phone();
        addPhone(newPhone);
    }

    public void addPhone(int arg0) {
        Phone newPhone = new Phone();
        newPhone.order = arg0;
        addPhone(newPhone);
    }

    public void addPhone(Phone arg0) {
        addPhone(arg0.loc, arg0.dimensions, arg0.order);
    }
    public void addPhone(Vector2 arg0, Vector2 arg1, int arg2) {
        Phone newPhone = new Phone(arg0, arg1, arg2);
        phones.add(newPhone);
        Vector2 currentLoc = new Vector2(0,0);
        for(Iterator<Phone> i = phones.iterator(); i.hasNext();) {
            Phone curr = i.next();
            curr.loc = currentLoc.cpy();
            currentLoc.y += curr.dimensions.y + 15;
        }
        boundWorld();
    }

    public void boundWorld() {
        float left = 0 , right = 0, top = 0 , bottom = 0;
        boolean noBounds = true;
        for(Iterator<Phone> i = phones.iterator(); i.hasNext();) {
            Phone curr = i.next();
            if (noBounds || curr.loc.x < left) {
                left = curr.loc.x;
            }
            if (noBounds || curr.loc.x + curr.dimensions.x  > right) {
                right = curr.loc.x + curr.dimensions.x;
            }
            if (noBounds || curr.loc.y  < bottom) {
                bottom = curr.loc.y;
            }
            if (noBounds || curr.loc.y + curr.dimensions.y  > top) {
                top = curr.loc.y + curr.dimensions.y;
            }
            noBounds = false;
        }
        if (left != 0 || bottom != 0) {
            trimCoords(new Vector2(left, bottom));
            boundWorld();
        } else {
            bigDimensions = new Vector2(right - left, top - bottom);
            Gdx.app.log("phlusko", ""+bigDimensions.toString() + " " + right + " " + left + " " + top + " " + bottom);
        }
    }

    public void trimCoords(Vector2 arg0) {
        for(Iterator<Phone> i = phones.iterator(); i.hasNext();) {
            Phone curr = i.next();
            curr.loc.x -= arg0.x;
            curr.loc.y -= arg0.y;
        }
    }

    public class Phone {
        Vector2 loc;
        Vector2 dimensions;
        int order;
        float scale = 100;

        public Phone() {
            order = 0;
            loc = new Vector2(0,0);
            dimensions = new Vector2(80, 60);
        }

        public Phone (Vector2 arg0) {
            this();
            loc = arg0.cpy();
        }

        public Phone (Vector2 arg0, Vector2 arg1) {
            this(arg0);
            dimensions = arg1.cpy();
        }

        public Phone (Vector2 arg0, Vector2 arg1, int arg2) {
            this(arg0, arg1);
            order = arg2;
        }

        public void setScale(float arg0) {
            scale = arg0;
            dimensions.x = 80 * (100 / scale);
            dimensions.y = 60 * (100 / scale);
        }
    }
}
