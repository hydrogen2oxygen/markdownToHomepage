package net.hydrogen2oxygen.markdowntohomepage.gui;

import lombok.Getter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ObjectDialog extends JDialog implements ActionListener {

    @Getter
    private Object object;
    private List<ObjectDialogTextfield> textfieldList = new ArrayList<>();

    public ObjectDialog(Object o) {
        super();
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

        try {
            Method[] methods = object.getClass().getDeclaredMethods();

            List<Method> methodList = new ArrayList<>();

            for (Method method : methods) {
                methodList.add(method);
            }

            Collections.sort(methodList, new Comparator<Method>() {
                @Override
                public int compare(Method o1, Method o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            for (Method method : methodList) {
                if (method.getName().startsWith("set")) {

                    final Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 1) {
                        System.out.println(method.getName());
                        method.invoke(object,"");

                        ObjectDialogTextfield objectDialogTextfield = new ObjectDialogTextfield(object,method, y);
                        objectDialogTextfield.addToComponent(this);
                        textfieldList.add(objectDialogTextfield);
                        y += 20;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(10,y + 10,200,20);
        saveButton.addActionListener(this);
        add(saveButton);

        setBounds(30,30,width,y+80);
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
