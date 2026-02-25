#!/usr/bin/env node

const path = require('path');
const { readFileSync, writeFileSync } = require('node:fs');

const schemaPath = path.resolve(__dirname, '../../../lsp/node_modules/@sap/cds-lsp/schemas/user-settings.json');
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
  .map(([key, config]) => ({
    key,
    defaultValue: getDefaultValue(config),
    enumValues: config.enum || null,
    markdownDescription: config.markdownDescription || null,
    category: config.category || null
  }));

const t = '    ';

// Generate method bodies
const staticInitializerBody = `
${t}${t}Map<String, Object> defaults = new LinkedHashMap<>();
${settings.map(s => `${t}${t}defaults.put("${s.key}", ${s.defaultValue});`).join('\n')}
${t}${t}DEFAULTS = Collections.unmodifiableMap(defaults);`;

function convertMarkdownToHtml(markdown) {
  return markdown
      .replace(/_([^_]+)_/g, '<i>$1</i>')
      .replace(/\*\*([^*]+)\*\*/g, '<b>$1</b>')
      .replace(/`([^`]+)`/g, '<code>$1</code>')
      .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '$1 ($2)')
      .replace(/\n/g, '<br>');
}

function formatJavaDescription(markdownDescription) {
  if (!markdownDescription) return 'null';
  const htmlContent = convertMarkdownToHtml(markdownDescription.replace(/\n+/, '\n'));
  const escaped = htmlContent.replace(/"/g, '\\"');
  return `"${escaped}"`;
}

const getDescriptionBody = `
${t}${t}return switch (settingKey) {
${settings.filter(s => s.markdownDescription).map(s =>
    `${t}${t}${t}case "${s.key}" -> ${formatJavaDescription(s.markdownDescription)};`
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

const getCategoryBody = `
${t}${t}return switch (settingKey) {
${settings.filter(s => s.category).map(s =>
    `${t}${t}${t}case "${s.key}" -> "${s.category.replace(/"/g, '\\"')}";`
).join('\n')}
${t}${t}${t}default -> null;
${t}${t}};`;

let patchedTgt = tgt;

// Replace method bodies using simplified lookbehind patterns
patchedTgt = patchedTgt.replace(
    /(?<=static\s*\{).*?(?=\n    })/sm,
    staticInitializerBody
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

// Remove getGroup method completely
patchedTgt = patchedTgt.replace(
    /\n\s*\/\/ Note: method body is generated\s*\n\s*public static String getGroup\([^)]*\)\s*\{[^}]*\n\s*\}/sm,
    ''
);

patchedTgt = patchedTgt.replace(
    /(?<=\bgetCategory\s*\([^)]*\)\s*\{).*?(?=\n    })/sm,
    getCategoryBody
);

writeFileSync(tgtPath, patchedTgt, 'utf8');

console.log('Generated CDS user settings successfully!');
