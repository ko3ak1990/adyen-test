package com.adyen.pathfilter

import java.nio.file.FileSystems
import java.nio.file.Paths

class GlobMatcher {
    fun matches(pattern: String, filePath: String): Boolean {
        val normalizedPattern = normalizePattern(pattern)
        val normalizedPath = normalizePath(filePath)

        if (matchesWithPattern(normalizedPattern, normalizedPath)) {
            return true
        }

        // java.nio glob treats '/**/' strictly; also try a collapsed variant to allow zero segments.
        if (normalizedPattern.contains("/**/")) {
            val collapsed = normalizedPattern.replace("/**/", "/")
            if (matchesWithPattern(collapsed, normalizedPath)) {
                return true
            }
        }

        return false
    }

    private fun matchesWithPattern(pattern: String, path: String): Boolean {
        val matcher = FileSystems.getDefault().getPathMatcher("glob:$pattern")
        return matcher.matches(Paths.get(path))
    }

    private fun normalizePattern(pattern: String): String =
        pattern.replace('\\', '/')

    private fun normalizePath(path: String): String =
        path.replace('\\', '/').removePrefix("./")
}
