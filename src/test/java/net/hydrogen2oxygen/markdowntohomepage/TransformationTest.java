package net.hydrogen2oxygen.markdowntohomepage;

import org.apache.commons.io.FileUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * See library from Atlassian https://github.com/atlassian/commonmark-java
 */
public class TransformationTest {

    /**
     * First I need to know that the commonmark lib works
     */
    @Test
    public void testSimpleTransformation() {

        Parser parser = Parser.builder().build();
        Node document = parser.parse("This is *Sparta*");
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String result = renderer.render(document);
        Assert.assertEquals("<p>This is <em>Sparta</em></p>\n", result);
    }

    /**
     * Next check if it is able to transform a file
     *
     * @throws IOException
     */
    @Test
    public void testFileTransformation() throws IOException {

        String markdownString = loadStringFromFile("test_simple_markdowns.md");
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownString);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String result = renderer.render(document);
        System.out.println(result);
        Assert.assertTrue(result.contains("<p>Alternatively, for H1 and H2, an underline-ish style:</p>"));

        String htmlFileContent = loadStringFromFile("testHeader.html") + result + loadStringFromFile("testFooter.html");
        saveStringToFile("target/testResult.html", htmlFileContent);
    }

    private String loadStringFromFile(String filePath) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filePath).getFile());
        return FileUtils.readFileToString(file, "UTF-8");
    }

    private void saveStringToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        FileUtils.writeStringToFile(file, content, "UTF-8");
    }
}
