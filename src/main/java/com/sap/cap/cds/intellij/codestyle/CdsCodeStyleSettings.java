package com.sap.cap.cds.intellij.codestyle;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOptionDef.Category;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.sap.cap.cds.intellij.util.ReflectionUtil.getFieldValue;
import static java.util.stream.Collectors.toMap;

public class CdsCodeStyleSettings extends CustomCodeStyleSettings {

    public static final Map<String, CdsCodeStyleOptionDef<?>> OPTION_DEFS = new HashMap<>();
    public static final Map<Category, Set<String>> CATEGORY_GROUPS = new HashMap<>();

    static {        
        OPTION_DEFS.put("alignAfterKey", new CdsCodeStyleOptionDef<>("alignAfterKey", true, "Align element names and 'select' items after 'key'", "Other", Category.ALIGNMENT));
        OPTION_DEFS.put("alignAnnotations", new CdsCodeStyleOptionDef<>("alignAnnotations", true, "Align annotations", "Annotations", Category.ALIGNMENT));
        OPTION_DEFS.put("alignPreAnnotations", new CdsCodeStyleOptionDef<>("alignPreAnnotations", true, "Pre-annotations", "Annotations", Category.ALIGNMENT));
        OPTION_DEFS.put("alignPostAnnotations", new CdsCodeStyleOptionDef<>("alignPostAnnotations", true, "Post-annotations", "Annotations", Category.ALIGNMENT));
        OPTION_DEFS.put("alignColonsInAnnotations", new CdsCodeStyleOptionDef<>("alignColonsInAnnotations", true, "Colons", "Annotations", Category.ALIGNMENT));
        OPTION_DEFS.put("alignValuesInAnnotations", new CdsCodeStyleOptionDef<>("alignValuesInAnnotations", true, "Values", "Annotations", Category.ALIGNMENT));
        OPTION_DEFS.put("alignActionsAndFunctions", new CdsCodeStyleOptionDef<>("alignActionsAndFunctions", true, "Align actions and functions", "Actions and functions", Category.ALIGNMENT));
        OPTION_DEFS.put("alignActionNames", new CdsCodeStyleOptionDef<>("alignActionNames", true, "Names", "Actions and functions", Category.ALIGNMENT));
        OPTION_DEFS.put("alignActionReturns", new CdsCodeStyleOptionDef<>("alignActionReturns", true, "'returns' keyword", "Actions and functions", Category.ALIGNMENT));
        OPTION_DEFS.put("alignAs", new CdsCodeStyleOptionDef<>("alignAs", true, "Align 'as'", "'as'", Category.ALIGNMENT));
        OPTION_DEFS.put("alignAsInEntities", new CdsCodeStyleOptionDef<>("alignAsInEntities", true, "In entities", "'as'", Category.ALIGNMENT));
        OPTION_DEFS.put("alignAsInSelectItems", new CdsCodeStyleOptionDef<>("alignAsInSelectItems", true, "In 'select' items", "'as'", Category.ALIGNMENT));
        OPTION_DEFS.put("alignAsInUsing", new CdsCodeStyleOptionDef<>("alignAsInUsing", true, "In 'using'", "'as'", Category.ALIGNMENT));
        OPTION_DEFS.put("alignExpressionsAndConditions", new CdsCodeStyleOptionDef<>("alignExpressionsAndConditions", true, "Align expressions and conditions", "Expressions and conditions", Category.ALIGNMENT));
        OPTION_DEFS.put("alignExprAndCondWithinBlock", new CdsCodeStyleOptionDef<>("alignExprAndCondWithinBlock", true, "Within block", "Expressions and conditions", Category.ALIGNMENT));
        OPTION_DEFS.put("alignTypes", new CdsCodeStyleOptionDef<>("alignTypes", true, "Align types of elements", "Types of elements", Category.ALIGNMENT));
        OPTION_DEFS.put("alignColonsBeforeTypes", new CdsCodeStyleOptionDef<>("alignColonsBeforeTypes", true, "Including colons", "Types of elements", Category.ALIGNMENT));
        OPTION_DEFS.put("alignEqualsAfterTypes", new CdsCodeStyleOptionDef<>("alignEqualsAfterTypes", true, "Including assignment operators", "Types of elements", Category.ALIGNMENT));
        OPTION_DEFS.put("alignTypesWithinBlock", new CdsCodeStyleOptionDef<>("alignTypesWithinBlock", true, "Within block", "Types of elements", Category.ALIGNMENT));
        OPTION_DEFS.put("alignCompositionStructToRight", new CdsCodeStyleOptionDef<>("alignCompositionStructToRight", true, "Align struct in 'composition' to the right", "Types of elements", Category.ALIGNMENT));
        OPTION_DEFS.put("keepEmptyBracketsTogether", new CdsCodeStyleOptionDef<>("keepEmptyBracketsTogether", true, "Keep empty brackets together", "Other", Category.WRAPPING_AND_BRACES));
        OPTION_DEFS.put("keepSingleLinedBlocksTogether", new CdsCodeStyleOptionDef<>("keepSingleLinedBlocksTogether", true, "Keep similar single-lined blocks together", "Other", Category.BLANK_LINES));
        OPTION_DEFS.put("keepOriginalEmptyLines", new CdsCodeStyleOptionDef<>("keepOriginalEmptyLines", true, "Keep original empty lines", "Other", Category.BLANK_LINES));
        OPTION_DEFS.put("maxKeepEmptyLines", new CdsCodeStyleOptionDef<>("maxKeepEmptyLines", 2, "Maximum consecutive empty lines", "Other", Category.BLANK_LINES));
        OPTION_DEFS.put("openingBraceInNewLine", new CdsCodeStyleOptionDef<>("openingBraceInNewLine", false, "Line wrapping before opening brace", "Other", Category.WRAPPING_AND_BRACES));
        OPTION_DEFS.put("selectInNewLine", new CdsCodeStyleOptionDef<>("selectInNewLine", true, "Line wrapping before 'select' of entity or view", "Other", Category.WRAPPING_AND_BRACES));
        OPTION_DEFS.put("tabSize", new CdsCodeStyleOptionDef<>("tabSize", 2, "Tab size", "Other", Category.TABS_AND_INDENTS));
        OPTION_DEFS.put("finalNewline", new CdsCodeStyleOptionDef<>("finalNewline", true, "Final newline", "Other", Category.WRAPPING_AND_BRACES));
        OPTION_DEFS.put("formatDocComments", new CdsCodeStyleOptionDef<>("formatDocComments", false, "Format markdown in doc comments", "Format markdown in doc comments", Category.OTHER));
        OPTION_DEFS.put("maxDocCommentLine", new CdsCodeStyleOptionDef<>("maxDocCommentLine", 60, "Max doc comment line length", "Format markdown in doc comments", Category.OTHER));
        OPTION_DEFS.put("whitespaceBeforeColon", new CdsCodeStyleOptionDef<>("whitespaceBeforeColon", true, "Blank before colon", "Before colon", Category.SPACES));
        OPTION_DEFS.put("whitespaceBeforeColonInAnnotation", new CdsCodeStyleOptionDef<>("whitespaceBeforeColonInAnnotation", false, "Blank before colon in annotations", "Before colon", Category.SPACES));
        OPTION_DEFS.put("whitespaceAfterColon", new CdsCodeStyleOptionDef<>("whitespaceAfterColon", true, "Blank after colon", "After colon", Category.SPACES));
        OPTION_DEFS.put("whitespaceAfterColonInAnnotation", new CdsCodeStyleOptionDef<>("whitespaceAfterColonInAnnotation", true, "Blank after colon in annotations", "After colon", Category.SPACES));
        OPTION_DEFS.put("whitespaceAfterComma", new CdsCodeStyleOptionDef<>("whitespaceAfterComma", true, "Blank after comma", "Other", Category.SPACES));
        OPTION_DEFS.put("whitespaceAroundAlignedOps", new CdsCodeStyleOptionDef<>("whitespaceAroundAlignedOps", true, "Blanks around aligned binary operators and colons", "Other", Category.SPACES));
        OPTION_DEFS.put("whitespaceAroundBinaryOps", new CdsCodeStyleOptionDef<>("whitespaceAroundBinaryOps", true, "Blanks around binary operators", "Other", Category.SPACES));
        OPTION_DEFS.put("whitespaceWithinBrackets", new CdsCodeStyleOptionDef<>("whitespaceWithinBrackets", false, "Blanks within brackets", "Other", Category.SPACES));
        
        CATEGORY_GROUPS.put(Category.ALIGNMENT, Set.of("Other", "Annotations", "Actions and functions", "'as'", "Expressions and conditions", "Types of elements"));
        CATEGORY_GROUPS.put(Category.WRAPPING_AND_BRACES, Set.of("Other"));
        CATEGORY_GROUPS.put(Category.BLANK_LINES, Set.of("Other"));
        CATEGORY_GROUPS.put(Category.TABS_AND_INDENTS, Set.of("Other"));
        CATEGORY_GROUPS.put(Category.OTHER, Set.of("Format markdown in doc comments"));
        CATEGORY_GROUPS.put(Category.SPACES, Set.of("Before colon", "After colon", "Other"));
    }
    
