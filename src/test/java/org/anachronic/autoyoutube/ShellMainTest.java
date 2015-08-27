package org.anachronic.autoyoutube;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ShellMainTest {
    ClassLoader cl;
    String mp3testfile;

    @Before
    public void setUp() {
        cl = getClass().getClassLoader();
        mp3testfile = "test.mp3";
    }

    @Test
    public void fileIsReadable() throws Exception {
        File testFile = new File(cl.getResource(mp3testfile).getFile());

        assertNotNull(testFile);

        Mp3File mp3File = new Mp3File(testFile);

        ID3v2 id3v2Tag = mp3File.getId3v2Tag();
        byte[] albumImageData = id3v2Tag.getAlbumImage();
        if (albumImageData != null) {
            System.out.println("Have album image data, length: " + albumImageData.length + " bytes");
            System.out.println("Album image mime type: " + id3v2Tag.getAlbumImageMimeType());
        }

        id3v2Tag.setArtist("Angelo Escobar");

        assertEquals("Angelo Escobar", id3v2Tag.getArtist());
        mp3File.save("test2.mp3");
    }

    @Test
    public void procIsExecable() throws Exception{
        Runtime rt = Runtime.getRuntime();
        String commands = "youtube-dl -x https://www.youtube.com/watch?v=9U8_RvXSzxw --audio-format mp3 -e";
        Process proc = rt.exec(commands);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

// read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
    }

    @Test
    public void workingDirTest() throws Exception{
        System.out.println(System.getProperty("user.dir"));
    }
}