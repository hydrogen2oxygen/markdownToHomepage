package net.hydrogen2oxygen.markdowntohomepage.transformator;

import com.redfin.sitemapgenerator.WebSitemapGenerator;
import lombok.Builder;
import net.hydrogen2oxygen.markdowntohomepage.domain.PostDetails;
import net.hydrogen2oxygen.markdowntohomepage.domain.TagAndRelatedPosts;
import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TransformFolder {

    private static Logger logger = LoggerFactory.getLogger(TransformFolder.class);

    @Builder
    public static void transformFolder(Website website) throws IOException {

        Map<String,TagAndRelatedPosts> tags = new HashMap<>();

        File sourceFolder = new File(website.getSourceFolder());
        File targetFolder = new File(website.getTargetFolder());
        WebSitemapGenerator webSitemapGenerator = WebSitemapGenerator.builder(website.getBaseUrl(), targetFolder)
                .build();

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

        StringBuilder indexContent = new StringBuilder();

        List<File> sourceFiles = getSourceFiles(sourceFolder);
        int count = 0;

        for (File sourceFile : sourceFiles) {

            String content = FileUtils.readFileToString(sourceFile, "UTF-8");
            PostDetails postDetails = new PostDetails();
            PostDetailsUtility.prefillPostDetails(content, postDetails);
            String categoriesList = generateCategoriesHtml(postDetails.getCategories());
            String tagsList = generateTagsHtml(postDetails.getTags());
            String headerContent = replaceAttributes(headerContentTemplate, postDetails, categoriesList, tagsList);
            String footerContent = replaceAttributes(footerContentTemplate, postDetails, categoriesList, tagsList);
            enhancePostDetails(postDetails, sourceFile.getName().replace(".md", ".html"));

            collectTagsAndRelatedPosts(tags, postDetails);

            String transformedHTML = MarkdownToHtmlTransformator.
                    builder().
                    source(sourceFile).
                    headerContent(headerContent).
                    footerContent(footerContent).
                    build();

            saveStringToFile(postDetails, targetFolder, transformedHTML);
            webSitemapGenerator.addUrl(website.getBaseUrl() + "/" + postDetails.getUrl());

            String url = String.format("<a href=\"%s\">%s</a><br>\n", postDetails.getUrl(), postDetails.getTitle());

            if (count > 3) {
                indexContent.append(url);
            } else {
                indexContent.append(MarkdownToHtmlTransformator.
                        builder().
                        source(sourceFile).
                        linkTitle(true).
                        url(postDetails.getUrl()).
                        headerContent("").
                        footerContent("").
                        build()).append("<hr>");
            }

            count++;

            logger.info(sourceFile.getName());
        }

        // Create index.html
        PostDetails postDetails = new PostDetails();
        postDetails.setTitle("");
        String headerContent = replaceAttributes(headerContentTemplate, postDetails, "", "");
        String footerContent = replaceAttributes(footerContentTemplate, postDetails, "", "");
        String html = headerContent + indexContent.toString() + footerContent;
        FileUtils.writeStringToFile(new File(targetFolder.getAbsolutePath() + File.separator + "index.html"), html, "UTF-8");

        // Copy Statics
        copyStatics(sourceFolder, targetFolder);

        webSitemapGenerator.write();
    }

    private static void copyStatics(File sourceFolder, File targetFolder) throws IOException {
        File staticsFolderSource = new File(sourceFolder.getAbsolutePath() + "/statics");

        if (staticsFolderSource.exists()) {
            File staticsFolderTarget = new File(targetFolder.getAbsolutePath() + "/statics");
            FileUtils.copyDirectory(staticsFolderSource, staticsFolderTarget);
        }
    }

    private static void collectTagsAndRelatedPosts(final Map<String, TagAndRelatedPosts> tags, PostDetails postDetails) {

        if (postDetails.getTags() == null) {
            return;
        }

        String tagParts [] = postDetails.getTags().split(",");

        for (String tag : tagParts) {
            if (tag.trim().length() == 0) {
                continue;
            }

            TagAndRelatedPosts tagAndRelatedPosts = tags.get(tag);

            if (tagAndRelatedPosts == null) {
                tagAndRelatedPosts = new TagAndRelatedPosts();
            }

            tagAndRelatedPosts.getPosts().add(postDetails.getUrl());
            tags.put(tag, tagAndRelatedPosts);
        }
    }

    private static List<File> getSourceFiles(File sourceFolder) {

        List<File> sourceFiles = new ArrayList<>();

        for (File sourceFile : sourceFolder.listFiles()) {
            if (!sourceFile.isFile()) continue;
            if (!sourceFile.getName().endsWith(".md")) continue;

            sourceFiles.add(sourceFile);
        }

        Collections.sort(sourceFiles, new Comparator<File>(){

            @Override
            public int compare(File o1, File o2) {
                return o2.getName().compareTo(o1.getName());
            }
        });

        return sourceFiles;
    }

    private static String replaceAttributes(String template, PostDetails postDetails, String categoriesList, String tagsList) {

        String text = template
                .replaceAll("#TITLE#", postDetails.getTitle())
                .replaceAll("#AUTHOR#", postDetails.getAuthor())
                .replaceAll("#DATE#", postDetails.getDate())
                .replaceAll("#CATEGORIES#", categoriesList)
                .replaceAll("#TAGS#", tagsList);
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

    private static PostDetails saveStringToFile(PostDetails postDetails, File targetFolder, String content) throws IOException {

        String dateParts [] = postDetails.getDateOnly().split("-");

        File yearDir = generateDirectory(targetFolder,dateParts[0]);
        File monthDir = generateDirectory(yearDir,dateParts[1]);
        File dayDir = generateDirectory(monthDir,dateParts[2]);
        File nameDir = generateDirectory(dayDir,postDetails.getFileNameWithoutDate());

        File file = new File(nameDir.getAbsolutePath() + File.separator + "index.html");
        FileUtils.writeStringToFile(file, content, "UTF-8");

        return postDetails;
    }

    private static void enhancePostDetails(final PostDetails postDetails, String fileName) {
        String date = postDetails.getDate();
        date = date.substring(0, date.indexOf("T"));
        String fileNameWithoutDate = fileName.replaceAll(date + "-","").replace(".html","");
        String url = date.replaceAll("-","/") + "/" + fileNameWithoutDate + "/index.html";

        postDetails.setDateOnly(date);
        postDetails.setFileNameWithoutDate(fileNameWithoutDate);
        postDetails.setUrl(url);
    }

    private static File generateDirectory(File targetFolder, String newFolderName) {

        File newFolder = new File(targetFolder.getAbsolutePath() + File.separator + newFolderName);

        if (!newFolder.exists()) {
            newFolder.mkdir();
        }
        return newFolder;
    }
}