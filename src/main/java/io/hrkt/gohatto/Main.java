package io.hrkt.gohatto;

import lombok.val;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    static {
        //System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "DEBUG");
    }
    static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        val options = new Options();
        val jarFileOption = Option.builder().argName("jar").longOpt("jar").hasArg(true).required(true).desc("path of the target jar file").build();
        options.addOption(jarFileOption);
        val forbiddenPackageListOption = Option.builder().argName("forbiddenPackageList").longOpt("forbiddenPackageList").hasArg(true).required(true).desc("path of the forbiddenPackageList").build();
        options.addOption(forbiddenPackageListOption);

        val parser = new DefaultParser();
        try {
            val cmd = parser.parse(options, args);
            val jarFilePath = cmd.getOptionValue("jar");
            val forbiddenPackageListPath = cmd.getOptionValue("forbiddenPackageList");

            val gohatto = new Gohatto();
            gohatto.init(jarFilePath, forbiddenPackageListPath);
            gohatto.executeRules();
        } catch (ParseException e) {
            LOG.error(e.getMessage(), e);
            System.exit(1);
        }

    }
}
