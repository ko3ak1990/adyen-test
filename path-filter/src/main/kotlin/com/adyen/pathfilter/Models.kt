package com.adyen.pathfilter

data class FilterConfig(val patterns: List<String>)

data class FiltersFile(val filters: LinkedHashMap<String, FilterConfig>)

data class CliArgs(
    val configPath: String,
    val outputPath: String,
    val changedFiles: List<String>
)

