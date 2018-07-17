package net.hydrogen2oxygen.markdowntohomepage.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Website {

    private String name;
    private String sourceFolder;
    private String targetFolder;
    private String headerFile;
    private String footerFile;
}