package com.cliffle.watch.io;

import java.io.FileOutputStream;
import java.util.Arrays;

/**
 * This command-line tool connects to a watch over Bluetooth SPP and sends a
 * message.
 * <p>
 * On Mac OS X, this works out of the box.  On Linux, you may need to use the
 * {@code rfcomm} utility to bind a serial port.  On Windows...good luck.
 * 
 * @author cbiffle
 */
public class SimpleDriver {
  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Usage: SimpleDriver <port>");
      return;
    }
    
    String port = args[0];
    System.out.println("Opening connection to watch...");
    FileOutputStream out = new FileOutputStream(port);
    WatchMessageStream stream = new WatchMessageStream(out);
    System.out.println("Connection open.");
    
    System.out.println("Requesting vibration.");
    MessageFactory mf = new MessageFactory();
    stream.write(mf.makeVibrateMessage(1000, 500, 2));
    Thread.sleep(5000);  // or the watch doesn't hear us.

    System.out.println("Nuking the screen.");
    byte[] pixels = new byte[96 * 2 / 8];
    Arrays.fill(pixels, (byte) 0xAA);
    stream.write(mf.makeTwoRowUpdateMessage(30, pixels));
    stream.write(mf.makeUpdateDisplayMessage());

    Thread.sleep(5000);  // or the watch doesn't hear us.
    stream.close();
  }
}
