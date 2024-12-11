function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

function removeMarkdownFormatting(input) {
    return input
        .replace(/_([^_]+)_/g, '$1')
        .replace(/\*\*([^*]+)\*\*/g, "'$1'")
        .replace(/\*([^*]+)\*/g, '$1');
}

module.exports = {
    capitalizeFirstLetter,
    removeMarkdownFormatting
}
