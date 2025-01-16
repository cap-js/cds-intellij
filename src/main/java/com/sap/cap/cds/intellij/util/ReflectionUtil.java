package com.sap.cap.cds.intellij.util;

import org.jetbrains.annotations.Nullable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReflectionUtil {

    public static Object getFieldValue(Object obj, String fieldName, @Nullable Class<?> superclass) throws NoSuchFieldException, IllegalAccessException {
        Field field = superclass == null ? obj.getClass().getDeclaredField(fieldName) : superclass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    public static void setFieldValue(Object obj, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    public static void setCustomOptionsEnablement(DefaultTreeModel model, Map<String, Boolean> enablementMap) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        setNodesEnablement(root, model, enablementMap);
    }

    private static void setNodesEnablement(DefaultMutableTreeNode root, DefaultTreeModel model, Map<String, Boolean> enablementMap) {
        List<Integer> changedChildIndexes = new ArrayList<>();
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
            if (child.getClass().getSimpleName().equals("MyToggleTreeNode")) {
                try {
                    Object key = getFieldValue(child, "myKey", null);
                    String optionName = (String) getFieldValue(key, "optionName", key.getClass().getSuperclass().getSuperclass());
                    if (enablementMap.containsKey(optionName)) {
                        setFieldValue(child, "isEnabled", enablementMap.get(optionName));
                        changedChildIndexes.add(i);
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    Logger.PLUGIN.error(e);
                }
            } else {
                setNodesEnablement(child, model, enablementMap);
            }
        }
        model.nodesChanged(root, changedChildIndexes.stream().mapToInt(Integer::intValue).toArray());
    }

}
