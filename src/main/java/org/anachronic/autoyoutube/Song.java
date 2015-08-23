package org.anachronic.autoyoutube;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Song {

    private String url;
    private String command;

    public Song(String url){
        this.url = url;

        this.command = "youtube-dl -x " + url + " --audio-format mp3";
    }

    public String getTitle() throws IOException{
        Runtime rt = Runtime.getRuntime();
        String thecommand = this.command + " -e";
        Process proc = rt.exec(thecommand);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        String s;
        String result = null;
        while ((s = stdInput.readLine()) != null) {
            result=s;
        }

        return result;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        this.command = "youtube-dl -x " + url + " --audio-format mp3";
    }

    public boolean download(){
        Runtime r = Runtime.getRuntime();
        Process proc = null;
        try {
            proc = r.exec(this.command);
        } catch (IOException e) {
            System.err.println("Cant read/write process output. Check that you have ffmpeg and youtube-dl installed");
            System.err.println("Don't forget to check that you can actually run those processes.");
            System.err.println("Exiting...");
            return false;
        }

        try {
            int result = proc.waitFor();
            if(result != 0){
                System.err.println("WARNING: Downloading process exited with non-zero exitcode" + result);
            }
        } catch (InterruptedException e) {
            System.err.println("ERROR: Downloading process was aborted. Exiting...");
            return false;
        }

        return true;
    }
}
