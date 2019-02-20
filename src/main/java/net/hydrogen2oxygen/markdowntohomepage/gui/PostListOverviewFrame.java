package net.hydrogen2oxygen.markdowntohomepage.gui;

import net.hydrogen2oxygen.markdowntohomepage.domain.Website;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class PostListOverviewFrame extends JInternalFrame {

    private DefaultListModel listModel;
    private Website website;

    public PostListOverviewFrame(Website website) {
        this.website = website;
        setTitle(website.getName() + " - Overview");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setClosable(true);
        setResizable(true);
        JList<String> jList = new JList<>();
        jList.setModel(new DefaultListModel());
        listModel = (DefaultListModel) jList.getModel();
        JScrollPane scrollPane = new JScrollPane(jList);
        add(scrollPane);

        jList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                handleMouseClick(evt);
            }
        });

        reloadWebsiteContent();

        setBounds(20,20,400,800);
        setVisible(true);
    }

    private void handleMouseClick(MouseEvent evt) {
        JList list = (JList)evt.getSource();
        if (evt.getClickCount() >= 2) {

            int index = list.locationToIndex(evt.getPoint());
            System.out.println(listModel.get(index));
            PostEditorInternalFrame postEditorInternalFrame = new PostEditorInternalFrame(website, (String) listModel.get(index));
            MarkdownToHomepageGui.getInstance().getDesktop().add(postEditorInternalFrame);
        }
    }

    public void reloadWebsiteContent() {

        System.out.println("Reload website");
        File folder = new File(website.getSourceFolder() + "/content/posts/");

        for (File file : folder.listFiles()) {
            listModel.addElement(file.getName());
        }
    }
}
