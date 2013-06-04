package aufgabe4;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: denisfleischhauer
 * Date: 04.06.13
 * Time: 12:28
 * To change this template use File | Settings | File Templates.
 */
public class Station extends Thread {
    private Connection connection;
    private Nutzdaten nutzdatenEmpfaenger;
    private Empfaenger empfaenger;
    private char stationsKlasse;
    private int sendeSlot;

    public Station(Connection server, Empfaenger empfaenger, char stationsKlasse) {
        this.stationsKlasse = stationsKlasse;
        this.connection = server;
        this.empfaenger = empfaenger;
        this.nutzdatenEmpfaenger = new Nutzdaten();
        this.sendeSlot = Integer.MIN_VALUE;
    }

    public void run() {
        try {
            //Warten bis zum Frameanfang 1000ms - ((aktuelle Zeit + Abweichung) % 1000ms) - Zeit fuer das Wecken
            sleep(1000 - (System.currentTimeMillis() % 1000) + empfaenger.getAbweichung());
            // und erstes Frame nur hoeren
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {

            if (sendeSlot == Integer.MIN_VALUE) {
                sendeSlot = empfaenger.getFreienSlot();
            }

            long abweichung = empfaenger.getAbweichung();

            Nachricht nachricht = new Nachricht(new byte[34]);
            nachricht.setNutzdaten(nutzdatenEmpfaenger.getNutzdaten());
            nachricht.setStationsKlasse(stationsKlasse);

            try {
                // Zeit bis zum Senden Schlafen
                sleep(sendeSlot * 40 + abweichung + 20);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            nachricht.setSendezeitpunkt(System.currentTimeMillis() + abweichung);

            Integer freierSlot = empfaenger.getFreienSlot();
            nachricht.setReservierterSlot(freierSlot);

            if (freierSlot == Integer.MIN_VALUE) {
                // keine Slots im aktuellen Frame verfuegbar, ueberspringen
                continue;
            }
            connection.send(nachricht.getBytes());
            DataSink.gibAus(nachricht.toString("<Station> gesendet"));

            try {
                // Restzeit des Frames schlafen
                sleep(1000 - (System.currentTimeMillis() % 1000) + abweichung);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            sendeSlot = freierSlot;
        }
    }

    public static void main(String... args) {
        try {
            Connection connection1 = new Connection(args[0], args[1], Integer.parseInt(args[2]));
            Connection connection2 = new Connection(args[0], args[1], Integer.parseInt(args[2]));

            new Station(connection1, new Empfaenger(connection2), args[3].charAt(0)).start();

            System.out.println("Station gestartet mit den Parametern: ");
            System.out.println("Netzwerkkarte " + args[0] + " Multicastadresse: " + args[1] + " Multicastport " + args[2] + " Klasse: " + args[3]);
        } catch (Exception e) {
            System.out.println("Uebergebene Prarameter sind falsch!!!");
        }
    }
}
