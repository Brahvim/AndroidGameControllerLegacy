import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * The {@code UdpSocket} class! This class helps two applications running on
 * different machines connect via networks following the "User Datagrm Protocol"
 * and let them listen to each other on a different thread for easier
 * asynchronous multitasking.<br>
 * <br>
 * Of course, it is based on classes from the
 * {@code java.net} package ;)
 */
public class UdpSocket {
    public final static int DEFAULT_TIMEOUT = 32;

    /**
     * The internal {@linkplain DatagramSocket} that takes care of
     * networking.<br>
     * <br>
     * If you need to change it, consider using the
     * {@link UdpSocket#setSocket(DatagramSocket)}
     * method (it pauses the receiving thread, swaps the socket, and resumes
     * listening).<br>
     * <br>
     * {@link UdpSocket#getSocket()} <b>should</b> be used for equality checks,
     * etcetera.
     */
    private DatagramSocket sock;
    /**
     * The internal, {@code private} {@linkplain UdpSocket.Receiver} instance.
     * In abstract words, it handles threading for receiving messages.
     */
    private Receiver receiver;

    /**
     * Holds the previous {@code DatagramPacket} that was received.
     */
    private DatagramPacket in;
    /**
     * Holds the previous {@code DatagramPacket} that was sent.
     */
    private DatagramPacket out;

    // Threading stuff *haha:*
    /**
     * The {@code UdpSocket.Receiver} class helps {@code UdpSocket}s receive data on
     * a separate thread. Other than aiding with application performance and modern
     * hardware programming practices, it is useful on systems like Android, where
     * receiving tasks must be done asynchronously.
     * 
     * @see UdpSocket
     * 
     * @author Brahvim
     */
    public class Receiver {
        Thread thread; // Ti's but a daemon thread.
        private boolean doRun;
        Runnable task;

        Receiver(UdpSocket p_parent) {
            this.task = new Runnable() {
                public void run() {
                    byte[] byteData = new byte[65535], // B I G ___ A L L O C A T I O N !
                            actualData; // This one's size is a bit random everytime. May the JVM handle it well!

                    // We got some work?
                    while (doRun) {
                        try {
                            in = new DatagramPacket(byteData, byteData.length);
                            sock.receive(in); // Fetch it well!
                        } catch (IOException e) {
                            if (e instanceof SocketTimeoutException) {
                                // ¯\_(ツ)_/¯
                                // System.out.println("Timeout ended! Continuing...");
                            } else if (e instanceof SocketException) {
                                doRun = false;
                                return;
                            } else
                                e.printStackTrace(); // ¯\_(ツ)_/¯
                        }

                        // Callback!:
                        if (in != null) { // This block also makes `actualData` remain on the stack!
                            InetAddress addr = in.getAddress();

                            if (addr == null)
                                continue;

                            // System.out.println("Calling `onReceive()`!");

                            actualData = new byte[in.getLength()];
                            System.arraycopy(byteData, 0, actualData, 0, actualData.length);
                            for (int i = 0; i < actualData.length; i++)
                                actualData[i] = byteData[i];

                            onReceive(in.getData(),
                                    addr.toString().substring(1),
                                    in.getPort());
                        }
                    }
                }
            };

            this.start();
        }

