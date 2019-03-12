package net.hydrogen2oxygen.markdowntohomepage.transformator;

import lombok.Builder;
import net.hydrogen2oxygen.markdowntohomepage.domain.PostDetails;
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

        String headerContentTemplate = "";
        String footerContentTemplate = "";

        if (!StringUtils.isEmpty(website.getHeaderFile())) {
            headerContentTemplate = FileUtils.readFileToString(new File(website.getHeaderFile()), "UTF-8");
        }

        if (!StringUtils.isEmpty(website.getFooterFile())) {
            footerContentTemplate = FileUtils.readFileToString(new File(website.getFooterFile()), "UTF-8");
        }

        for (File sourceFile : sourceFolder.listFiles()) {

            if (!sourceFile.isFile()) continue;
            if (!sourceFile.getName().endsWith(".md")) continue;

            String content = FileUtils.readFileToString(sourceFile, "UTF-8");
            PostDetails postDetails = new PostDetails();
            PostDetailsUtility.prefillPostDetails(content, postDetails);
            String headerContent = replaceAttributes(headerContentTemplate, postDetails);
            String footerContent = replaceAttributes(footerContentTemplate, postDetails);

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

    private static String replaceAttributes(String template, PostDetails postDetails) {

        String text = template
                .replaceAll("#TITLE#", postDetails.getTitle())
                .replaceAll("#AUTHOR#", postDetails.getAuthor())
                .replaceAll("#DATE#", postDetails.getDate())
                .replaceAll("#CATEGORIES#", generateCategoriesHtml(postDetails.getCategories()))
                .replaceAll("#TAGS#", generateTagsHtml(postDetails.getTags()));
        return text;
    }

    private static String generateTagsHtml(String tags) {

        if (tags == null || tags.trim().length() == 0) return "";

        StringBuilder str = new StringBuilder();

        str.append("Tags: <ul>");

        String[] parts = tags.split(",");

        for (String part : parts) {
            str.append(String.format("<li><a href=\"tag_%s.html\">%s</a></li>", part.trim().replaceAll(" ", "_"), part));
        }

        str.append("</ul>");

        return str.toString();
    }

    private static String generateCategoriesHtml(String categories) {
        // TODO
        return "";
    }

    private static void saveStringToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        FileUtils.writeStringToFile(file, content, "UTF-8");
    }
}