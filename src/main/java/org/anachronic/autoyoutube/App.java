package org.anachronic.autoyoutube;


import java.io.File;

public class App {
    private static final String TEMP_DIR = ".temp";
    public static String workingDir;
    public static String tempDir;

    public static void main(String[] args) {
        String url = args[0];

        File tempdir = new File(TEMP_DIR);
        workingDir = System.getProperty("user.dir");

        if (!tempdir.exists()) {
            if (tempdir.mkdir()) tempDir = System.getProperty("user.dir") + "/" + TEMP_DIR;
            else tempDir = workingDir;
        }


        Song aSong = new Song(url);
        aSong.downloadWithTags(true);
    }
}
