package p4u1.dd.phone_lan_frame.network;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Paul on 12/20/2016.
 */

public class BroadcastClient implements Runnable{
    volatile DatagramSocket c;
    private volatile String serverIP;

    public String getServerIP() {
        return serverIP;
    }

    public void stopSearch() {
        c.close();
    }

    public void findServer() {
        try {
            c = new DatagramSocket();
            c.setBroadcast(true);
            byte[] sendData = "DISCOVER_PHLUSKOSERVER_REQUEST".getBytes();
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
                c.send(sendPacket);
            } catch (Exception e) {
            }
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                        c.send(sendPacket);
                    } catch (Exception e) {
                    }
                }
            }
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            c.receive(receivePacket);
            String message = new String(receivePacket.getData()).trim();
            if (message.equals("DISCOVER_PHLUSKOSERVER_RESPONSE")) {
                serverIP = receivePacket.getAddress().getHostAddress();
                Gdx.app.log("phlusko", "found server " + serverIP + "");
            }
            c.close();
        } catch (SocketException e) {
        } catch (IOException ex) {
        }
    }

    @Override
    public void run() {
        this.findServer();
    }
}
