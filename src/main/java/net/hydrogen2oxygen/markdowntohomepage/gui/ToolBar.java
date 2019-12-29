package net.hydrogen2oxygen.markdowntohomepage.gui;

import com.jcraft.jsch.JSchException;
import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import net.hydrogen2oxygen.markdowntohomepage.service.WebsiteService;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collection;

public class ToolBar extends JInternalFrame {

    private JComboBox websitesSelectBox;

    public ToolBar(WebsiteService websiteService) {
        setLayout(null);
        setTitle("TOOLS");
        setResizable(false);
        setBorder(null);
        setBounds(0,0,1000,55);

        websitesSelectBox = new JComboBox();
        websitesSelectBox.setBounds(0,0,200,30);
        add(websitesSelectBox);

        try {
            Collection<Website> websites = websiteService.loadAllWebsites();
            for (Website website : websites) {
                websitesSelectBox.addItem(website.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        websitesSelectBox.addActionListener(e -> {
            JComboBox cb = (JComboBox)e.getSource();
            String websiteName = (String)cb.getSelectedItem();
            System.out.println(websiteName);

            try {
                Website website = websiteService.getByName(websiteName);
                MarkdownToHomepageGui.getInstance().getWebsiteService().synchronizeWebsite(website, object -> {

                });
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (GitAPIException ex) {
                ex.printStackTrace();
            } catch (JSchException ex) {
                ex.printStackTrace();
            }
        });

        JButton newPostButton = new JButton("New Post");
        newPostButton.addActionListener(e -> callWebsiteAction("New Post"));
        newPostButton.setBounds(202,0,100,30);
        add(newPostButton);

        JButton generateButton = new JButton("Generate");
        generateButton.addActionListener(e -> callWebsiteAction("Generate"));
        generateButton.setBounds(304,0,100,30);
        add(generateButton);

        JButton serveButton = new JButton("Serve");
        serveButton.addActionListener(e -> callWebsiteAction("Serve"));
        serveButton.setBounds(406,0,100,30);
        add(serveButton);

        JButton loadButton = new JButton("FTP-Upload");
        loadButton.addActionListener(e -> callWebsiteAction("FTP-Upload"));
        loadButton.setBounds(508,0,100,30);
        add(loadButton);

        setVisible(true);
    }

    private void callWebsiteAction(String actionName) {
        ActionEvent event = new ActionEvent(this, 0, actionName + " " + websitesSelectBox.getSelectedItem());
        MarkdownToHomepageGui.getInstance().actionPerformed(event);
    }
}
