package net.hydrogen2oxygen.markdowntohomepage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Setter;
import net.hydrogen2oxygen.markdowntohomepage.domain.ConfigurationObject;
import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import net.hydrogen2oxygen.markdowntohomepage.transformator.StringUtility;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class WebsiteService {

    @Setter
    @Value("${configFile:config.json}")
    private String configFilePath;

    public ConfigurationObject getConfigurationObject() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(ConfigurationObject.class, Website.class);
        ConfigurationObject configurationObject;
        File configFile = new File(configFilePath);

        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdir();
        }

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

        for (Website website : websites) {
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

        mapper.writeValue(new File(configFilePath), configurationObject);
    }

    private void removeEmptyWebsite(ConfigurationObject configurationObject) {
        configurationObject.getWebsites().remove(null);
    }

    public void delete(Website website) throws IOException {

        delete(website.getName());
    }

    public void delete(String name) throws IOException {

        ConfigurationObject configurationObject = getConfigurationObject();
        configurationObject.getWebsites().remove(name);
        saveConfigurationObject(configurationObject);
    }

    public void synchronizeWebsite(final Website website) throws IOException, GitAPIException, JSchException {

        if (StringUtility.isEmpty(website.getGitUrl())) {
            System.err.println("Website config has no git url!");
            return;
        }

        if (StringUtility.isEmpty(website.getSourceFolder())) {
            File sourceDir = new File(new File(configFilePath).getParentFile().getAbsolutePath() + "/" + website.getName().replaceAll(" ", ""));

            if (!sourceDir.exists()) {
                sourceDir.mkdir();
                website.setSourceFolder(sourceDir.getAbsolutePath());
                createOrUpdateWebsite(website);
            }
        }

        SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session ) {
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword(website.getGitPassword());
            }
        };

        CloneCommand cloneCommand = Git.cloneRepository();
        cloneCommand.setURI( website.getGitUrl() );
        cloneCommand.setDirectory(new File(website.getSourceFolder()));
        cloneCommand.setTransportConfigCallback( new TransportConfigCallback() {
            @Override
            public void configure( Transport transport ) {
                SshTransport sshTransport = ( SshTransport )transport;
                sshTransport.setSshSessionFactory( sshSessionFactory );
            }
        } );

        cloneCommand.call();
    }

    private String getWebsiteHost(Website website) {

        if (website.getGitUrl().contains("ssh")) {

            String host = website.getGitUrl().replace("ssh://","");
            host = host.substring(0,host.indexOf(":"));
            return host;
        }
        return  website.getGitUrl();
    }
}