package net.hydrogen2oxygen.markdowntohomepage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hydrogen2oxygen.markdowntohomepage.domain.ConfigurationObject;
import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class WebsiteService {

    @Value("${configFile:config.json}")
    private String configFilePath;

    public ConfigurationObject getConfigurationObject() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(ConfigurationObject.class, Website.class);
        ConfigurationObject configurationObject;
        File configFile = new File(configFilePath);

        if (!configFile.exists()) {
            configurationObject = new ConfigurationObject();
            saveConfigurationObject(configurationObject);
        } else {
            configurationObject = mapper.readValue(configFile, ConfigurationObject.class);
        }

        return configurationObject;
    }

    public Collection<Website> loadAllWebsites() throws IOException {
        ConfigurationObject configurationObject = getConfigurationObject();
        List<Website> list = new ArrayList<Website>(configurationObject.getWebsites().values());
        return list;
    }

    public Website getByName(String name) throws IOException {

        Collection<Website> websites = loadAllWebsites();

        for (Website website: websites) {
            if (name.equals(website.getName())) {
                return website;
            }
        }

        return null;
    }

    public void createOrUpdateWebsite(Website website) throws IOException {

        ConfigurationObject configurationObject = getConfigurationObject();
        configurationObject.getWebsites().put(website.getName(), website);
        saveConfigurationObject(configurationObject);
    }

    public void saveConfigurationObject(ConfigurationObject configurationObject) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        removeEmptyWebsite(configurationObject);

        mapper.writeValue(new File("config.json"), configurationObject);
    }

    private void removeEmptyWebsite(ConfigurationObject configurationObject) {
        configurationObject.getWebsites().remove(null);
    }

    public void delete(Website website) throws IOException {

        ConfigurationObject configurationObject = getConfigurationObject();
        configurationObject.getWebsites().remove(website.getName());
        saveConfigurationObject(configurationObject);
    }
}