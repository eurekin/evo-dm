package pl.eurekin.util;

import java.awt.BorderLayout;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;

/**
 * Helper class to make property introspection easier. Utility wrapper around
 * JavaBeans Introspector.
 *
 * @author Rekin
 */
public class Interrogator {

    /**
     * Returns object's property names. You can use <code>set</code> and
     * <code>get</code> methods to read and write them.
     *
     * @param obj object to interrogate
     * @return list of object's property names
     * @throws Exception if anything goes wrong
     */
    public static List<String> getPropertyNames(Object obj) throws Exception {
        ArrayList<String> result = new ArrayList();
        for (PropertyDescriptor p : Introspector.getBeanInfo(obj.getClass()).getPropertyDescriptors()) {
            result.add(p.getName());
        }
        return result;
    }

    /**
     * Set <code>obj</code>ect's <code>property</code> using <code>value</value>
     *
     * @param obj object
     * @param property property name
     * @param value to set
     * @throws Exception
     */
    public static void set(Object obj, String property, Object value) throws Exception {
        for (PropertyDescriptor p : Introspector.getBeanInfo(obj.getClass()).getPropertyDescriptors()) {
            if (p.getName().equals(property)) {
                Method writeMethod = p.getWriteMethod();
                writeMethod.invoke(obj, value);
            }
        }
    }

    /**
     * Return <code>obj</code>ect's <code>property</code>.
     *
     * @param obj which property to get
     * @param property name
     * @return property value
     * @throws Exception
     */
    public static Object get(Object obj, String property) throws Exception {
        Object result = null;
        for (PropertyDescriptor p : Introspector.getBeanInfo(obj.getClass()).getPropertyDescriptors()) {
            if (p.getName().equals(property)) {
                Method readMethod = p.getReadMethod();
                result = readMethod.invoke(obj);
            }
        }
        return result;
    }

    public static String interrogate(Object object) throws Exception {
        StringBuilder b = new StringBuilder();

        b.append(object.getClass().getName() + "'s properties:\n");
        for (PropertyDescriptor p : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
            String name = p.getName();
            Method readMethod = p.getReadMethod();
            Method writeMethod = p.getWriteMethod();
            String readMethodName = readMethod != null ? readMethod.getName() : "---";
            String writeMethodName = writeMethod != null ? writeMethod.getName() : "---";

            b.append(name);
            if (readMethod != null) {
                Object val = readMethod.invoke(object);
                b.append("\t" + val);
            }
            b.append("\t(read: " + readMethodName + ",\twrite: " + writeMethodName + ")\n");

        }

        return b.toString();
    }

    /**
     * Display interrogation table
     *
     * @param object
     * @throws Exception
     */
    public static void interrogateGUI(Object object) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame frame = new JFrame("Interrogating: " + object.getClass().getSimpleName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new BorderLayout());
        Object[][] values = new Object[][]{

            new Object[]{1, 2, 3, 4, 5}
        };
        ArrayList<String[]> plist = new ArrayList();
        String[] names = new String[]{"name", "type", "read method", "value", "write method"};
        for (PropertyDescriptor p : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
            String[] desc = new String[5];
            String name = p.getName();

            Method readMethod = p.getReadMethod();
            Method writeMethod = p.getWriteMethod();
            String readMethodName = readMethod != null ? readMethod.getName() : "---";
            String writeMethodName = writeMethod != null ? writeMethod.getName() : "---";
            desc[0] = name;
            desc[1] = p.getPropertyType().getSimpleName();
            desc[2] = readMethodName;
            desc[4] = writeMethodName;
            desc[3] = "---";
            if (readMethod != null) {
                Object val = readMethod.invoke(object);
                desc[3] = val == null ? "null" : val.toString();
            }

            plist.add(desc);
        }
        values = plist.toArray(new Object[][]{});

        JTable table = new JTable(values, names);
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane pane = new JScrollPane(table);
        panel.add(pane, BorderLayout.CENTER);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
    }
}