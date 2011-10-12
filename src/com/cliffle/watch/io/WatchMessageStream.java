package com.cliffle.watch.io;


/**
 * Provides MetaWatch-compatible message encapsulation and integrity checking
 * over any stream.
 * 
 * @author cbiffle
 */
public class WatchMessageStream {
  public static short computeCrc(WatchMessage message) {
    short crc = (short) 0xFFFF;
    crc = crcStep(crc, message.getType());
    
    byte[] bytes = message.getPayload();
    for (int j = 0; j < bytes.length; j++) {
      crc = crcStep(crc, bytes[j]);
    }
    int crc2 = crc - 0xffff0000;
    return (short) crc2;
  }
  
  private static short crcStep(short crc, byte c) {
    for (int i = 7; i >= 0; i--) {
      boolean c15 = ((crc >> 15 & 1) == 1);
      boolean bit = ((c >> (7 - i) & 1) == 1);
      crc <<= 1;
      if (c15 ^ bit)
              crc ^= 0x1021; // 0001 0000 0010 0001 (0, 5, 12)
    }
    return crc;
  }
  
}
