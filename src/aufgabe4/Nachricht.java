package aufgabe4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created with IntelliJ IDEA.
 * User: denisfleischhauer
 * Date: 04.06.13
 * Time: 20:26
 * To change this template use File | Settings | File Templates.
 */
public class Nachricht {
    private byte[] nachricht;

    public Nachricht(byte[] nachricht) {
        this.nachricht = nachricht;
    }

    public String getNachrichtenKopf(){
        return new String(getTeilVonByteArray(nachricht, 0, 10));
    }

    public char getStationsKlasse(){
        return (char) nachricht[24];
    }

    public int getReserviertenSlot(){
        return nachricht[25] & 0xFF;
    }

    public long getSendezeit(){
        return ByteBuffer.wrap(nachricht).getLong(26);
    }

    public void setNutzdaten(byte[] nutzdaten){
        for (int i = 0; i < nutzdaten.length; i++){
            try {
                nachricht[i] = nutzdaten[i];
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
    }

    public void setStationsKlasse(char stationsKlasse){
        nachricht[24] = (byte)stationsKlasse;
    }

    public void setReservierterSlot(Integer freierSlot){
        nachricht[25] = freierSlot.byteValue();
    }

    public void setSendezeitpunkt(long sendezeitpunkt){
        byte[] szp = ByteBuffer.allocate(8).putLong(sendezeitpunkt).array();

        for (int i = 26; i < nachricht.length; i++){
            nachricht[i] = szp[i-26];
        }
    }

    public byte[] getBytes(){
        return nachricht;
    }

    private byte[] getTeilVonByteArray(byte[] daten, int ab, int lenge) {
        return ByteBuffer.allocate(lenge).order(ByteOrder.BIG_ENDIAN).put(daten, ab, lenge).array();
    }

    public String toString(String absender){
        return absender + " {" + getNachrichtenKopf() + " " + getStationsKlasse()
                + " " + getReserviertenSlot() + " " + getSendezeit() + "}";
    }
}
