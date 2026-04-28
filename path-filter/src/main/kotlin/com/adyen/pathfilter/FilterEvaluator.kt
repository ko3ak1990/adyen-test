package com.adyen.pathfilter

class FilterEvaluator(
    private val matcher: GlobMatcher = GlobMatcher()
) {
    fun evaluate(filter: FilterConfig, changedFiles: List<String>): Boolean {
        val (rawExcludes, includes) = filter.patterns.partition { it.startsWith("!") }
        val excludes = rawExcludes.map { it.removePrefix("!") }

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
}
