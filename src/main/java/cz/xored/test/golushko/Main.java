package cz.xored.test.golushko;

import cz.xored.test.golushko.core.ApplicationStartup;
import org.apache.commons.cli.*;

public class Main {

    public static void main(String[] args) {



        Option configFilePathOption = Option.builder("c").hasArg().longOpt("config").desc("Path to config file").required().build();
        Options options = new Options();
        options.addOption(configFilePathOption);

        CommandLineParser cmdParser = new DefaultParser();

        try {
            CommandLine cmd = cmdParser.parse(options, args);
            ApplicationStartup startup = new ApplicationStartup();
            startup.start(cmd.getOptionValue("c"));

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("golushko-1.0-SNAPSHOT.jar", options, true);
        }



    }

}
