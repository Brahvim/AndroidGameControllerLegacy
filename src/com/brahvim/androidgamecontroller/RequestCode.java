package com.brahvim.androidgamecontroller;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/*
 * Guys,
 * seriously,
 * BIG thanks to Java!
 * ...for handling endinanness and a bunch of other low-level stuff! ":D!~ 👏
 * ...AND STILL BEING BLAZING FAST!
 */

public enum RequestCode {
    // #region Finally, here are da ENUMERATION values!
    //
    // SUPER IMPORTANT *Convention Note:*
    // I put brackets around enum fields so I can put values in there
    // later if I ever make a constructor. Will it happen *now?* No!
    // ...but safety's good!
    //

    /**
     * AGC pings all devices on your network with this request
     * so they respond and it can connect to them :D
     */
    FINDING_DEVICES(),

    /**
     * When a client wants to connect, they send this request:
     */
    ADD_ME(),

    // region Reasons from the client for exiting.
    /**
     * The server application is exiting.
     */
    SERVER_CLOSE(),

    /**
     * The client application is exiting.
     */
    CLIENT_CLOSE(),

    /**
     * The server can often tell client devices to disconnect because they haven't
     * messaged in a
     * long time.
     */
    SERVER_SAYS_CLIENTS_SHOULD_TIMEOUT(),

    /**
     * ...when a client wants to disconnect to save their own battery, they send
     * this over:
     */
    CLIENT_LOW_BATTERY(),
    // endregion

    /**
     * The server does not want more devices to connect.
     * Sorry, smaller brothers, ...who want to spoil their bigger brother's gaming
     * sessions using my app!
     */
    MAX_DEVICES(),

    /**
     * If the server operator accepts a client's `ADD_ME`, the server sends this.
     */
    CLIENT_WAS_REGISTERED(),

    /**
     * The operator denied the entry of this client on the server.
     * They may 'bail' them out of the ban list to be able to accpet any further
     * requests from the client.
     */
    CLIENT_WAS_REJECTED(),

    /**
     * "BRO you just got banned from the current AGC session! Bruh momentum..."
     */
    CLIENT_WAS_BANNED(),

    /**
     * The client will send extra data along with this request.
     * The *extra data* contains ONLY the bytes of a serialized
     * `com.brahvim.androidgamecontroller.serial.config.ConfigurationPacket` object.
     */
    CLIENT_SENDS_CONFIG(),

    SERVER_GOT_CONFIG();
    // #endregion

    public static final String CLIENT_CURRENT_VERSION = "v1.0.0";
    public static final String SERVER_CURRENT_VERSION = "v1.0.0";

    /**
     * The port that AGC servers are always on.
     */
    public static final int SERVER_PORT = 6443;

    /**
     * The bytes of this strings are suffixed when AGC sends a request code instead
     * of a packet with serialized controller data.
     */
    public final static byte[] CODE_SUFFIX = "CODE".getBytes(StandardCharsets.UTF_8);

    /**
     * This is how many bytes 'far' extra data starts in request packets.
     */
    public static final int EXTRA_DATA_START = Integer.BYTES + RequestCode.CODE_SUFFIX.length;

    // I want to send request codes in this manner:
    // `CODE` + <request code> + <extra data, separated by underscores..?>.

    // #region Static stuff comes first, here...
    public static boolean packetHasCode(byte[] p_pack) {
        // If the first bytes don't say "CODE", it's not a packet of code, it's data!
        // Lazy method: `return new String(p_pack).startsWith("CODE");` 🤣
        for (int i = 0; i < RequestCode.CODE_SUFFIX.length; i++)
            if (p_pack[i] != RequestCode.CODE_SUFFIX[i])
                return false;
        return true;
    }

    public static RequestCode fromPacket(byte[] p_bytes) {
        // Structure of a request-code packet (as a string) (WITHOUT THE `_`s):
        // `CODE_1234_ExtraData`.
        // ...where `1234` are the bytes of an integer,
        // and `ExtraData` is extra data attached.

        // The following logic parses out the integer bytes in the middle,
        // and then returns with the corresponding `RequestCode`:

        // int endOfInt = RequestCode.CODE_SUFFIX.length + Integer.BYTES; // Funny how
        // this fits in a byte as well.
        byte[] bytes = new byte[Integer.BYTES];

        // Copy da bytes!11!:
        for (int i = 0; i < Integer.BYTES; i++) { // Funny how this could fit in a byte as well!
            // System.out.printf("Read byte: `%c`, iterator: `%d`.\n", (char) i, i);
            bytes[i] = p_bytes[RequestCode.CODE_SUFFIX.length + i];
        }

        // Return da code!11:
        return RequestCode.values()[ByteBuffer.wrap(bytes).getInt()];
    }

    public static RequestCode fromBytes(byte[] p_bytes) {
        return RequestCode.values()[ByteBuffer.wrap(p_bytes).getInt()];
    }

    // public static byte[] toBytes(RequestCodes p_code) {
    // // return p_code.toBytes();
    // return ByteBuffer.allocate(Integer.BYTES).putInt(p_code.ordinal()).array();
    // }
    // #endregion

    // region INSTANCE methods!:
    public byte[] toBytes() {
        return ByteBuffer.allocate(Integer.BYTES).putInt(this.ordinal()).array();
    }
    // #endregion
}
