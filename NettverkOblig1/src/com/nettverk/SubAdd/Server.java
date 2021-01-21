package com.nettverk.SubAdd;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;

class Server {
    public static void main(String[] args) throws IOException {
        final int PORTNR = 55565;
        boolean running = true;
        PrintWriter skriveren = null;
        BufferedReader leseren = null;
        Socket forbindelse = null;

        try {
            ServerSocket server = new ServerSocket(PORTNR);

            System.out.println(LocalDateTime.now());
            forbindelse = server.accept();  // venter inntil noen tar kontakt
            System.out.println("Bruker "+ forbindelse.getLocalSocketAddress()+" er tilkoblet");

            /* Åpner strømmer for kommunikasjon med klientprogrammet */
            InputStreamReader leseforbindelse = new InputStreamReader(forbindelse.getInputStream());
            leseren = new BufferedReader(leseforbindelse);
            skriveren = new PrintWriter(forbindelse.getOutputStream(), true);

            /* Sender innledning til klienten */
            skriveren.println("Hei, du har kontakt med tjenersiden!");
            skriveren.println("Skriv et + eller - regnestykke \nSkriv 'exit' for å avslutte\nAvslutt med linjeskift.");

            /* Mottar data fra klienten */
            String enLinje = leseren.readLine();  // mottar en linje med tekst
            int buf = 0;
            while (enLinje != null && running) {  // forbindelsen på klientsiden er lukket
                if(enLinje.equals("exit")) {
                    System.out.println("Avslutter forbindelsen");
                    running = false;
                }
                buf = calculate(enLinje);
                System.out.println("En klient skrev: " + enLinje);
                System.out.println("Respons som sendes er '"+buf+"'");
                skriveren.println(enLinje+" = "+ buf);// sender svar til klienten
                enLinje = leseren.readLine();
            }


        } catch (Exception e){
            e.printStackTrace();
        } finally {
            /* Lukker forbindelsen */
            leseren.close();
            skriveren.close();
            forbindelse.close();
        }

    }

    // calculates + and - from a string
    static int calculate(String s) {
        String operators[] = s.split("[0-9]+");
        String operands[] = s.split("[+-]");
        int agregate = Integer.parseInt(operands[0]);
        for (int i = 1; i < operands.length; i++) {
            if (operators[i].equals("+"))
                agregate += Integer.parseInt(operands[i]);
            else
                agregate -= Integer.parseInt(operands[i]);
        }
        return agregate;
    }
}


