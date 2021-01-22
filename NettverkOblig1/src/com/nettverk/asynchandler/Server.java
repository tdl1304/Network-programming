package com.nettverk.asynchandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

public class Server {

    public static void main(String[] args) throws IOException {
        final int port = 55566;
        ServerSocket server = new ServerSocket(port);
        System.out.println(LocalDateTime.now());
        while(true) {
            // venter inntil noen tar kontakt
            Socket con = server.accept();
            System.out.println(LocalDateTime.now());
            System.out.println("Ny klient er tilkoblet ip "+con.getInetAddress());
            new ServerThread(con).start();
        }

    }
}

class ServerThread extends Thread {
    Socket serverSocket;
    BufferedReader reader;
    PrintWriter writer;
    boolean running = true;

    public ServerThread(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {


            /* Åpner strømmer for kommunikasjon med klientprogrammet */
            InputStreamReader leseforbindelse = new InputStreamReader(serverSocket.getInputStream());
            reader = new BufferedReader(leseforbindelse);
            writer = new PrintWriter(serverSocket.getOutputStream(), true);

            /* Sender innledning til klienten */
            writer.println("Hei, du har kontakt med tjenersiden!");
            writer.println("Skriv et + eller - regnestykke \nSkriv 'exit' for å avslutte\nAvslutt med linjeskift.");

            /* Mottar data fra klienten */
            String enLinje = reader.readLine();  // mottar en linje med tekst
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
                    writer.println("Ugyldig format");// sender svar til klienten
                } else {
                    System.out.println("Respons som sendes er '" + buf + "'");
                    writer.println(enLinje + " = " + buf);// sender svar til klienten
                }
                enLinje = reader.readLine();
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                /* Lukker forbindelsen */
                reader.close();
                writer.close();
                serverSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
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
