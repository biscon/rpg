package dk.bison.rpg.net;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class UDPClient implements Runnable {
    public static final String TAG = UDPClient.class.getSimpleName();
    DatagramSocket c = null;

    private void connect() {
        try {
            c = new DatagramSocket();
            c.setBroadcast(true);
            byte[] sendData = "DISCOVER_RPGSERVER_REQUEST".getBytes();

            try {

                byte[] buf = new byte[256];
                InetAddress ip = InetAddress.getByName("255.255.255.255");
                DatagramPacket packet = new DatagramPacket(sendData, sendData.length, ip, 8888);
                c.send(packet);
                Log.e(TAG, ": Request packet sent to: 255.255.255.255 (DEFAULT)");

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Broadcast the message over all the network interfaces
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }

                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                        c.send(sendPacket);
                    } catch (Exception e) {
                    }

                    Log.e(TAG, ": Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }

            Log.e(TAG, ": Done looping over all network interfaces. Now waiting for a reply!");

            //Wait for a response
            byte[] recvBuf = new byte[256];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            c.receive(receivePacket);

            //We have a response
            Log.e(TAG, ": Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

            //Check if the message is correct
            String message = new String(receivePacket.getData()).trim();
            if (message.equals("DISCOVER_FUIFSERVER_RESPONSE")) {
                //DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)
                Log.e(TAG, "Server's IP: " + receivePacket.getAddress());
            }

            //Close the port!
            c.close();
        } catch (IOException ex) {
            // Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        connect();
    }
}