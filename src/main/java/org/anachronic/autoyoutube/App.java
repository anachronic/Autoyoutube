package org.anachronic.autoyoutube;


import org.apache.commons.cli.*;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;

public class App {
    private static final String TEMP_DIR = ".temp";
    private static final String APP_NAME = "Autoyoutube";
    public static String workingDir;
    public static String tempDir;

    private static Options makeOptions(){
        Options options = new Options();

        Option url = Option.builder("u").hasArg().longOpt("url").required().
                desc("URL to scan (either a video or playlist)").argName("URL").build();
        Option playlist = Option.builder("p").hasArg(false).longOpt("playlist").required(false)
                .desc("Setting this options indicates that this URL is a playlist").build();
        Option backwards = Option.builder("b").hasArg(false).longOpt("backwards").required(false)
                .desc("Setting this option indicates that the title and song are backwards. e.g: Song - Artist")
                .build();

        options.addOption(url);
        options.addOption(playlist);
        options.addOption(backwards);

        return options;
    }

    public static void main(String[] args) {
        File tempdir = new File(TEMP_DIR);
        workingDir = System.getProperty("user.dir");

        if (tempdir.exists() || tempdir.mkdir()) {
            tempDir = System.getProperty("user.dir") + "/" + TEMP_DIR;
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

        String url = line.getOptionValue('u');

        Song aSong = new Song(url);

        if(line.hasOption('b')) aSong.downloadWithTags(false);
        else aSong.downloadWithTags(true);
    }

    public static void usage(final OutputStream out, final Options options){
        final PrintWriter writer = new PrintWriter(out);

        final HelpFormatter help = new HelpFormatter();
        help.printUsage(writer, 80, APP_NAME, options);
        writer.close();
    }

    public static void help(final OutputStream out, final Options options){
        final PrintWriter writer = new PrintWriter(out);

        String header = "autoyoutube Help";
        String footer = "That's it.";

        final HelpFormatter help = new HelpFormatter();
        help.printHelp(writer, 80, getUsageString(), header, options, 2, 4, footer);
        writer.close();
    }

    public static String getUsageString(){
        return "autoyoutube -u <URL> [-p]";
    }
}
