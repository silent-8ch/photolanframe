package p4u1.dd.phone_lan_frame.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Paul on 12/20/2016.
 */

public class BroadcastServer implements Runnable {
    @Override
    public void run() {
        DatagramSocket socket;
        try {
            socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);
            while (true) {
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);
                String message = new String(packet.getData()).trim();
                if (message.equals("DISCOVER_PHLUSKOSERVER_REQUEST")) {
                    byte[] sendData = "DISCOVER_PHLUSKOSERVER_RESPONSE".getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException ex) {
        }
    }
}
