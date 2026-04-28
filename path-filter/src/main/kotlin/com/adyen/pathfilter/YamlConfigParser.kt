package com.adyen.pathfilter

import org.yaml.snakeyaml.Yaml
import java.nio.file.Files
import java.nio.file.Path

class YamlConfigParser {
    fun parse(configPath: String): FiltersFile {
        val path = Path.of(configPath)
        require(Files.exists(path)) { "Config file does not exist: $configPath" }

        val root = runCatching {
            @Suppress("UNCHECKED_CAST")
            Yaml().load<Any>(Files.readString(path)) as? Map<String, Any>
                ?: throw IllegalArgumentException("YAML must contain a map at the top level")
        }.getOrElse { error ->
            throw IllegalArgumentException("Failed to parse YAML config: ${error.message}", error)
        }

        val filtersNode = root["filters"]
            ?: throw IllegalArgumentException("Missing required top-level key: filters")

        @Suppress("UNCHECKED_CAST")
        val filterMap = filtersNode as? Map<String, Any>
            ?: throw IllegalArgumentException("'filters' must be a map of filter names to pattern lists")

        val ordered = LinkedHashMap<String, FilterConfig>()
        filterMap.forEach { (name, value) ->
            val patterns = value.asPatternList(name)
            ordered[name] = FilterConfig(patterns)
        }

        return FiltersFile(ordered)
    }

    private fun Any.asPatternList(filterName: String): List<String> {
        @Suppress("UNCHECKED_CAST")
        val values = this as? List<Any>
            ?: throw IllegalArgumentException("Filter '$filterName' must be a list of string patterns")

        return values.mapIndexed { index, item ->
            item as? String
                ?: throw IllegalArgumentException("Filter '$filterName' contains non-string pattern at index $index")
        }
    }
}
