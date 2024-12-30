package com.sap.cap.cds.intellij.codestyle;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category;

import java.util.List;
import java.util.Set;

import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category.*;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Type.*;

// NOTE: class body is generated
public class CdsCodeStyleSettings extends CdsCodeStyleSettingsBase {

    public static final String SAMPLE_SRC = """
                                            using {
                                              Person    as User,
                                              UserIdent as UserID
                                            } from './persons';
                                            
                                            @requires             : 'authenticated-user'  @insertonly
                                            @PropertyRestrictions : true                  @Searchable : true
                                            entity Task : managed {
                                              key id       : /* UUID */ String(20) = 4;
                                                  assignee :            Association to one User @cds.on.insert /*1*/ :       #user;
                                                  subTasks :            Composition of many {
                                                                          id : Integer;
                                                                        }
                                                  priority :            Integer = 2;
                                                  dueOn    :            DateTime                @cds.on.insert       : /*2*/ #now;
                                            }
                                            
                                            context Lists {
                                              entity TaskList    as
                                                  select from Task as task
                                                  join ToDo as todo
                                                    on  todo.taskId =      't' + task.id
                                                    and task.dueOn  is not null
                                                  mixin {
                                                    taskType        : Association to one TaskType
                                            
                                                                        on taskType.available = true;
                                                    rejectionStatus : Association to one Text;
                                                  }
                                                  into {
                                                    task.id : String,
                                                    task.dueOn    as dueOn,
                                                    task.assignee as assignee
                                                  }
                                                union
                                                  select from GenericTasks
                                                  mixin {}
                                                  into {
                                                    'none' as id
                                                  }
                                                  actions {
                                                    action print()  returns Integer;
                                                    function sort() returns array of Task;
                                                  };
                                            
                                              entity HiddenTasks as
                                                select from Hidden {
                                            
                                            
                                                  // only expose ID
                                                  id
                                                }
                                            }
                                            
                                            
                                            type Number : Integer;
                                            /**
                                             * # The Text
                                             *
                                             * This is a very delicate sentence as it has few tiny words in it and will wrap if too long
                                             *
                                             * * * *
                                             *
                                             * - _italics_ and **bold**
                                             * - **bold** or _italics_
                                             *
                                             * 1. good
                                             * 2. cheap
                                             *
                                             *    | name    | age |
                                             *    | ------- | --: |
                                             *    | john    |  39 |
                                             *    | joe     |   2 |
                                             *    | grandma | 108 |
                                             */
                                            type Text : String;
                                            
                                            """;