    public boolean alignAfterKey = true;
    public boolean alignAnnotations = true;
    public boolean alignPreAnnotations = true;
    public boolean alignPostAnnotations = true;
    public boolean alignColonsInAnnotations = true;
    public boolean alignValuesInAnnotations = true;
    public boolean alignActionsAndFunctions = true;
    public boolean alignActionNames = true;
    public boolean alignActionReturns = true;
    public boolean alignAs = true;
    public boolean alignAsInEntities = true;
    public boolean alignAsInSelectItems = true;
    public boolean alignAsInUsing = true;
    public boolean alignExpressionsAndConditions = true;
    public boolean alignExprAndCondWithinBlock = true;
    public boolean alignTypes = true;
    public boolean alignColonsBeforeTypes = true;
    public boolean alignEqualsAfterTypes = true;
    public boolean alignTypesWithinBlock = true;
    public boolean alignCompositionStructToRight = true;
    public boolean keepEmptyBracketsTogether = true;
    public boolean keepSingleLinedBlocksTogether = true;
    public boolean keepOriginalEmptyLines = true;
    public int maxKeepEmptyLines = 2;
    public boolean openingBraceInNewLine = false;
    public boolean selectInNewLine = true;
    public int tabSize = 2;
    public boolean finalNewline = true;
    public boolean formatDocComments = false;
    public int maxDocCommentLine = 60;
    public boolean whitespaceBeforeColon = true;
    public boolean whitespaceBeforeColonInAnnotation = false;
    public boolean whitespaceAfterColon = true;
    public boolean whitespaceAfterColonInAnnotation = true;
    public boolean whitespaceAfterComma = true;
    public boolean whitespaceAroundAlignedOps = true;
    public boolean whitespaceAroundBinaryOps = true;
    public boolean whitespaceWithinBrackets = false;

    public CdsCodeStyleSettings(CodeStyleSettings settings) {
        super("CDSCodeStyleSettings", settings);
    }

    public Map<String, Object> getNonDefaultSettings() {
        return OPTION_DEFS.entrySet().stream()
                .map(entry -> {
                    Object fieldValue;
                    try {
                        fieldValue = getFieldValue(this, entry.getKey());
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }

                    if (!entry.getValue().defaultValue.equals(fieldValue)) {
                        return Map.entry(entry.getKey(), fieldValue);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
