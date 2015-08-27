package org.anachronic.autoyoutube;


import org.anachronic.autoyoutube.app.AppContext;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;

public class ShellMain {
    private static final String TEMP_DIR = ".temp";
    private static final String APP_NAME = "Autoyoutube";

    private static final String DEFAULT_SEPARATOR = " - ";
    public static String workingDir;
    public static String tempDir;

    private static Options makeOptions() {
        Options options = new Options();

        Option url = Option.builder("u").hasArg().longOpt("url").required().
                desc("URL to scan (either a video or playlist) [REQUIRED]").argName("URL").build();
        Option playlist = Option.builder("p").hasArg(false).longOpt("playlist").required(false)
                .desc("Setting this options indicates that this URL is a playlist").build();
        Option backwards = Option.builder("b").hasArg(false).longOpt("backwards").required(false)
                .desc("Setting this option indicates that the title and song are backwards. e.g: Song - Artist")
                .build();
        Option ignore = Option.builder("i").hasArg().required(false).longOpt("ignore")
                .desc("Setting this option ignores everything after (and including) the specified argument in the YouTube " +
                        "video String. e.g: Using this option with a video with title 'Artist - Song // garbage.' will ignore " +
                        "the '// garbage' string so it will parse only what is necessary.").build();
        Option separator = Option.builder("s").hasArg().required(false).longOpt("separator")
                .desc("Sepecifying this option changes the string separator between artist and song name string")
                .build();

        options.addOption(url);
        options.addOption(playlist);
        options.addOption(backwards);
        options.addOption(ignore);
        options.addOption(separator);

        return options;
    }

    public static void main(String[] args) {
        File tempdir = new File(AppContext.TEMP_DIR);
        workingDir = System.getProperty("user.dir");

        if (tempdir.exists() || tempdir.mkdir()) {
            tempDir = System.getProperty("user.dir") + "/" + AppContext.TEMP_DIR;
        } else {
            tempDir = workingDir;
        }

        Options options = makeOptions();

        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;

        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            help(System.out, options);
            System.exit(1);
        }

        String url;
        String separator;
        String ignore = null;

        if (line.hasOption('i')) {
            ignore = line.getOptionValue('i');
        }

        if (line.hasOption('s')) separator = line.getOptionValue('s');
        else separator = DEFAULT_SEPARATOR;

        url = line.getOptionValue('u');
        boolean artistFirst = !line.hasOption('b');

        if (line.hasOption('p')) {
            Playlist playlist = new Playlist(url);
            playlist.downloadAllSongs(artistFirst, separator, ignore);
        } else {
            Song aSong = new Song(url);
            aSong.downloadWithTags(!line.hasOption('b'), separator, ignore); //if tag is present send false.
        }

    }

    public static void usage(final OutputStream out, final Options options) {
        final PrintWriter writer = new PrintWriter(out);

        final HelpFormatter help = new HelpFormatter();
        help.printUsage(writer, 80, APP_NAME, options);
        writer.close();
    }

    public static void help(final OutputStream out, final Options options) {
        final PrintWriter writer = new PrintWriter(out);

        String header = "autoyoutube Help";
        String footer = "That's it.";

        final HelpFormatter help = new HelpFormatter();
        help.printHelp(writer, 80, getUsageString(), header, options, 2, 4, footer);
        writer.close();
    }

    public static String getUsageString() {
        return "autoyoutube -u <URL> [-p] [-b] [-i ignoreString]";
    }
}
