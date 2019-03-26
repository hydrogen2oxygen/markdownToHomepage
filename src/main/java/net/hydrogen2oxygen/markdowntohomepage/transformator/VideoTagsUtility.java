package net.hydrogen2oxygen.markdowntohomepage.transformator;

public class VideoTagsUtility {

    public static final String YOUTUBE_START_TAG = "{{< youtube ";
    public static final String END_TAG = " >}}";

    private VideoTagsUtility() {}
    
    public static String replaceVideoTags(final String html) {
        
        String replacedString = replaceYoutubeTags(html);
        replacedString = replaceVimeoTags(replacedString);
        return replacedString;
    }

    private static String replaceYoutubeTags(String html) {

        int count = 0;

        while (html.contains(YOUTUBE_START_TAG) && html.contains(END_TAG)) {
            String template = "<div style=\"position: relative; padding-bottom: 56.25%; padding-top: 30px; height: 0; overflow: hidden;\">\n" +
                    "<iframe src=\"//www.youtube.com/embed/%s\" style=\"position: absolute; top: 0; left: 0; width: 100%; height: 100%;\" allowfullscreen frameborder=\"0\" title=\"YouTube Video\"></iframe>\n" +
                    "</div>";

            String youTubeTag = html.substring(html.indexOf(YOUTUBE_START_TAG), html.indexOf(END_TAG) + END_TAG.length());
            System.out.println(youTubeTag);
            String youTubeTagParts [] = youTubeTag.split(" ");
            String embeddedVideoString = template.replace("%s",youTubeTagParts[2]);
            html = html.replace(youTubeTag, embeddedVideoString);

            count++;

            if (count > 10) {
                break;
            }
        }

        return html;
    }

    private static String replaceVimeoTags(String html) {

        return html;
    }
}
