#!/usr/bin/env node

const path = require('path');
const { readFileSync, writeFileSync, existsSync, realpathSync } = require('node:fs');

let cdsLspSchemaPath = path.resolve(__dirname, '../../../../cds-lsp/schemas/user-settings.json');
if (!existsSync(cdsLspSchemaPath)) {
  cdsLspSchemaPath = realpathSync(path.resolve(__dirname, '../../../..')) + '/cds-lsp/schemas/user-settings.json';
}

const schemaPath = path.resolve(__dirname, '../../../lsp/schemas/user-settings.json');
const metadataPath = path.resolve(__dirname, 'userSettingsMetadata.json');

if (!existsSync(cdsLspSchemaPath)) {
  console.error(`Source schema not found at ${cdsLspSchemaPath}`);
  console.error('Make sure to run schema generation in cds-lsp first');
  process.exit(1);
}

if (!existsSync(metadataPath)) {
  console.error(`Metadata file not found at ${metadataPath}`);
  process.exit(1);
}

const cdsLspSchema = JSON.parse(readFileSync(cdsLspSchemaPath, 'utf-8'));
const metadata = JSON.parse(readFileSync(metadataPath, 'utf-8'));

const cdsLspKeys = Object.keys(cdsLspSchema.properties || {});
const metadataKeys = Object.keys(metadata);

const newKeysInUpstream = cdsLspKeys.filter(key => !metadataKeys.includes(key));
if (newKeysInUpstream.length > 0) {
  console.error('\nERROR: New properties found in cds-lsp schema that are not in userSettingsMetadata.json:');
  newKeysInUpstream.forEach(key => console.error(`  - ${key}`));
  console.error('\nPlease add these properties to userSettingsMetadata.json with appropriate category, label, and optional group.');
  process.exit(1);
}

const labelMapping = {
  'Annotation Support': 'Contributions',
  'Code Completion': 'Completion',
  'Where-used': 'Search',
  'Symbols': 'Outline',
  'Type Generation': 'Type Generator',
  'Telemetry': 'Telemetry',
  'Validation': 'Diagnostics',
  'Misc': 'Editor'
};

const augmentedSchema = {
  ...cdsLspSchema,
  properties: {}
};

const orderedKeys = metadataKeys.filter(k => cdsLspKeys.includes(k));

for (const key of orderedKeys) {
  const property = cdsLspSchema.properties[key];
  if (!property) continue;
  
  const augmentedProperty = { ...property };
  
  const meta = metadata[key];
  if (meta) {
    if (meta.category) {
      augmentedProperty.category = meta.category;
    }
    if (meta.group) {
      augmentedProperty.group = meta.group;
    }
    if (meta.label) {
      augmentedProperty.label = meta.label;
    }
  } else {
    if (property.category && labelMapping[property.category]) {
      augmentedProperty.category = labelMapping[property.category];
    }
  }
  
  if (property.markdownDescription) {
    augmentedProperty.description = property.markdownDescription;
    delete augmentedProperty.markdownDescription;
  }
  
  augmentedSchema.properties[key] = augmentedProperty;
}

writeFileSync(schemaPath, JSON.stringify(augmentedSchema, null, 2), 'utf-8');
console.log(`Augmented schema written to ${schemaPath}`);
console.log(`Total properties: ${Object.keys(augmentedSchema.properties).length}`);

const schema = augmentedSchema;
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
    label: config.label || null,
    description: config.description || null,
    category: config.category || null,
    group: config.group || null
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

function convertMarkdownToHtml(markdown) {
  return markdown
      .replace(/_([^_]+)_/g, '<i>$1</i>')
      .replace(/\*\*([^*]+)\*\*/g, '<b>$1</b>')
      .replace(/`([^`]+)`/g, '<code>$1</code>')
      .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '$1 ($2)')
      .replace(/\n/g, '<br>');
}

function formatJavaDescription(description) {
  if (!description) return 'null';
  const htmlContent = convertMarkdownToHtml(description);
  const escaped = htmlContent.replace(/"/g, '\\"');
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

const getGroupBody = `
${t}${t}return switch (settingKey) {
${settings.filter(s => s.group).map(s =>
    `${t}${t}${t}case "${s.key}" -> "${s.group.replace(/"/g, '\\"')}";`
).join('\n')}
${t}${t}${t}default -> null;
${t}${t}};`;

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

patchedTgt = patchedTgt.replace(
    /(?<=\bgetGroup\s*\([^)]*\)\s*\{).*?(?=\n    })/sm,
    getGroupBody
);

patchedTgt = patchedTgt.replace(
    /(?<=\bgetCategory\s*\([^)]*\)\s*\{).*?(?=\n    })/sm,
    getCategoryBody
);

writeFileSync(tgtPath, patchedTgt, 'utf8');

console.log('Generated CDS user settings successfully!');
