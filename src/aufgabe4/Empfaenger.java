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
    private boolean[] freieSlots;
    private long abweichung;

    public Empfaenger(Connection connection) {
        this.connection = connection;
        this.freieSlots = new boolean[25];
        this.aktuelleFrameNummer = System.currentTimeMillis() / 1000;
        Arrays.fill(freieSlots, true);
        this.abweichung = 0;
        start();
    }

    public void run() {
        while (true){
            if ((System.currentTimeMillis() + abweichung / 1000) >= aktuelleFrameNummer){
                Arrays.fill(freieSlots, true);
                aktuelleFrameNummer = (System.currentTimeMillis() + abweichung / 1000);
                DataSink.gibAus("Neuer Frame: " + aktuelleFrameNummer);
            }

            byte[] msg = connection.receive();
            String nachrichtenKopf = new String(getTeilVonByteArray(msg, 0, 10));
            char stationsKlasse = (char)msg[24];
            int reservierterSlot = msg[25] & 0xFF;
            long sendezeitpunkt = ByteBuffer.wrap(msg).getLong(26);


            if (stationsKlasse == 'A'){
                abweichung = ((System.currentTimeMillis() - sendezeitpunkt)  + abweichung) / 2;
            }

            freieSlots[reservierterSlot] = false;

            String ausgabe = "<Empfaenger> Nachricht: {" + nachrichtenKopf.toString() + " " + stationsKlasse + " " + reservierterSlot
                    + " " + sendezeitpunkt + "} empfangen um: " + (System.currentTimeMillis() + abweichung)
                    + " im Slot: " + (((System.currentTimeMillis() + abweichung) % 1000)/40) + " Abweichung:" + abweichung;

            DataSink.gibAus(ausgabe);


        }

    }

    public long getAbweichung(){
        return abweichung;
    }

    private byte[] getTeilVonByteArray(byte[] daten, int ab, int lenge) {
        return ByteBuffer.allocate(lenge).order(ByteOrder.BIG_ENDIAN).put(daten, ab, lenge).array();
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
