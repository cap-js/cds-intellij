package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.codeStyle.CustomizableLanguageCodeStylePanel;
import com.intellij.application.options.codeStyle.SpeedSearchHelper;
import com.intellij.lang.LangBundle;
import com.intellij.openapi.application.ApplicationBundle;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.SpeedSearchComparator;
import com.intellij.ui.TreeTableSpeedSearch;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.editors.JBComboBoxTableCellEditorComponent;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.ui.render.RenderingUtil;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModel;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import com.intellij.ui.treeStructure.treetable.TreeTableCellRenderer;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.intellij.util.ui.*;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.List;
import java.util.*;

import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Type.*;

// Mostly copied from {@link com.intellij.application.options.codeStyle.OptionTableWithPreviewPanel}.

// TODO clean up unneeded corners

public abstract class CdsCodeStyleTabularPanelBase extends CustomizableLanguageCodeStylePanel implements CdsCodeStylePanel {
    private static final Logger LOG = Logger.getInstance(CdsCodeStyleTabularPanelBase.class);

    private static final KeyStroke ENTER_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
    public final ColumnInfo TITLE = new ColumnInfo("TITLE") {
        @Override
        public Object valueOf(Object o) {
            if (o instanceof MyTreeNode node) {
                return node.getText();
            }
            return o.toString();
        }

        @Override
        public Class getColumnClass() {
            return TreeTableModel.class;
        }
    };
    public final ColumnInfo VALUE = new ColumnInfo("VALUE") {
        private final TableCellEditor myEditor = new MyValueEditor();
        private final TableCellRenderer myRenderer = new MyValueRenderer();

        @Override
        public Object valueOf(Object o) {
            if (o instanceof MyTreeNode node) {
                return node.getValue();
            }

            return null;
        }

        @Override
        public TableCellRenderer getRenderer(Object o) {
            return myRenderer;
        }

        @Override
        public TableCellEditor getEditor(Object item) {
            return myEditor;
        }

        @Override
        public boolean isCellEditable(Object o) {
            return o instanceof MyTreeNode && ((MyTreeNode) o).isEnabled();
        }

        @Override
        public void setValue(Object o, Object o1) {
            MyTreeNode node = (MyTreeNode) o;
            node.setValue(o1);
        }
    };
    public final ColumnInfo[] COLUMNS = new ColumnInfo[]{TITLE, VALUE};
    private final JPanel myPanel = new JPanel();
    private final List<Option> myOptions = new ArrayList<>();
    protected TreeTable myTreeTable;
    protected boolean isFirstUpdate = true;
    private SpeedSearchHelper mySearchHelper;

    public CdsCodeStyleTabularPanelBase(CodeStyleSettings settings) {
        super(settings);
    }

    private static void resetNode(TreeNode node, CodeStyleSettings settings) {
        if (node instanceof MyTreeNode) {
            ((MyTreeNode) node).reset(settings);
        }
        for (int j = 0; j < node.getChildCount(); j++) {
            TreeNode child = node.getChildAt(j);
            resetNode(child, settings);
        }
    }

    private static void applyNode(TreeNode node, final CodeStyleSettings settings) {
        if (node instanceof MyTreeNode) {
            ((MyTreeNode) node).apply(settings);
        }
        for (int j = 0; j < node.getChildCount(); j++) {
            TreeNode child = node.getChildAt(j);
            applyNode(child, settings);
        }
    }

    private static boolean isModified(TreeNode node, final CodeStyleSettings settings) {
        if (node instanceof MyTreeNode) {
            if (((MyTreeNode) node).isModified(settings)) return true;
        }
        for (int j = 0; j < node.getChildCount(); j++) {
            TreeNode child = node.getChildAt(j);
            if (isModified(child, settings)) {
                return true;
            }
        }
        return false;
    }

    private static @NotNull String arrayToString(int @NotNull [] array) {
        int n = array.length;
        if (n == 0) return "";
        StringBuilder b = new StringBuilder(array.length * 4);
        b.append(array[0]);
        for (int i = 1; i < n; i++) b.append(',').append(' ').append(array[i]);
        return b.toString();
    }

