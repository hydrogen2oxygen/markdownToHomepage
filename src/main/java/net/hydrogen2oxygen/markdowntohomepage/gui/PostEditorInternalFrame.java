package net.hydrogen2oxygen.markdowntohomepage.gui;

import net.hydrogen2oxygen.markdowntohomepage.domain.Website;

import javax.swing.*;
import java.io.File;

public class PostEditorInternalFrame extends JInternalFrame {

    private File postFile;
    private Website website;

    public PostEditorInternalFrame(Website website, String postFileName) {

        postFile = new File(website.getSourceFolder() + "/content/posts/");

        setTitle(website.getName() + " - Post: " + postFileName);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setClosable(true);
        setResizable(true);

        loadPost();
    }

    private void loadPost() {

    }
}
