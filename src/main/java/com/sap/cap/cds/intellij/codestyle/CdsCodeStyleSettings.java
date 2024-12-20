package com.sap.cap.cds.intellij.codestyle;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category.*;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Type.*;


public class CdsCodeStyleSettings extends CdsCodeStyleSettingsBase {

    public static final Map<String, CdsCodeStyleOption<?>> OPTIONS = new LinkedHashMap<>();
    public static final Map<Category, Set<String>> CATEGORY_GROUPS = new LinkedHashMap<>();
    public static final String SAMPLE_SRC = """
                                            using {
                                                Employee    as Worker,
                                                WorkerIdent as WorkerID
                                            } from './employees';
                                            
                                            @requires            : 'verified-user'  @insertonly
                                            @PropertyRestrictions: true             @Searchable: true
                                            entity Project : managed {
                                                key projectId : /* UUID */ String(20) = 4;
                                                    owner     :            Association to one Worker @cds.on.insert /*1*/: #worker;
                                                    tasks     :            Composition of many {
                                                                               taskId : Integer;
                                                                           }
                                                    urgency   :            Integer    = 2;
                                                    deadline  :            DateTime                  @cds.on.insert      : /*2*/ #now;
                                            }
                                            
                                            context Records {
                                                entity ProjectRecords   as
                                                        select from Project as project
                                                        join Tasks as task
                                                            on  task.projectRef  =      'p' + project.projectId
                                                            and project.deadline is not null
                                                        mixin {
                                                            taskCategory    : Association to one TaskCategory
                                            
                                                                                  on taskCategory.active = true;
                                                            rejectionReason : Association to one Description;
                                                        }
                                                        into {
                                                            project.projectId : String,
                                                            project.deadline as deadline,
                                                            project.owner    as owner
                                                        }
                                                    union
                                                        select from GeneralProjects
                                                        mixin {}
                                                        into {
                                                            'none' as projectId
                                                        }
                                                    actions {
                                                        action printReport() returns Integer;
                                                        function sortTasks() returns array of Project;
                                                    };
                                            
                                                entity ArchivedProjects as
                                                    select from Archived {
                                            
                                            
                                                        // only expose projectId
                                                        projectId
                                                    }
                                            }
                                            
                                            type Identifier  : Integer;
                                            /**
                                             * # The Description
                                             *
                                             * This is a very precise sentence as it has minimal key phrases and will format properly if extended.
                                             *
                                             * * * *
                                             *
                                             *   - _italicized_ and **bold**
                                             * - **bold** or _italicized_
                                             *
                                             * 1. efficient
                                             *    2. scalable
                                             *    | name    | tenure |
                                             *   | ------- | -----: |
                                             *   | alice   |    10 |
                                             *  | bob     |3 |
                                             *  | manager |25 |
                                             */
                                            type Description : String;
                                            
                                            """;

