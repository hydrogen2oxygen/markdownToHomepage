package net.hydrogen2oxygen.markdowntohomepage.gui;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DynamicComponentsUtility {

    private DynamicComponentsUtility() {
    }

    public static int insertDynamicTextfields(Container container, Object object, List<ObjectDialogTextfield> textfieldList, int y) {
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
                        method.invoke(object, "");

                        ObjectDialogTextfield objectDialogTextfield = new ObjectDialogTextfield(object, method, y);
                        objectDialogTextfield.addToComponent(container);
                        textfieldList.add(objectDialogTextfield);
                        y += 20;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return y;
    }
}
