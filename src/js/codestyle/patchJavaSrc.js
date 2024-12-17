#!/bin/env node

const path = require('path');
const { readFileSync, writeFileSync } = require('node:fs');

const { capitalizeFirstLetter, removeMarkdownFormatting, toScreamingSnakeCase } = require('../util/string');

const schemaPath = path.resolve(__dirname, '../../../lsp/node_modules/@sap/cds-lsp/schemas/cds-prettier.json');
const schema = require(schemaPath);
const optsFromSchema = schema.properties;

const srcPath = path.resolve(__dirname, '../../../src/main/java/com/sap/cap/cds/intellij/codestyle/CdsCodeStyleSettings.java');
const src = readFileSync(srcPath, 'utf8');

function getGroup(label) {
  const reducedLabel = label.replace(/^(?:Align|Blanks?) /, '');
  return capitalizeFirstLetter(removeMarkdownFormatting(reducedLabel));
}

const parentOptionGroups = Object.values(optsFromSchema)
    .map(opt => opt.parentOption)
    .filter(Boolean)
    .reduce((acc, parentName) => {
      if (parentName in acc) {
        return acc;
      }
      const parentOpt = optsFromSchema[parentName];
      acc[parentName] = getGroup(parentOpt.label);
      return acc;
    }, {});

const categoryGroups = {};

const options = Object.entries(optsFromSchema)
    .sort(([name1], [name2]) => name1.localeCompare(name2))
    .map(([name, attribs]) => {
      const type = attribs.type === 'boolean'
          ? 'boolean'
          : 'int';
      const parentOption = attribs.parentOption;
      const group = parentOption
          ? parentOptionGroups[parentOption]
          : parentOptionGroups[name] || 'Other';
      const category = attribs.category === 'Alignment'
          ? 'ALIGNMENT'
          : attribs.category === 'Whitespace'
              ? 'SPACES'
              : attribs.category === 'Other'
                  ? name.includes('tab')
                      ? 'TABS_AND_INDENTS'
                      : name.includes('omments')
                          ? 'COMMENTS'
                          : /(?:Empty|Single)Line/.test(name)
                              ? 'BLANK_LINES'
                              : /keep.*(?:Line|Together)|max.*Line$|New[Ll]ine/.test(name)
                                  ? 'WRAPPING_AND_BRACES'
                                  : 'OTHER'
                  : 'OTHER';

      (categoryGroups[category] ??= new Set()).add(group);

      const defaultValue = attribs.enum
          ? `${enumName(name)}.${enumValueName(attribs.default)}.getId()`
          : attribs.default;

      return {
        name,
        type,
        values: attribs.enum,
        default: defaultValue,
        label: capitalizeFirstLetter(removeMarkdownFormatting(attribs.label)),
        group: capitalizeFirstLetter(group),
        category
      };
    })
    .filter(Boolean);

const t = '    ';

const initSection = `
${t}public static final Map<String, CdsCodeStyleOptionDef<?>> OPTION_DEFS = new HashMap<>();
${t}public static final Map<Category, Set<String>> CATEGORY_GROUPS = new HashMap<>();

${t}static {
${options.map(opt => `${t}${t}OPTION_DEFS.put("${opt.name}", new CdsCodeStyleOptionDef<>("${opt.name}", ${opt.default}, "${opt.label}", "${opt.group}", Category.${opt.category}));`).join('\n')}

${Object.entries(categoryGroups).map(([category, groups]) => `${t}${t}CATEGORY_GROUPS.put(Category.${category}, Set.of(${[...groups].map(g => `"${g}"`).join(', ')}));`).join('\n')}
${t}}

${options.map(opt => `${t}public ${opt.type} ${opt.name} = ${opt.default};`).join('\n')}
${options.filter(opt => opt.values).map(enumDef).join('\n')}

`;

const patchedSrc = src.replace(
    /(?<=(public class CdsCodeStyleSettings [\w ]*\{\n)).*(?=(\n\s*public CdsCodeStyleSettings))/s,
    initSection
);

writeFileSync(srcPath, patchedSrc, 'utf8');



function enumName(name) {
  return capitalizeFirstLetter(name);
}

function enumValueName(value) {
  return toScreamingSnakeCase(value);
}

function enumDef(attribs) {
  const name = enumName(attribs.name);
  const values = attribs.values.map((value, id) => `${t}${t}${enumValueName(value)}(${id}, "${value}")`).join(',\n');
  return `
${t}public enum ${name} {
${values};
${t}${t}private final String label;
${t}${t}private final int id;

${t}${t}${name}(int id, String label) {
${t}${t}${t}this.id = id;
${t}${t}${t}this.label = label;
${t}${t}}

${t}${t}public String getLabel() {
${t}${t}${t}return label;
${t}${t}}

${t}${t}public int getId() {
${t}${t}${t}return id;
${t}${t}}
${t}}`;
}
