package aufgabe4;

import java.io.IOException;
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
    private long alteFrameNummer;
    private long aktuelleSlotNummer;
    private long alteSlotNummer;

    private boolean[] freieSlots;
    private long abweichung;

    public Empfaenger(Connection connection) {
        this.connection = connection;

        this.aktuelleFrameNummer = System.currentTimeMillis() / 1000;
        this.alteFrameNummer =  System.currentTimeMillis() / 1000;

        this.aktuelleSlotNummer = 0;
        this.alteSlotNummer = 0;

        this.abweichung = 0;

        this.freieSlots = new boolean[25];
        Arrays.fill(freieSlots, true);
        start();
    }

    public void run() {
        while (true){
            Nachricht nachricht = new Nachricht(connection.receive());

            aktuelleFrameNummer = (System.currentTimeMillis() + abweichung) / 1000;
            aktuelleSlotNummer = ((System.currentTimeMillis() + abweichung) % 1000) / 40;

            // freie Slots fuer Reservierung zuruecksetzen wenn Frame zuende
            if (alteFrameNummer < aktuelleFrameNummer){
                Arrays.fill(freieSlots, true);
            }

            long empfangszeitpunkt = System.currentTimeMillis() + abweichung;

            // wenn gleicher Frame
            if (alteFrameNummer == aktuelleFrameNummer){
                // und Slot
                if (alteSlotNummer == aktuelleSlotNummer){
                    // dann Kollision
                    //DataSink.gibAus("--kollision im Frame: " + aktuelleFrameNummer%1000 + " Slot: " + aktuelleSlotNummer + nachricht.toString(""));
                    String msg = "--kollision im Frame: " + aktuelleFrameNummer%1000 + " Slot: " + aktuelleSlotNummer + nachricht.toString("");
                    try {
                        System.out.write(msg.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    // Nachricht nicht auswerten und Rest ueberspringen
                    continue;
                }
            }

            freieSlots[nachricht.getReserviertenSlot()] = false;

            // Akktualisierung der Abweichung wenn Nachricht von Station A
            if (nachricht.getStationsKlasse() == 'A'){
                abweichung = ((System.currentTimeMillis() - nachricht.getSendezeit()) + abweichung) / 2;
            }


            //DataSink.gibAus(nachricht.toString("emfpangen im Frame: " + (aktuelleFrameNummer%1000) + " Slot: " + aktuelleSlotNummer));
            String msg = nachricht.toString("emfpangen im Frame: " + (aktuelleFrameNummer%1000) + " Slot: " + aktuelleSlotNummer);
            try {
                System.out.write(msg.getBytes());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            alteFrameNummer = aktuelleFrameNummer;
            alteSlotNummer = aktuelleSlotNummer;
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
