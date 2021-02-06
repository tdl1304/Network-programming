package udp;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPClient {
    final static int PORT = 55565;

    public static void main(String[] args) throws IOException {
        InetAddress address;
        DatagramSocket socket = new DatagramSocket(); //random portnr
        address = InetAddress.getByName("localhost");
        String res = null;
        String msg = "";
        Scanner in = new Scanner(System.in);

        while(!msg.equals("exit")) {
            System.out.println("Skriv inn et regnestykke(+ eller -) etterfulgt av linjeskift:");
            msg = in.nextLine();
            res = sendEcho(address, socket, msg);
            System.out.println(res);
        }
        socket.close();
        in.close();
    }

    public static String sendEcho(InetAddress address, DatagramSocket socket, String msg) throws IOException {
        byte[] buf = msg.getBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, PORT);
        socket.send(packet);
        packet = new DatagramPacket(new byte[32], 32);
        socket.receive(packet);
        String received = new String(
                packet.getData(), 0, packet.getLength());
        return received;
    }
}
