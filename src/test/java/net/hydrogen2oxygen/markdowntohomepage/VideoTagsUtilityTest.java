package net.hydrogen2oxygen.markdowntohomepage;

import net.hydrogen2oxygen.markdowntohomepage.transformator.VideoTagsUtility;
import org.junit.Assert;
import org.junit.Test;

public class VideoTagsUtilityTest {

    @Test
    public void test() {

        String html = "<body>\n{{< youtube o42C6ajjqWg >}}\n{{< youtube Ta6wdv4lVKM >}}\n</body>";
        String expected = "<body>\n<div style=\"position: relative; padding-bottom: 56.25%; padding-top: 30px; height: 0; overflow: hidden;\">\n" +
                "<iframe src=\"//www.youtube.com/embed/o42C6ajjqWg\" style=\"position: absolute; top: 0; left: 0; width: 100%; height: 100%;\" allowfullscreen frameborder=\"0\" title=\"YouTube Video\"></iframe>\n" +
                "</div>\n"+
                "<div style=\"position: relative; padding-bottom: 56.25%; padding-top: 30px; height: 0; overflow: hidden;\">\n" +
                "<iframe src=\"//www.youtube.com/embed/Ta6wdv4lVKM\" style=\"position: absolute; top: 0; left: 0; width: 100%; height: 100%;\" allowfullscreen frameborder=\"0\" title=\"YouTube Video\"></iframe>\n" +
                "</div>\n</body>";

        html = VideoTagsUtility.replaceVideoTags(html);

        Assert.assertEquals(expected, html);
    }
}
