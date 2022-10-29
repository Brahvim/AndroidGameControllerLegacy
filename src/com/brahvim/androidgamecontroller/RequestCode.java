package com.brahvim.androidgamecontroller;

/*
 *
 * Guys,
 * seriously,
 * a BIG thanks,
 * to Java,
 * ...for handling endinanness and a bunch of other low-level stuff ";D!~ 👏
 *
 */

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

// I still put brackets around enum fields so I can put values in there
// later if I ever make a constructor.
public enum RequestCode {
  /**
   * AGC pings all on your network with this request
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
  MAX_DEVICES();

  public static final int SERVER_PORT = 6443;
  public final static byte[] CODE_SUFFIX = "CODE".getBytes(StandardCharsets.UTF_8);

  // I want to send request codes in this manner:
  // `CODE` + <the request code> + `_` + <extra data, preferably separated by
  // underscores..?>.

  public byte[] toBytes() {
    return ByteBuffer.allocate(Integer.BYTES).putInt(this.ordinal()).array();
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
      System.out.printf("Read byte: `%c`, iterator: `%d`.\n", (char) i, i);
      bytes[i] = p_bytes[RequestCode.CODE_SUFFIX.length + i];
    }

    // Return da code!11:
    return RequestCode.values()[ByteBuffer.wrap(bytes).getInt()];
  }

  public RequestCode fromBytes(byte[] p_bytes) {
    return RequestCode.values()[ByteBuffer.wrap(p_bytes).getInt()];
  }

  // public static byte[] toBytes(RequestCodes p_code) {
  // // return p_code.toBytes();
  // return ByteBuffer.allocate(Integer.BYTES).putInt(p_code.ordinal()).array();
  // }
}
