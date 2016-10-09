package dk.bison.rpg.net.server;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DiscoveryThread implements Runnable {
  public static final String TAG = DiscoveryThread.class.getSimpleName();
  DatagramSocket socket;

  @Override
  public void run() {
    try {
      //Keep a socket open to listen to all the UDP trafic that is destined for this port
      socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
      socket.setBroadcast(true);

      while (true) {
        Log.e(TAG, ">>>Ready to receive broadcast packets!");

        //Receive a packet
        byte[] recvBuf = new byte[256];
        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
        socket.receive(packet);

        //Packet received
        Log.e(TAG,  ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
        Log.e(TAG, ">>>Packet received; data: " + new String(packet.getData()));

        //See if the packet holds the right command (message)
        String message = new String(packet.getData()).trim();
        if (message.equals("DISCOVER_RPGSERVER_REQUEST")) {
          byte[] sendData = "DISCOVER_RPGSERVER_RESPONSE".getBytes();

          //Send a response
          DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
          socket.send(sendPacket);

          Log.e(TAG, ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
        }
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public static DiscoveryThread getInstance() {
    return DiscoveryThreadHolder.INSTANCE;
  }

  private static class DiscoveryThreadHolder {

    private static final DiscoveryThread INSTANCE = new DiscoveryThread();
  }

}