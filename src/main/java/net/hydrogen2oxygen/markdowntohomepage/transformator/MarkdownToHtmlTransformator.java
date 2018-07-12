package net.hydrogen2oxygen.markdowntohomepage.transformator;

import lombok.Builder;
import org.apache.commons.io.FileUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;
import java.io.IOException;

/**
 * TODO create one additional page for each tag and category (similar to wordpress)
 */
public class MarkdownToHtmlTransformator {

    @Builder
    public static String transformMarkDownToHtml(File source, File header, File footer) throws IOException {

        String markdownString = FileUtils.readFileToString(source, "UTF-8");

        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownString);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String result = renderer.render(document);

        String headerContent = FileUtils.readFileToString(header, "UTF-8");
        String footerContent = FileUtils.readFileToString(footer, "UTF-8");
        String htmlFileContent = headerContent + result + footerContent;

        return htmlFileContent;
    }
}