package net.hydrogen2oxygen.markdowntohomepage.transformator;

import lombok.Getter;
import net.hydrogen2oxygen.markdowntohomepage.domain.MarkDownDocument;
import org.springframework.util.StringUtils;

public class MetaDataExtractor {

    private String lastTag;

    @Getter
    private MarkDownDocument document;

    public MetaDataExtractor(MarkDownDocument document) {
        this.document = document;
    }

    public void extractMetaData(String line) {

        if (line.contains(":")) {

            if (line.split(":").length > 1) {
                String parts [] = line.split(":");
                line = parts[1];
                lastTag = cleanMetaData(parts[0]);
            } else {
                lastTag = cleanMetaData(line);
                return;
            }
        }

        if (!StringUtils.isEmpty(line) && lastTag != null) {
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
