package com.adyen.pathfilter

import java.io.File

object EnvWriter {
    fun write(outputPath: String, results: Map<String, Boolean>) {
        val file = File(outputPath)
        file.parentFile?.mkdirs()
        val content = buildString {
            results.forEach { (name, value) ->
                append(name)
                append('=')
                append(value)
                append('\n')
            }
        }
        file.writeText(content)
    }
}

