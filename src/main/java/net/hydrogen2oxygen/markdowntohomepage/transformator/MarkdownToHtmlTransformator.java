package net.hydrogen2oxygen.markdowntohomepage.transformator;

import lombok.Builder;
import net.hydrogen2oxygen.markdowntohomepage.domain.MarkDownDocument;
import org.apache.commons.io.FileUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;

public class MarkdownToHtmlTransformator {

    public static final String NEWLINE = "\n";

    @Builder
    public static String transformMarkDownToHtml(File source, String headerContent, String footerContent, String url, boolean linkTitle) {

        String markdownString = readFileToString(source);
        MarkDownDocument markDownDocument = extractMarkdownDocumentAndMetaData(markdownString);

        Parser parser = Parser.builder().build();
        Node document = parser.parse(markDownDocument.getContent());
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String htmlRendered = renderer.render(document);

        String h1Title = "";

        if (markDownDocument.getMetaData().get("title") != null && linkTitle) {
            h1Title = "<h1><a class=\"title\" href=\"" +  url + "\">" + markDownDocument.getMetaData().get("title").replaceAll("\"","") + "</a></h1>\n";
        } else if (markDownDocument.getMetaData().get("title") != null) {
            h1Title = "<h1>" + markDownDocument.getMetaData().get("title").replaceAll("\"","") + "</h1>\n";
        }

        String htmlFileContent = headerContent + h1Title + htmlRendered + footerContent;

        return htmlFileContent;
    }

    private static String readFileToString(File file) {
        try {
            return FileUtils.readFileToString(file, "UTF-8");
        } catch(Exception e) {
            return "";
        }
    }

    public static MarkDownDocument extractMarkdownDocumentAndMetaData(String markdownContent) {

        MarkDownDocument document = new MarkDownDocument();
        String md = StringUtility.cleanString(markdownContent);

        java.lang.StringBuilder strBuilder = new java.lang.StringBuilder();
        String [] lines = md.split(NEWLINE);
        boolean metaDataFound = false;
        boolean metaDataRemoved = false;
        MetaDataExtractor metaDataExtractor = new MetaDataExtractor(document);

        for (String line : lines) {

            if (!metaDataFound && line.equals("---")) {
                metaDataFound = true;
                continue;
            }

            if (!metaDataRemoved && !line.equals("---")) {
                metaDataExtractor.extractMetaData(line);
                continue;
            }

            if (!metaDataRemoved && line.equals("---")) {
                metaDataRemoved = true;
                continue;
            }

            if (metaDataFound && !metaDataRemoved) {
                continue;
            }

            strBuilder.append(line).append(NEWLINE);
        }

        document = metaDataExtractor.getDocument();
        String content = VideoTagsUtility.replaceVideoTags(strBuilder.toString());
        document.setContent(content);

        return document;
    }
}
