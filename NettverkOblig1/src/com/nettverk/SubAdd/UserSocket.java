package com.nettverk.SubAdd;

import java.io.*;
import java.net.*;
import java.util.Scanner;

class UserSocket {
    public static void main(String[] args) throws IOException {
        final int PORTNR = 55565;
        PrintWriter skriveren = null;
        BufferedReader leseren = null;
        Socket forbindelse = null;
        try {
            /* Bruker en scanner til å lese fra kommandovinduet */
            Scanner leserFraKommandovindu = new Scanner(System.in);
            System.out.print("Oppgi navnet på maskinen der tjenerprogrammet kjører: ");
            String tjenermaskin = leserFraKommandovindu.nextLine();

            /* Setter opp forbindelsen til tjenerprogrammet */
            forbindelse = new Socket(tjenermaskin, PORTNR);
            System.out.println("Nå er forbindelsen opprettet.");

            /* Åpner en forbindelse for kommunikasjon med tjenerprogrammet */
            InputStreamReader leseforbindelse = new InputStreamReader(forbindelse.getInputStream());
            leseren = new BufferedReader(leseforbindelse);
            skriveren = new PrintWriter(forbindelse.getOutputStream(), true);

            /* Leser 4 innledninger fra tjeneren og skriver den til kommandovinduet */
            System.out.println( leseren.readLine()+ "\n" + leseren.readLine()+"\n"+leseren.readLine()+ "\n" + leseren.readLine());

            /* Leser tekst fra kommandovinduet (brukeren) */
            String enLinje = leserFraKommandovindu.nextLine();
            while (!enLinje.equals("exit")) {
                skriveren.println(enLinje);  // sender teksten til tjeneren
                String respons = leseren.readLine();  // mottar respons fra tjeneren
                System.out.println("Fra tjenerprogrammet: " + respons);
                enLinje = leserFraKommandovindu.nextLine();
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            /* Lukker forbindelsen */
            leseren.close();
            skriveren.close();
            forbindelse.close();
        }

    }
}

