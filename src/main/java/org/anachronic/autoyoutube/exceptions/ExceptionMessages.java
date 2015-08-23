package org.anachronic.autoyoutube.exceptions;

public class ExceptionMessages {
    public static  void reportProcessIOEx() {
        System.err.println("Can't read/write process output. Check that you have ffmpeg and youtube-dl installed");
        System.err.println("Don't forget to check that you can actually run those processes.");
        System.err.println("Exiting...");
    }

    public static void reportFileIOEx(){
        System.err.println("Can't read/write the new downloaded file. Do you have write permissions on this folder?");
    }
}
