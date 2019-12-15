package net.hydrogen2oxygen.markdowntohomepage.transformator;

import com.redfin.sitemapgenerator.WebSitemapGenerator;
import lombok.Builder;
import net.hydrogen2oxygen.markdowntohomepage.domain.PostAndUrl;
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

    public static final String UTF_8 = "UTF-8";
    private static Logger logger = LoggerFactory.getLogger(TransformFolder.class);

    @Builder
    public static void transformFolder(Website website) throws IOException {

        Map<String, TagAndRelatedPosts> tags = new HashMap<>();
        RssFeedGenerator rssFeedGenerator = new RssFeedGenerator(website);

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
            createMissingStructure(new File(website.getHeaderFile()));
            headerContentTemplate = FileUtils.readFileToString(new File(website.getHeaderFile()), UTF_8);
        }

        if (!StringUtils.isEmpty(website.getFooterFile())) {
            createMissingStructure(new File(website.getFooterFile()));
            footerContentTemplate = FileUtils.readFileToString(new File(website.getFooterFile()), UTF_8);
        }

        StringBuilder indexContent = new StringBuilder();

        List<File> sourceFiles = getSourceFiles(sourceFolder);
        int count = 0;

        for (File sourceFile : sourceFiles) {

            String content = FileUtils.readFileToString(sourceFile, UTF_8);
            PostDetails postDetails = new PostDetails();
            PostDetailsUtility.prefillPostDetails(content, postDetails);

            if (postDetails.getTitle() == null) {
                System.err.println("Skipping " + sourceFile.getName());
                continue;
            }

            String categoriesList = generateCategoriesHtml(postDetails.getCategories());
            String tagsList = generateTagsHtml(postDetails.getTags(), website.getBaseUrl());
            System.out.println(sourceFile.getName());
            String headerContent = replaceAttributes(headerContentTemplate, postDetails, categoriesList, tagsList, website);
            String footerContent = replaceAttributes(footerContentTemplate, postDetails, categoriesList, tagsList, website);
            enhancePostDetails(postDetails, sourceFile.getName().replace(".md", ".html"));

            collectTagsAndRelatedPosts(tags, postDetails);

            rssFeedGenerator.addEntry(postDetails);

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
        createIndexHtml(targetFolder, headerContentTemplate, footerContentTemplate, indexContent, website);

        // Create Tags Pages
        generateTagsPages(tags, targetFolder, headerContentTemplate, footerContentTemplate, website);

        // Create Tag Cloud
        generateTagCloud(10, tags, targetFolder, website.getBaseUrl());

        // Copy Statics
        copyStatics(sourceFolder, targetFolder);

        // Generate .htaccess file
        FileUtils.writeStringToFile(new File(targetFolder.getAbsolutePath() + "/.htaccess.file"), "ErrorDocument 404 /404.html", UTF_8);

        webSitemapGenerator.write();

        rssFeedGenerator.generate(targetFolder.getAbsolutePath() + "/feed.rss");
    }

    private static void createMissingStructure(File file) throws IOException {
        // Init File and Folder structure if not exist
        if (!file.exists()) {
            // Check if folder exist
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            FileUtils.touch(file);
        }
    }

    private static void generateTagCloud(final int maxFontSize, Map<String, TagAndRelatedPosts> tags, File targetFolder, String baseUrl) throws IOException {

        // determine max size
        Integer maxSize = null;
        Integer minSize = null;

        for (String tag : tags.keySet()) {
            TagAndRelatedPosts tagAndRelatedPosts = tags.get(tag);

            if (maxSize == null) {
                maxSize = tagAndRelatedPosts.getPosts().size();
            }

            if (minSize == null) {
                minSize = tagAndRelatedPosts.getPosts().size();
            }

            if (tagAndRelatedPosts.getPosts().size() > maxSize) {
                maxSize = tagAndRelatedPosts.getPosts().size();
            }

            if (tagAndRelatedPosts.getPosts().size() < minSize) {
                minSize = tagAndRelatedPosts.getPosts().size();
            }
        }

        StringBuilder tagCloud = new StringBuilder();

        for (String tag : tags.keySet()) {
            TagAndRelatedPosts tagAndRelatedPosts = tags.get(tag);

            int displayFontSize = 1;

            if (tagAndRelatedPosts.getPosts().size() > minSize) {
                displayFontSize = (maxFontSize * (tagAndRelatedPosts.getPosts().size() - minSize)) / (maxSize - minSize);
            }

            if (tagCloud.length() > 0) {
                tagCloud.append(",  ");
            }

            String cleanedTag = tag.replaceAll(" ", "_").replaceAll("/", "_");
            cleanedTag = cleanNameDirectory(cleanedTag);
            tagCloud.append(String.format("<a class=\"cloud%s\" href=\"%s/tags/%s/\">%s</a>", baseUrl, displayFontSize, cleanedTag, tag));
        }

        FileUtils.writeStringToFile(new File(targetFolder.getAbsolutePath() + "/tagCloud.html"), tagCloud.toString(), UTF_8);
    }

    private static void generateTagsPages(Map<String, TagAndRelatedPosts> tags, File targetFolder, String headerContentTemplate, String footerContentTemplate, Website website) throws IOException {
        generateDirectory(targetFolder, "tags");
        File tagFolder = new File(targetFolder.getAbsolutePath() + "/tags");

        for (String tag : tags.keySet()) {
            TagAndRelatedPosts tagAndRelatedPosts = tags.get(tag);
            String headerContent = replaceAttributes(headerContentTemplate, tag, website);
            String footerContent = replaceAttributes(footerContentTemplate, tag, website);
            StringBuilder tagPageContent = new StringBuilder();
            tagPageContent.append(headerContent);
            tagPageContent.append("<h1>" + tag + "</h1>");
            tagPageContent.append("<ul>");

            String cleanedTag = tag.replaceAll(" ", "_").replaceAll("/", "_");
            cleanedTag = cleanNameDirectory(cleanedTag);

            collectUrlsForEachTag(tagAndRelatedPosts, tagPageContent, website.getBaseUrl());

            tagPageContent.append("</ul>");
            tagPageContent.append(footerContent);

            File newFolder = generateDirectory(tagFolder, cleanedTag);
            FileUtils.writeStringToFile(new File(newFolder.getAbsolutePath() + File.separator + "index.html"), tagPageContent.toString(), UTF_8);
        }
    }

    private static void collectUrlsForEachTag(TagAndRelatedPosts tagAndRelatedPosts, StringBuilder tagPageContent, String baseUrl) {
        for (PostAndUrl postAndUrl : tagAndRelatedPosts.getPosts()) {
            String url = String.format("<li><a href=\"%s/%s\">%s</a></li>\n", baseUrl, postAndUrl.getUrl().replaceAll(" ", "_"), postAndUrl.getTitle());
            tagPageContent.append(url);
        }
    }

    private static void createIndexHtml(File targetFolder, String headerContentTemplate, String footerContentTemplate, StringBuilder indexContent, Website website) throws IOException {
        PostDetails postDetails = new PostDetails();
        postDetails.setTitle("");
        String headerContent = replaceAttributes(headerContentTemplate, postDetails, "", "", website);
        String footerContent = replaceAttributes(footerContentTemplate, postDetails, "", "", website);
        String html = headerContent + indexContent.toString() + footerContent;
        FileUtils.writeStringToFile(new File(targetFolder.getAbsolutePath() + File.separator + "index.html"), html, UTF_8);
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

        String tagParts[] = postDetails.getTags().split(",");

        for (String tag : tagParts) {
            if (tag.trim().length() == 0) {
                continue;
            }

            TagAndRelatedPosts tagAndRelatedPosts = tags.get(tag);

            if (tagAndRelatedPosts == null) {
                tagAndRelatedPosts = new TagAndRelatedPosts();
            }

            PostAndUrl postAndUrl = new PostAndUrl();
            postAndUrl.setTitle(postDetails.getTitle());
            postAndUrl.setUrl(postDetails.getUrl());
            tagAndRelatedPosts.getPosts().add(postAndUrl);
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

        Collections.sort(sourceFiles, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                return o2.getName().compareTo(o1.getName());
            }
        });

        return sourceFiles;
    }

    private static String replaceAttributes(String template, PostDetails postDetails, String categoriesList, String tagsList, Website website) {

        try {
            String text = template
                    .replaceAll("#BASEURL#", website.getBaseUrl())
                    .replaceAll("#TITLE#", postDetails.getTitle())
                    .replaceAll("#AUTHOR#", postDetails.getAuthor())
                    .replaceAll("#DATE#", postDetails.getDate())
                    .replaceAll("#CATEGORIES#", categoriesList)
                    .replaceAll("#TAGS#", tagsList);
            return text;
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static String replaceAttributes(String template, String title, Website website) {

        String text = template
                .replaceAll("#TITLE#", title)
                .replaceAll("#BASEURL#", website.getBaseUrl())
                .replaceAll("#AUTHOR#", "")
                .replaceAll("#DATE#", "")
                .replaceAll("#CATEGORIES#", "")
                .replaceAll("#TAGS#", "");
        return text;
    }

    private static String generateTagsHtml(String tags, String baseUrl) {

        if (tags == null || tags.trim().length() == 0) return "";

        StringBuilder str = new StringBuilder();

        str.append("Tags: <ul>");

        String[] parts = tags.split(",");

        for (String tag : parts) {
            String cleanedTag = tag.trim().replaceAll(" ", "_").replaceAll("/", "_");
            cleanedTag = cleanNameDirectory(cleanedTag);
            str.append(String.format("<li><a href=\"%s/tags/%s/\">%s</a></li>", baseUrl, cleanedTag, tag));
        }

        str.append("</ul>");

        return str.toString();
    }

    private static String generateCategoriesHtml(String categories) {
        // TODO
        return "";
    }

    private static PostDetails saveStringToFile(PostDetails postDetails, File targetFolder, String content) throws IOException {

        String dateParts[] = postDetails.getDateOnly().split("-");

        File yearDir = generateDirectory(targetFolder, dateParts[0]);
        File monthDir = generateDirectory(yearDir, dateParts[1]);
        File dayDir = generateDirectory(monthDir, dateParts[2]);
        File nameDir = generateDirectory(dayDir, cleanNameDirectory(postDetails.getFileNameWithoutDate()));

        File file = new File(nameDir.getAbsolutePath() + File.separator + "index.html");
        FileUtils.writeStringToFile(file, content, UTF_8);

        return postDetails;
    }

    private static String cleanNameDirectory(String directoryName) {

        String cleanedDirectoryName = directoryName
                .replaceAll("'", "")
                .replaceAll("\"", "")
                .replaceAll("ä", "ae")
                .replaceAll("Ä", "Ae")
                .replaceAll("Ö", "Oe")
                .replaceAll("ö", "oe")
                .replaceAll("ü", "ue")
                .replaceAll("Ü", "Ue");

        return cleanedDirectoryName;
    }

    private static void enhancePostDetails(final PostDetails postDetails, String fileName) {
        String date = postDetails.getDate();
        date = date.substring(0, date.indexOf("T"));
        String fileNameWithoutDate = fileName.replaceAll(date + "-", "").replace(".html", "").replaceAll("/", "_");
        String url = date.replaceAll("-", "/") + "/" + cleanNameDirectory(fileNameWithoutDate); // + "/index.html";

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
