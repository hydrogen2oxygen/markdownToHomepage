package net.hydrogen2oxygen.markdowntohomepage.gui;

import lombok.Getter;

import javax.swing.*;
import java.lang.reflect.Method;

public class ObjectDialog extends JDialog {

    @Getter
    private Object object;

    public ObjectDialog(Object o) {

        super();
        this.object = o;
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        init();
    }

    private void init() {

        int width = 400;
        int height = 300;

        try {
            Method[] methods = object.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("set")) {

                    final Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 1) {
                        System.out.println(method.getName());
                        method.invoke(object,"");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
