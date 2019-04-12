package net.hydrogen2oxygen.markdowntohomepage.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDetails {

    private String title;
    private String author;
    private String type;
    private String date; // Format 2018-01-17T08:57:54+00:00
    private String dateOnly; // Format 2018-01-17
    private String fileNameWithoutDate;
    private String url; // /2018/01/17/la-differenza-tra-rimedi-policresti-e-rimedi-piccoli/
    private String categories;
    private String tags;
    private String transformedHTML;
}