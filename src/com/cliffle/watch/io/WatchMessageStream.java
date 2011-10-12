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
  
}
