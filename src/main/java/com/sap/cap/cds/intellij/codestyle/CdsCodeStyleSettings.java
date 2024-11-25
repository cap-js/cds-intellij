package com.sap.cap.cds.intellij.codestyle;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.sap.cap.cds.intellij.lsp.ServerDescriptor;
import com.sap.cap.cds.intellij.util.PathUtil;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sap.cap.cds.intellij.util.JsonUtil.getPropertyAtPath;

public class CdsCodeStyleSettings extends CustomCodeStyleSettings {

    public static final String RELATIVE_SCHEMA_PATH = "schemas/cds-prettier.json";

    public enum Region {
        ALIGNMENT
    }

    // region Alignment

    // region 'as' keyword
    public boolean ALIGN_AS = true;
    public boolean ALIGN_AS_IN_ENTITIES = true;
    public boolean ALIGN_AS_IN_SELECT_ITEMS = true;
    public boolean ALIGN_AS_IN_USING = true;
    // endregion

    // region 'key' keyword
    public boolean ALIGN_AFTER_KEY = true;
    // endregion

    // region Actions and functions
    public boolean ALIGN_ACTIONS_FUNCTIONS = true;
    public boolean ALIGN_ACTIONS_FUNCTIONS_NAMES = true;
    public boolean ALIGN_ACTIONS_FUNCTIONS_RETURNS = true;
    // endregion

    // region Annotations
    public boolean ALIGN_ANNOTATIONS = true;
    public boolean ALIGN_ANNOTATIONS_PRE = true;
    public boolean ALIGN_ANNOTATIONS_POST = true;
    public boolean ALIGN_ANNOTATIONS_COLONS = true;
    public boolean ALIGN_ANNOTATIONS_VALUES = true;
    // endregion

    // region Expressions and conditions
    public boolean ALIGN_EXPRESSIONS_CONDITIONS = true;
    public boolean ALIGN_EXPRESSIONS_CONDITIONS_WITHIN_BLOCK = true;
    // endregion

    // region Types
    public boolean ALIGN_TYPES = true;
    public boolean ALIGN_TYPES_WITHIN_BLOCK = true;
    public boolean ALIGN_TYPES_COLONS = true;
    public boolean ALIGN_TYPES_EQUALS = true;
    public boolean ALIGN_TYPES_COMPOSITION_STRUCT_RIGHT = true;
    // endregion

    // endregion

    public CdsCodeStyleSettings(CodeStyleSettings settings) {
        super("CDSCodeStyleSettings", settings);
    }

    public static List<String> readFromCdsLsp() {
        String schemaPath = PathUtil.resolve(ServerDescriptor.RELATIVE_SERVER_BASE_PATH + RELATIVE_SCHEMA_PATH);
        Map<String, Object> properties;
        try {
            String schema = new String(Files.readAllBytes(Paths.get(schemaPath)));
            properties = ((JSONObject) getPropertyAtPath(schema, new String[]{"properties"})).toMap();
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse .cdsprettier.json schema", e);
        }

        ArrayList<String> keys = new ArrayList<>(properties.keySet());

        return keys;
    }

    // TODO return actual settings somehow
}
