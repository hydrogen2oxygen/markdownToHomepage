package net.hydrogen2oxygen.markdowntohomepage.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class DataService {

    public static final String BLOGS_FOLDER = "blogs/";
    private Log log = LogFactory.getLog(DataService.class);

    private Map<String, File> databases = new HashMap<>();

    @PostConstruct
    public void init() {

        log.info("Initializing repositories ...");

        try {
            // Refresh all repositories ... load from database
            //getRepository("https://github.com/....git", "blogName");
        } catch (Exception e) {
            log.error("Error while refreshing repositories!", e);
            return;
        }

        log.info("... data repo is refreshed.");
    }

    public void getRepository(String url, String targetFolderName) throws GitAPIException, IOException {

        File repoDirectory = new File(BLOGS_FOLDER + targetFolderName);

        if (repoDirectory.isDirectory() && repoDirectory.listFiles().length > 0) {

            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            repositoryBuilder.findGitDir(repoDirectory);
            repositoryBuilder.setMustExist(true);
            Repository repository = repositoryBuilder.build();

            Git git = new Git(repository);

            StoredConfig config = git.getRepository().getConfig();

            log.info(git.getRepository().getConfig().toText());
            StatusCommand statusCommand = git.status();
            statusCommand.call();

            git.pull().call();

        } else {

            Git git = Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(new File(BLOGS_FOLDER + targetFolderName))
                    .call();

        }
    }
}
