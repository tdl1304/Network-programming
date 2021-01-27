package udp;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

class UDPServer {
    public static void main(String[] args) throws IOException {
        final int PORTNR = 55565;
        boolean running = true;
        PrintWriter skriveren = null;
        BufferedReader leseren = null;
        DatagramPacket dataRecieved;
        DatagramSocket server = null;

        try {
            server = new DatagramSocket(PORTNR);
            while(running) {
                byte[] buffer = new byte[32];
                dataRecieved = new DatagramPacket(buffer, 0, buffer.length); // set to recieve 32 bytes of data
                server.receive(dataRecieved);// venter inntil en pakke blir mottatt
                System.out.println(LocalDateTime.now());
                System.out.println(dataRecieved.getData());

                /* Mottar data fra klienten */
                String enLinje = "null";// mottar en linje med tekst
                int buf = 0;
                while (enLinje != null && running) {  // forbindelsen på klientsiden er lukket
                    if (enLinje.equals("exit")) {
                        System.out.println("Avslutter forbindelsen");
                        running = false;
                    }
                    buf = calculate(enLinje);
                    System.out.println("En klient skrev: " + enLinje);
                    if (buf == Integer.MAX_VALUE) {
                        System.out.println("Respons som sendes er: ugyldig format");
                        skriveren.println("Ugyldig format");// sender svar til klienten
                    } else {
                        System.out.println("Respons som sendes er '" + buf + "'");
                        skriveren.println(enLinje + " = " + buf);// sender svar til klienten
                    }
                    enLinje = leseren.readLine();
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
}



