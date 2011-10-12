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
  enum Type {
    SET_VIBRATE_MODE(0x23),
    ;
    private final byte type;
    Type(int type) { this.type = (byte) type; }
    public byte getType() { return type; }
  }
  
  public WatchMessage makeVibrateMessage(int onMs, int offMs, int count) {
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
}
