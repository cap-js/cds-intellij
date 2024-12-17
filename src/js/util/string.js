function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

function toScreamingSnakeCase(string) {
    return string
        .replace(/-/g, '_')
        .replace(/([a-z])([A-Z])/g, '$1_$2')
        .toUpperCase();
}

function removeMarkdownFormatting(input) {
    return input
        .replace(/_([^_]+)_/g, '$1')
        .replace(/\*\*([^*]+)\*\*/g, "'$1'")
        .replace(/\*([^*]+)\*/g, '$1');
}

module.exports = {
    capitalizeFirstLetter,
    toScreamingSnakeCase,
    removeMarkdownFormatting
}
