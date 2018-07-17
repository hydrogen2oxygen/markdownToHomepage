package net.hydrogen2oxygen.markdowntohomepage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hydrogen2oxygen.markdowntohomepage.domain.ConfigurationObject;
import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

@Component
public class WebsiteService {

    @Value("${configFile:config.json}")
    private String configFilePath;

    public Collection<Website> loadAllWebsites() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ConfigurationObject configurationObject;
        File configFile = new File(configFilePath);

        if (!configFile.exists()) {
            configurationObject = new ConfigurationObject();
        } else {
            configurationObject = mapper.readValue(configFile, ConfigurationObject.class);
        }

        return configurationObject.getWebsites();
    }
}