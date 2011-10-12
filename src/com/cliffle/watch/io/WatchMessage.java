package com.cliffle.watch.io;

/**
 * A message sent over a {@link WatchMessageStream}.
 * <p>
 * Messages have two important parts:
 * <ol>
 *  <li>A <em>message type</em> that tells how to decode the payload, and
 *  <li>Zero or more bytes of <em>payload</em>.
 * </ol>
 * 
 * @author cbiffle
 */
public class WatchMessage {
  private final byte type;
  private final byte[] payload;
  
  /**
   * Creates a new WatchMessage.  Note that {@code payload} isn't copied, so
   * it may be mutated after the message is created.  Be careful.
   * 
   * @param type type of message.
   * @param payload zero or more bytes of data.
   */
  public WatchMessage(byte type, byte[] payload) {
    this.type = type;
    this.payload = payload;
  }
  
  public byte getType() {
    return type;
  }
  
  public byte[] getPayload() {
    return payload;
  }
}
