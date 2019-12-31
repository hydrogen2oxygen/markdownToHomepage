package net.hydrogen2oxygen.markdowntohomepage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Setter;
import net.hydrogen2oxygen.markdowntohomepage.domain.ConfigurationObject;
import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import net.hydrogen2oxygen.markdowntohomepage.gui.ICallback;
import net.hydrogen2oxygen.markdowntohomepage.transformator.StringUtility;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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

    public List<Website> loadAllWebsites() throws IOException {
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

    public void synchronizeWebsite(final Website website, ICallback callback) throws IOException, GitAPIException, JSchException {

        new Runnable(){

            @Override
            public void run() {

                try {
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

                    if (!new File(website.getSourceFolder() + "/content").exists()) {
                        cloneRepository(website);
                    } else {
                        synchronizeRepository(website);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    callback.execute(website);
                }
            }
        }.run();
    }

    private void synchronizeRepository(Website website) throws IOException, GitAPIException {

        System.out.println("Synchronize repo " + website.getGitUrl() + " ...");
        SshSessionFactory sshSessionFactory = getSshSessionFactory(website);
        Repository repository = new FileRepository(new File(website.getSourceFolder() + "/.git/"));
        Git git = new Git(repository);

        PullCommand pullCommand = git.pull();
        CredentialsProvider credentials = new UsernamePasswordCredentialsProvider(website.getGitUser(), website.getGitPassword());
        pullCommand.setCredentialsProvider(credentials);
        pullCommand.setTransportConfigCallback(getTransportConfigCallback(sshSessionFactory));
        PullResult pullResult = pullCommand.call();
        System.out.println(pullResult);
    }

    private void cloneRepository(Website website) throws GitAPIException {

        System.out.println("Clone repo " + website.getGitUrl() + " ...");

        SshSessionFactory sshSessionFactory = getSshSessionFactory(website);

        CloneCommand cloneCommand = Git.cloneRepository();
        cloneCommand.setURI( website.getGitUrl() );
        cloneCommand.setDirectory(new File(website.getSourceFolder()));
        cloneCommand.setTransportConfigCallback(getTransportConfigCallback(sshSessionFactory));
        cloneCommand.call();
    }

    private TransportConfigCallback getTransportConfigCallback(final SshSessionFactory sshSessionFactory) {
        return new TransportConfigCallback() {
            @Override
            public void configure( Transport transport ) {
                SshTransport sshTransport = ( SshTransport )transport;
                sshTransport.setSshSessionFactory( sshSessionFactory );
            }
        };
    }

    private SshSessionFactory getSshSessionFactory(Website website) {
        return new JschConfigSessionFactory() {
                @Override
                protected void configure(OpenSshConfig.Host host, Session session ) {
                    session.setConfig("StrictHostKeyChecking", "no");
                    session.setPassword(website.getGitPassword());
                }
            };
    }

    private String getWebsiteHost(Website website) {

        if (website.getGitUrl().contains("ssh")) {

            String host = website.getGitUrl().replace("ssh://","");
            host = host.substring(0,host.indexOf(":"));
            return host;
        }
        return  website.getGitUrl();
    }

    public void commit() {

        try {
            for (Website website : loadAllWebsites()) {

                System.out.println("Commit " + website.getName() + " ... ");
                Repository repository = new FileRepository(new File(website.getSourceFolder() + "/.git/"));
                Git git = new Git(repository);
                git.add().addFilepattern(".").call();
                git.commit().setMessage("Initial commit " + Calendar.getInstance().getTimeInMillis()).call();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    public void pull() {

        try {
            for (Website website : loadAllWebsites()) {
                synchronizeRepository(website);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    public void push() {

        try {
            for (Website website : loadAllWebsites()) {

                Repository repository = new FileRepository(new File(website.getSourceFolder() + "/.git/"));
                SshSessionFactory sshSessionFactory = getSshSessionFactory(website);
                Git git = new Git(repository);
                PushCommand pushCommand = git.push();
                CredentialsProvider credentials = new UsernamePasswordCredentialsProvider(website.getGitUser(), website.getGitPassword());
                pushCommand.setCredentialsProvider(credentials);
                pushCommand.setTransportConfigCallback(getTransportConfigCallback(sshSessionFactory));
                pushCommand.call();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }
}
