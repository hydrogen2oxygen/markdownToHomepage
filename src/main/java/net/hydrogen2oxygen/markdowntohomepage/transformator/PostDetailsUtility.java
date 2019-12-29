package net.hydrogen2oxygen.markdowntohomepage.transformator;

import net.hydrogen2oxygen.markdowntohomepage.domain.MarkDownDocument;
import net.hydrogen2oxygen.markdowntohomepage.domain.PostDetails;
import org.springframework.util.StringUtils;

public class PostDetailsUtility {

    private PostDetailsUtility() {}

    public static void prefillPostDetails(String markdownString, PostDetails postDetails) {

        if (StringUtils.isEmpty(markdownString)) return;

        MarkDownDocument markDownDocument = MarkdownToHtmlTransformator.extractMarkdownDocumentAndMetaData(markdownString);
        postDetails.setAuthor(markDownDocument.getMetaData().get("author"));
        postDetails.setTitle(markDownDocument.getMetaData().get("title"));
        postDetails.setType(markDownDocument.getMetaData().get("type"));
        postDetails.setDraft(markDownDocument.getMetaData().get("draft"));
        postDetails.setDate(markDownDocument.getMetaData().get("date"));
        postDetails.setUrl(markDownDocument.getMetaData().get("url"));
        postDetails.setTags(markDownDocument.getMetaData().get("tags"));
        postDetails.setCategories(markDownDocument.getMetaData().get("categories"));
    }

    public static String removeDetailsFromMarkdownString(String markdownString) {
        String cleanString = markdownString.replaceAll("\r\n","\n");
        String lines [] = cleanString.split("\n");
        StringBuilder stringBuilder = new StringBuilder();

        int metaDataOccurenceSeparatorCounter = 0;

        for (String line : lines) {
            if ("---".equals(line)) {
                metaDataOccurenceSeparatorCounter++;
                continue;
            }

            if (metaDataOccurenceSeparatorCounter < 2) {
                continue;
            }

            if (stringBuilder.length() > 0) {
                stringBuilder.append("\n");
            }

            stringBuilder.append(line);
        }

        return stringBuilder.toString();
    }
}
