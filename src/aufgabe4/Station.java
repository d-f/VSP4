package aufgabe4;

import java.io.IOException;

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

    public Station(Connection server, char stationsKlasse, Integer gesamtAbweichung) {
        this.stationsKlasse = stationsKlasse;
        this.connection = server;
        this.empfaenger = new Empfaenger(connection, gesamtAbweichung);
        this.nutzdatenEmpfaenger = new Nutzdaten();
        this.sendeSlot = 0;
    }

    public void run() {
        initialisierung();

        while (true) {
            Nachricht nachricht = new Nachricht(new byte[34]);
            nachricht.setNutzdaten(nutzdatenEmpfaenger.getNutzdaten());
            //nachricht.setNutzdaten("team 6+99".getBytes());
            nachricht.setStationsKlasse(stationsKlasse);

            try {
                // Zeit bis zum Senden Schlafen
                sleep(sendeSlot * 40 + 20);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            Integer freierSlot = empfaenger.getFreienSlot();

            if (empfaenger.isKollision()) {

                sendeSlot = empfaenger.getFreienSlot();

            }

            nachricht.setSendezeitpunkt(empfaenger.getZeit());


            //System.out.println("---" + freierSlot);
            nachricht.setReservierterSlot(freierSlot);
            empfaenger.setBelegteSlots(freierSlot);
            connection.send(nachricht.getBytes());
            try {
                // Restzeit des Frames schlafen
                sleep(1000 - (empfaenger.synchrinisierteZeit() % 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            sendeSlot = freierSlot;
        }
    }

    private void initialisierung() {
        try {
            System.out.println("Initialisierung");
            //Warten bis zum Frameanfang 1000ms - ((aktuelle Zeit + Abweichung) % 1000ms) - Zeit fuer das Wecken
            sleep(1000 - (empfaenger.synchrinisierteZeit() % 1000));
            // und erstes Frame nur hoeren
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String... args) {
        try {
            Connection connection = new Connection(args[0], args[1], Integer.parseInt(args[2]));

            new Station(connection, args[3].charAt(0), Integer.parseInt(args[4])).start();

            System.out.println("Station gestartet mit den Parametern: ");
            String msg = "Netzwerkkarte " + args[0] + " Multicastadresse: " + args[1] + " Multicastport " + args[2] + " Klasse: " + args[3];
            System.out.println(msg);
        } catch (Exception e) {
            System.out.println("Uebergebene Prarameter sind falsch!!!");
            System.exit(0);
        }
    }
}
