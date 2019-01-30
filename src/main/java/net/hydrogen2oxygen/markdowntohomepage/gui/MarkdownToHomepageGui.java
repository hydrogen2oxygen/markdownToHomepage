package net.hydrogen2oxygen.markdowntohomepage.gui;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.JSplitPane.DIVIDER;

public class MarkdownToHomepageGui extends JFrame implements ActionListener {

    private static final String TITLE = "MarkdownToHomepage";
    private static MarkdownToHomepageGui instance;
    private JMenuBar menuBar;
    @Getter
    private JDesktopPane desktop;

    private MarkdownToHomepageGui() {
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

        if ("Exit".equals(actionCommand)) {
            dispose();
            return;
        }
    }
}
