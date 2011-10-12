package com.cliffle.watch.util;

/**
 * Useful functions for describing and enforcing conditions in code.  A less
 * polished version of the Guava class of the same name.
 * 
 * @author cbiffle
 */
public class Preconditions {
  private Preconditions() {}  // No instance for you.
  
  public static void checkArgument(boolean condition, String message) {
    if (!condition) throw new IllegalArgumentException(message);
  }
}
