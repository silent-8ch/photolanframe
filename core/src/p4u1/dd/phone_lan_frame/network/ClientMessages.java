package p4u1.dd.phone_lan_frame.network;

/**
 * Created by Paul on 1/10/2017.
 */

public class ClientMessages implements MessageAdapter {
    ClientWorkhorse client;
    int order = 0;
    public int type = MessageAdapter.CLIENT_MESSAGES;

    public ClientMessages(ClientWorkhorse arg0) {
        client = arg0;
    }
    @Override
    public MessageHolder getMessageHolder() {
        return client.messageHolder;
    }

    @Override
    public void sendMessage(String arg0) {
        client.sendMessage(arg0);
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
