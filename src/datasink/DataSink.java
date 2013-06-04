package datasink;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: denisfleischhauer
 * Date: 04.06.13
 * Time: 15:52
 * To change this template use File | Settings | File Templates.
 */
public class DataSink extends Thread{

    public DataSink() {
        start();
    }

    public void run() {
        while (true) {
            try {
                byte zwischenPuffer[] = new byte[1024];
                System.in.read(zwischenPuffer);
                System.out.println(new String(zwischenPuffer));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String... args){
       new DataSink();
    }
}