        public void start() {
            this.doRun = true;
            // Yes. If `task` is `null`, throw an error.
            this.thread = new Thread(this.task);
            this.thread.setDaemon(true);
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

    // #region Construction!~

    public UdpSocket() {
        this(0, UdpSocket.DEFAULT_TIMEOUT);
    }

    public UdpSocket(int p_port) {
        this(p_port, UdpSocket.DEFAULT_TIMEOUT);
    }

    UdpSocket(DatagramSocket p_sock) {
        this.sock = p_sock;
        this.receiver = new Receiver(this);
    }

    public static DatagramSocket createSocketForcingPort(int p_port, int p_timeout) {
        DatagramSocket ret = null;

        try {
            ret = new DatagramSocket(null);
            ret.setReuseAddress(true);
            ret.bind(new InetSocketAddress(p_port));
            ret.setSoTimeout(p_timeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public UdpSocket(int p_port, int p_timeout) {
        try {
            this.sock = new DatagramSocket(p_port);
            this.sock.setSoTimeout(p_timeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // #region Old "force port" method:
        // try {
        // this.sock = new DatagramSocket(p_port);
        // if (this.getPort() != p_port) {
        // System.out.printf("Could not bind to port `%d`. Forcing the OS...\n",
        // p_port);
        // this.forcePort(p_port);
        // }
        // this.sock.setSoTimeout(p_timeout);
        // } catch (SocketException e) {
        // e.printStackTrace();
        // try {
        // if (this.sock != null)
        // this.sock.close();
        // this.sock = new DatagramSocket(null);
        // this.sock.setReuseAddress(true);
        // this.sock.bind(new InetSocketAddress(p_port));
        // this.sock.setSoTimeout(p_timeout);
        // } catch (SocketException f) {
        // f.printStackTrace();
        // }
        // }
        // #endregion

        System.out.printf("Socket port: `%d`.\n", this.sock.getLocalPort());
        // System.out.println(this.sock.getLocalAddress());

        if (this.receiver == null)
            this.receiver = new Receiver(this);
        this.onStart();
    }
    // #endregion

    // #region Callbacks. These are what you get. LOOK HERE!
    /**
     * Simply called by the constructor of {@code UdpSocket}, really.
     */
    protected void onStart() {
    }

    /**
     * @apiNote {@code public} so you can generate fake events ;) *
     * @param p_data Always of length {@code 65535}. No more, no less!
     *               If you wish to make a string out of it, use the constructor
     *               {@code new String(p_data, 0, p_data.length)}. The {@code 0} is
     *               the first character of the string.
     * @param p_ip
     * @param p_port
     */
    public void onReceive(byte[] p_data, String p_ip, int p_port) {
    }

    /**
     * Called before {@code .close()} closes the thread and socket.
     */
    protected void onClose() {
    }
    // #endregion

    // region Getters and setters. They're all `public`.
    public DatagramSocket getSocket() {
        return this.sock;
    }

    public void setSocket(DatagramSocket p_sock) {
        this.receiver.stop();
        this.sock = p_sock;
        this.receiver.start();
    }

    public int getTimeout(int p_timeout) {
        try {
            return this.sock.getSoTimeout();
        } catch (SocketException e) {
            // Hope this never happens!:
            e.printStackTrace();
            return -1;
        }
    }

    public void setTimeout(int p_timeout) {
        try {
            this.sock.setSoTimeout(p_timeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return this.sock.getLocalPort();
    }

    public void setPort(int p_port) {
        try {
            InetAddress previous = this.sock.getLocalAddress();
            // boolean receiverWasNull = receiver == null;
            // ^^^ Used when the function is called from constructors.

            // if (!receiverWasNull)
            // this.receiver.stop();
            this.receiver.stop(); // Necessary to call either way!
            this.sock.close();

            this.sock = new DatagramSocket(null);
            this.sock.setReuseAddress(true);
            this.sock.bind(new InetSocketAddress(previous, p_port));

            // if (receiverWasNull)
            // this.receiver = new Receiver(this);

            this.receiver.start();

            System.out.printf("Successfully forced the port to: `%d`.\n", this.sock.getLocalPort());
        } catch (SocketException e) {
            System.out.printf("Setting the port to `%d` failed!\n", p_port);
            System.out.printf("Had to revert to port `%d`...\n", this.sock.getLocalPort());
            e.printStackTrace();
        }
    }

    public InetAddress getIp() {
        return this.sock.getLocalAddress();
    }

    public DatagramPacket getLastPacketSent() {
        return this.out;
    }

    public DatagramPacket getLastPacketReceived() {
        return this.in;
    }
    // #endregion

    // #region Other `public` methods!:
    public void send(byte[] p_data, String p_ip, int p_port) {
        // System.out.println("The socket sent some data!");
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
        this.setTimeout(0);

        // No need to stop the receiving thread!
        try {
            this.sock.setReuseAddress(false);
            this.sock.close();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // System.out.println("Socket closed...");
    }
    // #endregion
}
