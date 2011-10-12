package com.cliffle.watch.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import com.cliffle.watch.io.MessageFactory.DisplayBuffer;

/**
 * This command-line tool connects to a watch over Bluetooth SPP and displays an
 * image.
 * <p>
 * On Mac OS X, this works out of the box.  On Linux, you may need to use the
 * {@code rfcomm} utility to bind a serial port.  On Windows...good luck.
 * 
 * @author cbiffle
 */
public class ImageDriver {
  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.err.println("Usage: ImageDriver <port> <imagefile>");
      return;
    }
    
    String port = args[0];
    String filename = args[1];
    
    System.out.println("Opening connection to watch...");
    FileOutputStream out = new FileOutputStream(port);
    WatchMessageStream stream = new WatchMessageStream(out);
    System.out.println("Connection open.");
    
    MessageFactory mf = new MessageFactory();

    BufferedImage image = ImageIO.read(new File(filename));
    if (image.getHeight() > 96 || image.getWidth() > 96) {
      System.err.println("Image too big!  Must be less than 96x96.");
      stream.close();
      return;
    }
    
    System.out.println("Image loaded; building bitmap...");
    final int STRIDE = 96/8;
    byte[] bitmap = new byte[96 * STRIDE];
    int mask = 1, bytePos = 0;
    for (int y = 0; y < image.getHeight(); y++) {
      boolean yLsb = (y & 1) != 0;
      for (int x = 0; x < image.getWidth(); x++) {
        int rgb = image.getRGB(x, y);
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = (rgb >> 0) & 0xFF;
        int gray = (red + green + blue) / 3;
        
        boolean xLsb = (x & 1) != 0;
        int threshold;
        if (xLsb ^ yLsb) {
          threshold = 128 + 32;
        } else {
          threshold = 128 - 32;
        }
        
        if (gray < threshold) {
          bitmap[bytePos] |= mask;
        }
        
        mask <<= 1;
        if (mask == 0x100) {
          mask = 1; bytePos++;
        }
      }
    }
    
    System.out.println("Sending...");
    for (int row = 0; row < 96; row += 2) {
      stream.write(mf.makeDisplayUpdate(row, 2, DisplayBuffer.IDLE, bitmap, row * (96 / 8)));
    }
    stream.write(mf.makeUpdateDisplay(DisplayBuffer.IDLE, true));
    System.out.println("Sent!");

    Thread.sleep(15000);  // or the watch doesn't hear us.
    stream.close();
  }
}
