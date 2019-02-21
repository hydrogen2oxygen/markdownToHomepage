package net.hydrogen2oxygen.markdowntohomepage.gui;

import lombok.Getter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ObjectDialog extends JDialog implements ActionListener {

    @Getter
    private Object object;
    private List<ObjectDialogTextfield> textfieldList = new ArrayList<>();

    public ObjectDialog(Object o) {
        init(o);
    }

    public ObjectDialog(JDialog dialog, Object o) {
        super(dialog);
        init(o);
    }

    private void init(Object o) {

        this.object = o;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Settings " + o.getClass().getSimpleName());
        setModal(true);
        setLayout(null);
        int width = 400;
        int y = 10;

        y = DynamicComponentsUtility.insertDynamicTextfields(this, object, textfieldList, y);

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(10, y + 10, 200, 20);
        saveButton.addActionListener(this);
        add(saveButton);

        setBounds(30, 30, width, y + 80);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if ("Save".equals(e.getActionCommand())) {

            for (ObjectDialogTextfield dialogTextfield : textfieldList) {
                try {
                    dialogTextfield.transferValueToObject();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                }
            }

            dispose();
        }
    }
}
