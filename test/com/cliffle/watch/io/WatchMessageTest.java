package com.cliffle.watch.io;

import org.junit.Test;


public class WatchMessageTest {
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsLargePayloads() {
    new WatchMessage((byte) 0, new byte[251]);
  }
}
