package net.hydrogen2oxygen.markdowntohomepage.gui;

import net.hydrogen2oxygen.markdowntohomepage.domain.PostDetails;
import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import net.hydrogen2oxygen.markdowntohomepage.transformator.PostDetailsUtility;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostEditorInternalFrame extends JInternalFrame {

    private String content;
    private File postFile;
    private Website website;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private List<DynamicTextfield> textfieldList = new ArrayList<>();

    public PostEditorInternalFrame(Website website, String postFileName) {

        int y = 10;
        int width = 1000;
        int height = 800;

        postFile = new File(website.getSourceFolder() + "/content/posts/" + postFileName);

        loadContent();

        setTitle(website.getName() + " - Post: " + postFileName);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setClosable(true);
        setResizable(true);
        setLayout(null);

        JPanel postDetailPanel = new JPanel();
        postDetailPanel.setBackground(Colors.postDetailsPaneBackground);

        PostDetails postDetails = new PostDetails();
        PostDetailsUtility.prefillPostDetails(content, postDetails);
        y = DynamicComponentsUtility.insertDynamicTextfields(this, postDetails, textfieldList, y);
        postDetailPanel.setPreferredSize(new Dimension(width, y + 10));
        postDetailPanel.setBounds(0, 0, width - 10, y + 10);
        setTextfieldColors();
        add(postDetailPanel);

        Font font = new Font(Font.MONOSPACED, Font.BOLD, 14);
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(font);
        textArea.setText(PostDetailsUtility.removeDetailsFromMarkdownString(content));
        scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(0, y + 11, width - 10, height - (y + 40));
        add(scrollPane);

        setBounds(500, 20, width, height);
        setVisible(true);
        scrollPane.revalidate();

        try {
            setSelected(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

        toFront();
        moveToFront();
        scrollToTop();
    }

    private void loadContent() {
        try {
            content = FileUtils.readFileToString(postFile, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTextfieldColors() {
        for (DynamicTextfield textfield : textfieldList) {
            textfield.getLabel().setForeground(Color.white);
        }
    }

    private void scrollToTop() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                scrollPane.getVerticalScrollBar().setValue(0);
            }
        });
    }

}
