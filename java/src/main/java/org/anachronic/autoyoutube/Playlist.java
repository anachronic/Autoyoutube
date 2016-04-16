package org.anachronic.autoyoutube;


import org.anachronic.autoyoutube.exceptions.ExceptionMessages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Playlist {

    private String url;
    private List<String> videoIds;


    public Playlist(String url){
        this.url = url;
        videoIds = new ArrayList<String>();
    }

    private void getVideoIds(){
        String command = "youtube-dl " + url + " --get-id -s --no-warnings";

        System.out.println("Getting ids from videos on the playlist. This can take a while. Hold on...");

        Runtime rt = Runtime.getRuntime();
        Process proc = null;

        try {
            proc = rt.exec(command);
        } catch (IOException e) {
            ExceptionMessages.reportProcessIOEx();
            System.exit(-1);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        String anId;
        int videoCount = 1;

        //TODO: Handle YouTube error messages (like country blocked content or deleted videos).
        /*
        Idea for that: you can count the successful ids you get using the videoCount variable
        (which starts at 1). When you fail you have to start over (launch another youtube-dl process)
        but this time you have to set the option --playlist-start skipping one song. If you fail when getting
        the 30th song, you would use --playlist-start 31 next time you launch it. This needs further
        investigation, because you cant know the amount of songs beforehand. This could be repeated
        until you get no errors from youtube-dl stdout. Review another time?
         */
        try {
            while((anId = in.readLine()) != null){
                if(anId.contains("ERROR:") && anId.contains("YouTube said:"))
                    continue;
                videoIds.add(anId);
            }
        } catch (IOException e) {
            ExceptionMessages.reportProcessIOEx();
            System.exit(-1);
        }

        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            System.err.println("Couldn't get video ids from playlist. Received interrupt signal. Aborting...");
            System.exit(-1);
        }

        System.out.println("Ready to begin downloading mp3 files.");
    }

    public void downloadAllSongs(boolean artistExpectedAtBeginning, String separator, String ignoreAfter){
        getVideoIds();
        System.out.println("Downloading playlist. This can take a while. Hold on.");

        for(String songId : videoIds){
            String songURL = Song.VIDEO_URL_BASE + songId;

            Song song = new Song(songURL);
            song.downloadWithTags(artistExpectedAtBeginning, separator, ignoreAfter);
        }

        System.out.println("Finished downloading playlist. Enjoy!");
    }
}
