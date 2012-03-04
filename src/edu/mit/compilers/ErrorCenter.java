package edu.mit.compilers;

public class ErrorCenter {

  static String filename = "";

  public static void setFilename(String newFilename) {
    filename = newFilename;
  }

  public static void reportError(int line, int col, String message) {
    System.out.println("Error at " + filename + ":" + line + "," + col + ": " + message);
  }
  
}
