package net.hydrogen2oxygen.markdowntohomepage;

import net.hydrogen2oxygen.markdowntohomepage.transformator.MarkdownToHtmlTransformator;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class MarkdownToHtmlTransformatorTest {

    @Test
    public void testRemoveMarkDownMetadata() throws IOException {
        String md = FileUtils.readFileToString(new File("src/test/resources/2012-03-05-meissner-effect.md"),"UTF-8");
        String mdWithoutMetaData = MarkdownToHtmlTransformator.removeMetaData(md);
        Assert.assertTrue(md.contains("2012-03-05T10:04:45+00:00"));
        Assert.assertTrue(!mdWithoutMetaData.contains("2012-03-05T10:04:45+00:00"));
    }
}