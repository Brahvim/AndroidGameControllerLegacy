import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class UdpSocket {
    private Receiver receiver;
    private DatagramSocket sock;
    private DatagramPacket in, out;

    // Threading stuff *haha:*
    class Receiver {
        Thread thread;
        private boolean doRun;
        private Runnable task;

        Receiver(UdpSocket p_parent) {
            this.task = new Runnable() {
                public void run() {
                    in = new DatagramPacket(new byte[Integer.BYTES], Integer.BYTES);
                    while (doRun) {
                        System.out.println("Attempting to receive data...");
                        try {
                            System.out.println("Looking for data for the next 5 seconds...");
                            sock.receive(in);
                            System.out.println("Got data!");
                        } catch (IOException e) {
                            if (e instanceof SocketTimeoutException)
                                System.out.println("Just a timeout, continuing...");
                            else
                                e.printStackTrace();
                        }

                        if (in != null) {
                            InetAddress addr = in.getAddress();
                            if (addr == null)
                                continue;

                            String ip = addr.toString();
                            int port = in.getPort();

                            System.out.println("Calling `onReceive()`!");
                            onReceive(in.getData(), ip, port);
                            break;
                        }
                    }
                }
            };

            this.start();
        }

        public void start() {
            this.doRun = true;
            this.thread = new Thread(this.task);
            // this.thread.setDaemon(true);
            this.thread.start();

        }

        public void stop() {
            this.doRun = false;

            try {
                this.thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    UdpSocket() {
        try {
            this.sock = new DatagramSocket();
            this.sock.setSoTimeout(5);
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

    // #region `public` methods!:
    public void send(byte[] p_data, String p_ip, int p_port) {
        System.out.println("The socket sent some data!");
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
        this.send(p_message.getBytes(StandardCharsets.UTF_8),
                p_ip, p_port);

        // VSCode, please allow comments without an
        // extra space, I beg you. I need it for my style! I DON'T insert spaces for
        // code-only comments!
    }

    public void close() {
        this.onClose();
        this.receiver.stop();
        this.sock.close();
        System.out.println("Socket closed...");
    }
    // #endregion

    // #region Callbacks.
    protected void onStart() {
    }

    // `public` so you can generate fake events ;)
    public void onReceive(byte[] p_data, String p_ip, int p_port) {
    }

    protected void onClose() {
    }
    // #endregion

}