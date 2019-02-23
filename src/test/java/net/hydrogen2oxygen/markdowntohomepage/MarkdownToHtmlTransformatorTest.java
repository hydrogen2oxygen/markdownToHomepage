package net.hydrogen2oxygen.markdowntohomepage;

import net.hydrogen2oxygen.markdowntohomepage.domain.MarkDownDocument;
import net.hydrogen2oxygen.markdowntohomepage.transformator.MarkdownToHtmlTransformator;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class MarkdownToHtmlTransformatorTest {

    @Test
    public void testRemoveMarkDownMetadata() throws IOException {
        String md = FileUtils.readFileToString(new File("src/test/resources/2012-03-05-meissner-effect.md"), "UTF-8");
        MarkDownDocument document = MarkdownToHtmlTransformator.extractMarkdownDocumentAndMetaData(md);
        Assert.assertTrue(md.contains("2012-03-05T10:04:45+00:00"));
        Assert.assertTrue(!document.getContent().contains("2012-03-05T10:04:45+00:00"));
        Assert.assertTrue(document.getContent().length() > 0);
        Assert.assertTrue(document.getMetaData().size() > 0);
        Assert.assertTrue(document.getMetaData().get("tags").length() > 0);
        Assert.assertTrue("Uncategorized".equals(document.getMetaData().get("categories")));
    }

    @Test
    public void testNegativeScenarios() {

        try {
            String transformedHTML = MarkdownToHtmlTransformator.
                    builder().
                    source(new File("src/test/resources/2012-03-05-meissner-effect.md")).
                    build();
            Assert.assertTrue(!transformedHTML.contains("2012-03-05T10:04:45+00:00"));
            Assert.assertTrue(transformedHTML.length() > 0);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("A transformation failed, but it should have worked with just a source input!");
        }
    }

}
