package net.hydrogen2oxygen.markdowntohomepage.transformator;

public class StringUtility {

    public static String cleanString(String content) {
        String cleaned = content;
        cleaned = cleaned.replaceAll("\r\n", "\n");
        return cleaned;
    }

    public static boolean isEmpty(String text) {

        if (null == text) return true;
        if (text.trim().length() == 0) return true;

        return false;
    }
}
