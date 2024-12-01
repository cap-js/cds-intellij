#!/bin/env node

const path = require('path');
const { readFileSync, writeFileSync } = require('node:fs');

const { capitalizeFirstLetter, removeMarkdownFormatting } = require('../util/string');

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
    .map(([name, attribs]) => {
      const type = attribs.type === 'boolean'
          ? 'boolean'
          : attribs.type === 'number'
              ? 'int'
              : 'ENUM';
      // TODO enum
      if (type === 'ENUM') {
        return;
      }
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
                      : /(?:Empty|Single)Line/.test(name)
                          ? 'BLANK_LINES'
                          : /keep.*(?:Line|Together)|New[Ll]ine/.test(name)
                              ? 'WRAPPING_AND_BRACES'
                              : 'OTHER'
                  : 'OTHER';

      (categoryGroups[category] ??= new Set()).add(group);

      return {
        name,
        type,
        default: attribs.default,
        label: capitalizeFirstLetter(removeMarkdownFormatting(attribs.label)),
        group: capitalizeFirstLetter(group),
        category
      };
      //return `    public ${type} ${name};`
    })
    .filter(Boolean);

const optionFieldsSrc = options
    .reduce((acc, opt) => `${acc}\npublic ${opt.type} ${opt.name} = ${opt.default};`, '')
    .replace(/^/gm, '    ');

const staticMembers = `    public static final Map<String, CdsCodeStyleOption<?>> OPTIONS = new HashMap<>();\n    public static final Map<Category, Set<String>> CATEGORY_GROUPS = new HashMap<>();\n`;
const startStaticInit = '    static {';
const endStaticInit = '    }';

const optionsMapSrc = options
    .reduce((acc, opt, i) =>
            `${acc}\nOPTIONS.put("${opt.name}", new CdsCodeStyleOption<>("${opt.name}", ${opt.default}, "${opt.label}", "${opt.group}", Category.${opt.category}));`,
        ''
    ).replace(/^/gm, '        ');

const categoryGroupsMapSrc = Object.entries(categoryGroups)
    .reduce((acc, [category, groups]) => `${acc}\nCATEGORY_GROUPS.put(Category.${category}, Set.of(${[...groups].map(g => `"${g}"`).join(', ')}));`,
        ''
    ).replace(/^/gm, '        ');

const patched = src.replace(
    /(?<=(public class CdsCodeStyleSettings [\w ]*\{\n)).*(?=(\n\s*public CdsCodeStyleSettings))/sm,
    `\n${staticMembers}\n${startStaticInit}${optionsMapSrc}\n${categoryGroupsMapSrc}\n${endStaticInit}\n${optionFieldsSrc}\n`
);

writeFileSync(srcPath, patched, 'utf8');
