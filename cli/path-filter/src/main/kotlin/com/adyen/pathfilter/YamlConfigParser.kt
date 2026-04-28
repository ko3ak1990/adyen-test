package com.adyen.pathfilter

import org.yaml.snakeyaml.Yaml
import java.io.File

class YamlConfigParser {
    fun parse(configPath: String): FiltersFile {
        val file = File(configPath)
        if (!file.exists()) {
            throw IllegalArgumentException("Config file does not exist: $configPath")
        }

        val root = try {
            @Suppress("UNCHECKED_CAST")
            Yaml().load<Any>(file.readText()) as? Map<String, Any>
                ?: throw IllegalArgumentException("YAML must contain a map at the top level")
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to parse YAML config: ${e.message}", e)
        }

        val filtersNode = root["filters"]
            ?: throw IllegalArgumentException("Missing required top-level key: filters")

        @Suppress("UNCHECKED_CAST")
        val filterMap = filtersNode as? Map<String, Any>
            ?: throw IllegalArgumentException("'filters' must be a map of filter names to pattern lists")

        val ordered = LinkedHashMap<String, FilterConfig>()
        for ((name, value) in filterMap) {
            @Suppress("UNCHECKED_CAST")
            val patterns = value as? List<Any>
                ?: throw IllegalArgumentException("Filter '$name' must be a list of string patterns")

            val asStrings = patterns.mapIndexed { index, item ->
                item as? String
                    ?: throw IllegalArgumentException("Filter '$name' contains non-string pattern at index $index")
            }
            ordered[name] = FilterConfig(asStrings)
        }

        return FiltersFile(ordered)
    }
}

