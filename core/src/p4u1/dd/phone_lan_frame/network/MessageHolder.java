package p4u1.dd.phone_lan_frame.network;

import java.util.ArrayList;

/**
 * Created by Paul on 12/30/2016.
 */

public class MessageHolder {
    volatile ArrayList<String> messages = new ArrayList<String>();

    public synchronized int getListSize() {
        return messages.size();
    }

    public synchronized void addMessage(String arg0) {
        messages.add(arg0);
    }

    public synchronized String popMessage() {
        if (messages.size() > 0) {
            return messages.remove(0);
        } else {
            return "";
        }
    }
}
