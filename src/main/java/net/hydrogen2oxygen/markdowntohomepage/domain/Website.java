package net.hydrogen2oxygen.markdowntohomepage.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Website {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String repositoryUrl;
    private String repositoryUsername;
    private String repositoryPassword;
    private String sourceFolder;
    private String targetFolder;
    private String headerFile;
    private String footerFile;
}