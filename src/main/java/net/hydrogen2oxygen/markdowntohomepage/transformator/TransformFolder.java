package net.hydrogen2oxygen.markdowntohomepage.transformator;

import lombok.Builder;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TransformFolder {

    @Value("${headerFile}")
    private static String headerFile;

    @Value("${footerFile}")
    private static String footerFile;

    private static Logger logger = LoggerFactory.getLogger(TransformFolder.class);

    @Builder
    public static void transformFolder(String sourceFolderPath, String targetFolderPath, String configurationFile) throws IOException {

        // Workaround for command line mode
        loadConfigInCommandLineMode(configurationFile);

        File sourceFolder = new File(sourceFolderPath);
        File targetFolder = new File(targetFolderPath);

        if (!(sourceFolder.isDirectory() || sourceFolder.exists())) {
            logger.error("Source is not a folder or does not exist.");
            logger.error(sourceFolder.getAbsolutePath());
        }

        if (!(targetFolder.isDirectory() || targetFolder.exists())) {
            logger.error("Target is not a folder or does not exist.");
            logger.error(targetFolder.getAbsolutePath());
        }

        String headerContent = "";//readFileToString(header);
        String footerContent = "";//readFileToString(footer);

        if (!StringUtils.isEmpty(headerFile)) {
            headerContent = FileUtils.readFileToString(new File(headerFile), "UTF-8");
        }

        if (!StringUtils.isEmpty(footerFile)) {
            footerContent = FileUtils.readFileToString(new File(footerFile), "UTF-8");
        }

        for (File sourceFile : sourceFolder.listFiles()) {

            if (!sourceFile.isFile()) continue;
            if (!sourceFile.getName().endsWith(".md")) continue;

            String transformedHTML = MarkdownToHtmlTransformator.
                    builder().
                    source(sourceFile).
                    headerContent(headerContent).
                    footerContent(footerContent).
                    build();
            saveStringToFile(targetFolder.getAbsolutePath() + "/" + sourceFile.getName().replace(".md", ".html"), transformedHTML);

            logger.info(sourceFile.getName());
        }

    }

    private static void loadConfigInCommandLineMode(String configurationFile) throws IOException {
        if (configurationFile != null) {
            Properties prop = new Properties();
            InputStream input = new FileInputStream(configurationFile);
            prop.load(input);

            headerFile = prop.getProperty("headerFile");
            footerFile = prop.getProperty("footerFile");

            input.close();
        }
    }

    private static void saveStringToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        FileUtils.writeStringToFile(file, content, "UTF-8");
    }
}