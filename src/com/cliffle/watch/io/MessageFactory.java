package com.cliffle.watch.io;

import static com.cliffle.watch.util.Preconditions.*;

/**
 * Produces {@link WatchMessage} instances to do various things.  Since they're
 * immutable, you can keep them around for reuse if you like.
 * <p>
 * This implements the initial MetaWatch protocol used by the reference
 * implementation.
 * 
 * @author cbiffle
 */
public class MessageFactory {
  private static final int PIXELS_PER_ROW = 96;
  private static final int BITS_PER_PIXEL = 1;
  private static final int PIXELS_PER_BYTE = 8 / BITS_PER_PIXEL;
  private static final int BYTES_PER_ROW = PIXELS_PER_ROW / PIXELS_PER_BYTE;
  enum Type {
    SET_VIBRATE_MODE(0x23),
    SET_REAL_TIME_CLOCK(0x26),
    WRITE_BUFFER(0x40),
    UPDATE_DISPLAY(0x43),
    ;
    private final byte type;
    Type(int type) { this.type = (byte) type; }
    public byte getType() { return type; }
  }
  
  public enum DisplayBuffer {
    IDLE(0),
    APPLICATION(1),
    NOTIFICATION(2),
    SCROLL(3),
    ;
    private final byte code;
    DisplayBuffer(int code) { this.code = (byte) code; }
    public byte getCode() { return code; }
  }
  
  public WatchMessage makeVibrate(int onMs, int offMs, int count) {
    checkArgument(onMs >= 0 && onMs < 65536, "onMs must fit in 16 bits");
    checkArgument(offMs >= 0 && offMs < 65536, "offMs must fit in 16 bits");
    checkArgument(count >= 0 && count < 256, "count must fit in 8 bits");
    
    byte[] payload = new byte[] {
        0,  // unused padding byte
        1,  // vibration enable (0 would cancel any vibration)
        (byte) onMs,
        (byte) (onMs >> 8),
        (byte) offMs,
        (byte) (offMs >> 8),
        (byte) count,
    };
    
    return new WatchMessage(Type.SET_VIBRATE_MODE.getType(), payload);
  }
  
  public WatchMessage makeDisplayUpdate(int firstRow, int rowCount, DisplayBuffer buffer,
      byte[] packedPixels, int start) {
    checkArgument(firstRow >= 0 && firstRow < 96, "row must be between 0 and 95");
    checkArgument(rowCount == 1 || rowCount == 2, "rowCount must be 1 or 2");
    
    byte[] payload = new byte[1 + 1 + BYTES_PER_ROW + 1 + BYTES_PER_ROW];
    payload[0] = buffer.getCode();
    if (rowCount == 1) payload[0] |= (1 << 4);
    payload[1] = (byte) firstRow;
    System.arraycopy(packedPixels, start, payload, 2, BYTES_PER_ROW);
    if (rowCount == 2) {
      payload[2 + BYTES_PER_ROW] = (byte) (firstRow + 1);
      System.arraycopy(packedPixels, start + BYTES_PER_ROW, payload, 3 + BYTES_PER_ROW, BYTES_PER_ROW);
    }
    
    return new WatchMessage(Type.WRITE_BUFFER.getType(), payload);
  }
  
  public WatchMessage makeUpdateDisplay(DisplayBuffer newBuffer, boolean copy) {
    byte[] payload = {
      (byte) (newBuffer.getCode() | (copy? (1 << 4) : 0)),
    };
    return new WatchMessage(Type.UPDATE_DISPLAY.getType(), payload);
  }
  
  /**
   * Creates a message to set the watch's real-time clock.
   * 
   * @param year year of CE.
   * @param month month of Gregorian year: 1-12.
   * @param dayOfMonth day of month: 1-31
   * @param dayOfWeek day of week: 0 (Monday) - 6 (Sunday)
   * @param hour hour of day: 0-24
   * @param minute minute of hour: 0-59
   * @param second second of minute: 0-59
   * @param use24HourTime whether to use 24-hour ("military") time ({@code
   *     true}) or 12-hour time with AM/PM indicator ({@code false}).
   * @param dayFirst whether to show dates as DD/MM ({@code true}) or MM/DD
   *     ({@code false}). 
   * @return the constructed message.
   */
  public WatchMessage makeSetRTC(int year, int month, int dayOfMonth, int dayOfWeek,
      int hour, int minute, int second, boolean use24HourTime, boolean dayFirst) {
    byte[] payload = {
        0,  // Unused padding byte
        (byte) (year >> 8),  // Yes, year is sent big-endian.
        (byte) (year),
        (byte) month,
        (byte) dayOfMonth,
        (byte) dayOfWeek,
        (byte) hour,
        (byte) minute,
        (byte) second,
        (byte) (use24HourTime? 1 : 0),
        (byte) (dayFirst? 1 : 0),
    };
    
    return new WatchMessage(Type.SET_REAL_TIME_CLOCK.getType(), payload);
  }
}
