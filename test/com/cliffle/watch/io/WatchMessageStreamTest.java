package com.cliffle.watch.io;

import static org.junit.Assert.*;

import org.junit.Test;

import com.cliffle.watch.io.WatchMessage;


public class WatchMessageStreamTest {
  @Test public void testComputeCrc() {
    WatchMessage input = new WatchMessage((byte) 0xA5,
        new byte[] { 0x01, (byte) 0xDE, 0x55 });
    
    final short expectedCrc = 0x29AA;  // Computed using reference impl
    assertEquals(expectedCrc, WatchMessageStream.computeCrc(input));
  }
}
