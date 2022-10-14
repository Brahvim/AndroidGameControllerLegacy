import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpSocket extends Thread {
    private Receiver receiver;
    private DatagramSocket sock;
    private DatagramPacket in, out;

    // Threading stuff *haha:*
    class Receiver {
        Thread thread;

        Receiver(UdpSocket p_parent) {
            this.thread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            sock.receive(in);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        onReceive(in.getData(), in.getAddress().toString(), MAX_PRIORITY);
                    }
                }
            };
            this.thread.setDaemon(true);
            this.thread.start();
        }
    }

    UdpSocket() {
        try {
            this.sock = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        this.receiver = new Receiver(this);
        this.onStart();
    }

    // #region Getters
    public int getPort() {
        return this.sock.getLocalPort();
    }

    public DatagramPacket getLastPacketSent() {
        return out;
    }

    public DatagramPacket getLastPacketGot() {
        return in;
    }
    // #endregion

    // #region `send()` overloads:
    public void send(byte[] p_data, String p_ip, int p_port) {
        try {
            this.sock.send(out = new DatagramPacket(
                    p_data, p_data.length, InetAddress.getByName(p_ip), p_port));
        } catch (IOException e) {
            if (e instanceof UnknownHostException) {
                e.printStackTrace();
            } else {
                e.printStackTrace();
            }
        }
    }

    public void send(String p_message, String p_ip, int p_port) {
        byte[] bytes = null;
        try {
            bytes = p_message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // this.send(bytes, p_ip, p_port); // VSCode, please allow comments without an
        // extra space, I beg you. I need it for my style! I DON'T insert spaces for
        // code-only comments!

        try {
            this.sock.send(out = new DatagramPacket(
                    bytes, bytes.length, InetAddress.getByName(p_ip), p_port));
        } catch (IOException e) {
            if (e instanceof UnknownHostException) {
                e.printStackTrace();
            } else {
                e.printStackTrace();
            }
        }
    }
    // #endregion

    // #region Callbacks.
    protected void onStart() {
    }

    // `public` so you can generate fake events ;)
    public void onReceive(byte[] data, String p_ip, int p_port) {
    }

    protected void onClose() {
    }
    // #endregion

}
