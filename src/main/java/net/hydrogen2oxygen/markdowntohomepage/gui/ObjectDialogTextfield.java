package net.hydrogen2oxygen.markdowntohomepage.gui;

import lombok.Data;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Data
public class ObjectDialogTextfield {

    private JLabel label;
    private JTextField textField;
    private Method method;
    private Object object;
    private Integer y;

    public ObjectDialogTextfield(Object object, Method method, int y) {
        this.object = object;
        this.method = method;
        this.y = y;

        initWrapper();
    }

    private void initWrapper() {
        label = new JLabel(method.getName().replace("set", ""));
        textField = new JTextField();

        label.setBounds(10,y,120,20);
        textField.setBounds(134,y,200,20);
    }

    public void addToComponent(Container container) {
        container.add(label);
        container.add(textField);
    }

    public void transferValueToObject() throws InvocationTargetException, IllegalAccessException {
            method.invoke(object,textField.getText());
    }
}
