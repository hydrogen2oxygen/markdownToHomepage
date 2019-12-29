package net.hydrogen2oxygen.markdowntohomepage.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
    private String author;
    private String title;
    private String description;
    private String baseUrl;
    private String sourceFolder;
    private String targetFolder;
    private String headerFile;
    private String footerFile;
    private String gitUrl;
    private String gitUser;
    private String gitPassword;
    private String ftpUser;
    private String ftpPassword;
    private String ftpHost;
    private Integer ftpPort;
    private String ftpRootPath;

    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }
}
