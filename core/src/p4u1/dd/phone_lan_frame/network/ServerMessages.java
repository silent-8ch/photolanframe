package p4u1.dd.phone_lan_frame.network;

/**
 * Created by Paul on 1/10/2017.
 */

public class ServerMessages implements MessageAdapter {
    PhotoLanServer server;
    int order = 0;
    public int type = MessageAdapter.SERVER_MESSAGES;

    public ServerMessages(PhotoLanServer arg0) {
        this.server = arg0;
    }

    @Override
    public MessageHolder getMessageHolder() {
        return this.server.leaderMessages;
    }

    @Override
    public void sendMessage(String arg0) {
        this.server.messageHolder.addMessage(arg0);
    }

    @Override
    public void setOrder(int arg0) {
        order = arg0;
    }

    @Override
    public int getOrder() {
        return order;
    }
}