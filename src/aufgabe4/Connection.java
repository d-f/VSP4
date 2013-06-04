package aufgabe4;

import java.io.IOException;
import java.net.*;

/**
 * Created with IntelliJ IDEA.
 * User: denisfleischhauer
 * Date: 04.06.13
 * Time: 12:42
 * To change this template use File | Settings | File Templates.
 */
public class Connection {
    private MulticastSocket multicastSocket;
    private InetAddress multicastAdresse;
    private final int TTL = 1;

    public Connection(String netzwerkKarte, String multicastAdresse, int multicastPort) throws IOException {

        this.multicastAdresse = InetAddress.getByName(multicastAdresse);
        this.multicastSocket = new MulticastSocket(multicastPort);

        this.multicastSocket.joinGroup(new InetSocketAddress(multicastAdresse, multicastPort), NetworkInterface.getByName(netzwerkKarte));
        this.multicastSocket.setNetworkInterface(NetworkInterface.getByName(netzwerkKarte));

        this.multicastSocket.setTimeToLive(TTL);
    }

    public void send(byte[] nachricht) {
        DatagramPacket datagramPacket = new DatagramPacket(nachricht, nachricht.length, this.multicastAdresse, this.multicastSocket.getLocalPort());
        try {
            this.multicastSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public byte[] receive() {
        byte nachricht[] = new byte[34];
        DatagramPacket datagramPacket = new DatagramPacket(nachricht, nachricht.length);
        try {
            multicastSocket.receive(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return datagramPacket.getData();
    }
}
