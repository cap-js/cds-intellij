#!/usr/bin/env node

const path = require('path');
const { readFileSync, writeFileSync } = require('node:fs');
const assert = require('node:assert');

const { capitalizeFirstLetter, removeMarkdownFormatting, toScreamingSnakeCase } = require('../util/string');

const schemaPath = path.resolve(__dirname, '../../../lsp/node_modules/@sap/cds-lsp/schemas/cds-prettier.json');
const schema = require(schemaPath);
const optsFromSchema = schema.properties;
const sample = Object.values(optsFromSchema)[0].sample;

const srcPath = path.resolve(__dirname, '../../../src/templates/java/com/sap/cap/cds/intellij/codestyle/CdsCodeStyleSettings.java');
const tgtPath = path.resolve(__dirname, '../../../src/main/java/com/sap/cap/cds/intellij/codestyle/CdsCodeStyleSettings.java');
const src = readFileSync(srcPath, 'utf8');

function getGroup(label) {
  const reducedLabel = label.replace(/^(?:Align|Blanks?) /, '');
  return capitalizeFirstLetter(removeMarkdownFormatting(reducedLabel));
}

function getEnumName(name) {
  return capitalizeFirstLetter(name);
}

function getEnumValueName(value) {
  return toScreamingSnakeCase(value);
}

function getEnumDef(attribs) {
  const name = getEnumName(attribs.name);
  const values = attribs.values.map((value, id) => `${t}${t}${getEnumValueName(value)}(${id}, "${value}")`).join(',\n');
  return `
${t}public enum ${name} implements Enum {
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

const parentOptionGroups = Object.values(optsFromSchema)
    .map(opt => opt.parentOption)
    .filter(Boolean)
    .reduce((acc, parentName) => {
      if (parentName in acc) {
        return acc;
      }
      const parentLabel = optsFromSchema[parentName].label;
      return { ...acc, [parentName]: getGroup(parentLabel) };
    }, {});

const parentOptionChildren = Object.entries(optsFromSchema)
    .filter(([, opt]) => opt.parentOption)
    .reduce((acc, [name, opt]) => {
      const parent = opt.parentOption;
      (acc[parent] ??= []).push(name);
      return acc;
    }, {});

const categoryGroups = [
    'TABS_AND_INDENTS',
    'SPACES',
    'ALIGNMENT',
    'WRAPPING_AND_BRACES',
    'BLANK_LINES',
    'COMMENTS',
    'OTHER'
].reduce((acc, cur) => ({ ...acc, [cur]: new Set() }), {});

const options = Object.entries(optsFromSchema)
    .sort(([name1], [name2]) => name1.localeCompare(name2))
    .map(([name, attribs]) => {
      const type = attribs.type === 'boolean'
          ? 'BOOLEAN'
          : attribs.enum
              ? 'ENUM'
              : 'INT';
      const fieldType = type === 'ENUM'
          ? 'int'
          : type.toLowerCase();
      const parent = attribs.parentOption;
      const group = parent
          ? parentOptionGroups[parent]
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

      assert (category in categoryGroups, `Unknown category: ${category}`);
      categoryGroups[category].add(group);

      const defaultValue = attribs.enum
          ? `${getEnumName(name)}.${getEnumValueName(attribs.default)}.getId()`
          : attribs.default;

      return {
        name,
        type,
        fieldType,
        parent,
        children: parentOptionChildren[name],
        values: attribs.enum,
        default: defaultValue,
        label: capitalizeFirstLetter(removeMarkdownFormatting(attribs.label)),
        group: capitalizeFirstLetter(group),
        category
      };
    })
    .filter(Boolean);

const t = '    ';

// START CLASS BODY
const classBody = `
${t}// Generated code - do not edit manually

${t}public static final String SAMPLE_SRC = """
${n = `${t}public static final String SAMPLE_SRC = `.length, sample.replace(/^/gm, ' '.repeat(n))}
                                            """;

${t}static {
${options.map(opt =>
    `${t}${t}OPTIONS.put("${opt.name}", new CdsCodeStyleOption("${opt.name}", ${opt.type}, ${opt.default}, "${opt.label}", "${opt.group}", ${opt.category}, ` +
    `${opt.parent ? `"${opt.parent}"` : 'null'}, List.of(${opt.children?.map(c => `"${c}"`).join(', ') ?? ''})` +
    `${
        opt.values ? ', ' + opt.values.map(v => `${getEnumName(opt.name)}.${getEnumValueName(v)}`).join(', ') : ''
    }));`
).join('\n')}

${Object.entries(categoryGroups).map(([category, groups]) =>
    `${t}${t}CATEGORY_GROUPS.put(Category.${category}, Set.of(${[...groups].map(g => `"${g}"`).join(', ')}));`
).join('\n')}
${t}}
${t}public CdsCodeStyleSettings(CodeStyleSettings settings) {
${t}${t}super(settings);
${t}${t}settings.initIndentOptions();
${t}${t}settings.getIndentOptions().INDENT_SIZE = this.tabSize;
${t}${t}settings.getIndentOptions().USE_TAB_CHARACTER = false;
${t}}

${options.map(opt => `${t}public ${opt.fieldType} ${opt.name} = ${opt.default};`).join('\n')}
${options.filter(opt => opt.values).map(getEnumDef).join('\n')}

${t}public interface Enum {
${t}${t}String getLabel();

${t}${t}int getId();
${t}}

`;
// END CLASS BODY

const patchedSrc = src.replace(
    /(?<=public class CdsCodeStyleSettings extends CdsCodeStyleSettingsBase \{\n).*(?=^})/sm,
    classBody
);

writeFileSync(tgtPath, patchedSrc, 'utf8');
