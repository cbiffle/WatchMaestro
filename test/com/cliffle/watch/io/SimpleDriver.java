package com.cliffle.watch.io;

import java.io.FileOutputStream;

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
    
    /*
     * Per the reference implementation: the vibrate message is type 0x23.
     * It contains three arguments:
     *  - on-time: a 16-bit measure of the on cycle, in ms.
     *  - off-time: a 16-bit measure of the off cycle, in ms.
     *  - cycles: number of cycles to generate.
     */
    final int onTime = 1000;
    final int offTime = 100;
    final int cycles = 2;
    
    byte[] payload = new byte[] {
        0,  // unused padding byte
        1,  // vibration enable (0 would cancel any vibration)
        (byte) onTime,
        (byte) (onTime >> 8),
        (byte) offTime,
        (byte) (offTime >> 8),
        (byte) cycles,
    };
    
    System.out.println("Requesting vibration.");
    stream.write(new WatchMessage((byte) 0x23, payload));
    Thread.sleep(5000);
    stream.close();
  }
}
