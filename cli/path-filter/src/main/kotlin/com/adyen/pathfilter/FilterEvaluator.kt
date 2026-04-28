package com.adyen.pathfilter

class FilterEvaluator(
    private val matcher: GlobMatcher = GlobMatcher()
) {
    fun evaluate(filter: FilterConfig, changedFiles: List<String>): Boolean {
        val includes = filter.patterns.filterNot { it.startsWith("!") }
        val excludes = filter.patterns.filter { it.startsWith("!") }.map { it.removePrefix("!") }

        return changedFiles.any { file ->
            includes.any { include -> matcher.matches(include, file) } &&
                excludes.none { exclude -> matcher.matches(exclude, file) }
        }
    }

    fun evaluateAll(filtersFile: FiltersFile, changedFiles: List<String>): LinkedHashMap<String, Boolean> {
        val results = LinkedHashMap<String, Boolean>()
        for ((name, config) in filtersFile.filters) {
            results[name] = evaluate(config, changedFiles)
        }
        return results
    }
}

