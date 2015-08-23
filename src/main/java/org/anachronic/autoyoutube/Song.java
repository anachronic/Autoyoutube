package org.anachronic.autoyoutube;


import com.mpatric.mp3agic.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class Song {

    private String url;
    private String command;
    private String uuid;

    public Song(String url) {
        this.url = url;

        this.uuid = UUID.randomUUID().toString() + ".mp3";
        this.command = "youtube-dl -x " + url + " --audio-format mp3 -o " + this.uuid;
    }

    private static void reportIOEx() {
        System.err.println("Can't read/write process output. Check that you have ffmpeg and youtube-dl installed");
        System.err.println("Don't forget to check that you can actually run those processes.");
        System.err.println("Exiting...");
    }

    public String getTitle() {
        Runtime rt = Runtime.getRuntime();
        String thecommand = this.command + " -e";
        Process proc = null;
        try {
            proc = rt.exec(thecommand);
        } catch (IOException e) {
            reportIOEx();
        }

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        String s;
        String result = null;
        try {
            while ((s = stdInput.readLine()) != null) {
                result = s;
            }
        } catch (IOException e) {
            reportIOEx();
        }

        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            System.err.println("ERROR: Couldn't get YouTube title from video. Process was aborted. Exiting...");
            System.exit(-1);
        }

        return result;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private boolean mp3Download() {
        Runtime r = Runtime.getRuntime();
        Process proc = null;
        try {
            proc = r.exec(this.command);
        } catch (IOException e) {

            return false;
        }

        try {
            int result = proc.waitFor();
            if (result != 0) {
                System.err.println("WARNING: Downloading process exited with non-zero exitcode" + result);
            }
        } catch (InterruptedException e) {
            System.err.println("ERROR: Downloading process was aborted. Exiting...");
            return false;
        }

        return true;
    }

    public void downloadWithTags(boolean artistExpectedAtBeginning) {
        downloadWithTags(artistExpectedAtBeginning, " - ");
    }
    public void downloadWithTags(boolean artistExpectedAtBeginning, String separator) {
        String title = getTitle();

        mp3Download();

        Mp3File thisFile = null;
        try {
            thisFile = new Mp3File(this.uuid);
        } catch (Exception e){
            System.err.println("Can't seem to open and/or read the downloaded file. Does it exist?");
            System.err.println("Is it an mp3 file?");
            System.exit(0);
        }

        if (thisFile.hasId3v1Tag()) {
            thisFile.removeId3v1Tag();
        }
        if (thisFile.hasId3v2Tag()) {
            thisFile.removeId3v2Tag();
        }
        if (thisFile.hasCustomTag()) {
            thisFile.removeCustomTag();
        }

        ID3v2 tag = new ID3v24Tag();

        String[] split = title.split(separator);
        String artist, songName;
        if(artistExpectedAtBeginning){
            artist = split[0];
            songName = split[1];
        } else {
            artist = split[1];
            songName = split[0];
        }

        tag.setArtist(artist);
        tag.setTitle(songName);

        thisFile.setId3v2Tag(tag);

        try {
            thisFile.save(artist + " - " + songName + ".mp3");
        } catch (Exception e){
            System.err.println("Can't save the new downloaded file. Do you have write permissions on this folder?");
        }
    }
}
