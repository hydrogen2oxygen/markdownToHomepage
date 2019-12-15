package net.hydrogen2oxygen.markdowntohomepage.gui;

import com.jcraft.jsch.JSchException;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.resource.PathResourceManager;
import lombok.Getter;
import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import net.hydrogen2oxygen.markdowntohomepage.service.WebsiteService;
import net.hydrogen2oxygen.markdowntohomepage.transformator.TransformFolder;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static io.undertow.Handlers.resource;
import static javax.swing.JSplitPane.DIVIDER;

public class MarkdownToHomepageGui extends JFrame implements ActionListener {

    private static final String TITLE = "MarkdownToHomepage";
    private static MarkdownToHomepageGui instance;
    private JMenuBar menuBar;
    @Getter
    private JDesktopPane desktop;
    @Getter
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

        final JFrame that = this;

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(that,
                        "Are you sure you want to close this window?", "Close Window?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                    System.exit(0);
                }
            }
        });

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
        createWebsiteMenuItems("Websites", "Load", "Generate", "Serve");
        createMenuItem("Git", "Commit", "Pull", "Push");
        createMenuItem("FTP", "Upload");
    }

    private void createWebsiteMenuItems(String menuName, String... commandPrefixes) {

        java.util.List<String> websiteNames = new ArrayList<>();

        try {
            Collection<Website> websites = websiteService.loadAllWebsites();

            for (Website website : websites) {
                if (website.getName().length() > 0) {

                    for (String commandPrefix : commandPrefixes) {
                        websiteNames.add(commandPrefix + " " + website.getName());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(websiteNames);
        String[] list = new String[websiteNames.size()];
        list = websiteNames.toArray(list);

        createMenuItem(menuName, list);
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

    public static void main(String[] args) {
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

        if (actionCommand.startsWith("Generate ")) {

            try {
                Website website = websiteService.getByName(actionCommand.replace("Generate ",""));
                TransformFolder.builder()
                        .website(website)
                        .build();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return;
        }

        if (actionCommand.startsWith("Serve ")) {

            try {
                Website website = websiteService.getByName(actionCommand.replace("Serve ",""));
                startLocalServer(website);

                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI("http://localhost:7070"));
                    } catch (URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return;
        }

        if ("Exit".equals(actionCommand)) {
            dispose();
            return;
        }

        if ("Commit".equals(actionCommand)) {
            websiteService.commit();
            return;
        }

        if ("Pull".equals(actionCommand)) {
            websiteService.pull();
            return;
        }

        if ("Push".equals(actionCommand)) {
            websiteService.push();
            return;
        }

        if (actionCommand.startsWith("Load ")) {
            try {
                if (loadWebsite(actionCommand.replace("Load ", ""))) {
                    return;
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void startLocalServer(Website website) {

        Undertow.builder()
                .addHttpListener(7070, "localhost")
                .setServerOption(UndertowOptions.URL_CHARSET, "UTF8")
                .setHandler(resource(new PathResourceManager(Paths.get(website.getTargetFolder()), 100))
                        .setDirectoryListingEnabled(true))
                .build().start();
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

        try {
            postListOverviewFrame.setSelected(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

        desktop.add(postListOverviewFrame);
    }
}
