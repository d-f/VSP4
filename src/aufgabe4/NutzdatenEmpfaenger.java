package aufgabe4;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: denisfleischhauer
 * Date: 04.06.13
 * Time: 12:39
 * To change this template use File | Settings | File Templates.
 */
public class NutzdatenEmpfaenger extends Thread{
    private byte[] buffer;
    private boolean beenden = false;

    public NutzdatenEmpfaenger() {
        start();
    }

    public byte[] getNutzdaten() {
        return buffer.clone();
    }

    @Override
    public void run() {
        while (!beenden) {
            try {
                buffer = new byte[24];
                System.in.read(buffer, 0, buffer.length);
                //System.out.println("<NutzdatenEmpfaenger> neue Daten generiert: " + new String(buffer) + " laenge: " + buffer.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