    static {
        OPTIONS.put("alignActionNames", new CdsCodeStyleOption("alignActionNames", BOOLEAN, true, "Names", "Actions and functions", ALIGNMENT, "alignActionsAndFunctions", List.of()));
        OPTIONS.put("alignActionReturns", new CdsCodeStyleOption("alignActionReturns", BOOLEAN, true, "'returns' keyword", "Actions and functions", ALIGNMENT, "alignActionsAndFunctions", List.of()));
        OPTIONS.put("alignActionsAndFunctions", new CdsCodeStyleOption("alignActionsAndFunctions", BOOLEAN, true, "Align actions and functions", "Actions and functions", ALIGNMENT, null, List.of("alignActionNames", "alignActionReturns")));
        OPTIONS.put("alignAfterKey", new CdsCodeStyleOption("alignAfterKey", BOOLEAN, true, "Align element names and 'select' items after 'key'", "Other", ALIGNMENT, null, List.of()));
        OPTIONS.put("alignAnnotations", new CdsCodeStyleOption("alignAnnotations", BOOLEAN, true, "Align annotations", "Annotations", ALIGNMENT, null, List.of("alignPreAnnotations", "alignPostAnnotations", "alignColonsInAnnotations", "alignValuesInAnnotations")));
        OPTIONS.put("alignAs", new CdsCodeStyleOption("alignAs", BOOLEAN, true, "Align 'as'", "'as'", ALIGNMENT, null, List.of("alignAsInEntities", "alignAsInSelectItems", "alignAsInUsing")));
        OPTIONS.put("alignAsInEntities", new CdsCodeStyleOption("alignAsInEntities", BOOLEAN, true, "In entities", "'as'", ALIGNMENT, "alignAs", List.of()));
        OPTIONS.put("alignAsInSelectItems", new CdsCodeStyleOption("alignAsInSelectItems", BOOLEAN, true, "In 'select' items", "'as'", ALIGNMENT, "alignAs", List.of()));
        OPTIONS.put("alignAsInUsing", new CdsCodeStyleOption("alignAsInUsing", BOOLEAN, true, "In 'using'", "'as'", ALIGNMENT, "alignAs", List.of()));
        OPTIONS.put("alignColonsBeforeTypes", new CdsCodeStyleOption("alignColonsBeforeTypes", BOOLEAN, true, "Including colons", "Types of elements", ALIGNMENT, "alignTypes", List.of()));
        OPTIONS.put("alignColonsInAnnotations", new CdsCodeStyleOption("alignColonsInAnnotations", BOOLEAN, true, "Colons", "Annotations", ALIGNMENT, "alignAnnotations", List.of()));
        OPTIONS.put("alignCompositionStructToRight", new CdsCodeStyleOption("alignCompositionStructToRight", BOOLEAN, true, "Align struct in 'composition' to the right", "Types of elements", ALIGNMENT, "alignTypes", List.of()));
        OPTIONS.put("alignEqualsAfterTypes", new CdsCodeStyleOption("alignEqualsAfterTypes", BOOLEAN, true, "Including assignment operators", "Types of elements", ALIGNMENT, "alignTypes", List.of()));
        OPTIONS.put("alignExprAndCondWithinBlock", new CdsCodeStyleOption("alignExprAndCondWithinBlock", BOOLEAN, true, "Within block", "Expressions and conditions", ALIGNMENT, "alignExpressionsAndConditions", List.of()));
        OPTIONS.put("alignExpressionsAndConditions", new CdsCodeStyleOption("alignExpressionsAndConditions", BOOLEAN, true, "Align expressions and conditions", "Expressions and conditions", ALIGNMENT, null, List.of("alignExprAndCondWithinBlock")));
        OPTIONS.put("alignPostAnnotations", new CdsCodeStyleOption("alignPostAnnotations", BOOLEAN, true, "Post-annotations", "Annotations", ALIGNMENT, "alignAnnotations", List.of()));
        OPTIONS.put("alignPreAnnotations", new CdsCodeStyleOption("alignPreAnnotations", BOOLEAN, true, "Pre-annotations", "Annotations", ALIGNMENT, "alignAnnotations", List.of()));
        OPTIONS.put("alignTypes", new CdsCodeStyleOption("alignTypes", BOOLEAN, true, "Align types of elements", "Types of elements", ALIGNMENT, null, List.of("alignColonsBeforeTypes", "alignEqualsAfterTypes", "alignTypesWithinBlock", "alignCompositionStructToRight")));
        OPTIONS.put("alignTypesWithinBlock", new CdsCodeStyleOption("alignTypesWithinBlock", BOOLEAN, true, "Within block", "Types of elements", ALIGNMENT, "alignTypes", List.of()));
        OPTIONS.put("alignValuesInAnnotations", new CdsCodeStyleOption("alignValuesInAnnotations", BOOLEAN, true, "Values", "Annotations", ALIGNMENT, "alignAnnotations", List.of()));
        OPTIONS.put("cqlKeywordCapitalization", new CdsCodeStyleOption("cqlKeywordCapitalization", ENUM, CqlKeywordCapitalization.LOWER.getId(), "Capitalization style of CQL keywords", "Other", OTHER, null, List.of(), CqlKeywordCapitalization.LOWER, CqlKeywordCapitalization.UPPER, CqlKeywordCapitalization.TITLE, CqlKeywordCapitalization.AS_IS));
        OPTIONS.put("finalNewline", new CdsCodeStyleOption("finalNewline", BOOLEAN, true, "Final newline", "Other", WRAPPING_AND_BRACES, null, List.of()));
        OPTIONS.put("formatDocComments", new CdsCodeStyleOption("formatDocComments", BOOLEAN, false, "Format markdown in doc comments", "Format markdown in doc comments", COMMENTS, null, List.of("maxDocCommentLine")));
        OPTIONS.put("keepEmptyBracketsTogether", new CdsCodeStyleOption("keepEmptyBracketsTogether", BOOLEAN, true, "Keep empty brackets in same line", "Other", WRAPPING_AND_BRACES, null, List.of()));
        OPTIONS.put("keepOriginalEmptyLines", new CdsCodeStyleOption("keepOriginalEmptyLines", BOOLEAN, true, "Keep original empty lines", "Other", BLANK_LINES, null, List.of()));
        OPTIONS.put("keepPostAnnotationsInOriginalLine", new CdsCodeStyleOption("keepPostAnnotationsInOriginalLine", ENUM, KeepPostAnnotationsInOriginalLine.KEEP_LINE.getId(), "Line wrapping of post-annotations", "Other", WRAPPING_AND_BRACES, null, List.of(), KeepPostAnnotationsInOriginalLine.KEEP_LINE, KeepPostAnnotationsInOriginalLine.SEPARATE_LINE));
        OPTIONS.put("keepPreAnnotationsInOriginalLine", new CdsCodeStyleOption("keepPreAnnotationsInOriginalLine", ENUM, KeepPreAnnotationsInOriginalLine.KEEP_LINE.getId(), "Line wrapping of pre-annotations", "Other", WRAPPING_AND_BRACES, null, List.of(), KeepPreAnnotationsInOriginalLine.KEEP_LINE, KeepPreAnnotationsInOriginalLine.SEPARATE_LINE));
        OPTIONS.put("keepSingleLinedBlocksTogether", new CdsCodeStyleOption("keepSingleLinedBlocksTogether", BOOLEAN, true, "Keep similar single-lined blocks together", "Other", BLANK_LINES, null, List.of()));
        OPTIONS.put("maxDocCommentLine", new CdsCodeStyleOption("maxDocCommentLine", INT, 60, "Max doc comment line length", "Format markdown in doc comments", WRAPPING_AND_BRACES, "formatDocComments", List.of()));
        OPTIONS.put("maxKeepEmptyLines", new CdsCodeStyleOption("maxKeepEmptyLines", INT, 2, "Maximum consecutive empty lines", "Other", BLANK_LINES, null, List.of()));
        OPTIONS.put("openingBraceInNewLine", new CdsCodeStyleOption("openingBraceInNewLine", BOOLEAN, false, "Line wrapping before opening brace", "Other", WRAPPING_AND_BRACES, null, List.of()));
        OPTIONS.put("selectInNewLine", new CdsCodeStyleOption("selectInNewLine", BOOLEAN, true, "Line wrapping before 'select' of entity or view", "Other", WRAPPING_AND_BRACES, null, List.of()));
        OPTIONS.put("tabSize", new CdsCodeStyleOption("tabSize", INT, 2, "Tab size", "Other", TABS_AND_INDENTS, null, List.of()));
        OPTIONS.put("whitespaceAfterColon", new CdsCodeStyleOption("whitespaceAfterColon", BOOLEAN, true, "Blank after colon", "After colon", SPACES, null, List.of("whitespaceAfterColonInAnnotation")));
        OPTIONS.put("whitespaceAfterColonInAnnotation", new CdsCodeStyleOption("whitespaceAfterColonInAnnotation", BOOLEAN, true, "Blank after colon in annotations", "After colon", SPACES, "whitespaceAfterColon", List.of()));
        OPTIONS.put("whitespaceAfterComma", new CdsCodeStyleOption("whitespaceAfterComma", BOOLEAN, true, "Blank after comma", "Other", SPACES, null, List.of()));
        OPTIONS.put("whitespaceAroundAlignedOps", new CdsCodeStyleOption("whitespaceAroundAlignedOps", BOOLEAN, true, "Blanks around aligned binary operators and colons", "Other", SPACES, null, List.of()));
        OPTIONS.put("whitespaceAroundBinaryOps", new CdsCodeStyleOption("whitespaceAroundBinaryOps", BOOLEAN, true, "Blanks around binary operators", "Other", SPACES, null, List.of()));
        OPTIONS.put("whitespaceBeforeColon", new CdsCodeStyleOption("whitespaceBeforeColon", BOOLEAN, true, "Blank before colon", "Before colon", SPACES, null, List.of("whitespaceBeforeColonInAnnotation")));
        OPTIONS.put("whitespaceBeforeColonInAnnotation", new CdsCodeStyleOption("whitespaceBeforeColonInAnnotation", BOOLEAN, false, "Blank before colon in annotations", "Before colon", SPACES, "whitespaceBeforeColon", List.of()));
        OPTIONS.put("whitespaceWithinBrackets", new CdsCodeStyleOption("whitespaceWithinBrackets", BOOLEAN, false, "Blanks within brackets", "Other", SPACES, null, List.of()));

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
