package com.adyen.pathfilter

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap

class GlobMatcher {
    private val cache = ConcurrentHashMap<String, PathMatcher>()

    fun matches(pattern: String, filePath: String): Boolean {
        val normalizedPattern = normalizePattern(pattern)
        val path: Path = Paths.get(normalizePath(filePath))

        if (matcherFor(normalizedPattern).matches(path)) return true

        // java.nio glob treats '/**/' strictly; also try a collapsed variant to allow zero segments.
        if (normalizedPattern.contains("/**/")) {
            val collapsed = normalizedPattern.replace("/**/", "/")
            if (matcherFor(collapsed).matches(path)) return true
        }

        return false
    }

    private fun matcherFor(pattern: String): PathMatcher = cache.computeIfAbsent(pattern) {
        FileSystems.getDefault().getPathMatcher("glob:$it")
    }

    private fun normalizePattern(pattern: String): String =
        pattern.replace('\\', '/')

    private fun normalizePath(path: String): String =
        path.replace('\\', '/').removePrefix("./")
}
