package net.hydrogen2oxygen.markdowntohomepage.transformator;

import net.hydrogen2oxygen.markdowntohomepage.domain.PostDetails;
import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import net.hydrogen2oxygen.markdowntohomepage.service.WebsiteService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.Collection;

public class RepairUtility {

    public static void main(String [] args) throws Exception {

        WebsiteService websiteService = new WebsiteService();
        websiteService.setConfigFilePath("data/config.json");

        Collection<Website> websiteList = websiteService.loadAllWebsites();

        for (Website website : websiteList) {

            File folder = new File(website.getSourceFolder());

            for (File file : folder.listFiles()) {

                if (file.getName().endsWith(".md")) {
                    file = repairName(file);
                    System.out.println(file.getName());
                }
            }
        }
    }

    /**
     * Rename to convention 2018-01-21-TITLE-WITH-SPACES.md
     * @param file
     * @return
     * @throws IOException
     */
    public static File repairName(File file) throws IOException {

        if (!file.getName().startsWith("20")) {
            String content = FileUtils.readFileToString(file, "UTF-8");
            PostDetails postDetails = new PostDetails();
            PostDetailsUtility.prefillPostDetails(content, postDetails);

            if (postDetails.getTitle() == null) {
                System.out.println(file.getName());
            }

            String newName = file.getParent() + File.separator + postDetails.getDate().substring(0,10) + "-" + postDetails.getTitle().trim()
                    .replaceAll(" ","-")
                    .replaceAll("\"","")
                    .replaceAll("'","-")
                    .replaceAll("ä","ae")
                    .replaceAll("ö","oe")
                    .replaceAll("ü","ue")
                    + ".md";
            File fileRenamed = new File(newName);
            if (fileRenamed.exists()) {
                throw new IOException("This file already exists: " + newName);
            }
            file.renameTo(fileRenamed);
            System.out.println(fileRenamed.getAbsolutePath());
        }

        return file;
    }
}
