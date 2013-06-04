package aufgabe4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: denisfleischhauer
 * Date: 04.06.13
 * Time: 12:44
 * To change this template use File | Settings | File Templates.
 */
public class Empfaenger extends Thread{
    private Connection connection;

    private long aktuelleFrameNummer;
    private long aktuelleSlotNummer;

    private boolean[] freieSlots;
    private long abweichung;

    public Empfaenger(Connection connection) {
        this.connection = connection;
        this.freieSlots = new boolean[25];
        this.aktuelleFrameNummer = System.currentTimeMillis() / 1000;
        this.aktuelleSlotNummer = 0;
        Arrays.fill(freieSlots, true);
        this.abweichung = 0;
        start();
    }

    public void run() {
        while (true){
            //byte[] msg = connection.receive();
            Nachricht nachricht = new Nachricht(connection.receive());

            // freie Slots fuer Reservierung zuruecksetzen wenn Frame zuende
            if (aktuelleFrameNummer < (System.currentTimeMillis() + abweichung / 1000)){
                Arrays.fill(freieSlots, true);
            }

            long empfangszeitpunkt = System.currentTimeMillis() + abweichung;

            // wenn gleicher Frame
            if (aktuelleFrameNummer == empfangszeitpunkt / 1000){
                // und Slot
                if (aktuelleSlotNummer == (empfangszeitpunkt % 1000) / 40){
                    // dann Kollision
                    DataSink.gibAus("kollision im Frame: " + aktuelleFrameNummer + " Slot: " + aktuelleSlotNummer);
                    // Nachricht nicht auswerten und Rest ueberspringen
                    continue;
                }
            }

            aktuelleFrameNummer = System.currentTimeMillis() + abweichung / 1000;
            aktuelleSlotNummer = ((System.currentTimeMillis() + abweichung) % 1000) / 40;

            // Akktualisierung der Abweichung wenn Nachricht von Station A
            if (nachricht.getStationsKlasse() == 'A'){
                abweichung = ((System.currentTimeMillis() - nachricht.getSendezeit())  + abweichung) / 2;
            }

            freieSlots[nachricht.getReserviertenSlot()] = false;

            DataSink.gibAus(nachricht.toString("<Empfaenger> emfpangen"));
        }
    }

    public long getAbweichung(){
        return abweichung;
    }

    public int getFreienSlot() {
        LinkedList<Integer> liste = new LinkedList<Integer>();
        for(int i = 0; i < freieSlots.length; i++) {
            if (freieSlots[i]) {
                liste.add(1);
            }
        }

        if (liste.isEmpty()){
            return Integer.MIN_VALUE;
        }

        Random random = new Random();
        return random.nextInt(liste.size());
    }
}