    private static void collectOptions(Set<? super String> optionNames, final List<? extends Option> optionList) {
        for (Option option : optionList) {
            if (option.groupName != null) {
                optionNames.add(option.groupName);
            }
            optionNames.add(option.label);
        }
    }

    private static void updateColors(@NotNull JComponent component, @NotNull JTable table, boolean isSelected) {
        component.setOpaque(isSelected);
        component.setBackground(RenderingUtil.getBackground(table, isSelected));
        if (!(component instanceof ColoredLabel) || isSelected) {
            component.setForeground(RenderingUtil.getForeground(table, isSelected));
        }
    }

    @Override
    protected void init() {
        super.init();

        myPanel.setLayout(new GridBagLayout());
        initTables();

        myTreeTable = createOptionsTree(getSettings());
        myTreeTable.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        JBScrollPane scrollPane = new JBScrollPane(myTreeTable) {
            @Override
            public Dimension getMinimumSize() {
                return super.getPreferredSize();
            }
        };
        myPanel.add(scrollPane
                , new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        JBInsets.emptyInsets(), 0, 0));

        final JPanel previewPanel = createPreviewPanel();
        myPanel.add(previewPanel,
                new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        JBInsets.emptyInsets(), 0, 0));

        installPreviewPanel(previewPanel);
        addPanelToWatch(myPanel);

        isFirstUpdate = false;
        customizeSettings();

        showStandardOptions();
    }

    @Override
    public void showAllStandardOptions() {
    }

    @Override
    public void showStandardOptions(String... optionNames) {
    }

    protected TreeTable createOptionsTree(CodeStyleSettings settings) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        Map<String, DefaultMutableTreeNode> groupsMap = new HashMap<>();

        List<Option> sorted = sortOptions(myOptions);
        for (Option each : sorted) {
            String group = each.groupName;
            MyTreeNode newNode = new MyTreeNode(each, each.label, settings);

            DefaultMutableTreeNode groupNode = groupsMap.get(group);
            if (groupNode != null) {
                groupNode.add(newNode);
            } else {
                String groupName;

                if (group == null) {
                    groupName = each.label;
                    groupNode = newNode;
                } else {
                    groupName = group;
                    groupNode = new DefaultMutableTreeNode(groupName);
                    groupNode.add(newNode);
                }
                groupsMap.put(groupName, groupNode);
                rootNode.add(groupNode);
            }
        }

        ListTreeTableModel model = new ListTreeTableModel(rootNode, COLUMNS);
        TreeTable treeTable = new TreeTable(model) {
            @Override
            public TreeTableCellRenderer createTableRenderer(TreeTableModel treeTableModel) {
                TreeTableCellRenderer tableRenderer = super.createTableRenderer(treeTableModel);
                tableRenderer.setRootVisible(false);
                tableRenderer.setShowsRootHandles(true);
                return tableRenderer;
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                TreePath treePath = getTree().getPathForRow(row);
                if (treePath == null) return super.getCellRenderer(row, column);

                Object node = treePath.getLastPathComponent();

                @SuppressWarnings("unchecked")
                TableCellRenderer renderer = COLUMNS[column].getRenderer(node);
                return renderer == null ? super.getCellRenderer(row, column) : renderer;
            }

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                TreePath treePath = getTree().getPathForRow(row);
                if (treePath == null) return super.getCellEditor(row, column);

                Object node = treePath.getLastPathComponent();
                @SuppressWarnings("unchecked")
                TableCellEditor editor = COLUMNS[column].getEditor(node);
                return editor == null ? super.getCellEditor(row, column) : editor;
            }
        };
        TreeTableSpeedSearch speedSearch = TreeTableSpeedSearch.installOn(treeTable);
        speedSearch.setComparator(new SpeedSearchComparator(false));
        mySearchHelper = new SpeedSearchHelper(speedSearch);

        treeTable.setRootVisible(false);

        final JTree tree = treeTable.getTree();
        TreeCellRenderer titleRenderer = new MyTitleRenderer(mySearchHelper);
        tree.setCellRenderer(titleRenderer);
        tree.setShowsRootHandles(true);
        treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        treeTable.setTableHeader(null);

        TreeUtil.expandAll(tree);

        treeTable.getColumnModel().getSelectionModel().setAnchorSelectionIndex(1);
        treeTable.getColumnModel().getSelectionModel().setLeadSelectionIndex(1);

        int maxWidth = tree.getPreferredScrollableViewportSize().width + 10;
        final TableColumn titleColumn = treeTable.getColumnModel().getColumn(0);
        titleColumn.setPreferredWidth(maxWidth);
        titleColumn.setMinWidth(maxWidth);
        titleColumn.setMaxWidth(maxWidth);
        titleColumn.setResizable(false);

        final Dimension valueSize = new JLabel(ApplicationBundle.message("option.table.sizing.text")).getPreferredSize();
        treeTable.setPreferredScrollableViewportSize(JBUI.size(maxWidth + valueSize.width + 10, 20));

        return treeTable;
    }

    protected abstract void initTables();

    @Override
    public void addOption(CdsCodeStyleOption<?> option) {
        if (option.type == BOOLEAN) {
            myOptions.add(new BooleanOption(option.name, option.label, option.group, option.getAnchor(), option.getAnchorOptionName()));
        } else if (option.type == ENUM) {
            myOptions.add(new SelectionOption(option.name, option.label, option.group, option.getAnchor(), option.getAnchorOptionName(), option.getValuesLabels(), option.getValuesIds()));
        } else if (option.type == INT) {
            myOptions.add(new IntOption(option.name, option.label, option.group, option.getAnchor(), option.getAnchorOptionName(), 0, 100, (int) option.defaultValue));
        }
    }

    @Override
    public @NotNull Set<String> processListOptions() {
        Set<String> options = new HashSet<>();
        collectOptions(options, myOptions);
        return options;
    }

    @Override
    public void apply(@NotNull CodeStyleSettings settings) throws ConfigurationException {
        TableCellEditor editor = myTreeTable.getCellEditor();
        if (editor != null && !editor.stopCellEditing()) {
            throw new ConfigurationException(LangBundle.message("dialog.message.editing.cannot.be.stopped"));
        }
        TreeModel treeModel = myTreeTable.getTree().getModel();
        TreeNode root = (TreeNode) treeModel.getRoot();
        applyNode(root, settings);
    }

    @Override
    public boolean isModified(CodeStyleSettings settings) {
        TableCellEditor editor = myTreeTable.getCellEditor();
        if (editor != null) {
            return true; // to allow stop editing in #apply
        }
        TreeModel treeModel = myTreeTable.getTree().getModel();
        TreeNode root = (TreeNode) treeModel.getRoot();
        return isModified(root, settings);
    }

    @Override
    public JComponent getPanel() {
        return myPanel;
    }

    @Override
    protected void resetImpl(final @NotNull CodeStyleSettings settings) {
        TreeModel treeModel = myTreeTable.getTree().getModel();
        TreeNode root = (TreeNode) treeModel.getRoot();
        resetNode(root, settings);
        ((DefaultTreeModel) treeModel).nodeChanged(root);
    }

    @Override
    public void highlightOptions(@NotNull String searchString) {
        mySearchHelper.find(searchString);
    }

    protected static final class MyTreeNode extends DefaultMutableTreeNode {
        private final Option myKey;
        private final String myText;
        private Object myValue;

        public MyTreeNode(Option key, String text, CodeStyleSettings settings) {
            myKey = key;
            myText = text;
            myValue = key.getValue(settings);
            setUserObject(myText);
        }

        public Option getKey() {
            return myKey;
        }

        public String getText() {
            return myText;
        }

        public Object getValue() {
            return myValue;
        }

        public void setValue(Object value) {
            myValue = value;
        }

        public void reset(CodeStyleSettings settings) {
            setValue(myKey.getValue(settings));
        }

        public boolean isModified(final CodeStyleSettings settings) {
            return myValue != null && !myValue.equals(myKey.getValue(settings));
        }

        public void apply(final CodeStyleSettings settings) {
            myKey.setValue(myValue, settings);
        }

        public boolean isEnabled() {
            return myKey.isEnabled();
        }
    }

    protected abstract static class Option extends OrderedOption {
        final @NotNull String label;
        final @Nullable String groupName;
        private boolean myEnabled = true;

        protected Option(@NotNull String optionName,
                         @NotNull String label,
                         @Nullable String groupName,
                         @Nullable OptionAnchor anchor,
                         @Nullable String anchorOptionName) {
            super(optionName, anchor, anchorOptionName);
            this.label = label;
            this.groupName = groupName;
        }

        public boolean isEnabled() {
            return myEnabled;
        }

        public void setEnabled(boolean enabled) {
            myEnabled = enabled;
        }

        public abstract Object getValue(CodeStyleSettings settings);

        public abstract void setValue(Object value, CodeStyleSettings settings);
    }

    protected static final class ColoredLabel extends JLabel {
        public ColoredLabel(@Nls String text, Color foreground) {
            super(text);
            setForeground(foreground);
        }
    }

    public final class BooleanOption extends FieldOption {
        public BooleanOption(@NotNull String name,
                             @NotNull String label,
                             @Nullable String groupName,
                             @Nullable OptionAnchor anchor,
                             @Nullable String anchorFieldName) {
            super(name, label, groupName, anchor, anchorFieldName);
        }

        @Override
        public Object getValue(CodeStyleSettings settings) {
            try {
                return field.getBoolean(getSettings(settings));
            } catch (IllegalAccessException ignore) {
                return null;
            }
        }

        @Override
        public void setValue(Object value, CodeStyleSettings settings) {
            try {
                field.setBoolean(getSettings(settings), (Boolean) value);
            } catch (IllegalAccessException ignored) {
            }
        }
    }

    public final class SelectionOption extends FieldOption {
        final String @NotNull [] options;
        final int @NotNull [] ids;

        public SelectionOption(@NotNull String name,
                               @NotNull String label,
                               @Nullable String groupName,
                               @Nullable OptionAnchor anchor,
                               @Nullable String anchorFieldName,
                               String @NotNull [] options,
                               int @NotNull [] ids) {
            super(name, label, groupName, anchor, anchorFieldName);
            this.options = options;
            this.ids = ids;
        }

        @Override
        public Object getValue(CodeStyleSettings settings) {
            try {
                int value = field.getInt(getSettings(settings));
                for (int i = 0; i < ids.length; i++) {
                    if (ids[i] == value) return options[i];
                }
                String possibleValues = arrayToString(ids);
                LOG.error("Invalid option value " + value + " for " + field.getName() + " (possible ids: " + possibleValues + ')');
            } catch (IllegalAccessException ignore) {
            }
            return null;
        }

        @Override
        public void setValue(Object value, CodeStyleSettings settings) {
            try {
                for (int i = 0; i < ids.length; i++) {
                    if (options[i].equals(value)) {
                        field.setInt(getSettings(settings), ids[i]);
                        return;
                    }
                }
                LOG.error("Invalid option value " + value + " for " + field.getName());
            } catch (IllegalAccessException ignore) {
            }
        }
    }

    /**
     * @author Konstantin Bulenkov
     */
    private final class MyValueEditor extends AbstractTableCellEditor {
        public static final String STOP_CELL_EDIT_ACTION_KEY = "stopEdit";
        private final JCheckBox myBooleanEditor = new JBCheckBox();
        private final JBComboBoxTableCellEditorComponent myOptionsEditor = new JBComboBoxTableCellEditorComponent();
        private final IntegerField myIntOptionsEditor = new IntegerField();
        private final AbstractAction STOP_CELL_EDIT_ACTION = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopCellEditing();
            }
        };
        private JComponent myCurrentEditor = null;
        private MyTreeNode myCurrentNode = null;

        MyValueEditor() {
            final ActionListener itemChosen = new ActionListener() {
                @Override
                public void actionPerformed(@NotNull ActionEvent e) {
                    if (myCurrentNode != null) {
                        myCurrentNode.setValue(getCellEditorValue());
                        somethingChanged();
                    }
                }
            };
            myBooleanEditor.addActionListener(itemChosen);
            myOptionsEditor.addActionListener(itemChosen);
            UIUtil.applyStyle(UIUtil.ComponentStyle.SMALL, myBooleanEditor);
            UIUtil.applyStyle(UIUtil.ComponentStyle.SMALL, myOptionsEditor);
            UIUtil.applyStyle(UIUtil.ComponentStyle.MINI, myIntOptionsEditor);
        }

        @Override
        public Object getCellEditorValue() {
            if (myCurrentEditor == myOptionsEditor) {
                return myOptionsEditor.getEditorValue();
            } else if (myCurrentEditor == myBooleanEditor) {
                return myBooleanEditor.isSelected();
            } else if (myCurrentEditor == myIntOptionsEditor) {
                return myIntOptionsEditor.getValue();
            }

            return null;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            final DefaultMutableTreeNode defaultNode = (DefaultMutableTreeNode) ((TreeTable) table).getTree().
                    getPathForRow(row).getLastPathComponent();
            myCurrentEditor = null;
            myCurrentNode = null;
            if (defaultNode instanceof MyTreeNode node) {
                myCurrentNode = node;
                if (node.getKey() instanceof BooleanOption) {
                    myCurrentEditor = myBooleanEditor;
                    myBooleanEditor.setSelected(node.getValue() == Boolean.TRUE);
                    myBooleanEditor.setEnabled(node.isEnabled());
                } else if (node.getKey() instanceof IntOption intOption) {
                    myCurrentEditor = myIntOptionsEditor;
                    myIntOptionsEditor.setCanBeEmpty(true);
                    myIntOptionsEditor.setMinValue(intOption.getMinValue());
                    myIntOptionsEditor.setMaxValue(intOption.getMaxValue());
                    myIntOptionsEditor.setDefaultValue(intOption.getDefaultValue());
                    myIntOptionsEditor.setValue((Integer) node.getValue());
                } else if (node.getKey() instanceof SelectionOption) {
                    myCurrentEditor = myOptionsEditor;
                    myOptionsEditor.setCell(table, row, column);
                    myOptionsEditor.setText(String.valueOf(node.getValue()));
                    //noinspection ConfusingArgumentToVarargsMethod
                    myOptionsEditor.setOptions(((SelectionOption) node.getKey()).options);
                    myOptionsEditor.setDefaultValue(node.getValue());
                }
            }

            if (myCurrentEditor != null) {
                myCurrentEditor.setBackground(table.getBackground());
                if (myCurrentEditor instanceof JTextField) {
                    myCurrentEditor.getInputMap().put(ENTER_KEY_STROKE, STOP_CELL_EDIT_ACTION_KEY);
                    myCurrentEditor.getActionMap().put(STOP_CELL_EDIT_ACTION_KEY, STOP_CELL_EDIT_ACTION);
                }
            }
            return myCurrentEditor;
        }
    }

    protected abstract class FieldOption extends Option {
        final @NotNull Field field;

        FieldOption(@NotNull String name,
                    @NotNull String label,
                    @Nullable String groupName,
                    @Nullable OptionAnchor anchor,
                    @Nullable String anchorFieldName) {
            super(name, label, groupName, anchor, anchorFieldName);

            try {
                this.field = CdsCodeStyleSettings.class.getField(name);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        protected Object getSettings(CodeStyleSettings settings) {
            return settings.getCustomSettings(CdsCodeStyleSettings.class);
        }

    }

    public final class IntOption extends FieldOption {

        private final int myMinValue;
        private final int myMaxValue;
        private final int myDefaultValue;

        public IntOption(@NotNull String name,
                         @NotNull String label,
                         @Nullable String groupName,
                         @Nullable OptionAnchor anchor,
                         @Nullable String anchorFieldName,
                         int minValue,
                         int maxValue,
                         int defaultValue) {
            super(name, label, groupName, anchor, anchorFieldName);
            myMinValue = minValue;
            myMaxValue = maxValue;
            myDefaultValue = defaultValue;
        }

        @Override
        public Object getValue(CodeStyleSettings settings) {
            try {
                return field.getInt(getSettings(settings));
            } catch (IllegalAccessException e) {
                return null;
            }
        }

        @Override
        public void setValue(Object value, CodeStyleSettings settings) {
            //noinspection EmptyCatchBlock
            try {
                if (value instanceof Integer) {
                    field.setInt(getSettings(settings), (Integer) value);
                } else {
                    field.setInt(getSettings(settings), myDefaultValue);
                }
            } catch (IllegalAccessException e) {
            }
        }

        public int getMinValue() {
            return myMinValue;
        }

        public int getMaxValue() {
            return myMaxValue;
        }

        public int getDefaultValue() {
            return myDefaultValue;
        }
    }

    private final class MyTitleRenderer extends ColoredTreeCellRenderer {

        private final SpeedSearchHelper mySearchHelper;

        private MyTitleRenderer(SpeedSearchHelper helper) {
            mySearchHelper = helper;
        }

        @Override
        public void customizeCellRenderer(@NotNull JTree tree,
                                          Object value,
                                          boolean selected,
                                          boolean expanded,
                                          boolean leaf,
                                          int row,
                                          boolean hasFocus) {
            SimpleTextAttributes attributes = SimpleTextAttributes.REGULAR_ATTRIBUTES;
            String text;
            if (value instanceof MyTreeNode node) {
                if (node.getKey().groupName == null) {
                    attributes = SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES;
                }
                text = node.getText();
                setEnabled(node.isEnabled());
            } else {
                attributes = SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES;
                text = value.toString();
                setEnabled(true);
            }
            mySearchHelper.setLabelText(this, text, attributes.getStyle(), attributes.getFgColor(), attributes.getBgColor());
            setBackground(RenderingUtil.getBackground(tree, selected));
            setForeground(RenderingUtil.getForeground(tree, selected));
        }
    }

    private final class MyValueRenderer implements TableCellRenderer {
        private final OptionsLabel myComboBox = new OptionsLabel();
        private final JCheckBox myCheckBox = new JBCheckBox();
        private final JPanel myEmptyLabel = new JPanel();
        private final JLabel myIntLabel = new JLabel();
        private JTable myTable;
        private int myRow;
        private int myColumn;

        MyValueRenderer() {
            UIUtil.applyStyle(UIUtil.ComponentStyle.SMALL, myComboBox);
            UIUtil.applyStyle(UIUtil.ComponentStyle.SMALL, myCheckBox);
            UIUtil.applyStyle(UIUtil.ComponentStyle.SMALL, myIntLabel);
        }

        @Override
        public @NotNull Component getTableCellRendererComponent(@NotNull JTable table,
                                                                Object value,
                                                                boolean isSelected,
                                                                boolean hasFocus,
                                                                int row,
                                                                int column) {
            myTable = table;
            myRow = row;
            myColumn = column;
            boolean isEnabled = true;
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((TreeTable) table).getTree().
                    getPathForRow(row).getLastPathComponent();
            if (node instanceof MyTreeNode) {
                isEnabled = ((MyTreeNode) node).isEnabled();
            }
            if (!table.isEnabled()) {
                isEnabled = false;
            }
            if (value instanceof Boolean) {
                myCheckBox.setSelected((Boolean) value);
                myCheckBox.setEnabled(isEnabled);
                updateColors(myCheckBox, table, isSelected);
                return myCheckBox;
            } else if (value instanceof String) {
                myComboBox.setText((String) value);
                myComboBox.setEnabled(isEnabled);
                updateColors(myComboBox, table, isSelected);
                return myComboBox;
            } else if (value instanceof Integer) {
                @NlsSafe String text = value.toString();
                myIntLabel.setText(text);
                updateColors(myIntLabel, table, isSelected);
                return myIntLabel;
            }
            updateColors(myEmptyLabel, table, isSelected);
            return myEmptyLabel;
        }

        protected final class OptionsLabel extends JLabel {
            @Override
            public AccessibleContext getAccessibleContext() {
                if (accessibleContext == null) {
                    accessibleContext = new AccessibleOptionsLabel();
                }
                return accessibleContext;
            }

            protected final class AccessibleOptionsLabel extends AccessibleJLabel implements AccessibleAction {
                @Override
                public AccessibleRole getAccessibleRole() {
                    return AccessibleRole.PUSH_BUTTON;
                }

                @Override
                public AccessibleAction getAccessibleAction() {
                    return this;
                }

                @Override
                public int getAccessibleActionCount() {
                    return 1;
                }

                @Override
                public String getAccessibleActionDescription(int i) {
                    if (i == 0) {
                        return UIManager.getString("AbstractButton.clickText");
                    } else {
                        return null;
                    }
                }

                @Override
                public boolean doAccessibleAction(int i) {
                    if (i == 0) {
                        myTable.editCellAt(myRow, myColumn);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
    }
}
