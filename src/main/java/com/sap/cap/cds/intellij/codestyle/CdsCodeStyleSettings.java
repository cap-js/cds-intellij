package com.sap.cap.cds.intellij.codestyle;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class CdsCodeStyleSettings extends CdsCodeStyleSettingsBase {

    public static final Map<String, CdsCodeStyleOption<?>> OPTIONS = new HashMap<>();
    public static final Map<Category, Set<String>> CATEGORY_GROUPS = new HashMap<>();

    static {
        OPTIONS.put("alignActionNames", new CdsCodeStyleOption<>("alignActionNames", true, "Names", "Actions and functions", Category.ALIGNMENT));
        OPTIONS.put("alignActionReturns", new CdsCodeStyleOption<>("alignActionReturns", true, "'returns' keyword", "Actions and functions", Category.ALIGNMENT));
        OPTIONS.put("alignActionsAndFunctions", new CdsCodeStyleOption<>("alignActionsAndFunctions", true, "Align actions and functions", "Actions and functions", Category.ALIGNMENT));
        OPTIONS.put("alignAfterKey", new CdsCodeStyleOption<>("alignAfterKey", true, "Align element names and 'select' items after 'key'", "Other", Category.ALIGNMENT));
        OPTIONS.put("alignAnnotations", new CdsCodeStyleOption<>("alignAnnotations", true, "Align annotations", "Annotations", Category.ALIGNMENT));
        OPTIONS.put("alignAs", new CdsCodeStyleOption<>("alignAs", true, "Align 'as'", "'as'", Category.ALIGNMENT));
        OPTIONS.put("alignAsInEntities", new CdsCodeStyleOption<>("alignAsInEntities", true, "In entities", "'as'", Category.ALIGNMENT));
        OPTIONS.put("alignAsInSelectItems", new CdsCodeStyleOption<>("alignAsInSelectItems", true, "In 'select' items", "'as'", Category.ALIGNMENT));
        OPTIONS.put("alignAsInUsing", new CdsCodeStyleOption<>("alignAsInUsing", true, "In 'using'", "'as'", Category.ALIGNMENT));
        OPTIONS.put("alignColonsBeforeTypes", new CdsCodeStyleOption<>("alignColonsBeforeTypes", true, "Including colons", "Types of elements", Category.ALIGNMENT));
        OPTIONS.put("alignColonsInAnnotations", new CdsCodeStyleOption<>("alignColonsInAnnotations", true, "Colons", "Annotations", Category.ALIGNMENT));
        OPTIONS.put("alignCompositionStructToRight", new CdsCodeStyleOption<>("alignCompositionStructToRight", true, "Align struct in 'composition' to the right", "Types of elements", Category.ALIGNMENT));
        OPTIONS.put("alignEqualsAfterTypes", new CdsCodeStyleOption<>("alignEqualsAfterTypes", true, "Including assignment operators", "Types of elements", Category.ALIGNMENT));
        OPTIONS.put("alignExprAndCondWithinBlock", new CdsCodeStyleOption<>("alignExprAndCondWithinBlock", true, "Within block", "Expressions and conditions", Category.ALIGNMENT));
        OPTIONS.put("alignExpressionsAndConditions", new CdsCodeStyleOption<>("alignExpressionsAndConditions", true, "Align expressions and conditions", "Expressions and conditions", Category.ALIGNMENT));
        OPTIONS.put("alignPostAnnotations", new CdsCodeStyleOption<>("alignPostAnnotations", true, "Post-annotations", "Annotations", Category.ALIGNMENT));
        OPTIONS.put("alignPreAnnotations", new CdsCodeStyleOption<>("alignPreAnnotations", true, "Pre-annotations", "Annotations", Category.ALIGNMENT));
        OPTIONS.put("alignTypes", new CdsCodeStyleOption<>("alignTypes", true, "Align types of elements", "Types of elements", Category.ALIGNMENT));
        OPTIONS.put("alignTypesWithinBlock", new CdsCodeStyleOption<>("alignTypesWithinBlock", true, "Within block", "Types of elements", Category.ALIGNMENT));
        OPTIONS.put("alignValuesInAnnotations", new CdsCodeStyleOption<>("alignValuesInAnnotations", true, "Values", "Annotations", Category.ALIGNMENT));
        OPTIONS.put("cqlKeywordCapitalization", new CdsCodeStyleOption<>("cqlKeywordCapitalization", CqlKeywordCapitalization.LOWER.getId(), "Capitalization style of CQL keywords", "Other", Category.OTHER, CqlKeywordCapitalization.LOWER, CqlKeywordCapitalization.UPPER, CqlKeywordCapitalization.TITLE, CqlKeywordCapitalization.AS_IS));
        OPTIONS.put("finalNewline", new CdsCodeStyleOption<>("finalNewline", true, "Final newline", "Other", Category.WRAPPING_AND_BRACES));
        OPTIONS.put("formatDocComments", new CdsCodeStyleOption<>("formatDocComments", false, "Format markdown in doc comments", "Format markdown in doc comments", Category.COMMENTS));
        OPTIONS.put("keepEmptyBracketsTogether", new CdsCodeStyleOption<>("keepEmptyBracketsTogether", true, "Keep empty brackets in same line", "Other", Category.WRAPPING_AND_BRACES));
        OPTIONS.put("keepOriginalEmptyLines", new CdsCodeStyleOption<>("keepOriginalEmptyLines", true, "Keep original empty lines", "Other", Category.BLANK_LINES));
        OPTIONS.put("keepPostAnnotationsInOriginalLine", new CdsCodeStyleOption<>("keepPostAnnotationsInOriginalLine", KeepPostAnnotationsInOriginalLine.KEEP_LINE.getId(), "Line wrapping of post-annotations", "Other", Category.WRAPPING_AND_BRACES, KeepPostAnnotationsInOriginalLine.KEEP_LINE, KeepPostAnnotationsInOriginalLine.SEPARATE_LINE));
        OPTIONS.put("keepPreAnnotationsInOriginalLine", new CdsCodeStyleOption<>("keepPreAnnotationsInOriginalLine", KeepPreAnnotationsInOriginalLine.KEEP_LINE.getId(), "Line wrapping of pre-annotations", "Other", Category.WRAPPING_AND_BRACES, KeepPreAnnotationsInOriginalLine.KEEP_LINE, KeepPreAnnotationsInOriginalLine.SEPARATE_LINE));
        OPTIONS.put("keepSingleLinedBlocksTogether", new CdsCodeStyleOption<>("keepSingleLinedBlocksTogether", true, "Keep similar single-lined blocks together", "Other", Category.BLANK_LINES));
        OPTIONS.put("maxDocCommentLine", new CdsCodeStyleOption<>("maxDocCommentLine", 60, "Max doc comment line length", "Format markdown in doc comments", Category.WRAPPING_AND_BRACES));
        OPTIONS.put("maxKeepEmptyLines", new CdsCodeStyleOption<>("maxKeepEmptyLines", 2, "Maximum consecutive empty lines", "Other", Category.BLANK_LINES));
        OPTIONS.put("openingBraceInNewLine", new CdsCodeStyleOption<>("openingBraceInNewLine", false, "Line wrapping before opening brace", "Other", Category.WRAPPING_AND_BRACES));
        OPTIONS.put("selectInNewLine", new CdsCodeStyleOption<>("selectInNewLine", true, "Line wrapping before 'select' of entity or view", "Other", Category.WRAPPING_AND_BRACES));
        OPTIONS.put("tabSize", new CdsCodeStyleOption<>("tabSize", 2, "Tab size", "Other", Category.TABS_AND_INDENTS));
        OPTIONS.put("whitespaceAfterColon", new CdsCodeStyleOption<>("whitespaceAfterColon", true, "Blank after colon", "After colon", Category.SPACES));
        OPTIONS.put("whitespaceAfterColonInAnnotation", new CdsCodeStyleOption<>("whitespaceAfterColonInAnnotation", true, "Blank after colon in annotations", "After colon", Category.SPACES));
        OPTIONS.put("whitespaceAfterComma", new CdsCodeStyleOption<>("whitespaceAfterComma", true, "Blank after comma", "Other", Category.SPACES));
        OPTIONS.put("whitespaceAroundAlignedOps", new CdsCodeStyleOption<>("whitespaceAroundAlignedOps", true, "Blanks around aligned binary operators and colons", "Other", Category.SPACES));
        OPTIONS.put("whitespaceAroundBinaryOps", new CdsCodeStyleOption<>("whitespaceAroundBinaryOps", true, "Blanks around binary operators", "Other", Category.SPACES));
        OPTIONS.put("whitespaceBeforeColon", new CdsCodeStyleOption<>("whitespaceBeforeColon", true, "Blank before colon", "Before colon", Category.SPACES));
        OPTIONS.put("whitespaceBeforeColonInAnnotation", new CdsCodeStyleOption<>("whitespaceBeforeColonInAnnotation", false, "Blank before colon in annotations", "Before colon", Category.SPACES));
        OPTIONS.put("whitespaceWithinBrackets", new CdsCodeStyleOption<>("whitespaceWithinBrackets", false, "Blanks within brackets", "Other", Category.SPACES));

        CATEGORY_GROUPS.put(Category.ALIGNMENT, Set.of("Actions and functions", "Other", "Annotations", "'as'", "Types of elements", "Expressions and conditions"));
        CATEGORY_GROUPS.put(Category.OTHER, Set.of("Other"));
        CATEGORY_GROUPS.put(Category.WRAPPING_AND_BRACES, Set.of("Other", "Format markdown in doc comments"));
        CATEGORY_GROUPS.put(Category.COMMENTS, Set.of("Format markdown in doc comments"));
        CATEGORY_GROUPS.put(Category.BLANK_LINES, Set.of("Other"));
        CATEGORY_GROUPS.put(Category.TABS_AND_INDENTS, Set.of("Other"));
        CATEGORY_GROUPS.put(Category.SPACES, Set.of("After colon", "Other", "Before colon"));
    }

    public boolean alignActionNames = true;
    public boolean alignActionReturns = true;
    public boolean alignActionsAndFunctions = true;
    public boolean alignAfterKey = true;
    public boolean alignAnnotations = true;
    public boolean alignAs = true;
    public boolean alignAsInEntities = true;
    public boolean alignAsInSelectItems = true;
    public boolean alignAsInUsing = true;
    public boolean alignColonsBeforeTypes = true;
    public boolean alignColonsInAnnotations = true;
    public boolean alignCompositionStructToRight = true;
    public boolean alignEqualsAfterTypes = true;
    public boolean alignExprAndCondWithinBlock = true;
    public boolean alignExpressionsAndConditions = true;
    public boolean alignPostAnnotations = true;
    public boolean alignPreAnnotations = true;
    public boolean alignTypes = true;
    public boolean alignTypesWithinBlock = true;
    public boolean alignValuesInAnnotations = true;
    public int cqlKeywordCapitalization = CqlKeywordCapitalization.LOWER.getId();
    public boolean finalNewline = true;
    public boolean formatDocComments = false;
    public boolean keepEmptyBracketsTogether = true;
    public boolean keepOriginalEmptyLines = true;
    public int keepPostAnnotationsInOriginalLine = KeepPostAnnotationsInOriginalLine.KEEP_LINE.getId();
    public int keepPreAnnotationsInOriginalLine = KeepPreAnnotationsInOriginalLine.KEEP_LINE.getId();
    public boolean keepSingleLinedBlocksTogether = true;
    public int maxDocCommentLine = 60;
    public int maxKeepEmptyLines = 2;
    public boolean openingBraceInNewLine = false;
    public boolean selectInNewLine = true;
    public int tabSize = 2;
    public boolean whitespaceAfterColon = true;
    public boolean whitespaceAfterColonInAnnotation = true;
    public boolean whitespaceAfterComma = true;
    public boolean whitespaceAroundAlignedOps = true;
    public boolean whitespaceAroundBinaryOps = true;
    public boolean whitespaceBeforeColon = true;
    public boolean whitespaceBeforeColonInAnnotation = false;
    public boolean whitespaceWithinBrackets = false;

    public CdsCodeStyleSettings(CodeStyleSettings settings) {
        super("CDSCodeStyleSettings", settings);
    }

    public enum CqlKeywordCapitalization implements Enum {
        LOWER(0, "lower"),
        UPPER(1, "upper"),
        TITLE(2, "title"),
        AS_IS(3, "as-is");
        private final String label;
        private final int id;

        CqlKeywordCapitalization(int id, String label) {
            this.id = id;
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public int getId() {
            return id;
        }
    }

    public enum KeepPostAnnotationsInOriginalLine implements Enum {
        KEEP_LINE(0, "keepLine"),
        SEPARATE_LINE(1, "separateLine");
        private final String label;
        private final int id;

        KeepPostAnnotationsInOriginalLine(int id, String label) {
            this.id = id;
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public int getId() {
            return id;
        }
    }

    public enum KeepPreAnnotationsInOriginalLine implements Enum {
        KEEP_LINE(0, "keepLine"),
        SEPARATE_LINE(1, "separateLine");
        private final String label;
        private final int id;

        KeepPreAnnotationsInOriginalLine(int id, String label) {
            this.id = id;
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public int getId() {
            return id;
        }
    }

    public interface Enum {
        String getLabel();

        int getId();
    }

}
