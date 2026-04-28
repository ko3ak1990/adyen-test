package com.adyen.junitxmlfilename

class StackTraceLineExtractor {
    private val frameRegex = Regex("""\(([^:]+):(\d+)\)""")

    fun extract(stackTrace: String, expectedFileName: String?): Int? {
        if (expectedFileName.isNullOrBlank()) return null

        for (line in stackTrace.lineSequence()) {
            if (!line.contains(expectedFileName)) continue
            val match = frameRegex.find(line) ?: continue
            return match.groupValues[2].toIntOrNull()
        }
        return null
    }
}

