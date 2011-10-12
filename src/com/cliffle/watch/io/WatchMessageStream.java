package com.cliffle.watch.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Provides MetaWatch-compatible message encapsulation and integrity checking
 * over any stream.
 * 
 * @author cbiffle
 */
public class WatchMessageStream {
  // The encapsulation method restricts messages to 255 bytes.
  private static final int MAX_MESSAGE_SIZE = 255;
  // Messages are headed by an ASCII Start-Of-Frame character.
  static final int START_OF_FRAME = 0x01;
  
  public static short computeCrc(WatchMessage message) {
    short crc = (short) 0xFFFF;
    crc = crcStep(crc, message.getType());
    
    byte[] bytes = message.getPayload();
    for (int j = 0; j < bytes.length; j++) {
      crc = crcStep(crc, bytes[j]);
    }
    return crc;
  }
  
  private static short crcStep(short crc, byte c) {
    final int BIT15 = 1 << 15;
    for (int i = 1; i < 0x100; i <<= 1) {
      boolean c15 = (crc & BIT15) != 0;
      boolean bit = (c & i) != 0;
      crc <<= 1;
      if (c15 ^ bit) crc ^= 0x1021;  // 0001 0000 0010 0001 (0, 5, 12)
    }
    return crc;
  }
  
  // Visible for testing.
  static int lengthOnWire(WatchMessage message) {
    // Start-of-Frame, Length, Type, Payload, CRCx2.
    return 1 + 1 + 1 + message.getPayload().length + 2;
  }
  
  private final BufferedOutputStream out;

  public WatchMessageStream(OutputStream out) {
    this.out = new BufferedOutputStream(out, MAX_MESSAGE_SIZE);
  }
  
  public void write(WatchMessage message) throws IOException {
    out.write(START_OF_FRAME);
    out.write(lengthOnWire(message));
    out.write(message.getType());
    out.write(message.getPayload());
    
    // CRCs are sent little-endian.
    short crc = computeCrc(message);
    out.write(crc);
    out.write(crc >> 8);
    
    out.flush();
  }
  
}
