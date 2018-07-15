package net.hydrogen2oxygen.markdowntohomepage;

import net.hydrogen2oxygen.markdowntohomepage.transformator.TransformFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

/**
 * The Spring Boot Application acts as a server if the argument --servermode is delivered.
 * Else the main method acts as a simple command line tool.
 */
@SpringBootApplication
public class MarkdowntohomepageApplication {

    private static Logger logger = LoggerFactory.getLogger(MarkdowntohomepageApplication.class);

    public static void main(String[] args) throws IOException {

        logger.info("MarkdowntohomepageApplication Version 1.0");
        logger.info("-----------------------------------------");

        boolean serverMode = false;

        for (String argument : args) {

            logger.info(argument);

            if ("--servermode".equals(argument)) {
                serverMode = true;
            }
        }

        logger.info("-----------------------------------------");

        if (serverMode) {
            SpringApplication.run(MarkdowntohomepageApplication.class, args);
            return;
        }

        if (args.length < 3) {
            logger.info("=== Syntax ===");
            logger.info(" [sourceFolder] [targetFolder] [optional:configurationFile");

            return;
        }


        TransformFolder.builder()
                .sourceFolderPath(args[0])
                .targetFolderPath(args[1])
                .configurationFile(args[2])
                .build();

        logger.info("-----------------------------------------");
    }
}
