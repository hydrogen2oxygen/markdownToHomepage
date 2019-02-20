package net.hydrogen2oxygen.markdowntohomepage.gui;

import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;

public class PostEditorInternalFrame extends JInternalFrame {

    private File postFile;
    private Website website;
    private JTextPane textArea;
    private JScrollPane scrollPane;

    public PostEditorInternalFrame(Website website, String postFileName) {

        postFile = new File(website.getSourceFolder() + "/content/posts/" + postFileName);

        setTitle(website.getName() + " - Post: " + postFileName);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setClosable(true);
        setResizable(true);

        Font font  = new Font(Font.MONOSPACED, Font.BOLD,  14);
        textArea = new JTextPane();
        textArea.setFont(font);
        scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane);

        try {
            loadPost();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setBounds(500,20,600,800);
        setVisible(true);

        try {
            setSelected(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

        toFront();
        moveToFront();
    }

    private void loadPost() throws IOException {

        String content = FileUtils.readFileToString(postFile, "UTF-8");
        StyledDocument sdoc = textArea.getStyledDocument();
        textArea.setText(content);
        scrollPane.revalidate();
    }


}
