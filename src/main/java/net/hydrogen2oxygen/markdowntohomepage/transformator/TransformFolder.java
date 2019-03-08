package net.hydrogen2oxygen.markdowntohomepage.transformator;

import lombok.Builder;
import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

public class TransformFolder {

    private static Logger logger = LoggerFactory.getLogger(TransformFolder.class);

    @Builder
    public static void transformFolder(Website website) throws IOException {

        File sourceFolder = new File(website.getSourceFolder());
        File targetFolder = new File(website.getTargetFolder());

        System.out.println(targetFolder.getAbsolutePath());

        if (!(sourceFolder.isDirectory() || sourceFolder.exists())) {
            logger.error("Source is not a folder or does not exist.");
            logger.error(sourceFolder.getAbsolutePath());
        }

        if (!(targetFolder.isDirectory() || targetFolder.exists())) {
            logger.error("Target is not a folder or does not exist.");
            logger.error(targetFolder.getAbsolutePath());
        }

        String headerContent = "";
        String footerContent = "";

        if (!StringUtils.isEmpty(website.getHeaderFile())) {
            headerContent = FileUtils.readFileToString(new File(website.getHeaderFile()), "UTF-8");
        }

        if (!StringUtils.isEmpty(website.getFooterFile())) {
            footerContent = FileUtils.readFileToString(new File(website.getFooterFile()), "UTF-8");
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

    private static void saveStringToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        FileUtils.writeStringToFile(file, content, "UTF-8");
    }
}