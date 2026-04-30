package com.adyen.pathfilter

class FilterEvaluator(
    private val matcher: GlobMatcher = GlobMatcher()
) {
    private data class SplitPatterns(val includes: List<String>, val excludes: List<String>)

    fun evaluate(filter: FilterConfig, changedFiles: List<String>): Boolean {
        val (includes, excludes) = split(filter)
        return changedFiles.any { file ->
            includes.any { include -> matcher.matches(include, file) } &&
                excludes.none { exclude -> matcher.matches(exclude, file) }
        }
    }

    fun evaluateAll(filtersFile: FiltersFile, changedFiles: List<String>): LinkedHashMap<String, Boolean> {
        return filtersFile.filters.entries.associateTo(linkedMapOf()) { (name, config) ->
            name to evaluate(config, changedFiles)
        }
    }

    private fun split(filter: FilterConfig): SplitPatterns {
        val (rawExcludes, includes) = filter.patterns.partition { it.startsWith("!") }
        return SplitPatterns(includes, rawExcludes.map { it.removePrefix("!") })
    }
}
