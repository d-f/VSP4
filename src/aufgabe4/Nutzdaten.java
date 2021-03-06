package aufgabe4;

import java.io.IOException;

public class Nutzdaten extends Thread {

    private byte[] nutzdaten;

    public Nutzdaten() {
        nutzdaten = new byte[24];
        start();
    }

    public void run() {
        while (true) {
            try {
                byte zwischenPuffer[] = new byte[24];
                System.in.read(zwischenPuffer, 0, zwischenPuffer.length);
                synchronized (this.nutzdaten){
                    this.nutzdaten = zwischenPuffer;
                }
                //setNutzdaten(zwischenPuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized byte[] getNutzdaten() {
        return nutzdaten.clone();
    }

    private synchronized void setNutzdaten(byte buffer[]) {
        this.nutzdaten = buffer.clone();
    }
}
