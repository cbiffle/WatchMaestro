package com.cliffle.watch.io;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.cliffle.watch.io.WatchMessage;


public class WatchMessageStreamTest {
  @Test public void testComputeCrc() {
    WatchMessage input = new WatchMessage((byte) 0xA5,
        new byte[] { 0x01, (byte) 0xDE, 0x55 });
    
    final short expectedCrc = 0x7300;  // Computed using reference impl
    assertEquals(expectedCrc, WatchMessageStream.computeCrc(input));
  }
  
  @Test public void testCrcStepFromDocumentation() {
    short crc = (short) 0xFFFF;
    byte[] input = { 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39 };
    
    for (byte b : input) {
      crc = WatchMessageStream.crcStep(crc, b);
    }
    
    assertEquals((short) 0x89F6, crc);
  }
  @Test public void testLengthOnWire() {
    // Too simple to fail?  Maybe.  But I'm feeling paranoid.
    WatchMessage input = new WatchMessage((byte) 0xA5,
        new byte[] { 0x01, (byte) 0xDE, 0x55 });
    assertEquals(1 + 1 + 1 + 3 + 2, WatchMessageStream.lengthOnWire(input));
  }
  
  @Test public void testWriteEmpty() throws IOException {
    WatchMessage empty = new WatchMessage((byte) 0x42, new byte[0]);
    ByteArrayOutputStream sink = new ByteArrayOutputStream();
    WatchMessageStream stream = new WatchMessageStream(sink);
    
    stream.write(empty);
    
    byte[] result = sink.toByteArray();
    assertEquals(1 + 1 + 1 + 0 + 2, result.length);
    assertArrayEquals(new byte[] {
        WatchMessageStream.START_OF_FRAME,
        (byte) WatchMessageStream.lengthOnWire(empty),
        0x42,
        (byte) WatchMessageStream.computeCrc(empty),
        (byte) (WatchMessageStream.computeCrc(empty) >> 8)
    }, result);
  }
  
  @Test public void testWriteNonEmpty() throws IOException {
    WatchMessage msg = new WatchMessage((byte) 0x42,
        new byte[] { 1, 2, 3, 4, 5 });
    ByteArrayOutputStream sink = new ByteArrayOutputStream();
    WatchMessageStream stream = new WatchMessageStream(sink);
    
    stream.write(msg);
    
    byte[] result = sink.toByteArray();
    assertEquals(1 + 1 + 1 + 5 + 2, result.length);
    assertArrayEquals(new byte[] {
        WatchMessageStream.START_OF_FRAME,
        (byte) WatchMessageStream.lengthOnWire(msg),
        0x42,
        1,2,3,4,5,
        (byte) WatchMessageStream.computeCrc(msg),
        (byte) (WatchMessageStream.computeCrc(msg) >> 8)
    }, result);
  }
}
