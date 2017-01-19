package p4u1.dd.phone_lan_frame.network;

/**
 * Created by Paul on 1/10/2017.
 */

public interface MessageAdapter {
    public int type = 0;
    public static int SERVER_MESSAGES = 1;
    public static int CLIENT_MESSAGES = 2;
    abstract MessageHolder getMessageHolder();
    abstract void sendMessage(String arg0);
    abstract void setOrder(int arg0);
    abstract int getOrder();
}
