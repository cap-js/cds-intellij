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
  if (type === 'integer' || type === 'number') return config.default;
  if (type === 'array') return `"${Array.isArray(config.default) ? config.default.join(',') : ''}"`;
  return `"${(config.default || '').replace(/"/g, '\\"')}"`;
}

const settings = Object.entries(settingsFromSchema)
  .sort(([key1], [key2]) => key1.localeCompare(key2))
  .map(([key, config]) => ({
    key,
    defaultValue: getDefaultValue(config),
    enumValues: config.enum || null,
    label: config.label || null,
    description: config.description || null
  }));

const t = '    ';

// Generate method bodies
const staticInitializerBody = `
${t}${t}Map<String, Object> defaults = new LinkedHashMap<>();
${settings.map(s => `${t}${t}defaults.put("${s.key}", ${s.defaultValue});`).join('\n')}
${t}${t}DEFAULTS = Collections.unmodifiableMap(defaults);`;

const getLabelBody = `
${t}${t}return switch (settingKey) {
${settings.filter(s => s.label).map(s =>
    `${t}${t}${t}case "${s.key}" -> "${s.label.replace(/"/g, '\\"')}";`
).join('\n')}
${t}${t}${t}default -> null;
${t}${t}};`;

function formatJavaDescription(description) {
  if (!description) return 'null';
  const escaped = description.replace(/"/g, '\\"').replace(/\n/g, '\\n');
  return `"${escaped}"`;
}

const getDescriptionBody = `
${t}${t}return switch (settingKey) {
${settings.filter(s => s.description).map(s =>
    `${t}${t}${t}case "${s.key}" -> ${formatJavaDescription(s.description)};`
).join('\n')}
${t}${t}${t}default -> null;
${t}${t}};`;

const getEnumValuesBody = `
${t}${t}return switch (settingKey) {
${settings.filter(s => s.enumValues).map(s =>
    `${t}${t}${t}case "${s.key}" -> new String[]{${s.enumValues.map(v => `"${v}"`).join(', ')}};`
).join('\n')}
${t}${t}${t}default -> null;
${t}${t}};`;

const hasEnumValuesBody = `
${t}${t}return getEnumValues(settingKey) != null;`;

let patchedTgt = tgt;

// Replace method bodies using simplified lookbehind patterns
patchedTgt = patchedTgt.replace(
    /(?<=static\s*\{).*?(?=\n    })/sm,
    staticInitializerBody
);

patchedTgt = patchedTgt.replace(
    /(?<=\bgetLabel\s*\([^)]*\)\s*\{).*?(?=\n    })/sm,
    getLabelBody
);

patchedTgt = patchedTgt.replace(
    /(?<=\bgetDescription\s*\([^)]*\)\s*\{).*?(?=\n    })/sm,
    getDescriptionBody
);

patchedTgt = patchedTgt.replace(
    /(?<=\bgetEnumValues\s*\([^)]*\)\s*\{).*?(?=\n    })/sm,
    getEnumValuesBody
);

patchedTgt = patchedTgt.replace(
    /(?<=\bhasEnumValues\s*\([^)]*\)\s*\{).*?(?=\n    })/sm,
    hasEnumValuesBody
);

writeFileSync(tgtPath, patchedTgt, 'utf8');

console.log('Generated CDS user settings successfully!');
