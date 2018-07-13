package net.hydrogen2oxygen.markdowntohomepage.transformator;

import lombok.Getter;
import net.hydrogen2oxygen.markdowntohomepage.domain.MarkDownDocument;

public class MetaDataExtractor {

    private String lastTag;

    @Getter
    private MarkDownDocument document;

    public MetaDataExtractor(MarkDownDocument document) {
        this.document = document;
    }

    public void extractMetaData(String line) {

        if (line.contains(":")) {
            lastTag = cleanMetaData(line);
            return;
        }

        if (line.contains("-") && lastTag != null) {
            setMetaData(line);
        }
    }

    private void setMetaData(String line) {

        String metaData = document.getMetaData().get(lastTag);

        if (metaData == null) {
            metaData = cleanMetaData(line);
        } else {
            metaData += "," + cleanMetaData(line);
        }

        document.getMetaData().put(lastTag, metaData);
    }

    private String cleanMetaData(String line) {

        return line.replaceAll(",", "").replaceAll(":", "").replaceAll("-", "").trim();
    }
}
