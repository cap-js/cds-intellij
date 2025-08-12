#!/usr/bin/env node

const path = require('path');
const { readFileSync, writeFileSync } = require('node:fs');

const schemaPath = path.resolve(__dirname, '../../../lsp/schemas/user-settings.json');
const schema = require(schemaPath);
const settingsFromSchema = schema.properties;

const srcPath = path.resolve(__dirname, '../../../src/templates/java/com/sap/cap/cds/intellij/usersettings/CdsUserSettings.java');
const tgtPath = path.resolve(__dirname, '../../../src/main/java/com/sap/cap/cds/intellij/usersettings/CdsUserSettings.java');
const src = readFileSync(srcPath, 'utf8');

function getDefaultValue(config) {
  const type = config.type;
  if (type === 'boolean') return config.default;
  if (type === 'integer') return config.default;
  if (type === 'array') return `"${Array.isArray(config.default) ? config.default.join(',') : ''}"`;
  return `"${(config.default || '').replace(/"/g, '\\"')}"`;
}

const settings = Object.entries(settingsFromSchema)
  .sort(([key1], [key2]) => key1.localeCompare(key2))
  .map(([key, config]) => ({
    key,
    defaultValue: getDefaultValue(config),
    enumValues: config.enum || null,
    label: config.label || null
  }));

const t = '        ';

const getAllSettingsMethod = `public Map<String, Object> getAllSettings() {
        Map<String, Object> defaults = new HashMap<>();
${settings.map(s => `${t}defaults.put("${s.key}", ${s.defaultValue});`).join('\n')}
        return defaults;
    }

    public static String getLabel(String settingKey) {
        switch (settingKey) {
${settings.filter(s => s.label).map(s =>
    `${t}${t}case "${s.key}": return "${s.label.replace(/"/g, '\\"')}";`
).join('\n')}
${t}${t}default: return null;
        }
    }

    public static String[] getEnumValues(String settingKey) {
        switch (settingKey) {
${settings.filter(s => s.enumValues).map(s =>
    `${t}${t}case "${s.key}": return new String[]{${s.enumValues.map(v => `"${v}"`).join(', ')}};`
).join('\n')}
${t}${t}default: return null;
        }
    }

    public static boolean hasEnumValues(String settingKey) {
        return getEnumValues(settingKey) != null;
    }`;

const patchedSrc = src.replace(
    /public Map<String, Object> getAllSettings\(\) \{[\s\S]*?return defaults;\s*\}/,
    getAllSettingsMethod
);

writeFileSync(tgtPath, patchedSrc, 'utf8');

console.log('Generated CDS user settings successfully!');
