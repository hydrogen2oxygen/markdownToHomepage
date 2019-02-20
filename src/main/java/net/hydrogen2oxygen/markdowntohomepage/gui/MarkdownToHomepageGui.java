package net.hydrogen2oxygen.markdowntohomepage.gui;

import com.jcraft.jsch.JSchException;
import lombok.Getter;
import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import net.hydrogen2oxygen.markdowntohomepage.service.WebsiteService;
import net.hydrogen2oxygen.markdowntohomepage.transformator.StringUtility;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static javax.swing.JSplitPane.DIVIDER;

public class MarkdownToHomepageGui extends JFrame implements ActionListener {

    private static final String TITLE = "MarkdownToHomepage";
    private static MarkdownToHomepageGui instance;
    private JMenuBar menuBar;
    @Getter
    private JDesktopPane desktop;
    private WebsiteService websiteService;

    private MarkdownToHomepageGui() {

        websiteService = new WebsiteService();
        websiteService.setConfigFilePath("./data/config.json");

        setTitle(TITLE);
        setBounds(new Rectangle(0, 0, 1000, 900));
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Colors.desktopPaneBackground);
        getContentPane().setForeground(Color.WHITE);
        createMenuBar();

        desktop = new JDesktopPane();
        desktop.setBackground(Colors.desktopPaneBackground);
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        setContentPane(desktop);

        setVisible(true);
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBackground(Colors.menuBarBackground);
        menuBar.setForeground(Color.WHITE);
        menuBar.setOpaque(true);

        createMenuItems();

        setJMenuBar(menuBar);
    }

    private void createMenuItems() {
        createMenuItem("File", "New Blog", DIVIDER, "Exit");
        createWebsiteMenuItems();
    }

    private void createWebsiteMenuItems() {

        java.util.List<String> websiteNames = new ArrayList<>();

        try {
            Collection<Website> websites = websiteService.loadAllWebsites();

            for (Website website : websites) {
                if (website.getName().length() > 0) {
                    websiteNames.add(website.getName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(websiteNames);
        String [] list = new String[websiteNames.size()];
        list = websiteNames.toArray(list);

        createMenuItem("Websites", list);
    }

    private void createMenuItem(String menuName, String... items) {

        JMenu menu = new JMenu(menuName);
        menu.setForeground(Color.WHITE);
        menuBar.add(menu);

        for (String itemName : items) {

            if (DIVIDER.equals(itemName)) {
                menu.addSeparator();
                continue;
            }

            JMenuItem menuItem = new JMenuItem(itemName);
            menuItem.setBackground(new Color(100, 110, 118));
            menuItem.setForeground(Color.WHITE);
            menuItem.addActionListener(this);
            menu.add(menuItem);
        }
    }

    public static void main(String [] args) {
        getInstance();
    }

    public static MarkdownToHomepageGui getInstance() {

        if (instance == null) {
            instance = new MarkdownToHomepageGui();
        }

        return instance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String actionCommand = e.getActionCommand();

        if ("New Blog".equals(actionCommand)) {
            ObjectDialog objectDialog = new ObjectDialog(new Website());
            objectDialog.setVisible(true);
            Website website = (Website) objectDialog.getObject();
            System.out.println(website);

            try {
                websiteService.createOrUpdateWebsite(website);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return;
        }

        if ("Exit".equals(actionCommand)) {
            dispose();
            return;
        }

        try {
            if (loadWebsite(actionCommand)) {
                return;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private boolean loadWebsite(String websiteName) throws IOException {

        Website website = websiteService.getByName(websiteName);

        if (website == null) {
            return false;
        }

        loadWebsite(website);

        return true;
    }

    private void loadWebsite(Website website) {

        System.out.println("load website ...");
        System.out.println(website);

        final PostListOverviewFrame postListOverviewFrame = new PostListOverviewFrame(website);

        try {
            websiteService.synchronizeWebsite(website, new ICallback() {
                @Override
                public void execute(Object object) {
                    postListOverviewFrame.reloadWebsiteContent();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        }

        postListOverviewFrame.setBounds(20,20,400,800);
        postListOverviewFrame.setVisible(true);

        try {
            postListOverviewFrame.setSelected(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

        desktop.add(postListOverviewFrame);
    }
}
