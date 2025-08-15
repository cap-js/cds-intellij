#!/usr/bin/env node

const path = require('path');
const { readFileSync, writeFileSync } = require('node:fs');

const schemaPath = path.resolve(__dirname, '../../../lsp/schemas/user-settings.json');
const schema = require(schemaPath);
const settingsFromSchema = schema.properties;

const tgtPath = path.resolve(__dirname, '../../../src/main/java/com/sap/cap/cds/intellij/usersettings/CdsUserSettings.java');
const tgt = readFileSync(tgtPath, 'utf8');

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

const t = '    ';

// Generate method bodies
const getDefaultsBody = `
${t}${t}Map<String, Object> defaults = new HashMap<>();
${settings.map(s => `${t}${t}defaults.put("${s.key}", ${s.defaultValue});`).join('\n')}
${t}${t}return defaults;`;

const getLabelBody = `
${t}${t}switch (settingKey) {
${settings.filter(s => s.label).map(s =>
    `${t}${t}${t}case "${s.key}": return "${s.label.replace(/"/g, '\\"')}";`
).join('\n')}
${t}${t}${t}default: return null;
${t}${t}}`;

const getEnumValuesBody = `
${t}${t}switch (settingKey) {
${settings.filter(s => s.enumValues).map(s =>
    `${t}${t}${t}case "${s.key}": return new String[]{${s.enumValues.map(v => `"${v}"`).join(', ')}};`
).join('\n')}
${t}${t}${t}default: return null;
${t}${t}}`;

const hasEnumValuesBody = `
${t}${t}return getEnumValues(settingKey) != null;`;

let patchedTgt = tgt;

// Replace method bodies using simplified lookbehind patterns
patchedTgt = patchedTgt.replace(
    /(?<=private\s+static\s+[^\n{]*\bgetDefaults\([^\n{]*\{\s*).*?(?=\n    }$)/sm,
    getDefaultsBody
);

patchedTgt = patchedTgt.replace(
    /(?<=public\s+static\s+[^\n{]*\bgetLabel\([^\n{]*\{\s*).*?(?=\n    }$)/sm,
    getLabelBody
);

patchedTgt = patchedTgt.replace(
    /(?<=public\s+static\s+[^\n{]*\bgetEnumValues\([^\n{]*\{\s*).*?(?=\n    }$)/sm,
    getEnumValuesBody
);

patchedTgt = patchedTgt.replace(
    /(?<=public\s+static\s+[^\n{]*\bhasEnumValues\([^\n{]*\{\s*).*?(?=\n    }$)/sm,
    hasEnumValuesBody
);

writeFileSync(tgtPath, patchedTgt, 'utf8');

console.log('Generated CDS user settings successfully!');
