package net.hydrogen2oxygen.markdowntohomepage.transformator;

public class StringUtility {

    public static String cleanString(String content) {
        String cleaned = content;
        cleaned = cleaned.replaceAll("\r\n", "\n");
        return cleaned;
    }
}
