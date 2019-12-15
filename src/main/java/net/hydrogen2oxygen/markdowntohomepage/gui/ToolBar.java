package net.hydrogen2oxygen.markdowntohomepage.gui;

import com.jcraft.jsch.JSchException;
import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import net.hydrogen2oxygen.markdowntohomepage.service.WebsiteService;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class ToolBar extends JInternalFrame {

    public ToolBar(WebsiteService websiteService) {
        setLayout(null);
        setTitle("TOOLS");
        setResizable(false);
        setBorder(null);
        setBounds(0,0,1000,55);

        JComboBox websitesSelectBox = new JComboBox();
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

        JComboBox postSelectBox = new JComboBox();
        postSelectBox.setBounds(202,0,300,30);
        add(postSelectBox);

        websitesSelectBox.addActionListener(e -> {
            JComboBox cb = (JComboBox)e.getSource();
            String websiteName = (String)cb.getSelectedItem();
            System.out.println(websiteName);

            try {
                postSelectBox.removeAllItems();
                Website website = websiteService.getByName(websiteName);
                MarkdownToHomepageGui.getInstance().getWebsiteService().synchronizeWebsite(website, object -> {
                    File folder = new File(website.getSourceFolder());
                    for (File file : folder.listFiles()) {
                        if (file.getName().endsWith("md")) {
                            postSelectBox.addItem(file.getName());
                        }
                    }
                });
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (GitAPIException ex) {
                ex.printStackTrace();
            } catch (JSchException ex) {
                ex.printStackTrace();
            }
        });

        postSelectBox.addActionListener(e -> {
            JComboBox cb = (JComboBox)e.getSource();
            String postName = (String)cb.getSelectedItem();
            System.out.println(postName);
        });

        JButton newPostButton = new JButton("New Post");
        newPostButton.addActionListener(e -> System.out.println("New Post"));
        newPostButton.setBounds(504,0,100,30);
        add(newPostButton);

        JButton generateButton = new JButton("Generate");
        generateButton.addActionListener(e -> System.out.println("Generate"));
        generateButton.setBounds(606,0,100,30);
        add(generateButton);

        setVisible(true);
    }
}
