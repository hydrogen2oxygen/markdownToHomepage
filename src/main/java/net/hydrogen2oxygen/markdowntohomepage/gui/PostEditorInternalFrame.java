package net.hydrogen2oxygen.markdowntohomepage.gui;

import net.hydrogen2oxygen.markdowntohomepage.domain.PostDetails;
import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import net.hydrogen2oxygen.markdowntohomepage.transformator.PostDetailsUtility;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        postFile = new File(website.getSourceFolder() + "/" + postFileName);

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
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePost();
            }
        });
        saveButton.setBounds(10, y, 200, 20);
        postDetailPanel.add(saveButton);
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

    private void savePost() {
        System.err.println("-----------------");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("---\n");
        stringBuilder.append("title: ").append(getTextFieldValue("title")).append("\n");
        stringBuilder.append("author: ").append(getTextFieldValue("author")).append("\n");
        stringBuilder.append("type: ").append(getTextFieldValue("type")).append("\n");
        stringBuilder.append("date: ").append(getTextFieldValue("date")).append("\n");
        stringBuilder.append("url: ").append(getTextFieldValue("url")).append("\n");
        appendList(stringBuilder,"categories");
        appendList(stringBuilder,"tags");

        stringBuilder.append("---\n");

        stringBuilder.append(textArea.getText());

        try {
            FileUtils.writeStringToFile(postFile, stringBuilder.toString(),"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(stringBuilder);

    }

    private void appendList(StringBuilder stringBuilder, String key) {
        if (getTextFieldValue(key).length() > 0) {

            StringBuilder tagsString = new StringBuilder();
            String tags[] = getTextFieldValue(key).split(",");
            stringBuilder.append(key).append(": \n");

            for (String tag : tags) {

                if (tag == null) continue;

                tag = tag.replaceAll(" ","").trim();

                if (tag.length() == 0) {
                    continue;
                }

                stringBuilder.append("  - ");
                stringBuilder.append(tag);
                stringBuilder.append("\n");
            }
        }
    }

    private String getTextFieldValue(String key) {

        for (DynamicTextfield textfield : textfieldList) {
            if (textfield.getLabel().getText().toLowerCase().equals(key.toLowerCase())) {
                return textfield.getTextField().getText();
            }
        }

        return "";
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
