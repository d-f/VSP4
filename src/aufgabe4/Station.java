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
public class Station extends Thread{
    private Connection connection;
    private InputReader nutzdatenEmpfaenger;
    private Empfaenger empfaenger;
    private char stationsKlasse;
    private int sendeSlot;

    public Station(Connection server, Empfaenger empfaenger, char stationsKlasse) {
        this.stationsKlasse = stationsKlasse;
        this.connection = server;
        this.empfaenger = empfaenger;
        this.nutzdatenEmpfaenger = new InputReader();
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
            Integer freierSlot = empfaenger.getFreienSlot();

            if (sendeSlot == Integer.MIN_VALUE){
                sendeSlot = empfaenger.getFreienSlot();
            }
            long abweichung = empfaenger.getAbweichung();
            byte[] nutzdaten = nutzdatenEmpfaenger.getNutzdaten();

            //byte[] nutzdaten = "team 06-01".getBytes();

            byte[] msg = new byte[34];
            //kopieren von Nutzdaten
            for (int i = 0; i < nutzdaten.length; i++){
                try {
                    msg[i] = nutzdaten[i];
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
            }
            //kopieren von Stationsklasse
            msg[24] = (byte)stationsKlasse;
            msg[25] = freierSlot.byteValue();

            try {
                sleep(sendeSlot * 40 + abweichung + 20);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            Long sendezeitpunkt = System.currentTimeMillis() + abweichung;
            byte[] szp = ByteBuffer.allocate(8).putLong(sendezeitpunkt).array();

            for (int i = 26; i < msg.length; i++){
                msg[i] = szp[i-26];
            }


            connection.send(msg);
            String augabe = ("<Station> sende Nachricht: {" + new String(nutzdaten) + " " + stationsKlasse
                    + " " + freierSlot + " " + sendezeitpunkt + "}");
            sendeSlot = freierSlot;
            DataSink.gibAus(augabe);
            try {
                sleep(1000 - (System.currentTimeMillis() % 1000) + abweichung);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
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
