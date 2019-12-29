package net.hydrogen2oxygen.markdowntohomepage.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hydrogen2oxygen.markdowntohomepage.transformator.RepairUtility;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDetails {

    private String title;
    private String author;
    private String type;
    private String description;
    private String draft;
    private String date; // Format 2018-01-17T08:57:54+00:00
    private String dateOnly; // Format 2018-01-17
    private String fileNameWithoutDate;
    private String url; // /2018/01/17/la-differenza-tra-rimedi-policresti-e-rimedi-piccoli/
    private String categories;
    private String tags;
    private String transformedHTML;

    public void initNewPostDetails(Website website) {
        title = "New Post";
        author = website.getAuthor();
        draft = "true";
        date = LocalDateTime.now().toString();
        dateOnly = date.substring(0,10);
        url = "/" + dateOnly.replaceAll("-","/") + "/" + RepairUtility.convertTitelToUrlFragment(title);
    }

    public Boolean isDraft() {
        if (draft != null && Boolean.valueOf(draft)) {
            return true;
        }

        return false;
    }
}
