package org.anachronic.autoyoutube;


import com.mpatric.mp3agic.*;
import org.anachronic.autoyoutube.exceptions.ExceptionMessages;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Song {

    private String url;
    private String command;
    private String destination;

    private static final String FFMPEG_DESTINATION_STRING = "[ffmpeg] Destination: ";

    public Song(String url) {
        this.url = url;

        this.destination = null;
        this.command = "youtube-dl -x " + url + " --audio-format mp3";
    }

    public String getTitle() {
        Runtime rt = Runtime.getRuntime();
        String thecommand = this.command + " -e --encoding UTF-8";
        Process proc = null;
        try {
            proc = rt.exec(thecommand);
        } catch (IOException e) {
            ExceptionMessages.reportProcessIOEx();
            System.exit(-1);
        }

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        String s;
        String result = null;
        try {
            while ((s = stdInput.readLine()) != null) {
                result = s;
            }
        } catch (IOException e) {
            ExceptionMessages.reportProcessIOEx();
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
        System.setProperty("user.dir", App.tempDir);
        Runtime r = Runtime.getRuntime();
        Process proc;
        try {
            proc = r.exec(this.command);
        } catch (IOException e) {

            return false;
        }

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line = null;

        try {
            while ((line = stdInput.readLine()) != null) {
                if (line.startsWith(FFMPEG_DESTINATION_STRING)) {
                    destination = line.substring(FFMPEG_DESTINATION_STRING.length());
                }
            }
        } catch (IOException e) {
            ExceptionMessages.reportProcessIOEx();
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

        System.out.println("Downloading: " + title);

        mp3Download();

        Mp3File thisFile = null;
        try {
            File mp3f = new File(this.destination);
            thisFile = new Mp3File(mp3f);
        } catch (Exception e) {
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
        if (artistExpectedAtBeginning) {
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
            System.setProperty("user.dir", App.workingDir);
            thisFile.save(artist + " - " + songName + ".mp3");
        } catch (Exception e) {
            ExceptionMessages.reportFileIOEx();
        }

        System.setProperty("user.dir", App.tempDir);
        File oldfile = new File(destination);
        if (!oldfile.delete()) {
            System.err.println("WARNING: could not remove the old file.");
        }

        System.out.println("Finished: " + title);
    }
}