    static {
        OPTIONS.put("alignActionNames", new CdsCodeStyleOption<>("alignActionNames", BOOLEAN, true, "Names", "Actions and functions", ALIGNMENT));
        OPTIONS.put("alignActionReturns", new CdsCodeStyleOption<>("alignActionReturns", BOOLEAN, true, "'returns' keyword", "Actions and functions", ALIGNMENT));
        OPTIONS.put("alignActionsAndFunctions", new CdsCodeStyleOption<>("alignActionsAndFunctions", BOOLEAN, true, "Align actions and functions", "Actions and functions", ALIGNMENT));
        OPTIONS.put("alignAfterKey", new CdsCodeStyleOption<>("alignAfterKey", BOOLEAN, true, "Align element names and 'select' items after 'key'", "Other", ALIGNMENT));
        OPTIONS.put("alignAnnotations", new CdsCodeStyleOption<>("alignAnnotations", BOOLEAN, true, "Align annotations", "Annotations", ALIGNMENT));
        OPTIONS.put("alignAs", new CdsCodeStyleOption<>("alignAs", BOOLEAN, true, "Align 'as'", "'as'", ALIGNMENT));
        OPTIONS.put("alignAsInEntities", new CdsCodeStyleOption<>("alignAsInEntities", BOOLEAN, true, "In entities", "'as'", ALIGNMENT));
        OPTIONS.put("alignAsInSelectItems", new CdsCodeStyleOption<>("alignAsInSelectItems", BOOLEAN, true, "In 'select' items", "'as'", ALIGNMENT));
        OPTIONS.put("alignAsInUsing", new CdsCodeStyleOption<>("alignAsInUsing", BOOLEAN, true, "In 'using'", "'as'", ALIGNMENT));
        OPTIONS.put("alignColonsBeforeTypes", new CdsCodeStyleOption<>("alignColonsBeforeTypes", BOOLEAN, true, "Including colons", "Types of elements", ALIGNMENT));
        OPTIONS.put("alignColonsInAnnotations", new CdsCodeStyleOption<>("alignColonsInAnnotations", BOOLEAN, true, "Colons", "Annotations", ALIGNMENT));
        OPTIONS.put("alignCompositionStructToRight", new CdsCodeStyleOption<>("alignCompositionStructToRight", BOOLEAN, true, "Align struct in 'composition' to the right", "Types of elements", ALIGNMENT));
        OPTIONS.put("alignEqualsAfterTypes", new CdsCodeStyleOption<>("alignEqualsAfterTypes", BOOLEAN, true, "Including assignment operators", "Types of elements", ALIGNMENT));
        OPTIONS.put("alignExprAndCondWithinBlock", new CdsCodeStyleOption<>("alignExprAndCondWithinBlock", BOOLEAN, true, "Within block", "Expressions and conditions", ALIGNMENT));
        OPTIONS.put("alignExpressionsAndConditions", new CdsCodeStyleOption<>("alignExpressionsAndConditions", BOOLEAN, true, "Align expressions and conditions", "Expressions and conditions", ALIGNMENT));
        OPTIONS.put("alignPostAnnotations", new CdsCodeStyleOption<>("alignPostAnnotations", BOOLEAN, true, "Post-annotations", "Annotations", ALIGNMENT));
        OPTIONS.put("alignPreAnnotations", new CdsCodeStyleOption<>("alignPreAnnotations", BOOLEAN, true, "Pre-annotations", "Annotations", ALIGNMENT));
        OPTIONS.put("alignTypes", new CdsCodeStyleOption<>("alignTypes", BOOLEAN, true, "Align types of elements", "Types of elements", ALIGNMENT));
        OPTIONS.put("alignTypesWithinBlock", new CdsCodeStyleOption<>("alignTypesWithinBlock", BOOLEAN, true, "Within block", "Types of elements", ALIGNMENT));
        OPTIONS.put("alignValuesInAnnotations", new CdsCodeStyleOption<>("alignValuesInAnnotations", BOOLEAN, true, "Values", "Annotations", ALIGNMENT));
        OPTIONS.put("cqlKeywordCapitalization", new CdsCodeStyleOption<>("cqlKeywordCapitalization", ENUM, CqlKeywordCapitalization.LOWER.getId(), "Capitalization style of CQL keywords", "Other", OTHER, CqlKeywordCapitalization.LOWER, CqlKeywordCapitalization.UPPER, CqlKeywordCapitalization.TITLE, CqlKeywordCapitalization.AS_IS));
        OPTIONS.put("finalNewline", new CdsCodeStyleOption<>("finalNewline", BOOLEAN, true, "Final newline", "Other", WRAPPING_AND_BRACES));
        OPTIONS.put("formatDocComments", new CdsCodeStyleOption<>("formatDocComments", BOOLEAN, false, "Format markdown in doc comments", "Format markdown in doc comments", COMMENTS));
        OPTIONS.put("keepEmptyBracketsTogether", new CdsCodeStyleOption<>("keepEmptyBracketsTogether", BOOLEAN, true, "Keep empty brackets in same line", "Other", WRAPPING_AND_BRACES));
        OPTIONS.put("keepOriginalEmptyLines", new CdsCodeStyleOption<>("keepOriginalEmptyLines", BOOLEAN, true, "Keep original empty lines", "Other", BLANK_LINES));
        OPTIONS.put("keepPostAnnotationsInOriginalLine", new CdsCodeStyleOption<>("keepPostAnnotationsInOriginalLine", ENUM, KeepPostAnnotationsInOriginalLine.KEEP_LINE.getId(), "Line wrapping of post-annotations", "Other", WRAPPING_AND_BRACES, KeepPostAnnotationsInOriginalLine.KEEP_LINE, KeepPostAnnotationsInOriginalLine.SEPARATE_LINE));
        OPTIONS.put("keepPreAnnotationsInOriginalLine", new CdsCodeStyleOption<>("keepPreAnnotationsInOriginalLine", ENUM, KeepPreAnnotationsInOriginalLine.KEEP_LINE.getId(), "Line wrapping of pre-annotations", "Other", WRAPPING_AND_BRACES, KeepPreAnnotationsInOriginalLine.KEEP_LINE, KeepPreAnnotationsInOriginalLine.SEPARATE_LINE));
        OPTIONS.put("keepSingleLinedBlocksTogether", new CdsCodeStyleOption<>("keepSingleLinedBlocksTogether", BOOLEAN, true, "Keep similar single-lined blocks together", "Other", BLANK_LINES));
        OPTIONS.put("maxDocCommentLine", new CdsCodeStyleOption<>("maxDocCommentLine", INT, 60, "Max doc comment line length", "Format markdown in doc comments", WRAPPING_AND_BRACES));
        OPTIONS.put("maxKeepEmptyLines", new CdsCodeStyleOption<>("maxKeepEmptyLines", INT, 2, "Maximum consecutive empty lines", "Other", BLANK_LINES));
        OPTIONS.put("openingBraceInNewLine", new CdsCodeStyleOption<>("openingBraceInNewLine", BOOLEAN, false, "Line wrapping before opening brace", "Other", WRAPPING_AND_BRACES));
        OPTIONS.put("selectInNewLine", new CdsCodeStyleOption<>("selectInNewLine", BOOLEAN, true, "Line wrapping before 'select' of entity or view", "Other", WRAPPING_AND_BRACES));
        OPTIONS.put("tabSize", new CdsCodeStyleOption<>("tabSize", INT, 2, "Tab size", "Other", TABS_AND_INDENTS));
        OPTIONS.put("whitespaceAfterColon", new CdsCodeStyleOption<>("whitespaceAfterColon", BOOLEAN, true, "Blank after colon", "After colon", SPACES));
        OPTIONS.put("whitespaceAfterColonInAnnotation", new CdsCodeStyleOption<>("whitespaceAfterColonInAnnotation", BOOLEAN, true, "Blank after colon in annotations", "After colon", SPACES));
        OPTIONS.put("whitespaceAfterComma", new CdsCodeStyleOption<>("whitespaceAfterComma", BOOLEAN, true, "Blank after comma", "Other", SPACES));
        OPTIONS.put("whitespaceAroundAlignedOps", new CdsCodeStyleOption<>("whitespaceAroundAlignedOps", BOOLEAN, true, "Blanks around aligned binary operators and colons", "Other", SPACES));
        OPTIONS.put("whitespaceAroundBinaryOps", new CdsCodeStyleOption<>("whitespaceAroundBinaryOps", BOOLEAN, true, "Blanks around binary operators", "Other", SPACES));
        OPTIONS.put("whitespaceBeforeColon", new CdsCodeStyleOption<>("whitespaceBeforeColon", BOOLEAN, true, "Blank before colon", "Before colon", SPACES));
        OPTIONS.put("whitespaceBeforeColonInAnnotation", new CdsCodeStyleOption<>("whitespaceBeforeColonInAnnotation", BOOLEAN, false, "Blank before colon in annotations", "Before colon", SPACES));
        OPTIONS.put("whitespaceWithinBrackets", new CdsCodeStyleOption<>("whitespaceWithinBrackets", BOOLEAN, false, "Blanks within brackets", "Other", SPACES));

        CATEGORY_GROUPS.put(Category.TABS_AND_INDENTS, Set.of("Other"));
        CATEGORY_GROUPS.put(Category.SPACES, Set.of("After colon", "Other", "Before colon"));
        CATEGORY_GROUPS.put(Category.ALIGNMENT, Set.of("Actions and functions", "Other", "Annotations", "'as'", "Types of elements", "Expressions and conditions"));
        CATEGORY_GROUPS.put(Category.WRAPPING_AND_BRACES, Set.of("Other", "Format markdown in doc comments"));
        CATEGORY_GROUPS.put(Category.BLANK_LINES, Set.of("Other"));
        CATEGORY_GROUPS.put(Category.COMMENTS, Set.of("Format markdown in doc comments"));
        CATEGORY_GROUPS.put(Category.OTHER, Set.of("Other"));
    }
    public CdsCodeStyleSettings(CodeStyleSettings settings) {
        super("CDSCodeStyleSettings", settings);
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
