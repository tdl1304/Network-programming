package udp;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;

class UDPServer {


    public static void main(String[] args) throws IOException {
        final int PORTNR = 55565;
        DatagramSocket server = null;

        try {
            server = new DatagramSocket(PORTNR); //Lytteport
            while (true) {
                byte[] buffer = new byte[32];
                DatagramPacket dataRecieved = new DatagramPacket(buffer, buffer.length); // set to recieve 32 bytes of data
                server.receive(dataRecieved);// venter inntil en pakke blir mottatt
                System.out.println(LocalDateTime.now());
                String data = new String(dataRecieved.getData(), 0, dataRecieved.getLength());
                InetAddress address = dataRecieved.getAddress();
                int port = dataRecieved.getPort();
                System.out.println("Mottok '" + data + "' fra klienten " + address.toString() + ':' + port);

                try {
                    // Mottar data fra klienten
                    String enLinje = data;// mottar en linje med tekst
                    int buf = 0;
                    buf = calculate(enLinje);
                    System.out.println("En klient skrev: " + enLinje);
                    if (buf == Integer.MAX_VALUE) {
                        System.out.println("Respons som sendes er: ugyldig format");
                        sendMsg("Ugyldig format", address, port, server);// sender svar til klienten
                    } else {
                        System.out.println("Respons som sendes er '" + buf + "'");
                        // sender svar til klienten
                        sendMsg(enLinje + " = " + buf, address, port, server);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /* Lukker forbindelsen */
            server.close();
        }

    }

    // calculates + and - from a string
    static int calculate(String s) {
        try {
            String[] operators = s.split("[0-9]+");
            String[] operands = s.split("[+-]");
            int agregate = Integer.parseInt(operands[0]);
            for (int i = 1; i < operands.length; i++) {
                if (operators[i].equals("+"))
                    agregate += Integer.parseInt(operands[i]);
                else
                    agregate -= Integer.parseInt(operands[i]);
            }
            return agregate;
        } catch (NumberFormatException ne) {
            System.out.println(ne.getLocalizedMessage() + " sending response to client");
            return Integer.MAX_VALUE;
        }
    }

    static void sendMsg(String s, InetAddress address, int port, DatagramSocket socket) throws IOException {
        byte[] buf = s.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
    }


}



