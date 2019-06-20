package net.hydrogen2oxygen.markdowntohomepage.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostAndUrl {

    private String title;
    private String url;
}
